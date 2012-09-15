package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.CollisionMessage;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogicListener;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;

public class CollisionMessageHandler extends AbstractMessageHandler<CollisionMessage> {
	
	private IClientLogicListener listener;

	public CollisionMessageHandler(IClientLogicListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void handleMessage(CommunicationPartner partner, CollisionMessage message)
			throws InvalidMessageException {
		listener.collisionDetected();
	}
}
