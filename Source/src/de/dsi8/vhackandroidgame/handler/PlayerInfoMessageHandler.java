package de.dsi8.vhackandroidgame.handler;

import org.andengine.util.color.Color;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.model.Message;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.PlayerInfoMessage;
import de.dsi8.vhackandroidgame.logic.contract.IRemoteView;

public class PlayerInfoMessageHandler extends AbstractMessageHandler<PlayerInfoMessage> {

	private final IRemoteView view;

	public PlayerInfoMessageHandler(IRemoteView view) {
		this.view = view;
	}
	
	@Override
	public void handleMessage(CommunicationPartner partner, PlayerInfoMessage message)
			throws InvalidMessageException {
		this.view.setPlayerInfo(message.name, getColorById(message.playerId));
	}

	
	private Color getColorById(int id) {
		switch (id) {
		case 0:
			return Color.CYAN;
		case 1:
			return Color.RED;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.YELLOW;
		default:
			return Color.TRANSPARENT;
		}
	}
}
