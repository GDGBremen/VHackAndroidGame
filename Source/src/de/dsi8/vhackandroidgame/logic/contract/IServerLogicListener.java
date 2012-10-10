/*******************************************************************************
 * Copyright (C) 2012 Henrik Voß, Sven Nobis and Nicolas Gramlich (AndEngine)
 * 
 * This file is part of VHackAndroidGame
 * (https://github.com/SvenTo/VHackAndroidGame)
 * 
 * VHackAndroidGame is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.RacerGameActivity;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;

/**
 * Interface from the {@link ServerLogic} to the {@link RacerGameActivity}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public interface IServerLogicListener {
	
	/**
	 * Moves the car to a new Position.
	 * valueX and valueY define a unit vector of the direction and speed. 
	 * 
	 * @param carId		Id of the car to be moved
	 * @param valueX 	x-value of the unit vector.	
	 * @param valueY 	Y-value of the unit vector.	
	 */
	void driveCar(int carId, float valueX, float valueY);
	
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
