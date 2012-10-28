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

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.immersion.uhl.Launcher;

import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.model.CollisionType;
import de.dsi8.vhackandroidgame.logic.contract.IRemoteLogic;
import de.dsi8.vhackandroidgame.logic.contract.IRemoteView;
import de.dsi8.vhackandroidgame.logic.impl.RemoteLogic;

public class RemoteActivity extends AbstractConnectionActivity implements
		IRemoteView, SensorEventListener {

	private IRemoteLogic		clientLogic;

	private static final int	CAMERA_WIDTH	= 320;

	private static final int	CAMERA_HEIGHT	= 192;

	private BitmapTextureAtlas	mOnScreenControlTexture;

	private Launcher			mHapticLauncher;

	private static final String	LOG_TAG			= RemoteActivity.class
														.getSimpleName();

	/*
	 * Wait until this before sending the next
	 */
	private long				nextSendl;

	private Camera				mCamera;
	private ITextureRegion		mOnScreenControlBaseTextureRegion;

	private ITextureRegion		mOnScreenControlKnobTextureRegion;

	private Scene				mScene;
	private Vibrator			mVibrator;

	private SensorManager		sensorManager;

	private BitmapTextureAtlas	mFontTexture;

	private Font				mFont;

	private boolean				enableControls;

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

		sensorManager = (SensorManager) this
				.getSystemService(this.SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				sensorManager.SENSOR_DELAY_GAME);

		try {
			mHapticLauncher = new Launcher(this);
		} catch (RuntimeException ex) {
			Log.v(LOG_TAG, "No Haptic supported", ex);
		}
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	protected void onPause() {
		super.onPause();

		try {
			if (mHapticLauncher != null) {
				mHapticLauncher.stop();
			}
		} catch (RuntimeException ex) {
			Log.v(LOG_TAG, "No Haptic supported", ex);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (clientLogic != null) {
			try {
				clientLogic.close();
			} catch (IOException e) {
				Log.w(LOG_TAG, "Can not close the connection the server.", e);
			}
		}
	}

	@Override
	protected void onConnected(SocketConnection connection) {
		this.clientLogic = new RemoteLogic(RemoteActivity.this, connection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.mCamera);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mFont = FontFactory.create(this.getFontManager(),
				this.getTextureManager(), 256, 256,
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(
				this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Scene onCreateScene() {
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		return this.mScene;
	}

	@Override
	public void connectionLost(ConnectionProblemException ex) {
		Log.e(LOG_TAG, "connectionLost", ex);
		// TODO: Show Dialog!
		finish();
	}

	@Override
	public void collisionDetected(CollisionType collidesWith) {
		try {
			if (mHapticLauncher != null) {
				switch (collidesWith) {
					case CAR:
						mHapticLauncher.play(Launcher.IMPACT_METAL_100);
						break;
					case BUMPER:
						mHapticLauncher.play(Launcher.IMPACT_RUBBER_100);
						break;
					case WALL:
						mHapticLauncher.play(Launcher.EXPLOSION4);
						break;
				}
			} else {
				mVibrator.vibrate(200);
			}
		} catch (RuntimeException ex) {
			Log.v(LOG_TAG, "No Haptic supported", ex);
			mVibrator.vibrate(200);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
				case Sensor.TYPE_ACCELEROMETER:
					int accellerometerSpeedX = (int) event.values[1];
					int accellerometerSpeedY = (int) event.values[0];
					onAccelerometerChanged(accellerometerSpeedX,
							accellerometerSpeedY);
					/*
					 * if (enableControls) {
					 * this.clientLogic.driveCar(accellerometerSpeedX,
					 * accellerometerSpeedY); }
					 */
					break;
			}
		}
	}

	private static final boolean	ADAPTIVE_ACCEL_FILTER	= true;
	float							lastAccel[]				= new float[2];
	float							accelFilter[]			= new float[2];

	public void onAccelerometerChanged(float accelX, float accelY) {
		// high pass filter
		float updateFreq = 30; // match this to your update speed
		float cutOffFreq = 0.9f;
		float RC = 1.0f / cutOffFreq;
		float dt = 1.0f / updateFreq;
		float filterConstant = RC / (dt + RC);
		float alpha = filterConstant;
		float kAccelerometerMinStep = 0.033f;
		float kAccelerometerNoiseAttenuation = 3.0f;

		if (ADAPTIVE_ACCEL_FILTER) {
			float d = (float) clamp(
					Math.abs(norm(accelFilter[0], accelFilter[1])
							- norm(accelX, accelY))
							/ kAccelerometerMinStep - 1.0f, 0.0f, 1.0f);
			alpha = d * filterConstant / kAccelerometerNoiseAttenuation
					+ (1.0f - d) * filterConstant;
		}

		accelFilter[0] = (float) (alpha * (accelFilter[0] + accelX - lastAccel[0]));
		accelFilter[1] = (float) (alpha * (accelFilter[1] + accelY - lastAccel[1]));

		lastAccel[0] = accelX;
		lastAccel[1] = accelY;

		if (enableControls) {
			this.clientLogic.driveCar(accelFilter[0], accelFilter[1]);
		}
	}

	private double norm(float x, float y) {
		return Math.sqrt(x * x + y * y);
	}

	private double clamp(double v, double min, double max) {
		if (v > max) {
			return max;
		} else if (v < min) {
			return min;
		} else {
			return v;
		}
	}

	@Override
	public void setPlayerInfo(String name, Color color) {
		enableControls = true;
		mScene.setColor(color);
		mScene.setBackground(new Background(color));
		final VertexBufferObjectManager vertexBufferObjectManager = this
				.getVertexBufferObjectManager();
		final Text centerText = new Text(0, 0, this.mFont, name,
				new TextOptions(HorizontalAlign.CENTER),
				vertexBufferObjectManager);

		mScene.attachChild(centerText);
	}

}
