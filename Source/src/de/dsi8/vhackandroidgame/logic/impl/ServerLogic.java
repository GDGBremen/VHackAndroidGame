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

import android.util.Log;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.IServerCommunication;
import de.dsi8.dsi8acl.communication.contract.IServerCommunicationListener;
import de.dsi8.dsi8acl.communication.impl.ServerCommunication;
import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.connection.impl.TCPSocketConnector;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.RacerGameActivity;
import de.dsi8.vhackandroidgame.communication.model.CollisionMessage;
import de.dsi8.vhackandroidgame.handler.DriveMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;

/**
 * The logic on the {@link RacerGameActivity}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class ServerLogic implements IServerLogic, IServerCommunicationListener {

	/**
	 * Log-Tag.
	 */
	private static final String LOG_TAG = ServerLogic.class.getSimpleName();
	
	/**
	 * Interface to the {@link RacerGameActivity}.
	 */
	private final IServerLogicListener listener;
	
	/**
	 * Interface to the server communication.
	 */
	private final IServerCommunication communication;
	
	/**
	 * Creates the logic.
	 * @param listener	Interface to the {@link RacerGameActivity}.	
	 */
	public ServerLogic(IServerLogicListener listener) {
		this.listener = listener;
		IConnector connector = VHackAndroidGameConfiguration.getProtocol().createConnector();
		this.communication = new ServerCommunication(this, connector, 20);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		this.communication.startListen();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		this.communication.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newPartner(ICommunicationPartner partner) {
		Log.i(LOG_TAG, "newPartner");
		partner.registerMessageHandler(new DriveMessageHandler(this.listener));
		this.listener.addCar(partner.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectionLost(ICommunicationPartner partner,
			ConnectionProblemException ex) {
		Log.i(LOG_TAG, "connectionLost", ex);
		this.listener.removeCar(partner.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void socketListenerProblem(Exception ex) {
		Log.e(LOG_TAG, "socketListenerProblem", ex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collisionDetected(int carId) {
		this.communication.sendMessage(carId, new CollisionMessage());
	}

}
