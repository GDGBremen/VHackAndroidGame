package de.dsi8.vhackandroidgame.logic.impl;

import android.content.Context;
import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.connection.impl.TCPProtocol;
import de.dsi8.dsi8acl.connection.impl.WiFiChecker;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

/**
 * Configuration-parameter of the App.
 *  
 * @author Sven Nobis <sven.nobis@gmail.com>
 *
 */
public class VHackAndroidGameConfiguration {
	
	private final static String DEFAULT_PASSWORD = "";
	private final static String URL_BASE = "http://vhackandroidgame.dsi8.de/connect/";
	private final static int PORT = 4254;
	
	private final TCPProtocol protocol;
	private final Context context;
	private final WiFiChecker wifiChecker;
	
	public VHackAndroidGameConfiguration(Context context) {
		this.context = context;
		this.wifiChecker = new WiFiChecker(context);
		protocol = new TCPProtocol(URL_BASE, PORT);
		registerProtocols();
	}
	
	public final TCPProtocol getProtocol() {
		return protocol;
	}
	
	public final WiFiChecker getWiFiChecker() {
		return wifiChecker;
	}
	
	public ConnectionParameter getConnectionDetails() {
		ConnectionParameter parameter = protocol.getConnectionDetails(DEFAULT_PASSWORD);
		wifiChecker.addSSIDToConnectionDetails(parameter);
		return parameter;
	}
	
	private void registerProtocols() {
		SocketConnection.registerProtocol(protocol);
	}
}
