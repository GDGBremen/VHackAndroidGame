package de.dsi8.vhackandroidgame.logic.impl;

import de.dsi8.dsi8acl.connection.model.AbstractCommunicationConfiguration;

/**
 * Configuration-parameter of the App.
 *  
 * @author Sven Nobis <sven.nobis@gmail.com>
 *
 */
public class VHackAndroidGameConfiguration extends AbstractCommunicationConfiguration {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getURLBase() {
		return "http://vhackandroidgame.dsi8.de/connect/";
	}
	
}
