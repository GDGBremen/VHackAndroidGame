package de.dsi8.vhackandroidgame.communication;

import java.util.Collection;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import de.dsi8.vhackandroidgame.communication.model.CarMessage;
import de.dsi8.vhackandroidgame.communication.model.CarMessage.ACTION;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;
import de.dsi8.vhackandroidgame.logic.model.PresentationPartner;

public class NetworkRectangle extends Rectangle {

	private ServerLogic serverLogic; 
	
	private int id;
	
	public NetworkRectangle(int id, ServerLogic serverLogic, float pX, float pY, float pWidth, float pHeight) {
		super(pX, pY, pWidth, pHeight, (VertexBufferObjectManager) null);
		this.serverLogic = serverLogic;
		this.id = id;
	}

	
	@Override
	public void setRotation(float pRotation) {
		CarMessage message = new CarMessage();
		message.rotation = pRotation;
		message.action = ACTION.ROTATE;
		message.id = this.id;
		
		this.serverLogic.sendMessageToAllPresentationPartner(message);
		
		Log.d("NetworkRectangle", "setRotation(" + pRotation + ")");
			
	}
	
	@Override
	public void setPosition(float pX, float pY) {
		CarMessage message = new CarMessage();
		message.positionX = pX;
		message.positionY = pY;
		message.id = this.id;
		message.action = ACTION.MOVE;
		
		Log.d("NetworkRectangle", "setPosition(" + pX + ", " + pY + ")");
			
		this.serverLogic.sendMessageToAllPresentationPartner(message);
	}
}
