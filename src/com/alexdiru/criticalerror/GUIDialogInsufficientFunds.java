package com.alexdiru.criticalerror;


public class GUIDialogInsufficientFunds extends GUIDialogAlert {

	private int mCost;
	private int mCurrentMoney;
	
	public GUIDialogInsufficientFunds(GameActivity gameActivity, int cost, int currentMoney) {
		super(gameActivity);
		mCost = cost;
		mCurrentMoney = currentMoney;
	}
	
	public void createAndShow() {
		super.createAndShow(mGameActivity.getBaseContext().getString(R.string.insufficient_funds), 
				mGameActivity.getBaseContext().getString(R.string.insufficient_funds_body_prefix1) + " " + String.valueOf(mCost) +  
				mGameActivity.getBaseContext().getString(R.string.insufficient_funds_body_prefix2) + " " + String.valueOf(mCurrentMoney) + "." );
	}
}
