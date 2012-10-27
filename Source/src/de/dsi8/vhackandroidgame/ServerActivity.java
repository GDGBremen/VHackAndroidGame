package de.dsi8.vhackandroidgame;

import android.app.Activity;
import android.os.Bundle;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;
import de.dsi8.vhackandroidgame.logic.impl.VHackAndroidGameConfiguration;

public class ServerActivity extends Activity implements IServerLogicListener {

	private IServerLogic serverLogic;
	
	private VHackAndroidGameConfiguration gameConfig;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.gameConfig = new VHackAndroidGameConfiguration(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		
		this.serverLogic = new ServerLogic(this.gameConfig, this);
		this.serverLogic.start();
	}

}
