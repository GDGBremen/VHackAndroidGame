package de.dsi8.vhackandroidgame.logic.impl;

import de.dsi8.dsi8acl.connection.model.AbstractCommunicationConfiguration;

public class VHackAndroidGameConfiguration extends AbstractCommunicationConfiguration {

	@Override
	public String getURLBase() {
		return "http://vhackandroidgame.dsi8.de/connect/";
	}
	
}
