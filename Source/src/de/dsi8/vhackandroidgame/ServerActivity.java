package de.dsi8.vhackandroidgame;

import android.app.Activity;
import android.os.Bundle;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;

public class ServerActivity extends Activity implements IServerLogicListener {

	private IServerLogic serverLogic;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		this.serverLogic = new ServerLogic(this);
		this.serverLogic.start();
	}

}
