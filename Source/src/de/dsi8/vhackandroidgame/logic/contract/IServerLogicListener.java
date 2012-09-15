package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.communication.model.DriveMessage;

public interface IServerLogicListener {
	/***
	 * @param valueX See {@link DriveMessage}
	 * @param valueY See {@link DriveMessage}
	 */
	void driveCar(int carId, float valueX, float valueY);
	
	void addCar(int carId);
	
	void removeCar(int carId);
}
