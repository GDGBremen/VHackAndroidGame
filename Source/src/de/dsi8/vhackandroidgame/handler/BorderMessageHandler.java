package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.BorderMessage;
import de.dsi8.vhackandroidgame.logic.impl.PresentationLogic;

public class BorderMessageHandler extends AbstractMessageHandler<BorderMessage> {

	private PresentationLogic presentationLogic;

	public BorderMessageHandler(PresentationLogic presentationLogic) {
		this.presentationLogic = presentationLogic;
	}

	@Override
	public void handleMessage(CommunicationPartner partner, BorderMessage message)
			throws InvalidMessageException {
		presentationLogic.getPresentationView().updateBorders(message.top,
				message.right, message.bottom, message.left);
	}

}
