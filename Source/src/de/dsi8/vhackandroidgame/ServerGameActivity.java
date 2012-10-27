package de.dsi8.vhackandroidgame;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;

public class ServerGameActivity extends ListActivity {
	private ListView			listView;
	private ScoreboardAdapater	adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scoreboard);
		this.listView = getListView();
		this.adapter = new ScoreboardAdapater(this);
		this.listView.setAdapter(adapter);
	}
}
