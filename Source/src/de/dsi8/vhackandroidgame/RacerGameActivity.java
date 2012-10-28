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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.PixelFormat;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage.QRCodePosition;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogic;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationView;
import de.dsi8.vhackandroidgame.logic.impl.PresentationLogic;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 22:43:20 - 15.07.2010
 */
public class RacerGameActivity extends AbstractConnectionActivity implements
		IPresentationView {
	private static final int BALL_SIZE = 40;

	// ===========================================================
	// Constants
	// ===========================================================

	private static final String LOG_TAG = RacerGameActivity.class.getSimpleName();

	private static final int RACETRACK_WIDTH = 64;

	private static final int OBSTACLE_SIZE = 16;
	public static final int CAR_SIZE = BALL_SIZE;

	private static final int CAMERA_WIDTH = 1920;
	private static final int CAMERA_HEIGHT = 1080;

	// ===========================================================
	// Fields
	// ===========================================================

	private Camera mCamera;

	private BitmapTextureAtlas mVehiclesTexture;
	private TiledTextureRegion mVehiclesTextureRegion;

	private BitmapTextureAtlas mBoxTexture;
	private ITextureRegion mBoxTextureRegion;

	private Scene mScene;
	
	private Map<Integer, CarView> cars = new HashMap<Integer, RacerGameActivity.CarView>();

	private IPresentationLogic presentationLogic;

	private BitmapTextureAtlas qrCodeAtlas;

	private TextureRegion qrCodeAtlasRegion;
	
	private Rectangle borderTop;
	private Rectangle borderRight;
	private Rectangle borderBottom;
	private Rectangle borderLeft;

	private Sprite barcodeCenter;
	private Sprite barcodeTop;
	private Sprite barcodeRight;
	private Sprite barcodeBottom;
	private Sprite barcodeLeft;
	
	
	private ITextureRegion mTrackTextureRegion;

	private TextureRegion mBallTextureRegion;

	private ITextureRegion mEffectSunTextureRegion;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		getIntent().setData(Uri.parse("http://vhackandroidgame.dsi8.de/connect/?port=4254&protocol=tcp&host=192.168.178.20&password=&wifiap=Jans%20WLAN"));
		super.onCreate(pSavedInstanceState);
	}
	
	@Override
	protected void onConnected(SocketConnection connection) {
		this.presentationLogic = new PresentationLogic(RacerGameActivity.this, connection);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() {
		super.onStop();

		if (presentationLogic != null) {
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

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new FillResolutionPolicy(), this.mCamera);
	}
 
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mVehiclesTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 128, 16, TextureOptions.BILINEAR);
		this.mVehiclesTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(this.mVehiclesTexture, this,
						"vehicles.png", 0, 0, 6, 1);
		this.mVehiclesTexture.load();

		this.mBoxTexture = new BitmapTextureAtlas(this.getTextureManager(), 32,
				32, TextureOptions.BILINEAR);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mBoxTexture, this, "box.png", 0, 0);
		this.mBoxTexture.load();
		
		try {
			this.mTrackTextureRegion = loadResource(this, getTextureManager(), PixelFormat.RGBA_8888, TextureOptions.BILINEAR, "gfx/track.png");
			this.mBallTextureRegion = loadResource(this, getTextureManager(), PixelFormat.RGBA_8888, TextureOptions.BILINEAR, "gfx/ball.png");
			this.mEffectSunTextureRegion = loadResource(this, getTextureManager(), PixelFormat.RGBA_8888, TextureOptions.BILINEAR, "gfx/suneffect.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scene onCreateScene() {
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.qrCodeAtlas = new BitmapTextureAtlas(this.getTextureManager(),
				150, 750, TextureOptions.DEFAULT);

		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		this.borderBottom = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH,
				2, vertexBufferObjectManager);
		this.borderTop = new Rectangle(0, 0, CAMERA_WIDTH, 2,
				vertexBufferObjectManager);
		this.borderLeft = new Rectangle(0, 0, 2, CAMERA_HEIGHT,
				vertexBufferObjectManager);
		this.borderRight = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT,
				vertexBufferObjectManager);
		
		Sprite track = new Sprite(0, 0, this.mTrackTextureRegion, vertexBufferObjectManager);
		track.setSize(CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mScene.attachChild(track);

		
		
//		Body ballBody = PhysicsFactory.createCircleBody(this.serverLogic.getPhysicsWorld(), ball, BodyType.DynamicBody, );
		//this.serverLogic.getPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(ball, ballBody, true, true));
		
		return this.mScene;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addCar(int carId, float pX, float pY, float rotation) {
		CarView carView = new CarView();
		carView.id = carId;

		org.andengine.util.color.Color color;
		switch (carId) {
			case 0:
				color = org.andengine.util.color.Color.CYAN;
				break;
			case 1:
				color =  org.andengine.util.color.Color.RED;
				break;
			case 2:
				color =  org.andengine.util.color.Color.GREEN;
				break;
			case 3:
				color =  org.andengine.util.color.Color.YELLOW;
				break;
			default:
				color =  org.andengine.util.color.Color.TRANSPARENT;
				break;
		}
		
		carView.car = new Sprite(pX, pY, this.mBallTextureRegion, getVertexBufferObjectManager());
		carView.car.setSize(40, BALL_SIZE);
		carView.car.setColor(color); 
		this.mScene.attachChild(carView.car);

		this.cars.put(carId, carView);
	}

	@Override
	public void removeCar(int carId) {
		CarView carView = this.cars.remove(carId);
		if (carView != null) {
			// this.mPhysicsWorld.unregisterPhysicsConnector(carView.physicsConnector);
			this.mScene.detachChild(carView.car);
		}
	}

	public class CarView {
		public int id;
		public Sprite car;
	}

	@Override
	public void showQRCode(String text, QRCodePosition position) {
		// TODO: use the given position information.
		MultiFormatWriter writer = new MultiFormatWriter();
		try {
			final BitMatrix bitmatrix = writer.encode(text,
					BarcodeFormat.QR_CODE, 150, 150);

			final IBitmapTextureAtlasSource baseTextureSource = new EmptyBitmapTextureAtlasSource(
					150, 150);
			final BaseBitmapTextureAtlasSourceDecorator decoratedTextureAtlasSource = new BaseBitmapTextureAtlasSourceDecorator(
					baseTextureSource) {
				@Override
				protected void onDecorateBitmap(Canvas pCanvas)
						throws Exception {
					pCanvas.drawRGB(0, 0, 0);
					this.mPaint.setColor(Color.WHITE);
					for (int y = 0; y < bitmatrix.getHeight(); y++) {
						for (int x = 0; x < bitmatrix.getWidth(); x++) {
							if (!bitmatrix.get(x, y)) {
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
			final int bardcodeSize = CAMERA_HEIGHT - 2 * RACETRACK_WIDTH;

			final TextureRegion qrCodeAtlasRegion;

			switch (position) {
			case CENTER:
				qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromSource(this.qrCodeAtlas,
								decoratedTextureAtlasSource, 0, 0);
				this.qrCodeAtlas.load();
				this.barcodeCenter = new Sprite(CAMERA_WIDTH / 2 - bardcodeSize
						/ 2, RACETRACK_WIDTH, bardcodeSize, bardcodeSize,
						qrCodeAtlasRegion, this.getVertexBufferObjectManager());
				this.mScene.attachChild(this.barcodeCenter);
				break;
			case TOP:
				qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromSource(this.qrCodeAtlas,
								decoratedTextureAtlasSource, 0, 150);
				this.qrCodeAtlas.load();
				this.barcodeTop = new Sprite(CAMERA_WIDTH / 2 - bardcodeSize
						/ 2, 2, bardcodeSize, bardcodeSize, qrCodeAtlasRegion,
						this.getVertexBufferObjectManager());
				this.mScene.attachChild(this.barcodeTop);
				break;
			case RIGHT:
				qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromSource(this.qrCodeAtlas,
								decoratedTextureAtlasSource, 0, 300);
				this.qrCodeAtlas.load();
				this.barcodeRight = new Sprite(CAMERA_WIDTH - 2 - bardcodeSize,
						RACETRACK_WIDTH, bardcodeSize, bardcodeSize,
						qrCodeAtlasRegion, this.getVertexBufferObjectManager());
				this.mScene.attachChild(this.barcodeRight);
				break;
			case BOTTOM:
				qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromSource(this.qrCodeAtlas,
								decoratedTextureAtlasSource, 0, 450);
				this.qrCodeAtlas.load();
				this.barcodeBottom = new Sprite(CAMERA_WIDTH / 2 - bardcodeSize
						/ 2, CAMERA_HEIGHT - 2 - bardcodeSize, bardcodeSize,
						bardcodeSize, qrCodeAtlasRegion,
						this.getVertexBufferObjectManager());
				this.mScene.attachChild(this.barcodeBottom);
				break;
			case LEFT:
				qrCodeAtlasRegion = BitmapTextureAtlasTextureRegionFactory
						.createFromSource(this.qrCodeAtlas,
								decoratedTextureAtlasSource, 0, 600);
				this.qrCodeAtlas.load();
				this.barcodeLeft = new Sprite(2, RACETRACK_WIDTH, bardcodeSize,
						bardcodeSize, qrCodeAtlasRegion,
						this.getVertexBufferObjectManager());
				this.mScene.attachChild(this.barcodeLeft);
				break;
			default:
				// impossible
				break;

			}
		} catch (WriterException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void disableQRCode(QRCodePosition position) {
		switch (position) {
		case CENTER:
			this.mScene.detachChild(this.barcodeCenter);
			break;
		case TOP:
			this.mScene.detachChild(this.barcodeTop);
			break;
		case RIGHT:
			this.mScene.detachChild(this.barcodeRight);
			break;
		case BOTTOM:
			this.mScene.detachChild(this.barcodeBottom);
			break;
		case LEFT:
			this.mScene.detachChild(this.barcodeLeft);
			break;
		default:
			// impossible
			break;
		}
	}
	
	@Override
	public void updateBorders(boolean top, boolean right, boolean bottom,
			boolean left) {
		if (top) {
			this.mScene.attachChild(this.borderTop);
		} else {
			this.mScene.detachChild(this.borderTop);
		}
		if (right) {
			this.mScene.attachChild(this.borderRight);
		} else {
			this.mScene.detachChild(this.borderRight);
		}
		if (bottom) {
			this.mScene.attachChild(this.borderBottom);
		} else {
			this.mScene.detachChild(this.borderBottom);
		}
		if (left) {
			this.mScene.attachChild(this.borderLeft);
		} else {
			this.mScene.detachChild(this.borderLeft);
		}
	}


	@Override
	public void moveCar(int carId, float pX, float pY) {
		CarView carView = this.cars.get(carId);
		if (carView != null && carView.car != null) {
			carView.car.setPosition(pX, pY);
		}
	}

	@Override
	public void rotateCar(int carId, float rotation) {
		CarView carView = this.cars.get(carId);
		if (carView != null && carView.car != null) {
			carView.car.setRotation(rotation);
		}
	}

	private TextureRegion loadResource(final Context pContext,
			final TextureManager pTextureManager, final PixelFormat pFormat,
			final TextureOptions pOptions, final String pPath)
			throws IOException {
		final BitmapTexture texture = new BitmapTexture(pTextureManager,
				new IInputStreamOpener() {
					@Override
					public InputStream open() throws IOException {
						return pContext.getAssets().open(pPath);
					}
				}, BitmapTextureFormat.fromPixelFormat(pFormat), pOptions);

		texture.load();

		return TextureRegionFactory.extractFromTexture(texture);
	}

}
