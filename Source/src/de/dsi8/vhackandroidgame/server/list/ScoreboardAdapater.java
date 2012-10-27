package de.dsi8.vhackandroidgame.server.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.dsi8.vhackandroidgame.R;
import de.dsi8.vhackandroidgame.server.model.Player;

public class ScoreboardAdapater extends BaseAdapter {
	private List<Player>	players;
	private LayoutInflater	inflater;
	private Context			context;

	public ScoreboardAdapater(Context context) {
		this.players = new ArrayList<Player>();
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return players.size();
	}

	@Override
	public Player getItem(int position) {
		return (players.size() > position) ? players.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @param player
	 */
	public void addPlayer(Player player) {
		if (!this.players.contains(player)) {
			this.players.add(player);
			this.notifyDataSetChanged();
		}
	}

	/**
	 * @param player
	 */
	public void removePlayer(Player player) {
		if (this.players.contains(player)) {
			this.players.remove(player);
			this.notifyDataSetChanged();
		}
	}

	/**
	 * Helper class to improve the ListView's performance
	 * 
	 * @author tmesserschmidt
	 * 
	 */
	private class ViewHolder {
		public TextView	id;
		public TextView	checkpoints;
		public TextView	rounds;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		final Player player = getItem(position);

		if (player != null) {
			if (convertView == null) {
				convertView = this.inflater.inflate(
						R.layout.adapter_scoreboard, parent, false);
				holder = new ViewHolder();
				holder.id = (TextView) convertView
						.findViewById(R.id.adapter_player_id);
				holder.checkpoints = (TextView) convertView
						.findViewById(R.id.adapter_player_checkpoints);
				holder.rounds = (TextView) convertView
						.findViewById(R.id.adapter_player_rounds);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.id.setText(this.context.getString(
					R.string.scoreboard_player_id, player.getId()));
			holder.checkpoints.setText(this.context.getString(
					R.string.scoreboard_player_checkpoints,
					player.getCheckpointsPassed()));
			holder.rounds.setText(this.context.getString(
					R.string.scoreboard_player_rounds,
					player.getRoundsFinished()));
		}

		return convertView;
	}

	/**
	 * This method gets triggered whenever a player passes a checkpoint. It
	 * makes sure that the list is always refreshed.
	 */
	public void sortPlayers() {
		Collections.sort(players);
		this.notifyDataSetChanged();
	}
}
