package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.contract.IServerToPresentation;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage;

public class QRCodeMessageHandler extends AbstractMessageHandler<QRCodeMessage> {

	private IServerToPresentation	listener;

	public QRCodeMessageHandler(IServerToPresentation listener) {
		this.listener = listener;
	}

	@Override
	public void handleMessage(CommunicationPartner partner, QRCodeMessage message)
			throws InvalidMessageException {
		listener.showQRCode(message.text);

	}

}
