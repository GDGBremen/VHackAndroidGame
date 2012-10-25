/*******************************************************************************
 * Copyright (C) 2012 Henrik Voß, Sven Nobis and Nicolas Gramlich (AndEngine)
 * 
 * This file is part of VHackAndroidGame
 * (https://github.com/SvenTo/VHackAndroidGame)
 * 
 * VHackAndroidGame is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package de.dsi8.vhackandroidgame;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.math.MathUtils;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogic;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogicListener;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.PresentationLogic;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 22:43:20 - 15.07.2010
 */
public class RacerGameActivity extends SimpleBaseGameActivity implements IServerLogicListener, IPresentationLogicListener, ContactListener {
	// ===========================================================
	// Constants
	// ===========================================================


	private static final String LOG_TAG = RacerGameActivity.class.getSimpleName();
	
	private static final int RACETRACK_WIDTH = 64;

	private static final int OBSTACLE_SIZE = 16;
	private static final int CAR_SIZE = 16;

	private static final int CAMERA_WIDTH = RACETRACK_WIDTH * 5;
	private static final int CAMERA_HEIGHT = RACETRACK_WIDTH * 3;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mVehiclesTexture;
	private TiledTextureRegion mVehiclesTextureRegion;

	private BitmapTextureAtlas mBoxTexture;
	private ITextureRegion mBoxTextureRegion;

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	
	private IServerLogic serverLogic;
	
	private final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	
	private Map<Integer, CarView> cars = new HashMap<Integer, RacerGameActivity.CarView>();

	private IPresentationLogic presentationLogic;
	
	private ConnectTask connectTask;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		this.serverLogic = new ServerLogic(this);
		this.serverLogic.start();
		
		connectTask = new ConnectTask();
		connectTask.start();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		try {
			this.serverLogic.close();
		} catch (IOException e) {
			Log.w(LOG_TAG, "Can not close the server", e);
		}
		

		connectTask.interrupt();
		if(presentationLogic != null) {
			try {
				presentationLogic.close();
			} catch (IOException e) {
				Log.w(LOG_TAG, "Can not close the connection the server.", e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mVehiclesTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 16, TextureOptions.BILINEAR);
		this.mVehiclesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mVehiclesTexture, this, "vehicles.png", 0, 0, 6, 1);
		this.mVehiclesTexture.load();

		this.mBoxTexture = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBoxTexture, this, "box.png", 0, 0);
		this.mBoxTexture.load();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scene onCreateScene() {
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);

		this.mPhysicsWorld.setContactListener(this);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
		return this.mScene;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void driveCar(int carId, float valueX, float valueY) {
		CarView carView = this.cars.get(Integer.valueOf(carId));
		
		final Vector2 velocity = Vector2Pool.obtain(valueX * 5, valueY * 5);
		carView.body.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);

		final float rotationInRad = (float)Math.atan2(-valueX, valueY);
		carView.body.setTransform(carView.body.getWorldCenter(), rotationInRad);

		carView.car.setRotation(MathUtils.radToDeg(rotationInRad));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCar(int carId) {
		CarView carView = new CarView();
		carView.id = carId;
		
		carView.car = new TiledSprite(20, 20, CAR_SIZE, CAR_SIZE, this.mVehiclesTextureRegion, this.getVertexBufferObjectManager());
		carView.car.setCurrentTileIndex(carId%6);

		carView.body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, carView.car, BodyType.DynamicBody, carFixtureDef);
		
		this.mScene.attachChild(carView.car);
		carView.physicsConnector = new PhysicsConnector(carView.car, carView.body, true, false);
		this.mPhysicsWorld.registerPhysicsConnector(carView.physicsConnector);
		this.cars.put(carId, carView);
	}

	@Override
	public void removeCar(int carId) {
		CarView carView = this.cars.remove(carId);
		if (carView != null) {
			this.mPhysicsWorld.unregisterPhysicsConnector(carView.physicsConnector);
			this.mScene.detachChild(carView.car);
		}
	}

	private int getCarIdFromBody(Body body) {
		for (CarView carView : this.cars.values()) {
			if (carView.body == body) {
				return carView.id;
			}
		}
		
		return -1;
	}
	
	@Override
	public void beginContact(Contact contact) {
		int firstCarId = getCarIdFromBody(contact.getFixtureA().getBody());
		if (firstCarId > -1) {
			RacerGameActivity.this.serverLogic.collisionDetected(firstCarId);
		}
		
		int secondCarId = getCarIdFromBody(contact.getFixtureB().getBody());
		if (secondCarId > -1) {
			RacerGameActivity.this.serverLogic.collisionDetected(secondCarId);
		}
	}

	/**
	 * The Task that should connect the client with the host.
	 */
	private class ConnectTask extends Thread  {
		
		@Override
		public void run() {
			try {
				synchronized (this) {
					// Workaround for multicall of onStart by the AndEngine
					this.wait(2000);
					Log.i(LOG_TAG, "Not interrupted");
					Socket socket = new Socket("127.0.0.1", ConnectionParameter.getDefaultConnectionDetails().port);
					presentationLogic = new PresentationLogic(RacerGameActivity.this, socket);
				}
			} catch(InterruptedException ex) {
				Log.i(LOG_TAG, "Interrupted");
				
			} catch (Exception e) {
				Log.e(LOG_TAG, "Interrupted", e);
			}
		}
		
	}
	

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) { /* Not required */ }

	@Override
	public void preSolve(Contact arg0, Manifold arg1) { /* Not required */ }

	@Override
	public void endContact(Contact arg0) { /* Not required */ }
	
	public class CarView {
		public int id;
		
		public Body body;
		
		public TiledSprite car;
		
		public PhysicsConnector physicsConnector;
	}
}
