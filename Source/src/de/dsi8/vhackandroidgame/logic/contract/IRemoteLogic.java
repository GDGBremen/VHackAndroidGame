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

import java.io.Closeable;

import de.dsi8.vhackandroidgame.RemoteActivity;
import de.dsi8.vhackandroidgame.logic.impl.RemoteLogic;


/**
 * Interface from the {@link RemoteActivity} to the {@link RemoteLogic}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public interface IRemoteLogic extends Closeable {
	
	/**
	 * Move the car to a new position.
	 * valueX and valueY define a unit vector of the direction and speed. 
	 * 
	 * @param valueX 	x-value of the unit vector.	
	 * @param valueY 	Y-value of the unit vector.	
	 */
	void driveCar(float valueX, float valueY);
}
