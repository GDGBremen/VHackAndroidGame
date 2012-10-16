package de.dsi8.vhackandroidgame.communication.contract;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;

public interface IDrive {
	/**
	 * Moves the car to a new Position.
	 * valueX and valueY define a unit vector of the direction and speed. 
	 * 
	 * @param remotePartner		the remote partner
	 * @param valueX 	x-value of the unit vector.	
	 * @param valueY 	Y-value of the unit vector.	
	 */
	void driveCar(CommunicationPartner remotePartner, float valueX, float valueY);
}
