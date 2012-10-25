package de.dsi8.vhackandroidgame.logic.impl;

import java.io.IOException;
import java.net.Socket;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.impl.TCPConnection;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.contract.IPresentationServerListener;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.handler.CarMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogic;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogicListener;

public class PresentationLogic implements IPresentationLogic, ICommunicationPartnerListener, IPresentationServerListener {

	private IPresentationLogicListener listener;

	/**
	 * Connection to the server.
	 */
	private final ICommunicationPartner serverPartner;
	
	public PresentationLogic(IPresentationLogicListener listener, Socket socket) {
		this.listener = listener;
		
		this.serverPartner = new CommunicationPartner(this, new TCPConnection(socket));
		this.serverPartner.registerMessageHandler(new CarMessageHandler(this));
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


	@Override
	public void addCar(int carId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeCar(int carId) {
		// TODO Auto-generated method stub
		
	}
}
