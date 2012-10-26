package de.dsi8.vhackandroidgame.communication;

import java.util.Collection;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import de.dsi8.vhackandroidgame.communication.model.CarMessage;
import de.dsi8.vhackandroidgame.communication.model.CarMessage.ACTION;
import de.dsi8.vhackandroidgame.logic.model.PresentationPartner;

public class NetworkRectangle extends Rectangle {

	private Collection<PresentationPartner> presentation;
	
	private int id;
	
	public NetworkRectangle(int id, Collection<PresentationPartner> presentation, float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, (VertexBufferObjectManager) null);
		this.presentation = presentation;
		this.id = id;
	}

	
	@Override
	public void setRotation(float pRotation) {
		for (PresentationPartner p : this.presentation) {
			CarMessage message = new CarMessage();
			message.rotation = pRotation;
			message.action = ACTION.ROTATE;
			message.id = this.id;
			p.communicationPartner.sendMessage(message);
		}
	}
	
	@Override
	public void setPosition(float pX, float pY) {
		for (PresentationPartner p : this.presentation) {
			CarMessage message = new CarMessage();
			message.positionX = pX;
			message.positionY = pY;
			message.id = this.id;
			message.action = ACTION.MOVE;
			p.communicationPartner.sendMessage(message);
		}
	}
}
