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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.IServerCommunication;
import de.dsi8.dsi8acl.communication.contract.IServerCommunicationListener;
import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.communication.impl.ServerCommunication;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.connection.impl.TCPSocketConnector;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.RacerGameActivity;
import de.dsi8.vhackandroidgame.communication.model.CarMessage;
import de.dsi8.vhackandroidgame.communication.model.CollisionMessage;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage;
import de.dsi8.vhackandroidgame.handler.DriveMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IGameCoordinatorLogic;
import de.dsi8.vhackandroidgame.logic.contract.IGameCoordinatorLogicListener;

/**
 * The logic on the {@link RacerGameActivity}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class GameCoordinatorLogic implements IGameCoordinatorLogic, IServerCommunicationListener {

	/**
	 * Log-Tag.
	 */
	private static final String LOG_TAG = GameCoordinatorLogic.class.getSimpleName();
	
	/**
	 * Interface to the {@link RacerGameActivity}.
	 */
	private final IGameCoordinatorLogicListener listener;
	
	/**
	 * Interface to the server communication.
	 */
	private final IServerCommunication communication;
	
	
	private Map<Integer, CommunicationPartner> remotePartner = new HashMap<Integer, CommunicationPartner>();
	
	private int numRemotePartner = 0;
	
	
	private Map<Integer, CommunicationPartner> presentationPartner = new HashMap<Integer, CommunicationPartner>();
	
	private int numPresentationPartner = 0;
	
	
	
	/**
	 * Creates the logic.
	 * @param listener	Interface to the {@link RacerGameActivity}.	
	 */
	public GameCoordinatorLogic(IGameCoordinatorLogicListener listener, IRemoteConnection presentationConnection) {
		this.listener = listener;
		
		ConnectionParameter.setStaticCommunicationConfiguration(new VHackAndroidGameConfiguration());
		int port = ConnectionParameter.getDefaultConnectionDetails().port;
		this.communication = new ServerCommunication(this, new TCPSocketConnector(port), 20);
		
		// XXX Listener set to null
		CommunicationPartner partner = new CommunicationPartner(null, presentationConnection);
		partner.registerMessageHandler(new AbstractMessageHandler<GameModeMessage>() {
			@Override
			public void handleMessage(CommunicationPartner partner, GameModeMessage message) throws InvalidMessageException {

			}
		});
		newPresentationPartner(partner);
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
		partner.registerMessageHandler(new AbstractMessageHandler<GameModeMessage>() {
			@Override
			public void handleMessage(CommunicationPartner partner, GameModeMessage message) throws InvalidMessageException {
				if (message.remote) {
					newRemotePartner(partner);
				} else {
					newPresentationPartner(partner);
				}
			}
		});
		
		//this.listener.addCar(partner.getId());
		// TODO send addCar to the GamePresentationLogic 
	}
	
	private void newRemotePartner(CommunicationPartner partner) {
		this.remotePartner.put(numRemotePartner, partner);
		
		for (CommunicationPartner p : this.presentationPartner.values()) {
			p.sendMessage(new CarMessage(numRemotePartner, true));
		}
		numRemotePartner++;
	}
	
	private void newPresentationPartner(CommunicationPartner partner) {
		this.presentationPartner.put(numPresentationPartner, partner);
		

		
		numPresentationPartner++;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectionLost(ICommunicationPartner partner,
			ConnectionProblemException ex) {
		Log.i(LOG_TAG, "connectionLost", ex);
		//this.listener.removeCar(partner.getId());
		// TODO send removeCar to the GamePresentationLogic 
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

	@Override
	public void test() {
		CommunicationPartner partner = this.presentationPartner.get(0);
		partner.sendMessage(new CarMessage(1, true));
		partner.sendMessage(new QRCodeMessage("hallo"));
	}

}
