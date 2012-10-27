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
package de.dsi8.vhackandroidgame.handler;

import org.andengine.extension.physics.box2d.util.Vector2Pool;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.vhackandroidgame.communication.model.DriveMessage;
import de.dsi8.vhackandroidgame.logic.impl.ServerLogic;

/**
 * Handles the {@link DriveMessage}.
 *
 * @author Henrik Voß <hennevoss@gmail.com>
 *
 */
public class DriveMessageHandler extends AbstractMessageHandler<DriveMessage> {

	/**
	 * Interface to the {@link ServerLogic}.
	 */
	private ServerLogic serverLogic;

	/**
	 * Creates the handler.
	 * @param serverLogic	Interface to the {@link ServerLogic}.	
	 */
	public DriveMessageHandler(ServerLogic serverLogic) {
		this.serverLogic = serverLogic;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleMessage(CommunicationPartner partner, DriveMessage message) throws InvalidMessageException {
		//TODO driveCar
		Body body = this.serverLogic.getCarBody(partner);
		
		Log.d("DriveMessageHandler", "new DriveMessage " + message.valueX + "   "  + message.valueY);
		
		final Vector2 velocity = Vector2Pool.obtain(message.valueX * 5, message.valueY * 5);
		
		body.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);

		final float rotationInRad = (float)Math.atan2(-message.valueX, message.valueY);
		body.setTransform(body.getWorldCenter(), rotationInRad);

//		carView.car.setRotation(MathUtils.radToDeg(rotationInRad)); // TODO Move to Presentation Handler
	}
}
