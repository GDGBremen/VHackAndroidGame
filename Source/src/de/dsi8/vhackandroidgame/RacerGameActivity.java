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

import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.math.MathUtils;

import android.graphics.Canvas;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.dsi8.dsi8acl.connection.impl.TCPSocketConnector;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 22:43:20 - 15.07.2010
 */
public class RacerGameActivity extends SimpleBaseGameActivity implements IServerLogicListener {
	// ===========================================================
	// Constants
	// ===========================================================

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

	private BitmapTextureAtlas mRacetrackTexture;
	private ITextureRegion mRacetrackStraightTextureRegion;
	private ITextureRegion mRacetrackCurveTextureRegion;


	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;
	
	private IServerLogic serverLogic;
	
	private final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
	
	private Map<Integer, CarView> cars = new HashMap<Integer, RacerGameActivity.CarView>();

	private BitmapTextureAtlas qrCodeAtlas;

	private TextureRegion qrCodeAtlasRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	
	@Override
	protected void onStart() {
		super.onStart();
		
		serverLogic = new ServerLogic(this);
		serverLogic.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		serverLogic.close();
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mVehiclesTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 16, TextureOptions.BILINEAR);
		this.mVehiclesTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mVehiclesTexture, this, "vehicles.png", 0, 0, 6, 1);
		this.mVehiclesTexture.load();

		this.mRacetrackTexture = new BitmapTextureAtlas(this.getTextureManager(), 128, 256, TextureOptions.REPEATING_NEAREST);
		this.mRacetrackStraightTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mRacetrackTexture, this, "racetrack_straight.png", 0, 0);
		this.mRacetrackCurveTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mRacetrackTexture, this, "racetrack_curve.png", 0, 128);
		this.mRacetrackTexture.load();

		

		this.mBoxTexture = new BitmapTextureAtlas(this.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBoxTexture, this, "box.png", 0, 0);
		this.mBoxTexture.load();
		
		createBarcode();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);

		this.mPhysicsWorld.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void endContact(Contact arg0) {
				// TODO Auto-generated method stub
				
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
		});
		
		this.initRacetrack();
		this.initRacetrackBorders();
		this.initObstacles();
		
		int bardcodeSize = CAMERA_HEIGHT - 2 * RACETRACK_WIDTH;
		
		final Sprite barcode = new Sprite(CAMERA_WIDTH / 2 - bardcodeSize/2 , RACETRACK_WIDTH, bardcodeSize, bardcodeSize, qrCodeAtlasRegion, this.getVertexBufferObjectManager());
		this.mScene.attachChild(barcode);

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

				
		
		return this.mScene;
	}
	
	public int getCarIdFromBody(Body body) {
		for (CarView carView : this.cars.values()) {
			if (carView.body == body) {
				return carView.id;
			}
		}
		
		return -1;
	}
	

	@Override
	public void onGameCreated() {

	}

	// ===========================================================
	// Methods
	// ===========================================================


	private void createBarcode() {
		ConnectionParameter param = ConnectionParameter.getDefaultConnectionDetails();
		MultiFormatWriter writer = new MultiFormatWriter();
		try {
			final BitMatrix bitmatrix = writer.encode(param.toConnectionURL(), BarcodeFormat.QR_CODE, 150, 150);
			
		
			this.qrCodeAtlas = new BitmapTextureAtlas(this.getTextureManager(), 150, 150, TextureOptions.DEFAULT);
	
			final IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(150, 150);
			final BaseBitmapTextureAtlasSourceDecorator decoratedTextureAtlasSource = new BaseBitmapTextureAtlasSourceDecorator(baseTextureSource) {
				@Override
				protected void onDecorateBitmap(Canvas pCanvas) throws Exception {
					pCanvas.drawRGB(0, 0, 0);
					this.mPaint.setColor(Color.WHITE);
					for(int y = 0; y < bitmatrix.getHeight(); y++) {
						for(int x = 0; x < bitmatrix.getWidth(); x++) {
							if(!bitmatrix.get(x, y)) {
								pCanvas.drawPoint(x, y, mPaint);
							}
						}
					}
				}
	
				@Override
				public BaseBitmapTextureAtlasSourceDecorator deepCopy() {
					throw new RuntimeException();
				}
			};

			this.qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory.createFromSource(this.qrCodeAtlas, decoratedTextureAtlasSource, 0, 0);
			this.qrCodeAtlas.load();
		
		
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	private void initObstacles() {
		this.addObstacle(CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);
		this.addObstacle(CAMERA_WIDTH / 2, CAMERA_HEIGHT - RACETRACK_WIDTH / 2);
	}

	private void addObstacle(final float pX, final float pY) {
		final Sprite box = new Sprite(pX, pY, OBSTACLE_SIZE, OBSTACLE_SIZE, this.mBoxTextureRegion, this.getVertexBufferObjectManager());

		final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0.1f, 0.5f, 0.5f);
		final Body boxBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, box, BodyType.DynamicBody, boxFixtureDef);
		boxBody.setLinearDamping(10);
		boxBody.setAngularDamping(10);

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(box, boxBody, true, true));

		this.mScene.attachChild(box);
	}

	private void initRacetrack() {
		/* Straights. */
		{
			final ITextureRegion racetrackHorizontalStraightTextureRegion = this.mRacetrackStraightTextureRegion.deepCopy();
			racetrackHorizontalStraightTextureRegion.setTextureWidth(3 * this.mRacetrackStraightTextureRegion.getWidth());

			final ITextureRegion racetrackVerticalStraightTextureRegion = this.mRacetrackStraightTextureRegion;

			/* Top Straight */
			this.mScene.attachChild(new Sprite(RACETRACK_WIDTH, 0, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion, this.getVertexBufferObjectManager()));
			/* Bottom Straight */
			this.mScene.attachChild(new Sprite(RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, 3 * RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackHorizontalStraightTextureRegion, this.getVertexBufferObjectManager()));

			/* Left Straight */
			final Sprite leftVerticalStraight = new Sprite(0, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion, this.getVertexBufferObjectManager());
			leftVerticalStraight.setRotation(90);
			this.mScene.attachChild(leftVerticalStraight);
			/* Right Straight */
			final Sprite rightVerticalStraight = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackVerticalStraightTextureRegion, this.getVertexBufferObjectManager());
			rightVerticalStraight.setRotation(90);
			this.mScene.attachChild(rightVerticalStraight);
		}

		/* Edges */
		{
			final ITextureRegion racetrackCurveTextureRegion = this.mRacetrackCurveTextureRegion;

			/* Upper Left */
			final Sprite upperLeftCurve = new Sprite(0, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			upperLeftCurve.setRotation(90);
			this.mScene.attachChild(upperLeftCurve);

			/* Upper Right */
			final Sprite upperRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, 0, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			upperRightCurve.setRotation(180);
			this.mScene.attachChild(upperRightCurve);

			/* Lower Right */
			final Sprite lowerRightCurve = new Sprite(CAMERA_WIDTH - RACETRACK_WIDTH, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			lowerRightCurve.setRotation(270);
			this.mScene.attachChild(lowerRightCurve);

			/* Lower Left */
			final Sprite lowerLeftCurve = new Sprite(0, CAMERA_HEIGHT - RACETRACK_WIDTH, RACETRACK_WIDTH, RACETRACK_WIDTH, racetrackCurveTextureRegion, this.getVertexBufferObjectManager());
			this.mScene.attachChild(lowerLeftCurve);
		}
	}


	private void initRacetrackBorders() {
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

		final Rectangle bottomOuter = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle topOuter = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle leftOuter = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle rightOuter = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final Rectangle bottomInner = new Rectangle(RACETRACK_WIDTH, CAMERA_HEIGHT - 2 - RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle topInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, CAMERA_WIDTH - 2 * RACETRACK_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle leftInner = new Rectangle(RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH, vertexBufferObjectManager);
		final Rectangle rightInner = new Rectangle(CAMERA_WIDTH - 2 - RACETRACK_WIDTH, RACETRACK_WIDTH, 2, CAMERA_HEIGHT - 2 * RACETRACK_WIDTH, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, topOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftOuter, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightOuter, BodyType.StaticBody, wallFixtureDef);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, bottomInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, topInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, leftInner, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, rightInner, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(bottomOuter);
		this.mScene.attachChild(topOuter);
		this.mScene.attachChild(leftOuter);
		this.mScene.attachChild(rightOuter);

		this.mScene.attachChild(bottomInner);
		this.mScene.attachChild(topInner);
		this.mScene.attachChild(leftInner);
		this.mScene.attachChild(rightInner);
	}

	@Override
	public void driveCar(int carId, float valueX, float valueY) {
		CarView carView = this.cars.get(carId);
		
		final Vector2 velocity = Vector2Pool.obtain(valueX * 5, valueY * 5);
		carView.body.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);

		final float rotationInRad = (float)Math.atan2(-valueX, valueY);
		carView.body.setTransform(carView.body.getWorldCenter(), rotationInRad);

		carView.car.setRotation(MathUtils.radToDeg(rotationInRad));
	}

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
	
	public class CarView {
		public int id;
		
		public Body body;
		
		public TiledSprite car;
		
		public PhysicsConnector physicsConnector;
	}
}
