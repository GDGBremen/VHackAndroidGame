package de.dsi8.vhackandroidgame.logic.contract;

import de.dsi8.vhackandroidgame.communication.contract.ICar;
import de.dsi8.vhackandroidgame.communication.contract.IDrive;

public interface IGamePresentationLogicListener extends IDrive, ICar {

	
	void addObstacle(final float pX, final float pY);
	
	void showQRCode(final String text);
	
}
