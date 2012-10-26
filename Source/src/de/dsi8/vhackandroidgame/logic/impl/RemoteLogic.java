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

import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import com.badlogic.gdx.math.Vector2;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.impl.TCPConnection;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.RemoteActivity;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.handler.CollisionMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IRemoteLogic;
import de.dsi8.vhackandroidgame.logic.contract.IRemoteView;

/**
 * Logic of the remote smartphone.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class RemoteLogic implements IRemoteLogic, ICommunicationPartnerListener {

	/**
	 * Connection to the server.
	 */
	private final ICommunicationPartner serverPartner;

	
	/**
	 * Listener to the {@link RemoteActivity}.
	 */
	private final IRemoteView remoteView;
	
	
	/**
	 * Creates the client-logic.
	 * @param remoteView		Listenerlistener on the {@link RemoteActivity}.
	 * @param socket		Socket to the Server.
	 */
	public RemoteLogic(IRemoteView remoteView, Socket socket) {
		this.remoteView = remoteView;
		this.serverPartner = new CommunicationPartner(this, new TCPConnection(socket));
		this.serverPartner.registerMessageHandler(new CollisionMessageHandler(this));
		this.serverPartner.initialized();
		this.serverPartner.sendMessage(new GameModeMessage(true));
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectionLost(CommunicationPartner partner, ConnectionProblemException ex) {
		this.remoteView.connectionLost(ex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void driveCar(float valueX, float valueY) {
		this.serverPartner.sendMessage(new DriveMessage(valueX, valueY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		this.serverPartner.close();
	}

	public IRemoteView getRemoteView() {
		return this.remoteView;
	}

	
}
