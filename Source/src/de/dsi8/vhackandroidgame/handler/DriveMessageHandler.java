package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;

public class DriveMessageHandler extends AbstractMessageHandler<DriveMessage> {
	
	private IServerLogicListener listener;

	public DriveMessageHandler(IServerLogicListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void handleMessage(CommunicationPartner partner, DriveMessage message)
			throws InvalidMessageException {
		listener.driveCar(partner.getId(), message.valueX, message.valueY);
	}
}
