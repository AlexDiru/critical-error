package com.alexdiru.criticalerror;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * Optimisations avoid getters and setters use static if method access isn't
 * needed enhanced loop beware floats bit shifting use library functions such as
 * System.arraycopy
 * 
 * @author Alex
 * 
 */

public class GameThread extends Thread {

	public static final int mWorldWidth = 1280;
	public static final int mWorldHeight = 720;

	// Variables to scale pixels
	public static float mScaleX;
	public static float mScaleY;

	// Matrix for scaling bitmaps
	public static Matrix mScaleMatrix;

	public static Context mContext;
	public static GameActivity mGameActivity;

	// Controls whether the thread is running (run() method)
	public boolean mRun = false;

	// The surface this thread (and only this thread) writes upon
	public static SurfaceHolder mSurfaceHolder;

	// The view
	public GameView mGameView;

	// Last time we updated the game physics
	private long mLastTime = 0;

	private long mScore = 0;
	private long mHighScore = 0;

	private int mLevelMoneyEarned; // Money over the previous level
	private int mMoneyEarned; // Money over set of levels until all lives lost
	private int mLevelScoreEarned; // Score over the previous level

	private int mCurrentLevel;

	private DataLander mLander = new DataLander(this);
	private DataPlanet mPlanet;

	private GameUI mUI;

	public GameShop mShop = new GameShop();

	private boolean mPaused = false;

