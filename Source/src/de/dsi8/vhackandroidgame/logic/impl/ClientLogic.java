package de.dsi8.vhackandroidgame.logic.impl;

import java.net.Socket;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogic;
import de.dsi8.vhackandroidgame.logic.contract.IClientLogicListener;

public class ClientLogic implements IClientLogic, ICommunicationPartnerListener {

	private final ICommunicationPartner partner;
	private final IClientLogicListener listener;
	
	public ClientLogic(IClientLogicListener listener, Socket socket) {
		this.listener = listener;
		this.partner = new CommunicationPartner(this, socket);
		this.partner.initialized();
	}
	
	@Override
	public void connectionLost(CommunicationPartner partner,
			ConnectionProblemException ex) {
		listener.connectionLost(ex);
	}

	@Override
	public void driveCar(float valueX, float valueY) {
		partner.sendMessage(new DriveMessage(valueX, valueY));
	}

	@Override
	public void close() {
		partner.close();
	}
	
}
