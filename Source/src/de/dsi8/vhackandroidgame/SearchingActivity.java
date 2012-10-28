package de.dsi8.vhackandroidgame;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.stalhut.networkfinderlibrary.UDPClient;
import de.stalhut.networkfinderlibrary.UDPMessage;

public class SearchingActivity extends Activity {

	private OwnUDPClient client = new OwnUDPClient();

	private TreeMap<String, Long> serverList = new TreeMap<String, Long>();
	private ArrayAdapter<String> serverListAdapter;

	private final static int TIMEOUT = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		final ListView listView = (ListView) findViewById(R.id.resultListView);
		this.serverListAdapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1);
		listView.setAdapter(serverListAdapter);

		if (Build.VERSION.SDK_INT >= 11) {
			this.client.executeOnExecutor(new DirectExecutor());
		} else {
			this.client.execute();
		}
	}
	
	@Override
	protected void onStop() {
		this.client.cancel(true);
		super.onStop();
	}

	private void addServerToList(final String ipaddress) {
		if (serverList.containsKey(ipaddress)) {
			this.serverList.put(ipaddress, System.currentTimeMillis());
		} else {
			this.serverList.put(ipaddress, System.currentTimeMillis());
			this.serverListAdapter.add(ipaddress);
			this.serverListAdapter.notifyDataSetChanged();
		}
	}

	public void updateServerList() {
		TreeMap<String, Long> serverListNeu = new TreeMap<String, Long>();
		Set<Map.Entry<String, Long>> entrySet = serverList.entrySet();
		for (Map.Entry<String, Long> paar : entrySet) {
			if (paar.getValue() > System.currentTimeMillis() - TIMEOUT) {
				serverListNeu.put(paar.getKey(), paar.getValue());
			} else {
				this.serverListAdapter.remove(paar.getKey());
			}
		}
		this.serverList = serverListNeu;
		this.serverListAdapter.notifyDataSetChanged();
	}

	private void connect(final String ipAddress) {
		this.client.cancel(true);
		Intent i = new Intent(this, RacerGameActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setData(Uri
				.parse("http://vhackandroidgame.dsi8.de/connect/?port=4254&protocol=tcp&host="
						+ ipAddress + "&password="));
		startActivity(i);
	}

	private class OwnUDPClient extends UDPClient {

		@Override
		protected void onProgressUpdate(UDPMessage... values) {
			if (values.length > 0) {
				addServerToList(values[0].sender.getHostAddress());
				connect(values[0].sender.getHostAddress());
			}
			updateServerList();
		}
	}

	private class DirectExecutor implements Executor {
		@Override
		public void execute(Runnable r) {
			new Thread(r).start();
		}
	}
}
