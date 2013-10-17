package com.alexdiru.criticalerror;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GameUI {
	private static Paint mScorePaint;
	private static Paint mFuelPaint;
	private static Paint mStatePaint;
	private static Paint mZonesPaint;
	private static Paint mLevelPaint;
	private static boolean mLeftAlignVelocity = true;
	private static Bitmap mPause;

	/**
	 * The bounding box to detect whether the pause button was touched
	 */
	public Rect mPauseBoundingBox;

	private static int mTextSize = (int) (30 * GameThread.mScaleY);

	/**
	 * Initialises the user interface
	 */
	public GameUI() {
		mTextSize = (int) (30 * GameThread.mScaleY);

		mScorePaint = new Paint();
		mScorePaint.setColor(Color.CYAN);
		mScorePaint.setTextSize(mTextSize);

		mStatePaint = new Paint();
		mStatePaint.setColor(Color.RED);
		mStatePaint.setTextSize(mTextSize);

		mFuelPaint = new Paint();
		mFuelPaint.setColor(Color.MAGENTA);
		mFuelPaint.setTextSize(mTextSize);

		mZonesPaint = new Paint();
		mZonesPaint.setColor(Color.GREEN);
		mZonesPaint.setTextSize(mTextSize);

		mLevelPaint = new Paint();
		mLevelPaint.setColor(Color.GRAY); 
		mLevelPaint.setTextSize(mTextSize);

		mPause = ToolsBitmapHelper.loadBitmap(R.drawable.pause);
		Log.d("hi", "HI");
	}

	private void renderScore(Canvas canvas, long score) {
		String scoreText = "Score: " + score;
		int width = (int) mFuelPaint.measureText(scoreText);
		canvas.drawText(scoreText, (GameActivity.mScreenWidth >> 1) - (width >> 1), 60 * GameThread.mScaleY, mScorePaint);
	}

	private void renderFuel(Canvas canvas, int currentFuel, int maxFuel) {
		String fuelText = "Fuel: " + currentFuel + "/" + maxFuel;
		int width = (int) mFuelPaint.measureText(fuelText);
		canvas.drawText(fuelText, (GameActivity.mScreenWidth - width) >> 1, GameActivity.mScreenHeight - 60 * GameThread.mScaleY, mFuelPaint);
	}

	private void renderLevel(Canvas canvas, int currentLevel) {
		String levelText = "Level: " + currentLevel;
		canvas.drawText(levelText, 0, 60 * GameThread.mScaleY, mLevelPaint);
	}

	private void renderLives(Canvas canvas, int currentLives, Bitmap mLanderBitmap) {
		int width = mLanderBitmap.getWidth();
		int height = GameActivity.mScreenHeight - mLanderBitmap.getHeight();

		for (int i = 1; i <= currentLives; i++)
			canvas.drawBitmap(mLanderBitmap, GameActivity.mScreenWidth - i * width, height, null);
	}

	private void renderZones(Canvas canvas, int currentZones, int maxZones) {
		String text = "Zones: " + currentZones + "/" + maxZones;
		int width = (int) mZonesPaint.measureText(text);
		canvas.drawText(text, GameActivity.mScreenWidth - width, 60 * GameThread.mScaleY, mZonesPaint);
	}

	private void renderVelocity(Canvas canvas, float xVelocity, float yVelocity, float maxXVelocityToLand, float maxYVelocityToLand) {
		Paint mXVelocityPaint = new Paint();
		mXVelocityPaint.setTextSize(mTextSize);
		Paint mYVelocityPaint = new Paint();
		mYVelocityPaint.setTextSize(mTextSize);

		mXVelocityPaint.setColor(Math.abs(xVelocity) <= maxXVelocityToLand ? Color.GREEN : Color.RED);
		mYVelocityPaint.setColor(yVelocity <= maxYVelocityToLand ? Color.GREEN : Color.RED);

		String xVelocityText = "X: " + xVelocity;
		String yVelocityText = "Y: " + yVelocity;
		int xWidth = (int) mXVelocityPaint.measureText(xVelocityText);
		int yWidth = (int) mYVelocityPaint.measureText(yVelocityText);
		if (mLeftAlignVelocity) {
			canvas.drawText(xVelocityText, 0, GameActivity.mScreenHeight - 120 * GameThread.mScaleY, mXVelocityPaint);
			canvas.drawText(yVelocityText, 0, GameActivity.mScreenHeight - 60 * GameThread.mScaleY, mYVelocityPaint);
		} else {
			canvas.drawText(xVelocityText, GameActivity.mScreenWidth - xWidth, GameActivity.mScreenHeight - 120 * GameThread.mScaleY, mXVelocityPaint);
			canvas.drawText(yVelocityText, GameActivity.mScreenWidth - yWidth, GameActivity.mScreenHeight - 60 * GameThread.mScaleY, mYVelocityPaint);
		}
	}

	private void renderPause(Canvas canvas) {
		int x1 = (GameActivity.mScreenWidth - mPause.getWidth()) >> 2;
		int y1 = GameActivity.mScreenHeight - mPause.getHeight();
		canvas.drawBitmap(mPause, x1, y1, null);

		// Create bounding box
		mPauseBoundingBox = new Rect(x1, y1, x1 + mPause.getWidth(), y1 + mPause.getHeight());
	}

	/**
	 * Renders the entirety of the user interface
	 * @param canvas The canvas to draw to
	 * @param lander The lander the player is using
	 * @param score The score the player has
	 * @param currentLevel The current level number the player is on
	 * @param planet The current level the player is on
	 */
	public void render(Canvas canvas, DataLander lander, long score, int currentLevel, DataPlanet planet) {
		if (canvas != null) {
			renderScore(canvas, score);
			renderFuel(canvas, lander.mCurrentFuel, GameUpgrades.getMaximumFuel());
			renderVelocity(canvas, lander.mDX, lander.mDY, lander.mMaximumXVelocityToLand, lander.mMaximumYVelocityToLand);
			renderLives(canvas, lander.mCurrentLives, DataLander.mBitmap);
			renderLevel(canvas, currentLevel);
			if (planet != null)
				renderZones(canvas, planet.mLandingZonesCompleted, planet.mLandingZoneCount);
			renderPause(canvas);
		}
	}
}
