package de.dsi8.vhackandroidgame.communication.contract;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.vhackandroidgame.logic.impl.RemoteLogic;

/**
 * This interface listens on messages from the {@link RemoteLogic}.
 * It will be uses in the ServerLogic.
 *
 * @author henrik
 *
 */
public interface IRemoteToServer {
	
	/**
	 * Moves the car to a new Position.
	 * valueX and valueY define a unit vector of the direction and speed. 
	 * 
	 * @param carId		Id of the car to be moved
	 * @param valueX 	x-value of the unit vector.	
	 * @param valueY 	Y-value of the unit vector.	
	 */
	void driveCar(CommunicationPartner partners, float valueX, float valueY);
}
