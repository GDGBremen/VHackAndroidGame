package de.dsi8.vhackandroidgame.handler;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage;
import de.dsi8.vhackandroidgame.logic.contract.IGamePresentationLogicListener;

public class QRCodeMessageHandler extends AbstractMessageHandler<QRCodeMessage> {

	private IGamePresentationLogicListener	listener;

	public QRCodeMessageHandler(IGamePresentationLogicListener listener) {
		this.listener = listener;
	}

	@Override
	public void handleMessage(CommunicationPartner partner, QRCodeMessage message)
			throws InvalidMessageException {
		listener.showQRCode(message.text);

	}

}
