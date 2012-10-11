package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.communication.contract.ICar;
import de.dsi8.vhackandroidgame.communication.contract.IDrive;

public interface IGamePresentationLogicListener extends IDrive, ICar {

	
	void addObstacle(final float pX, final float pY);
	

	/**
	 * Moves the car to a new Position.
	 * valueX and valueY define a unit vector of the direction and speed. 
	 * 
	 * @param carId		Id of the car to be moved
	 * @param valueX 	x-value of the unit vector.	
	 * @param valueY 	Y-value of the unit vector.	
	 */
	void driveCar(int carId, float valueX, float valueY);
	
}
