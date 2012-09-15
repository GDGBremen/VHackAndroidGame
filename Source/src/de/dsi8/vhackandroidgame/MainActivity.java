package de.dsi8.vhackandroidgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void clickStartServer(View button) {
    	Intent intent = new Intent(this, RacerGameActivity.class);
    	startActivity(intent);
    }
    
    public void clickStartClient(View button) {
    	Intent intent = new Intent(this, RemoteActivity.class);
    	startActivity(intent);
    }
}
