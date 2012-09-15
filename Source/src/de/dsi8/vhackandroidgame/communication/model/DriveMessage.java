package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;


public class DriveMessage extends Message {
	
	public DriveMessage(@JsonProperty("valueX") float valueX,
						@JsonProperty("valueY") float valueY) {
		this.valueX = valueX;
		this.valueY = valueY;
	}
	
	/**
	 * A floating-point number of the On-Screen-Control.
	 */
	public final float valueX;
	
	/**
	 * A floating-point number of the On-Screen-Control.
	 */
	public final float valueY;
}
