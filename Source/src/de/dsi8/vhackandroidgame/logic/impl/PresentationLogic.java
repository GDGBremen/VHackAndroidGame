package de.dsi8.vhackandroidgame.logic.impl;

import java.io.IOException;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.handler.BorderMessageHandler;
import de.dsi8.vhackandroidgame.handler.CarMessageHandler;
import de.dsi8.vhackandroidgame.handler.QRCodeMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogic;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationView;

public class PresentationLogic implements IPresentationLogic, ICommunicationPartnerListener {

	private IPresentationView presentationView;

	/**
	 * Connection to the server.
	 */
	private final ICommunicationPartner serverPartner;
	
	public PresentationLogic(IPresentationView presentationView, IRemoteConnection connection) {
		this.presentationView = presentationView;
		
		this.serverPartner = new CommunicationPartner(this, connection);
		this.serverPartner.registerMessageHandler(new CarMessageHandler(this));
		this.serverPartner.registerMessageHandler(new QRCodeMessageHandler(this));
		this.serverPartner.registerMessageHandler(new BorderMessageHandler(this));
		this.serverPartner.initialized();
		this.serverPartner.sendMessage(new GameModeMessage(false));
	}
	
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connectionLost(CommunicationPartner partner,
			ConnectionProblemException ex) {
		// TODO Auto-generated method stub
		
	}


	public IPresentationView getPresentationView() {
		return presentationView;
	}
}
