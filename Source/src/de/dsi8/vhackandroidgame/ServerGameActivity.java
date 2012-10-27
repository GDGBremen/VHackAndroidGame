package de.dsi8.vhackandroidgame;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;
import de.dsi8.vhackandroidgame.logic.impl.VHackAndroidGameConfiguration;
import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;

public class ServerGameActivity extends ListActivity implements IServerLogicListener {
	private ListView			listView;
	private ScoreboardAdapater	adapter;


	private IServerLogic serverLogic;
	
	private VHackAndroidGameConfiguration gameConfig;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scoreboard);
		this.listView = getListView();
		this.adapter = new ScoreboardAdapater(this);
		this.listView.setAdapter(adapter);
		
		this.gameConfig = new VHackAndroidGameConfiguration(this);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		
		this.serverLogic = new ServerLogic(this.gameConfig, this);
		this.serverLogic.start();
	}
}
