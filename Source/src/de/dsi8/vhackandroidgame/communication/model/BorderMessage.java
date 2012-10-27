package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class BorderMessage extends Message {
	public final boolean top;
	public final boolean right;
	public final boolean bottom;
	public final boolean left;
	
	public BorderMessage(@JsonProperty("top") boolean top,
			@JsonProperty("right") boolean right,
			@JsonProperty("bottom") boolean bottom,
			@JsonProperty("left") boolean left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
}