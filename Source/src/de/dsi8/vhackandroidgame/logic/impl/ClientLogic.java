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
package de.dsi8.vhackandroidgame.logic.impl;

import java.net.Socket;

import org.andengine.engine.handler.collision.CollisionHandler;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.handler.CollisionMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogic;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogicListener;

public class ClientLogic implements IClientLogic, ICommunicationPartnerListener {

	private final ICommunicationPartner partner;
	private final IClientLogicListener listener;
	
	public ClientLogic(IClientLogicListener listener, Socket socket) {
		this.listener = listener;
		this.partner = new CommunicationPartner(this, socket);
		this.partner.registerMessageHandler(new CollisionMessageHandler(listener));
		this.partner.initialized();
	}
	
	@Override
	public void connectionLost(CommunicationPartner partner,
			ConnectionProblemException ex) {
		listener.connectionLost(ex);
	}

	@Override
	public void driveCar(float valueX, float valueY) {
		partner.sendMessage(new DriveMessage(valueX, valueY));
	}

	@Override
	public void close() {
		partner.close();
	}
	
}
