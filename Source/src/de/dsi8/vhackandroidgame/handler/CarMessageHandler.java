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
package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.contract.ICar;
import de.dsi8.vhackandroidgame.communication.contract.IDrive;
import de.dsi8.vhackandroidgame.communication.model.CarMessage;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.logic.contract.IGameCoordinatorLogicListener;
import de.dsi8.vhackandroidgame.logic.impl.GameCoordinatorLogic;

/**
 * Handles the {@link DriveMessage}.
 *
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class CarMessageHandler extends AbstractMessageHandler<CarMessage> {

	/**
	 * Interface to the {@link GameCoordinatorLogic}.
	 */
	private ICar listener;

	/**
	 * Creates the handler.
	 * @param listener	Interface to the {@link GameCoordinatorLogic}.	
	 */
	public CarMessageHandler(ICar listener) {
		this.listener = listener;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(CommunicationPartner partner, CarMessage message) throws InvalidMessageException {
		if (message.add) {
			this.listener.addCar(message.id);
		} else {
			this.listener.removeCar(message.id);
		}
	}
}
