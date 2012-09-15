package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.dsi8acl.exception.ConnectionProblemException;

public interface IClientLogicListener {
	void connectionLost(ConnectionProblemException ex);
}
