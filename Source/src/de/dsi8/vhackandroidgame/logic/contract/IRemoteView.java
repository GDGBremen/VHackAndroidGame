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

import org.andengine.util.color.Color;

import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.RemoteActivity;
import de.dsi8.vhackandroidgame.communication.model.CollisionType;
import de.dsi8.vhackandroidgame.logic.impl.RemoteLogic;

/**
 * Interface from the {@link RemoteLogic} to the {@link RemoteActivity}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public interface IRemoteView {
	
	/**
	 * The connection to the Server lost.
	 * @param ex	
	 */
	void connectionLost(ConnectionProblemException ex);
	
	

	/**
	 * A collision was detected.
	 * @param collidesWith 
	 */
	void collisionDetected(CollisionType collidesWith);



	void setPlayerInfo(String name, Color color);
}
