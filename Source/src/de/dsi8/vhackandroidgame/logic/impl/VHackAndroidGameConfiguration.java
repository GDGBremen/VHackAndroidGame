package de.dsi8.vhackandroidgame.logic.impl;

import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.connection.impl.TCPProtocol;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

/**
 * Configuration-parameter of the App.
 *  
 * @author Sven Nobis <sven.nobis@gmail.com>
 *
 */
public class VHackAndroidGameConfiguration {

	private final static TCPProtocol protocol;
	
	public final static String DEFAULT_PASSWORD = "";
	public final static String URL_BASE = "http://vhackandroidgame.dsi8.de/connect/";
	public final static int PORT = 4254;
	public final static String KEY_X_COORDINATE = "x";
	public final static String KEY_Y_COORDINATE = "y";
	
	static {
		protocol = new TCPProtocol(URL_BASE, PORT);
	}
	
	public static final TCPProtocol getProtocol() {
		return protocol;
	}
	
	// TODO: Add some custom parameters 
	public static ConnectionParameter getConnectionDetailsForRemote() {
		return protocol.getConnectionDetails(DEFAULT_PASSWORD);
	}
	
	public static ConnectionParameter getConnectionDetailsForPresentation(int x, int y) {
		ConnectionParameter connectionDetails = protocol.getConnectionDetails(DEFAULT_PASSWORD);
		
		connectionDetails.setParameter(KEY_X_COORDINATE, Integer.toString(x));
		connectionDetails.setParameter(KEY_Y_COORDINATE, Integer.toString(y));
		
		return connectionDetails;
	}
	
	public static void registerProtocols() {
		SocketConnection.registerProtocol(protocol);
	}
}
