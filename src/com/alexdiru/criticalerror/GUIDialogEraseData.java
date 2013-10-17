package com.alexdiru.criticalerror;

import java.io.IOException;

public class GUIDialogEraseData extends GUIDialogYesNo {

	public GUIDialogEraseData(GameActivity gameActivity) {
		super(gameActivity);
	}

	public void positiveAction() {
		try {
    		 GameUpgrades.eraseData();
    	 } catch (IOException ex) {
    	 }
	}
	
	public void negativeAction() {
	}

	public void createAndShow() {
		super.createAndShow(mGameActivity.getBaseContext().getString(R.string.erase_data), mGameActivity.getBaseContext().getString(R.string.confirm));
	}
}
