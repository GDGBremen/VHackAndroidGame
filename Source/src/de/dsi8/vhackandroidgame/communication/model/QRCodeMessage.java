package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class QRCodeMessage extends Message {

	public final String	text;
	
	public final QRCodePosition position;

	public QRCodeMessage(@JsonProperty("text") String text,
			@JsonProperty("position") QRCodePosition position) {
		this.text = text;
		this.position = position;
	}
	
	public static enum QRCodePosition {
		TOP,
		
		RIGHT,
		
		BOTTOM,
		
		LEFT,
		
		CENTER
	}
}
