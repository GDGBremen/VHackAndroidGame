package de.dsi8.vhackandroidgame;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.widget.ListView;
import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;
import de.dsi8.vhackandroidgame.server.model.Player;

import java.util.ArrayList;
import java.util.Random;


public class ServerGameActivity extends ListActivity {
	private ListView			listView;
	private ScoreboardAdapater	adapter;
    private ArrayList<Player> mPlayers;

    Handler mHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int i= new Random().nextInt(4);
            mPlayers.get(i).incrementCheckpointsPassed();
            mHandler.sendEmptyMessageDelayed(1,3000);
            return true;
        }
    });

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scoreboard);
		this.listView = getListView();
		this.adapter = new ScoreboardAdapater(this);
		this.listView.setAdapter(adapter);

        final ActionBar actionbar=getActionBar();
        actionbar.setDisplayUseLogoEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setTitle(R.string.scoreboard_title);
        if(BuildConfig.DEBUG){
            createFakePlayers();
        }
	}

    private void createFakePlayers() {
        mPlayers=new ArrayList<Player>();
        ScoreboardAdapater adapter =new ScoreboardAdapater(this);
        for(int i=0;i<4;i++){
            Player p=new Player(i);
            mPlayers.add(p);
            adapter.addPlayer(p);
        }

        Player.setAdapter(adapter);
        this.listView.setAdapter(adapter);

       mHandler.sendEmptyMessageDelayed(1,3000);
    }
}
