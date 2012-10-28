package de.dsi8.vhackandroidgame.server.model;

import android.widget.Adapter;
import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;

public class Player implements Comparable<Player> {
	private static ScoreboardAdapater	adapter;
	private int							id;
	private int							checkpointsPassed	= 0;
	private int							roundsFinished		= 0;
    private boolean                     finishedNewRound=false;

    /**
	 * @param id
	 */
	public Player(int id) {
		this.id = id;
	}

	public static void setAdapter(final Adapter adapter) {
		Player.adapter = (ScoreboardAdapater) adapter;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the checkpointsPassed
	 */
	public int getCheckpointsPassed() {
		return this.checkpointsPassed;
	}

    public boolean hasFinishedNewRound() {
        return finishedNewRound;
    }

    /**
	 * increments the amount of passed checkoints
	 */
	public void incrementCheckpointsPassed() {
		this.checkpointsPassed++;
		if ((this.checkpointsPassed % 3) == 0) {

			this.roundsFinished++;
            this.finishedNewRound=true;
		}
        this.finishedNewRound=false;
		adapter.sortPlayers(this);
	}

	/**
	 * @return the roundsFinished
	 */
	public int getRoundsFinished() {
		return this.roundsFinished;
	}

	@Override
	public int compareTo(Player another) {
		if (this.checkpointsPassed > another.checkpointsPassed) {
			return -1;
		} else if (this.checkpointsPassed < another.checkpointsPassed) {
			return 1;
		}
		return 0;
	}
}
