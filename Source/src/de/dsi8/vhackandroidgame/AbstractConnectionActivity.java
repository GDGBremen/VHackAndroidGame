package de.dsi8.vhackandroidgame;

import java.net.MalformedURLException;

import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.vhackandroidgame.logic.impl.VHackAndroidGameConfiguration;

public abstract class AbstractConnectionActivity extends SimpleBaseGameActivity {
	
	private static final String LOG_TAG = AbstractConnectionActivity.class.getSimpleName();
	
	protected ConnectionParameter connectionParameter;
	private ConnectTask connectTask;
	protected VHackAndroidGameConfiguration gameConfig;
	
	private AlertDialog sameNetworkDialog;
	
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		this.gameConfig = new VHackAndroidGameConfiguration(this);
		this.handler = new Handler();
		
		try {
			this.connectionParameter = new ConnectionParameter(getIntent().getData().toString());
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Invalid Connection Parameter", e);
			finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(LOG_TAG, "onStart");
		
		handler.postDelayed(connectRunnable, 2000);
	}
	
	private Runnable connectRunnable = new Runnable() {
		@Override
		public void run() {
			// Connect
			if(gameConfig.getWiFiChecker().inSameNetwork(connectionParameter)) {
				connect();
			} else {
				if(sameNetworkDialog == null) {
					sameNetworkDialog = buildSameNetworkAlertDialog();
					sameNetworkDialog.show();
				}
			}
		}
	};
	
	private AlertDialog buildSameNetworkAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.same_network_dialog_title);
		builder.setMessage(R.string.same_network_dialog_msg);
		// Add the buttons
		builder.setPositiveButton(android.R.string.yes, reconnectDialogClickListener);
		builder.setNegativeButton(android.R.string.no, reconnectDialogClickListener);
		
		return builder.create();
	}
	
	DialogInterface.OnClickListener reconnectDialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	    	sameNetworkDialog = null;
	    	
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	            connect();
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	            finish();
	            break;
	        }
	    }
	};

	private void showConnectionFailedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.connection_falied_dialog_title);
		builder.setMessage(R.string.connection_falied_dialog_msg);
		// Add the buttons
		builder.setPositiveButton(R.string.retry, reconnectDialogClickListener);
		builder.setNegativeButton(android.R.string.cancel, reconnectDialogClickListener);
		
		builder.create().show();
	}
		
	private void connect() {
		connectTask = new ConnectTask();
		connectTask.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(LOG_TAG, "onStop");
		
		handler.removeCallbacks(connectRunnable);
		if(connectTask != null) {
			connectTask.interrupt();
		}
	}
	
	/**
	 * The Task that should connect the client with the host.
	 */
	private class ConnectTask extends Thread  {
		/**
		 * Connecting to the Host.
		 */
		@Override
		public void run() {
			try {
				final SocketConnection connection = SocketConnection.connect(connectionParameter);
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						onConnected(connection);
					}
				});
			} catch (Exception e) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						showConnectionFailedDialog();
					}
				});
				Log.i(LOG_TAG, "ConnectionTask", e);
			}
		}
	}
	
	protected abstract void onConnected(SocketConnection connection);
}
