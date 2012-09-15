package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.communication.model.DriveMessage;

public interface IServerLogicListener {
	/***
	 * @param speed See {@link DriveMessage}
	 * @param rotation See {@link DriveMessage}
	 */
	void driveCar(int carId, float speed, float rotation);
	
	void addCar(int carId);
	
	void removeCar(int carId);
}
