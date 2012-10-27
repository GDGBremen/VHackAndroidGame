package de.dsi8.vhackandroidgame.server.list;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.dsi8.vhackandroidgame.R;
import de.dsi8.vhackandroidgame.server.model.Player;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tmesserschmidt
 * @author zero
 */

public class ScoreboardAdapater extends BaseAdapter {
	private List<Player>	players;
	private LayoutInflater	inflater;
	private Context			context;
	private Player			dirty;
    private static final long ROTATE_DURATION =400 ; //in milliseconds

    /**
	 * @param context
	 */
	public ScoreboardAdapater(Context context) {
		this.players = new ArrayList<Player>();
		this.context = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.players.size();
	}

	@Override
	public Player getItem(int position) {
		return (this.players.size() > position) ? this.players.get(position)
				: null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Returns the {@link Player}'s position.
	 * 
	 * @param player
	 * @return
	 */
	public int getPosition(Player player) {
		return this.players.indexOf(player);
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
		public View		container;
		public View		border;
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
				holder.border = convertView.findViewById(R.id.adapter_border);
				holder.container = convertView
						.findViewById(R.id.adapter_container);
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
			holder.container
					.setBackgroundColor(getColorForPlayer(player, false));
			holder.border.setBackgroundColor(getColorForPlayer(player, true));
            rotateAnimation(player,holder.container);
            checkTextAnimations(player,holder);

		}

		return convertView;
	}

	/**
	 * @param player
	 * @param border
	 * @return returns a color for the background of the view
	 */
	private int getColorForPlayer(final Player player, boolean border) {
		if (!border || (this.dirty != null && player != this.dirty)
				|| this.dirty == null) {
			switch (player.getId()) {
				case 0:
					return Color.CYAN;
				case 1:
					return Color.RED;
				case 2:
					return Color.GREEN;
				case 3:
					return Color.YELLOW;
				default:
					return Color.TRANSPARENT;
			}
		} else if (border && this.dirty != null && player == this.dirty) {
			return Color.MAGENTA;
		} else {
			return Color.TRANSPARENT;
		}
	}

    public void rotateAnimation(final Player player,final View view){
        if(player==this.dirty){
//            view.animate().rotationY(360f).setDuration(ROTATE_DURATION)
//                    .withEndAction(new Runnable() {
//                        @Override
//                        public void run() {
//                            view.setRotationY(0);
//                        }
//                    });
        }
    }

    public void setTextSizeAnimation(final Player player, final TextView textView,final long delay) {
//        if(player==this.dirty){
//            final float size=textView.getTextSize();
//           textView.animate().scaleX(2).scaleY(2).withEndAction(new Runnable() {
//               @Override
//               public void run() {
//                   textView.setScaleX(1);
//                   textView.setScaleY(1);
//               }
//           }).setStartDelay(delay);
//        }
    }

    public void checkTextAnimations(final Player player,final  ViewHolder holder){
        if(player!=this.dirty){
            return;
        }
        setTextSizeAnimation(player,holder.checkpoints,ROTATE_DURATION+20);

        if(player.hasFinishedNewRound()){
            setTextSizeAnimation(player,holder.rounds,ROTATE_DURATION+40);
        }
    }

	/**
	 * This method gets triggered whenever a player passes a checkpoint. It
	 * makes sure that the list is always refreshed.
	 * 
	 * @param player
	 */
	public void sortPlayers(Player player) {
		this.dirty = player;
		Collections.sort(this.players);
		this.notifyDataSetChanged();
	}
}
