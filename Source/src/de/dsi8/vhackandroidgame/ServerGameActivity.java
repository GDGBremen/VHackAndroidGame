package de.dsi8.vhackandroidgame;

import java.util.ArrayList;
import java.util.Random;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;
import de.dsi8.vhackandroidgame.logic.impl.VHackAndroidGameConfiguration;
import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;
import de.dsi8.vhackandroidgame.server.model.Player;

public class ServerGameActivity extends ListActivity implements
		IServerLogicListener {
	private ListView						listView;
	private ScoreboardAdapater				adapter;
	private ArrayList<Player>				mPlayers;

	Handler									mHandler	= new Handler(
																new Handler.Callback() {
																	@Override
																	public boolean handleMessage(
																			Message message) {
																		int i = new Random()
																				.nextInt(4);
																		mPlayers.get(
																				i)
																				.incrementCheckpointsPassed();
																		mHandler.sendEmptyMessageDelayed(
																				1,
																				3000);
																		return true;
																	}
																});

	private IServerLogic					serverLogic;

	private VHackAndroidGameConfiguration	gameConfig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scoreboard);
		this.listView = getListView();
		this.adapter = new ScoreboardAdapater(this);
		this.listView.setAdapter(adapter);
		this.gameConfig = new VHackAndroidGameConfiguration(this);

		if (BuildConfig.DEBUG) {
			createFakePlayers();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		this.serverLogic = new ServerLogic(this.gameConfig, this);
		this.serverLogic.start();
	}

	private void createFakePlayers() {
		mPlayers = new ArrayList<Player>();
		ScoreboardAdapater adapter = new ScoreboardAdapater(this);
		for (int i = 0; i < 4; i++) {
			Player p = new Player(i);
			mPlayers.add(p);
			adapter.addPlayer(p);
		}

		Player.setAdapter(adapter);
		this.listView.setAdapter(adapter);

		mHandler.sendEmptyMessageDelayed(1, 3000);
	}
}
