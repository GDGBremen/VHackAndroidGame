package de.dsi8.vhackandroidgame.server.model;

import de.dsi8.vhackandroidgame.server.list.ScoreboardAdapater;

public class Player implements Comparable<Player> {
	private int					id;
	private int					checkpointsPassed	= 0;
	private int					roundsFinished		= 0;
	private ScoreboardAdapater	adpater;

	/**
	 * @param id
	 * @param checkpointsPassed
	 * @param roundsFinished
	 * @param adapter 
	 */
	public Player(int id, int checkpointsPassed, int roundsFinished,
			ScoreboardAdapater adapter) {
		this.id = id;
		this.checkpointsPassed = checkpointsPassed;
		this.roundsFinished = roundsFinished;
		this.adpater = adapter;
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

	/**
	 * increments the amount of passed checkoints
	 */
	public void incrementCheckpointsPassed() {
		this.checkpointsPassed++;
		if ((this.checkpointsPassed % 4) == 0) {
			this.roundsFinished++;
		}
		this.adpater.sortPlayers();
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
			return 1;
		} else if (this.checkpointsPassed < another.checkpointsPassed) {
			return -1;
		}
		return 0;
	}
}
