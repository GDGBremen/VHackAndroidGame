package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class GameModeMessage extends Message {
	
	public GameModeMessage(
			@JsonProperty("remote") boolean remote) {
		this.remote = remote;
	}

	public final boolean remote;
}
