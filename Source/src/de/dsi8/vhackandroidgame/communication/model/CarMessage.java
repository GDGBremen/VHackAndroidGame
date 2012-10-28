package de.dsi8.vhackandroidgame.communication.model;

import org.andengine.util.color.Color;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.dsi8.dsi8acl.connection.model.Message;

public class CarMessage extends Message {

	public static enum ACTION {ADD, REMOVE, MOVE, ROTATE}
	
	public static enum COLOR {RED, BLUE, GREEN, YELLOW}
	
	public int id;

	public ACTION action;
	
	public float positionX;
	
	public float positionY;
	
	public float rotation;
	
	public COLOR color;
}
