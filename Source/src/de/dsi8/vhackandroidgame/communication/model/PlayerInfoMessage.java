package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class PlayerInfoMessage extends Message {
	
	public PlayerInfoMessage(
			@JsonProperty("name") String name,
			@JsonProperty("playerId") int playerId) {
		this.name = name;
		this.playerId = playerId;		
	}
	
	public final String name;
	public final int playerId;
}
