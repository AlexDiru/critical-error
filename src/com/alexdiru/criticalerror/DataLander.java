package com.alexdiru.criticalerror;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class DataLander implements Parcelable {

	public static final int mLanderWidth = 72; // Initial pixel size of the
												// lander sprite
	public static final int mLanderHeight = 72;

	private boolean mHideFuelBitmap = false;
	private float mPixelX = 0;
	private float mPixelY = 0;
	private float mWorldX = 0;
	private float mWorldY = 0;
	private float mFuelAcceleration = 3;
	private float mGravityAcceleration = -1;

	// Collision vectors
	private DataVector2D mLeftDiagonalVector;
	private DataVector2D mRightDiagonalVector;
	private DataVector2D mStraightVector;

	private GameThread mGameThread;

	/**
	 * The bitmap for the lander itself
	 */
	public static Bitmap mBitmap;

	/**
	 * The bitmap for the fuel in the left direction
	 */
	public static Bitmap mFuelLeft;

	/**
	 * The bitmap for the fuel in the right direction
	 */
	public static Bitmap mFuelRight;

	/**
	 * The bitmap for the fuel in the up direction
	 */
	public static Bitmap mFuelUp;

	/**
	 * If the lander is moving left
	 */
	public boolean mMoveLeft;

	/**
	 * If the lander is moving right
	 */
	public boolean mMoveRight;

	/**
	 * If the lander is moving up
	 */
	public boolean mMoveUp;

	/*
	 * X Velocity
	 */
	public float mDX = 0;

	/**
	 * Y Velocity
	 */
	public float mDY = 0;

	/**
	 * The current fuel the lander has
	 */
	public int mCurrentFuel;

	/**
	 * The maximum X velocity the lander can have to safely land
	 */
	public int mMaximumXVelocityToLand = 20;

	/**
	 * The maximum Y velocity the lander can have to safely land
	 */
	public int mMaximumYVelocityToLand = 40;

	/**
	 * The number of lives the player has
	 */
	public int mCurrentLives = 3;

	/* CHEATS */
	public boolean mGodLandingModeEnabled = false; // Land on any velocity
	private boolean mInfiniteFuelEnabled = false; // Fuel never runs out

	/**
	 * The cliff line the player is currently in contact with
	 */
	public DataCliffLine mCollisionLine = null;

	/**
	 * Creates the lander
	 * 
	 * @param gameThread
	 *            The thread the game runs in
	 */
	public DataLander(GameThread gameThread) {
		mGameThread = gameThread;
	}

	/**
	 * Renders the lander
	 * 
	 * @param canvas
	 *            The canvas to draw to
	 */
	public void render(Canvas canvas) {
		// Scale according to screen size
		mPixelX = mWorldX * GameThread.mScaleX;
		mPixelY = mWorldY * GameThread.mScaleY;

		// Draw fuel
		if (!mHideFuelBitmap) {
			if (mMoveRight)
				canvas.drawBitmap(mFuelRight, mPixelX - mFuelLeft.getWidth(), mPixelY, null);

			if (mMoveLeft)
				canvas.drawBitmap(mFuelLeft, mPixelX + mBitmap.getWidth(), mPixelY, null);

			if (mMoveUp)
				canvas.drawBitmap(mFuelUp, mPixelX, mPixelY + mBitmap.getHeight(), null);

			mHideFuelBitmap = true;
		} else {
			mHideFuelBitmap = false;
		}

		canvas.drawBitmap(mBitmap, mPixelX, mPixelY, null);
	}

	/**
	 * Update the physics
	 * 
	 * @param elapsed
	 *            Time elapsed since last update
	 */
	public void updatePhysics(float elapsed) {
		// Make sure there is no cliff line below the player before gravity
		// occurs
		if (mCollisionLine == null)
			mDY -= mGravityAcceleration;
		else if (mDY > 0)
			mDY = 0;

		mWorldX += elapsed * mDX;
		mWorldY += elapsed * mDY;

		updateVectors();

		// Out of map bounds
		if (mWorldX <= 0 || (mWorldX >= GameThread.mWorldWidth - DataLander.mBitmap.getWidth() & mDX > 0) || mWorldY <= 0)
			mGameThread.reduceLife();

	}

	/**
	 * Manages the user's input
	 */
	public void handleUserInput() {
		// No fuel - can't move
		if (mCurrentFuel < 1)
			return;

		if (mMoveLeft) {
			if (!mInfiniteFuelEnabled)
				mCurrentFuel--;
			mDX -= mFuelAcceleration;
		}

		if (mMoveRight) {
			if (!mInfiniteFuelEnabled)
				mCurrentFuel--;
			mDX += mFuelAcceleration;
		}

		if (mMoveUp) {
			if (!mInfiniteFuelEnabled)
				mCurrentFuel--;
			mDY -= mFuelAcceleration;
		}
	}

	/**
	 * Sets up the lander in a planet
	 * 
	 * @param planet
	 *            The planet to set up in
	 */
	public void initialise(DataPlanet planet) {
		mWorldX = planet.mStartPositionX;
		mWorldY = planet.mStartPositionY;
		mCurrentFuel = GameUpgrades.getMaximumFuel();
		mDX = 0;
		mDY = 0;
	}

	/**
	 * Updates the lander's collision vectors according to its position
	 */
	private void updateVectors() {
		float landerEndX = mWorldX + mLanderWidth;
		float landerEndY = mWorldY + mLanderHeight;

		float offsetX = mLanderWidth >> 3;
		float offsetY = mLanderHeight >> 3;

		mLeftDiagonalVector = new DataVector2D(mWorldX + offsetX, mWorldY + offsetY, landerEndX - offsetX, landerEndY - offsetY);
		mStraightVector = new DataVector2D(mWorldX + (mLanderWidth >> 1), mWorldY + offsetY, mWorldX + (mLanderWidth >> 1), landerEndY - offsetY);
		mRightDiagonalVector = new DataVector2D(landerEndX - offsetX, mWorldY + offsetY, mWorldX + offsetX, landerEndY - offsetY);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Allows the object to be parcelled up
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(mPixelX);
		dest.writeFloat(mPixelY);
		dest.writeFloat(mWorldX);
		dest.writeFloat(mWorldY);
		dest.writeFloat(mDX);
		dest.writeFloat(mDY);
		dest.writeFloat(mFuelAcceleration);
		dest.writeFloat(mGravityAcceleration);
		dest.writeParcelable(mBitmap, flags);
		dest.writeInt(mCurrentFuel);
	}

	/**
	 * Checks if the lander is colliding with a cliffline
	 * 
	 * @param cliffLine
	 *            The cliffline
	 * @return A collision flag
	 */
	public boolean isCollidingWith(DataCliffLine cliffLine) {

		DataVector2D cliffVector = cliffLine.toVector2D();

		// Check if the cliff vector intersects with any of the player's vectors
		if (cliffVector.intersectsWith(mLeftDiagonalVector) != null ||
				cliffVector.intersectsWith(mRightDiagonalVector) != null ||
				cliffVector.intersectsWith(mStraightVector) != null) {
			mCollisionLine = cliffLine;
			return true;
		}

		return false;
	}

	/**
	 * Checks if the lander is colliding with a star
	 * 
	 * @param star
	 *            The star
	 * @return A collision flag
	 */
	public boolean isCollidingWidth(DataStar star) {
		RectF boundingBox = star.getBoundingBox();

		if (mLeftDiagonalVector.intersectsWith(boundingBox) != null ||
				mRightDiagonalVector.intersectsWith(boundingBox) != null ||
				mStraightVector.intersectsWith(boundingBox) != null)
			return true;

		return false;
	}
}
