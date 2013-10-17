package com.alexdiru.criticalerror;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class GameShop {
	public static Bitmap mButtonBitmap;
	public static Bitmap mThinButtonBitmap;
	public static Bitmap mBackBitmap;
	public static Bitmap mBackground;

	public static Rect mFuelBoundingBox;
	public static Rect mLivesBoundingBox;
	public static Rect mScoreBoundingBox;
	public static Rect mBackBoundingBox;
	public static Rect mEraseDataBoundingBox;

	public static GameThread mGameThread;

	private static boolean mActive = false;

	public void construct(GameView gameView, GameThread gameThread) {
		synchronized (this) {
			try {
				GameUpgrades.loadData();
			} catch (Exception ex) {
			}

			mButtonBitmap = ToolsBitmapHelper.loadBitmap(R.drawable.shop_add_icon);
			mThinButtonBitmap = ToolsBitmapHelper.loadBitmap(R.drawable.menu_icon);
			mBackground = ToolsBitmapHelper.loadBitmap(R.drawable.background);
			mBackBitmap = ToolsBitmapHelper.loadBitmap(R.drawable.shop_back_icon);
			mGameThread = gameThread;
			mFuelBoundingBox = new Rect();
			mLivesBoundingBox = new Rect();
			mScoreBoundingBox = new Rect();
			mBackBoundingBox = new Rect();
			mEraseDataBoundingBox = new Rect();

			mActive = true;
		}
	}

	public void handleTouch(MotionEvent e, GameActivity gameActivity) {
		if (mFuelBoundingBox == null || mLivesBoundingBox == null || mScoreBoundingBox == null || mEraseDataBoundingBox == null || mBackBoundingBox == null)
			return;

		//Fuel
		if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mFuelBoundingBox))
			if (GameUpgrades.mFuelLevel < GameUpgrades.mMaxLevel)
				if (GameUpgrades.mMoney >= GameUpgrades.getLevelCost(GameUpgrades.mFuelLevel+1)) {
					//Buy upgrade
					GameUpgrades.mMoney -=  GameUpgrades.getLevelCost(GameUpgrades.mFuelLevel+1);
					GameUpgrades.mFuelLevel++;
					try {
						GameUpgrades.saveData();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					//Alert can't buy
					GameThread.mGameActivity.showInsufficientFundsDialog(GameUpgrades.getLevelCost(GameUpgrades.mFuelLevel+1), GameUpgrades.mMoney);
				}
		
		//Lives
		if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mLivesBoundingBox))
			if (GameUpgrades.mLivesLevel < GameUpgrades.mMaxLevel)
				if (GameUpgrades.mMoney >= GameUpgrades.getLevelCost(GameUpgrades.mLivesLevel+1)) {
					//Buy upgrade
					GameUpgrades.mMoney -=  GameUpgrades.getLevelCost(GameUpgrades.mLivesLevel+1);
					GameUpgrades.mLivesLevel++;
					try {
						GameUpgrades.saveData();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					//Alert can't buy
					GameThread.mGameActivity.showInsufficientFundsDialog(GameUpgrades.getLevelCost(GameUpgrades.mLivesLevel+1), GameUpgrades.mMoney);
				}
		
		//Score Multiplier
		if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mScoreBoundingBox))
			if (GameUpgrades.mScoreToMoneyMultiplierLevel < GameUpgrades.mMaxLevel)
				if (GameUpgrades.mMoney >= GameUpgrades.getLevelCost(GameUpgrades.mScoreToMoneyMultiplierLevel+1)) {
					//Buy upgrade
					GameUpgrades.mMoney -=  GameUpgrades.getLevelCost(GameUpgrades.mScoreToMoneyMultiplierLevel+1);
					GameUpgrades.mScoreToMoneyMultiplierLevel++;
					try {
						GameUpgrades.saveData();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					//Alert can't buy
					GameThread.mGameActivity.showInsufficientFundsDialog(GameUpgrades.getLevelCost(GameUpgrades.mScoreToMoneyMultiplierLevel+1), GameUpgrades.mMoney);
				}
		
		if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mBackBoundingBox)) {
			GameThread.mGameActivity.finish();
			//Shop will have been destructed, so exit function straight away
			return;
		}
		if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mEraseDataBoundingBox))
			gameActivity.showShopDialog();
	}

	public void render(Canvas canvas) {

		synchronized (this) {
			if (!mActive)
				return;

			canvas.drawBitmap(mBackground, 0, 0, null);

			int yBorder = (int) (20 * GameThread.mScaleX);
			int xIcon = yBorder;
			int yIcon = (int) (130 * GameThread.mScaleY);

			Paint paint = new Paint();
			paint.setTextSize(30 * GameThread.mScaleY);
			paint.setTypeface(GameActivity.mTypefaceSevenEight);
			paint.setColor(Color.WHITE);
			String money = GameThread.mGameActivity.getString(R.string.money_prefix) + GameUpgrades.mMoney;
			ToolsTextDrawer.drawSingleLineText(canvas, money, null, (GameActivity.mScreenWidth >> 2)*3, (int)(60*GameThread.mScaleY));
			xIcon = yBorder;
			

			//Shop title
			String shopTitle = GameThread.mGameActivity.getString(R.string.menu_shop);
			ToolsTextDrawer.drawSingleLineText(canvas, shopTitle, null,GameActivity.mScreenWidth >> 1,(int)( 60*GameThread.mScaleY));
			
			paint.setColor(Color.BLACK);
			renderRow(canvas, xIcon, yIcon, yBorder, GameThread.mContext.getString(R.string.lives), GameUpgrades.mLivesLevel, mLivesBoundingBox, paint);
			yIcon += yBorder + mButtonBitmap.getHeight();
			renderRow(canvas, xIcon, yIcon, yBorder, GameThread.mContext.getString(R.string.fuel), GameUpgrades.mFuelLevel, mFuelBoundingBox, paint);
			yIcon += yBorder + mButtonBitmap.getHeight();
			renderRow(canvas, xIcon, yIcon, yBorder, GameThread.mContext.getString(R.string.score), GameUpgrades.mScoreToMoneyMultiplierLevel, mScoreBoundingBox, paint);
			yIcon += yBorder + mButtonBitmap.getHeight();
			
			//Back button
			

			// Erase data button
			// Set position to bottom right corner
			xIcon = GameActivity.mScreenWidth - yBorder - mThinButtonBitmap.getWidth();
			yIcon = GameActivity.mScreenHeight - yBorder - mThinButtonBitmap.getHeight();
			renderThinButton(canvas, xIcon, yIcon, yBorder, GameThread.mContext.getString(R.string.erase_data), mEraseDataBoundingBox, paint);
			yIcon -= yBorder;
			yIcon -= mThinButtonBitmap.getHeight();
			renderThinButton(canvas, xIcon, yIcon, yBorder, GameThread.mContext.getString(R.string.back), mBackBoundingBox, paint);
		}
	}
	
	private static void renderThinButton(Canvas canvas, int x, int y, int border, String text, Rect boundingBox, Paint paint) {
		canvas.drawBitmap(mThinButtonBitmap, x, y, null);
		Rect textBounds = new Rect();
		paint.getTextBounds(text, 0, 1, textBounds);
		ToolsTextDrawer.drawSingleLineText(canvas, text, paint, x + (mThinButtonBitmap.getWidth() >> 1), y + (mThinButtonBitmap.getHeight() >> 1));
		//canvas.drawText(text, x + ((mThinButtonBitmap.getWidth() - (int) paint.measureText(text)) >> 1), y + ((mThinButtonBitmap.getHeight() + textBounds.height()) >> 1), paint);

		// Bounding Box
		boundingBox.left = x;
		boundingBox.top = y;
		boundingBox.right = x + mThinButtonBitmap.getWidth();
		boundingBox.bottom = y + mThinButtonBitmap.getHeight();
	}

	private static void renderRow(Canvas canvas, int x, int y, int border, String infoText, int level, Rect boundingBox, Paint paint) {

		canvas.drawBitmap(mButtonBitmap, x, y, null);
		Rect textBounds = new Rect();
		paint.getTextBounds(infoText, 0, 1, textBounds);
		ToolsTextDrawer.drawSingleLineText(canvas, infoText, paint, x + (mButtonBitmap.getWidth() >> 1), y + (mButtonBitmap.getHeight() >> 1));
		x += mButtonBitmap.getWidth() + border;

		canvas.drawBitmap(mButtonBitmap, x, y, null);
		paint.getTextBounds(String.valueOf(level), 0, 1, textBounds);
		ToolsTextDrawer.drawSingleLineText(canvas, String.valueOf(level), paint, x + (mButtonBitmap.getWidth() >> 1), y + (mButtonBitmap.getHeight() >> 1));
		x += mButtonBitmap.getWidth() + border;

		// Increase level button
		if (level < GameUpgrades.mMaxLevel) {
			canvas.drawBitmap(mButtonBitmap, x, y, null);
			
			ToolsTextDrawer.drawSingleLineText(canvas, "+", paint, x + (mButtonBitmap.getWidth() >> 1), y + (mButtonBitmap.getHeight() >> 1));
			
			// Bounding box for button
			boundingBox.top = y;
			boundingBox.left = x;
			boundingBox.right = x + mButtonBitmap.getWidth();
			boundingBox.bottom = y + mButtonBitmap.getHeight();

			x += mButtonBitmap.getWidth() + border;
		}

	}

	public void destruct() {
		synchronized (this) {
			try {
				GameUpgrades.saveData();
			} catch (IOException ex) {
			}

			mButtonBitmap = null;
			mThinButtonBitmap = null;
			mBackground = null;
			mBackBitmap = null;
			mActive = false;
			mFuelBoundingBox = null;
			mLivesBoundingBox = null;
			mScoreBoundingBox = null;
			mBackBoundingBox = null;
			mEraseDataBoundingBox = null;
		}

	}
}
