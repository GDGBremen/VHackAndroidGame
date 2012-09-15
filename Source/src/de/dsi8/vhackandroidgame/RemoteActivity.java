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
import de.dsi8.vhackandroidgame.logic.contract.IClientLogic;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ClientLogic;

public class RemoteActivity extends SimpleBaseGameActivity implements IClientLogicListener {

	private IClientLogic clientLogic;
	
	private static final int CAMERA_WIDTH = 320;
	
	private static final int CAMERA_HEIGHT = 192;
	
	private BitmapTextureAtlas mOnScreenControlTexture;

	
	private static final String LOG_TAG = "RemoteActivity";
	
	

	private Camera mCamera;
	private ITextureRegion mOnScreenControlBaseTextureRegion;
	
	private ITextureRegion mOnScreenControlKnobTextureRegion;
	
	private Scene mScene;
	
	private ConnectionParameter connectionParameter;
	private ConnectTask connectTask;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		
		connectionParameter = new ConnectionParameter("http://vhackandroidgame.dsi8.de/connect/?host=192.168.43.245&port=4254&password=");//getIntent().getData().toString());
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
		if(clientLogic != null) {
			clientLogic.close();
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
						clientLogic = new ClientLogic(RemoteActivity.this, result.getResult());
					} else {
						Log.e(LOG_TAG, "IOException", result.getError());
						finish();
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
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		this.mOnScreenControlTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_base.png", 0, 0);
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mOnScreenControlTexture, this, "onscreen_control_knob.png", 128, 0);
		this.mOnScreenControlTexture.load();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Scene onCreateScene() {
		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));

		this.initOnScreenControls();

		return this.mScene;
	}
	
	/**
	 * Initialize the On-Screen-Controls.
	 */
	private void initOnScreenControls() {
		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(CAMERA_WIDTH - this.mOnScreenControlBaseTextureRegion.getWidth(), CAMERA_HEIGHT - this.mOnScreenControlBaseTextureRegion.getHeight(), this.mCamera, this.mOnScreenControlBaseTextureRegion, this.mOnScreenControlKnobTextureRegion, 0.1f, this.getVertexBufferObjectManager(), new IAnalogOnScreenControlListener() {
			@Override
			public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY) {
				// TODO Zum Server funken
				
				if(clientLogic != null) {
					RemoteActivity.this.clientLogic.driveCar(pValueX, pValueY);
				}
			}

			@Override
			public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl) {/* Nothing. */}
		});
		analogOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.refreshControlKnobPosition();

		this.mScene.setChildScene(analogOnScreenControl);
	}

	@Override
	public void connectionLost(ConnectionProblemException ex) {
		Log.e(LOG_TAG, "connectionLost", ex);
	}
}
