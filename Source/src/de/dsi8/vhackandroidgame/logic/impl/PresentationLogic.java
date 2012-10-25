package de.dsi8.vhackandroidgame.logic.impl;

import java.io.IOException;
import java.net.Socket;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.connection.impl.TCPConnection;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.communication.contract.IServerToPresentation;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage;
import de.dsi8.vhackandroidgame.handler.CarMessageHandler;
import de.dsi8.vhackandroidgame.handler.QRCodeMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogic;
import de.dsi8.vhackandroidgame.logic.contract.IPresentationLogicListener;

public class PresentationLogic implements IPresentationLogic, ICommunicationPartnerListener, IServerToPresentation {

	private IPresentationLogicListener listener;

	/**
	 * Connection to the server.
	 */
	private final ICommunicationPartner serverPartner;
	
	public PresentationLogic(IPresentationLogicListener listener, IRemoteConnection connection) {
		this.listener = listener;
		
		this.serverPartner = new CommunicationPartner(this, connection);
		this.serverPartner.registerMessageHandler(new CarMessageHandler(this));
		this.serverPartner.registerMessageHandler(new QRCodeMessageHandler(this));
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
		this.listener.addCar(carId);
	}


	@Override
	public void removeCar(int carId) {
		this.listener.removeCar(carId);
	}


	@Override
	public void showQRCode(String str) {
		this.listener.showQRCode(str);
	}
}
