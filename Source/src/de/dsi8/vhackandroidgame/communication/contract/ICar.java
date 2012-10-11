package de.dsi8.vhackandroidgame.communication.contract;

public interface ICar {

	
	/**
	 * Adds a new car with the id <code>carId</code> to the Map.
	 *   
	 * @param carId		Id of the car.
	 */
	void addCar(int carId);
	
	/**
	 * Removes the car with the Id <code>carId</code> from the Map.
	 * 
	 * @param carId		Id of the car
	 */
	void removeCar(int carId);

	
	
}
