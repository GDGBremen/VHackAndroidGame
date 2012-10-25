package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class CarMessage extends Message {

	public CarMessage(@JsonProperty("id") int id,
			@JsonProperty("add") boolean add) {
		this.id = id;
		this.add = add;
	}

	public final int id;

	public final boolean add;
}