	public GameThread(GameActivity gameActivity) {

		Log.d("functions", "GameThread() - Begin");

		mGameActivity = gameActivity;

		// For scaling objects to make resolution independent
		mScaleX = GameActivity.mScreenWidth / (float) mWorldWidth;
		mScaleY = GameActivity.mScreenHeight / (float) mWorldHeight;
		mScaleMatrix = new Matrix();
		mScaleMatrix.postScale(mScaleX, mScaleY);
		mContext = gameActivity.getApplicationContext();

		mUI = new GameUI();

		// Load data
		try {
			GameUpgrades.loadData();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("functions", "GameThread() - End");
	}

	public GameThread(GameView gameView, int screenWidth, int screenHeight, GameActivity gameActivity) {
		this(gameActivity);
		Log.d("functions", "GameThread(GameView, int, int) - Begin");

		mGameView = gameView;

		mSurfaceHolder = gameView.getHolder();

		DataLander.mBitmap = ToolsBitmapHelper.loadBitmap(R.drawable.yellow_ball);
		DataLander.mFuelRight = ToolsBitmapHelper.loadBitmap(R.drawable.fuel_right);
		DataLander.mFuelLeft = ToolsBitmapHelper.loadBitmap(R.drawable.fuel_left);
		DataLander.mFuelUp = ToolsBitmapHelper.loadBitmap(R.drawable.fuel_up);

		setState(ToolsGameState.mGameState);

		Log.d("functions", "GameThread(GameView, int, int) - End");
	}

	public GameThread(GameView gameView, GameThread oldThread, GameActivity gameActivity) {

		this(gameActivity);

		Log.d("functions", "GameThread(GameView, GameThread) - Begin");
		mGameView = gameView;

		mSurfaceHolder = gameView.getHolder();

		// Transfer the old values
		mRun = oldThread.mRun;
		mLastTime = oldThread.mLastTime;
		mScore = oldThread.mScore;

		mLander = oldThread.mLander;

		mPlanet = oldThread.mPlanet;
		mUI = oldThread.mUI;

		Log.d("functions", "GameThread(GameView, GameThread) - End");
	}

	void shutdownAndAwaitTermination(ExecutorService pool) {
		pool.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}

		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private int getCampaignLevelSeed() {
		switch (((mCurrentLevel - 1) % 15) + 1) {
		case 1:
			return 666;
		case 2:
			return 6;
		case 3:
			return 11;
		case 4:
			return 13;
		case 5:
			return 26;
		case 6:
			return 1;
		case 7:
			return 2;
		case 8:
			return 3;
		case 9:
			return 5;
		case 10:
			return 8;
		case 11:
			return 0;
		case 12:
			return 4;
		case 13:
			return 7;
		case 14:
			return 10;
		case 15:
			return 12;
		default:
			return -1;
		}
	}

	private void checkCollision() {
		mLander.mCollisionLine = null;

		// Cliff Lines
		for (DataCliffLine cliffLine : mPlanet.mCliffLines)
			if (mLander.isCollidingWith(cliffLine))
				if (cliffLine.mLineType == DataCliffLine.LINETYPE_FLAT) {
					// Check velocities
					if ((Math.abs(mLander.mDX) <= mLander.mMaximumXVelocityToLand &&
							Math.abs(mLander.mDY) <= mLander.mMaximumYVelocityToLand) ||
							mLander.mGodLandingModeEnabled) {
						if (cliffLine.mActive) {
							increaseScore(cliffLine.mScore);
							cliffLine.consumeScore();
							mPlanet.mLandingZonesCompleted++;

							if (mPlanet.mLandingZonesCompleted == mPlanet.mLandingZoneCount)
								setState(ToolsGameState.STATE_WIN);
						}
					} else {
						reduceLife();
						break;
					}
				}
				else
				{
					reduceLife();
					break;
				}

		// Stars
		for (DataStar star : mPlanet.mStars)
			if (star.mActive)
				if (mLander.isCollidingWidth(star)) {
					increaseScore(star.mScore);
					star.mActive = false;
				}
	}

	public void reduceLife() {
		mLander.mCurrentLives--;

		// No lives left, therefore lost
		if (mLander.mCurrentLives == 0)
			setState(ToolsGameState.STATE_LOSE);
		else {
			restartLevel();
		}
	}

	private void restartLevel() {
		mPlanet.restart();
		mLander.initialise(mPlanet);
	}

	/*
	 * Called when app is destroyed, so not really that important here But if
	 * (later) the game involves more thread, we might need to stop a thread,
	 * and then we would need this Dare I say memory leak...
	 */
	public void cleanup() {
		this.mGameView = null;
		mSurfaceHolder = null;
		mLander = null;
		mPlanet = null;
	}

	/*
	 * Restore/save state (do not confuse with ToolsGameState.mGameState states)
	 * of a game, i.e. all needed variable. Doing this and we can pause the game
	 * and recieve calls.
	 */
	public synchronized void restoreState(Bundle savedState) {
		synchronized (mSurfaceHolder) {
			setState(ToolsGameState.STATE_PAUSE);

			mScore = savedState.getLong("score");

			mLander = savedState.getParcelable("mLander");
			mPlanet = savedState.getParcelable("mPlanet");

			mScaleX = savedState.getFloat(getClass().getName() + "mScaleX");
			mScaleY = savedState.getFloat(getClass().getName() + "mScaleY");

			setState(savedState.getInt(getClass().getName() + "ToolsGameState.mGameState"));
		}
	}

	public Bundle saveState(Bundle map) {
		synchronized (mSurfaceHolder) {
			if (map != null) {
				map.putLong("score", mScore);

				map.putParcelable("mLander", mLander);

				map.putFloat(getClass().getName() + "mScaleX", mScaleX);
				map.putFloat(getClass().getName() + "mScaleY", mScaleY);

				map.putInt(getClass().getName() + "ToolsGameState.mGameState", ToolsGameState.mGameState);

			}
		}
		return map;
	}

	// The thread start
	@Override
	public void run() {
		Log.d("functions", "run() - Begin");

		Canvas canvasRun;
		while (mRun) {
			canvasRun = null;
			try {
				canvasRun = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					if (ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING) {

						final long now = System.currentTimeMillis();
						final float elapsed = (now - mLastTime) / 1000.0f;

						// Run the logic in its own thread
						Thread t1 = new Thread(new Runnable() {
							public void run() {
								mLander.handleUserInput();
								mLander.updatePhysics(elapsed);
							}
						});
						t1.start();

						// Star's visual positions are separate from the
						// lander's positions
						Thread t2 = new Thread(new Runnable() {
							public void run() {
								// Update stars
								for (DataStar star : mPlanet.mStars)
									star.update(elapsed);
							}
						});
						t2.start();

						try {
							t1.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						try {
							t2.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						checkCollision();

						mLastTime = now;
					}

					doDraw(canvasRun);
				}
			} finally {
				if (canvasRun != null) {
					if (mSurfaceHolder != null)
						mSurfaceHolder.unlockCanvasAndPost(canvasRun);
				}
			}
		}

	}

	private void increaseScore(int amount) {
		mScore += amount;
		mLevelScoreEarned += amount;
	}

	/**
	 * Renders the graphics to the canvas
	 * 
	 * @param canvas
	 *            The canvas to draw to
	 */
	protected void doDraw(Canvas canvas) {
		if (canvas == null)
			return;

		if (ToolsGameState.mGameState == ToolsGameState.STATE_SHOP) {
			// Render the shop
			mShop.render(canvas);
		} else if (ToolsGameState.mGameState == ToolsGameState.STATE_LOSE) {
			// Render the score and money the player gained
			canvas.drawColor(Color.BLACK);
			Paint paint = new Paint();
			paint.setTextSize(30 * GameThread.mScaleY);
			paint.setColor(Color.WHITE);
			GamePostLevelScreen.render(canvas, mScore, mMoneyEarned);

		} else if (ToolsGameState.mGameState == ToolsGameState.STATE_PAUSE) {
			try {
				ToolsTextDrawer.drawSingleLineText(canvas, "Paused", null, GameActivity.mScreenWidth >> 1, GameActivity.mScreenHeight >> 1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (ToolsGameState.mGameState == ToolsGameState.STATE_WIN) {

			// Render the score and money the player gained
			canvas.drawColor(Color.BLACK);
			GamePostLevelScreen.render(canvas, mScore, mLevelMoneyEarned, mMoneyEarned, mCurrentLevel);
		} else if (ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING) {

			// Render the planet, DataLander and user interface
			// Make sure to do null checks
			if (mPlanet != null)
				mPlanet.render(canvas);

			if (mLander != null)
				mLander.render(canvas);

			if (mUI != null)
				mUI.render(canvas, mLander, mScore, mCurrentLevel, mPlanet);
		}
	}

	/*
	 * Control functions
	 */

	// Finger touches the screen
	public boolean onTouch(MotionEvent e) {

		//synchronized (mSurfaceHolder) {

			mLander.mMoveLeft = false;
			mLander.mMoveRight = false;
			mLander.mMoveUp = false;
			this.actionOnTouch(e);
		//}

		return false;
	}

	private void actionOnTouch(MotionEvent event) {
		final MotionEvent e = event;
		final boolean isPressed = e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE;
		boolean originalPaused = mPaused;
		
		// One thread to manage game controls
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				if (ToolsGameState.mGameState == ToolsGameState.STATE_SHOP) {
					if (e.getAction() == MotionEvent.ACTION_DOWN)
						mShop.handleTouch(e, mGameActivity);
				} else if (ToolsGameState.mGameState == ToolsGameState.STATE_LOSE) {
					if (e.getAction() == MotionEvent.ACTION_DOWN)
						ToolsActivity.mActivity.finish();
				} else if (ToolsGameState.mGameState == ToolsGameState.STATE_PAUSE) {
					if (e.getAction() == MotionEvent.ACTION_DOWN)
						if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mUI.mPauseBoundingBox))
							mPaused = false;
				} else if (ToolsGameState.mGameState == ToolsGameState.STATE_WIN) {
					if (e.getAction() == MotionEvent.ACTION_DOWN)
						setState(ToolsGameState.STATE_NEWLEVEL);
				} else {
					// Get the direction of the fuel
					// 1/5 left side of screen (move left)
					if (e.getX() <= GameActivity.mScreenWidth / 5) {
						mLander.mMoveLeft = isPressed;
					}
					// 1/5 right side of screen (move right)
					if (e.getX() >= (GameActivity.mScreenWidth / 5) << 2) {
						mLander.mMoveRight = isPressed;
					}
					// 1/3 top of screen (move up)
					if (e.getY() <= GameActivity.mScreenHeight / 3 && e.getX() < (GameActivity.mScreenWidth / 5) << 2 && e.getX() > (GameActivity.mScreenWidth / 5)) {
						mLander.mMoveUp = isPressed;
					}
				}
			}
		});
		t1.start();

		// Another thread for pause button
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				if (ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING)
					if (e.getAction() == MotionEvent.ACTION_DOWN)
						if (ToolsTouchHelper.isTouchInsideBoundingBox(e, mUI.mPauseBoundingBox))
							mPaused = true;
			}
		});
		t2.start();

		//Join threads
		try {
			t1.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			t2.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (mPaused && !originalPaused)
			pause();
		else if (!mPaused && originalPaused)
			unpause();
		
		return;
	}

	/*
	 * Game states
	 */
	public void pause() {
		synchronized (mSurfaceHolder) {
			if (ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING)
				setState(ToolsGameState.STATE_PAUSE);
		}
	}

	public void unpause() {
		// Move the real time clock up to now
		synchronized (mSurfaceHolder) {
			mLastTime = System.currentTimeMillis();
		}
		
		//Subtle change
		ToolsGameState.mGameState = ToolsGameState.STATE_RUNNING;
	}

	public void setState(int mode) {
		synchronized (this) {
			// If currently in shop - cleanup
			if (ToolsGameState.mGameState == ToolsGameState.STATE_SHOP)
				mShop.destruct();

			// Set the game state to the new value
			ToolsGameState.mGameState = mode;

			if (ToolsGameState.mGameState == ToolsGameState.STATE_LOSE) {
				if (mScore > mHighScore)
					mHighScore = mScore;

				// Convert score to money
				mLevelMoneyEarned = (int) ((float) mLevelScoreEarned * GameUpgrades.getScoreToMoneyMultiplier());
				mMoneyEarned += mLevelMoneyEarned;
				GameUpgrades.mMoney += mMoneyEarned;
				try {
					GameUpgrades.saveData();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (ToolsGameState.mGameState == ToolsGameState.STATE_WIN) {
				mLevelMoneyEarned = (int) ((float) mLevelScoreEarned * GameUpgrades.getScoreToMoneyMultiplier());
				mMoneyEarned += mLevelMoneyEarned;
			} else if (ToolsGameState.mGameState == ToolsGameState.STATE_SHOP) {
				mShop.construct(mGameView, this);
			} else if (ToolsGameState.mGameState == ToolsGameState.STATE_NEWLEVEL) {
				mCurrentLevel++;
				if (ToolsGameState.mGameMode == ToolsGameState.GAMEMODE_CAMPAIGN)
					mPlanet.generate(getCampaignLevelSeed());
				else
					mPlanet.generate(-1);

				mLander.initialise(mPlanet);
				mLevelMoneyEarned = 0;
				mLevelScoreEarned = 0;
				ToolsGameState.mGameState = ToolsGameState.STATE_RUNNING;
			} else if (ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING) {

				mCurrentLevel = 1;

				synchronized (mSurfaceHolder) {
					mPlanet = new DataPlanet();

					if (ToolsGameState.mGameMode == ToolsGameState.GAMEMODE_CAMPAIGN)
						mPlanet.generate(getCampaignLevelSeed());
					else
						mPlanet.generate(-1);

					mLander.initialise(mPlanet);
					mLander.mCurrentLives = GameUpgrades.getMaximumLives();

				}

				// Reset score
				mScore = 0;
				mMoneyEarned = 0;
				mLevelScoreEarned = 0;
				mLastTime = System.currentTimeMillis() + 100;
			}
		}
	}
}