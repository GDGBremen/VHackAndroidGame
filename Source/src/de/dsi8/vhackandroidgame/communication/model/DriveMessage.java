package de.dsi8.vhackandroidgame.communication.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;


public class DriveMessage extends Message {
	
	public DriveMessage(@JsonProperty("rotation") float rotation,
						@JsonProperty("speed") float speed) {
		this.rotation = rotation;
		this.speed = speed;
	}
	
	/**
	 * A floating-point number to adjust speed in the range:
	 * -1.0f to 1.0f.
	 */
	public final float speed;
	
	/**
	 * Rotation of the "steering wheel":
	 * -1.0f: maximum to the left
	 * 0: no rotation (drive forward)
	 * +1.0f: maximum to the right
	 */
	public final float rotation;
}
