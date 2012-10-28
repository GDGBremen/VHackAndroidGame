/*******************************************************************************
 * Copyright (C) 2012 Henrik Voß, Sven Nobis and Nicolas Gramlich (AndEngine)
 * 
 * This file is part of VHackAndroidGame
 * (https://github.com/SvenTo/VHackAndroidGame)
 * 
 * VHackAndroidGame is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package de.dsi8.vhackandroidgame.logic.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andlabs.andengine.extension.physicsloader.PhysicsEditorLoader;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.IServerCommunication;
import de.dsi8.dsi8acl.communication.contract.IServerCommunicationListener;
import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.communication.impl.ServerCommunication;
import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.dsi8acl.connection.model.Message;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.RacerGameActivity;
import de.dsi8.vhackandroidgame.communication.NetworkRectangle;
import de.dsi8.vhackandroidgame.communication.model.CarMessage;
import de.dsi8.vhackandroidgame.communication.model.CarMessage.ACTION;
import de.dsi8.vhackandroidgame.communication.model.CollisionMessage;
import de.dsi8.vhackandroidgame.communication.model.CollisionType;
import de.dsi8.vhackandroidgame.communication.model.GameModeMessage;
import de.dsi8.vhackandroidgame.communication.model.PlayerInfoMessage;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage;
import de.dsi8.vhackandroidgame.communication.model.QRCodeMessage.QRCodePosition;
import de.dsi8.vhackandroidgame.handler.DriveMessageHandler;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogic;
import de.dsi8.vhackandroidgame.logic.contract.IServerLogicListener;
import de.dsi8.vhackandroidgame.logic.model.PresentationPartner;
import de.dsi8.vhackandroidgame.logic.model.RemotePartner;

/**
 * The logic on the {@link RacerGameActivity}.
 * 
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class ServerLogic implements IServerLogic, IServerCommunicationListener, ContactListener {

	/**
	 * Log-Tag.
	 */
	private static final String LOG_TAG = ServerLogic.class.getSimpleName();
	
	/**
	 * Interface to the {@link RacerGameActivity}.
	 */
	private final IServerLogicListener listener;
	
	/**
	 * Interface to the server communication.
	 */
	private final IServerCommunication communication;
	
	private final VHackAndroidGameConfiguration gameConfig;
	private final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

	private PhysicsWorld mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
	
	/**
	 * All connected remote partner.
	 */
	private Map<Integer, RemotePartner> remotePartner = new HashMap<Integer, RemotePartner>();
	
	/**
	 * Number of remote partner.
	 */
	private int numRemotePartner = 0;
	
	/**
	 * 
	 */
	private Map<Integer, PresentationPartner> presentationPartner = new HashMap<Integer, PresentationPartner>();
	
	/**
	 * Number of presentation partner.
	 */
	private int numPresentationPartner = 0;
	
	private boolean qrCodeVisible = false;
	
	/**
	 * Creates the logic.
	 * @param listener	Interface to the {@link RacerGameActivity}.	
	 */
	public ServerLogic(Context context, VHackAndroidGameConfiguration gameConfig, IServerLogicListener listener) {
		this.listener = listener;
		this.gameConfig = gameConfig;
		IConnector connector = gameConfig.getProtocol().createConnector();
		this.communication = new ServerCommunication(this, connector, 20);
		
		
		final PhysicsEditorLoader loader = new PhysicsEditorLoader();
		try {
			loader.load(context, this.mPhysicsWorld, "track.xml", (IAreaShape) null, false, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new UpdateThread().start();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		this.communication.startListen();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		this.communication.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newPartner(ICommunicationPartner partner) {
		Log.i(LOG_TAG, "newPartner");
		partner.registerMessageHandler(new AbstractMessageHandler<GameModeMessage>() {
			@Override
			public void handleMessage(CommunicationPartner partner, GameModeMessage message) throws InvalidMessageException {
				if (message.remote) {
					newRemotePartner(partner);
				} else {
					newPresentationPartner(partner);
				}
			}
		});
		
		//this.listener.addCar(partner.getId());
		// TODO send addCar to the GamePresentationLogic 
	}
	
	/**
	 * A new remote partner is connecting.
	 * @param partner	the new remote partner
	 */
	private void newRemotePartner(CommunicationPartner partner) {
		RemotePartner rPartner = new RemotePartner();
		partner.registerMessageHandler(new DriveMessageHandler(this));
		rPartner.communicationPartner = partner;
		rPartner.id = this.numRemotePartner++;
		
		final float PX = 20;
		final float PY = 20;
		final float ROTATION = 0;
		
		// TODO make network-magic to the rectangle
		Rectangle rectangle = new NetworkRectangle(rPartner.id, this, PX, PY, RacerGameActivity.CAR_SIZE, RacerGameActivity.CAR_SIZE);
		
		rPartner.body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, rectangle, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(1, 0.1f, 0.5f));
		
		rPartner.physicsConnector = new PhysicsConnector(rectangle, rPartner.body, true, true);
		this.mPhysicsWorld.registerPhysicsConnector(rPartner.physicsConnector);
		
		this.remotePartner.put(rPartner.id, rPartner);
		
		CarMessage message = new CarMessage();
		message.positionX = PX;
		message.positionY = PY;
		message.rotation = ROTATION;
		message.action = ACTION.ADD;
		message.id = rPartner.id;
		sendMessageToAllPresentationPartner(message);
		
		rPartner.communicationPartner.sendMessage(new PlayerInfoMessage("Player " + rPartner.id, rPartner.id));
		
		this.listener.registerPlayer(rPartner.id);
	}
	
	/**
	 * A new presentation partner is connecting.
	 * @param partner	the new presentation partner
	 */
	private void newPresentationPartner(CommunicationPartner partner) {
		PresentationPartner pPartner = new PresentationPartner();
		pPartner.communicationPartner = partner;
		pPartner.id = this.numPresentationPartner++;
		
		this.presentationPartner.put(pPartner.id, pPartner);
		
		this.numPresentationPartner++;
		
		showBardcode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectionLost(ICommunicationPartner partner, ConnectionProblemException ex) {
		Log.i(LOG_TAG, "connectionLost", ex);
		int idOfRemotePartner = getIdOfRemotePartner(partner);
		RemotePartner rPartner = this.remotePartner.get(idOfRemotePartner);
		
		if (rPartner != null) {
			this.mPhysicsWorld.unregisterPhysicsConnector(rPartner.physicsConnector);
			CarMessage carMessage = new CarMessage();
			carMessage.action = ACTION.REMOVE;
			carMessage.id = rPartner.id;
			sendMessageToAllPresentationPartner(carMessage);
			
			rPartner.body = null;
			rPartner.physicsConnector = null;
			rPartner.communicationPartner.close();
			rPartner.communicationPartner = null;
			this.remotePartner.remove(rPartner);
			this.listener.removePlayer(rPartner.id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void socketListenerProblem(Exception ex) {
		Log.e(LOG_TAG, "socketListenerProblem", ex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collisionDetected(int carId, CollisionType collidesWith) {
		this.communication.sendMessage(carId, new CollisionMessage(collidesWith));
	}
	
	/**
	 * Return's the id of an remote partner.
	 * @param partner	the remote partner is to be returned to the id
	 * @return			id of the remote partner
	 */
	private int getIdOfRemotePartner(ICommunicationPartner partner) {
		Iterator<Entry<Integer, RemotePartner>> iterator = this.remotePartner.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Integer, RemotePartner> next = iterator.next();
			if (partner == next.getValue().communicationPartner) {
				return next.getKey().intValue();
			}
		}
		return -1;
	}
	

	@Override
	public void onCreateScene() {
		this.mPhysicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);

		this.mPhysicsWorld.setContactListener(this);
	}
	
	@Override
	public void beginContact(Contact contact) {
		int firstCarId = getCarIdFromBody(contact.getFixtureA().getBody());
		int secondCarId = getCarIdFromBody(contact.getFixtureB().getBody());
		
		if (firstCarId > -1) {
			collisionDetected(firstCarId, getCollisionType(contact.getFixtureB()));
		}
		if (secondCarId > -1) {
			collisionDetected(secondCarId, getCollisionType(contact.getFixtureA()));
		}
	}
	
	public CollisionType getCollisionType(Fixture fixture) {
		int carId = getCarIdFromBody(fixture.getBody());
		if(carId > -1) {
			return CollisionType.CAR;
		} else if(fixture.getRestitution() > 0) {
			return CollisionType.BUMPER;
		} else {
			return CollisionType.WALL;
		}
	}
		

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) { /* Not required */
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) { /* Not required */
	}

	@Override
	public void endContact(Contact arg0) { /* Not required */
	}
	
	private int getCarIdFromBody(Body body) {
		for (RemotePartner rPartner : this.remotePartner.values()) {
			if (rPartner.body == body) {
				return rPartner.id;
			}
		}
		
		return -1;
	}
	
	public Body getCarBody(CommunicationPartner partner) {
		for (RemotePartner rPartner : this.remotePartner.values()) {
			if (rPartner.communicationPartner == partner) {
				return rPartner.body;
			}
		}
		
		return null;
	}

	@Override
	public PhysicsWorld getPhysicsWorld() {
		return this.mPhysicsWorld;
	}
	
	public void sendMessageToAllPresentationPartner(Message message) {
		for (PresentationPartner p : this.presentationPartner.values()) {
			p.communicationPartner.sendMessage(message);
		}
	}
	
	public class UpdateThread extends Thread {
		 @Override
		public void run() {
			 long lastUpdate = System.currentTimeMillis();
			while (true) {
				final long now = System.currentTimeMillis();
				final float timeElapsed = (now - lastUpdate) / 1000f;
				ServerLogic.this.mPhysicsWorld.onUpdate(timeElapsed);
				lastUpdate = now;
				try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			}
		}
	}
	
	public void checkpointsPassed(RemotePartner remotePartner) {
		listener.incrementCheckpointsPassed(remotePartner.id);
	}

	@Override
	public void showBardcode() {
		CommunicationPartner partner = this.presentationPartner.get(0).communicationPartner;
		if(qrCodeVisible) {
			partner.sendMessage(new QRCodeMessage(null, QRCodePosition.CENTER));
		} else {
			ConnectionParameter connectionDetails = gameConfig.getConnectionDetails();
			partner.sendMessage(new QRCodeMessage(connectionDetails.toConnectionURL(), QRCodePosition.CENTER));
		}
		qrCodeVisible = !qrCodeVisible;
		
	}
}
