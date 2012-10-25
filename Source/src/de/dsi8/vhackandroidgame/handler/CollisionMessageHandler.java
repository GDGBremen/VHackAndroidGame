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
import de.dsi8.vhackandroidgame.communication.model.CollisionMessage;
import de.dsi8.vhackandroidgame.logic.impl.RemoteLogic;

/**
 * Handles the {@link CollisionMessage}.
 *
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class CollisionMessageHandler extends AbstractMessageHandler<CollisionMessage> {
	
	/**
	 * Interface to the {@link RemoteLogic}.
	 */
	private RemoteLogic remoteLogic;

	/**
	 * Creates the MessageHandler
	 * @param remoteLogic		Interface to the {@link RemoteLogic}.
	 */
	public CollisionMessageHandler(RemoteLogic remoteLogic) {
		this.remoteLogic = remoteLogic;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(CommunicationPartner partner, CollisionMessage message) throws InvalidMessageException {
		this.remoteLogic.getRemoteView().collisionDetected();
	}
}
