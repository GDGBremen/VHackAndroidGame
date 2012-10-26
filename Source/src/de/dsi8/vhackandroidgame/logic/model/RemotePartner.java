package de.dsi8.vhackandroidgame.logic.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;

public class RemotePartner {

	public int id;
	
	public CommunicationPartner communicationPartner;
	
	public Body body;
}
