package de.dsi8.vhackandroidgame;

import java.net.Socket;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import de.dsi8.dsi8acl.common.utils.AsyncTaskResult;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ClientLogic;

public class RemoteActivity extends SimpleBaseGameActivity implements IClientLogicListener {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final int RACETRACK_WIDTH = 64;

	private static final int OBSTACLE_SIZE = 16;
	private static final int CAR_SIZE = 16;

	private static final int CAMERA_WIDTH = RACETRACK_WIDTH * 5;
	private static final int CAMERA_HEIGHT = RACETRACK_WIDTH * 3;
	
	private BitmapTextureAtlas mOnScreenControlTexture;
	
	private static final String LOG_TAG = "RemoteActivity";
	
	

	private Camera mCamera;

	private ITextureRegion mOnScreenControlBaseTextureRegion;
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	

	private Scene mScene;
	
	private ConnectionParameter connectionParameter;
	private ConnectTask connectTask;
	public ClientLogic logic;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		
		connectionParameter = new ConnectionParameter(getIntent().getData().toString());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		connectTask = new ConnectTask();
		connectTask.execute((Object)null);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		connectTask.cancel(true);
		if(logic != null) {
			logic.close();
		}
	}
	
	/**
	 * The Task that should connect the client with the host.
	 */
	private class ConnectTask extends AsyncTask<Object, Object, AsyncTaskResult<Socket>>  {
				/**
				 * Connecting to the Host.
				 */
				@Override
				protected AsyncTaskResult<Socket> doInBackground(
						Object... params) {
					try {
						Socket socket = new Socket(connectionParameter.host, connectionParameter.port);
						return new AsyncTaskResult<Socket>(socket);
					} catch (Exception e) {
						return new AsyncTaskResult<Socket>(e);
					}
				}
				
				/**
				 * Connection is open, initialize the logic.
				 */
				@Override
				protected void onPostExecute(AsyncTaskResult<Socket> result) {
					if(result.getError() == null) {
						logic = new ClientLogic(RemoteActivity.this, result.getResult());
					} else {
						Log.e(LOG_TAG, "IOException", result.getError());
					}
				}
	}
	
    @Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}
    
    
	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
		
	}

	@Override
	protected Scene onCreateScene() {


		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.initOnScreenControls();

		return this.mScene;
	}
	
	private void initOnScreenControls() {
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(0, CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				// TODO Zum Server funken
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {
				/* Nothing. */
			}
		});
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		//		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		//		analogOnScreenControl.getControlBase().setScale(0.75f);
		//		analogOnScreenControl.getControlKnob().setScale(0.75f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.mScene.setChildScene(analogOnScreenControl);
	}

	@Override
	public void connectionLost(ConnectionProblemException ex) {
		Log.e(LOG_TAG, "connectionLost", ex);
	}
}
