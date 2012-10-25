package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class QRCodeMessage extends Message {

	public String	text;

	public QRCodeMessage(@JsonProperty("text") String text) {
		this.text = text;
	}
}
