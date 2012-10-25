package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage.QRCodePosition;


public interface IPresentationView {

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
	

	void showQRCode(String str, QRCodePosition position);

	void updateBorders(boolean top, boolean right, boolean bottom, boolean left);
}
