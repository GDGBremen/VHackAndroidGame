package de.dsi8.vhackandroidgame.logic.impl;

import android.util.Log;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.IServerCommunication;
import de.dsi8.dsi8acl.communication.contract.IServerCommunicationListener;
import de.dsi8.dsi8acl.communication.impl.ServerCommunication;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.vhackandroidgame.handler.DriveMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;

public class ServerLogic implements IServerLogic, IServerCommunicationListener {

	private static final String LOG_TAG = "ServerLogic";
	private final IServerLogicListener listener;
	private final IServerCommunication communication;
	
	public ServerLogic(IServerLogicListener listener) {
		this.listener = listener;
		communication = new ServerCommunication(this, 4);
	}
	
	public void start() {
		communication.startListen();
	}
	
	public void close() {
		communication.close();
	}
	
	@Override
	public void newPartner(ICommunicationPartner partner) {
		Log.i(LOG_TAG, "New Partner!");
		partner.registerMessageHandler(new DriveMessageHandler(listener));
		listener.addCar(partner.getId());
	}

	@Override
	public void connectionLost(ICommunicationPartner partner,
			ConnectionProblemException ex) {
		Log.i(LOG_TAG, "connectionLost", ex);
		listener.removeCar(partner.getId());
	}

	@Override
	public void socketListenerProblem(Exception ex) {
		// TODO Auto-generated method stub
		Log.e(LOG_TAG, "socketListenerProblem", ex);
	}

}
