package de.dsi8.vhackandroidgame.logic.contract;

public interface IServerLogic {
	void start();
	void close();
	void collisionDetected(int carId);
}
