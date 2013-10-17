package com.alexdiru.criticalerror;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

public class DataCliffLine implements Parcelable {

	public static final int LINETYPE_FLAT = 0x0;
	public static final int LINETYPE_ASCENDING = 0x1;
	public static final int LINETYPE_DESCENDING = 0x2;
	
	private static Paint mScorePaint = null;
	
	private int mScorePositionX;
	private int mScorePositionY;
	private int mLandingPaintWidth;
	
	/**
	 * The start point of the cliff line
	 */
	public ParcelablePoint mStart;
	
	/**
	 * The end point of the cliff line
	 */
	public ParcelablePoint mEnd;
	
	/**
	 * The path of the polygon used to render the cliff line
	 */
	public SerializablePath path;
	
	/**
	 * The type the cliff is (flat/ascending/descending)
	 */
	public int mLineType;
	
	/**
	 * The score the line gives the player if it is a landing zone
	 */
	public int mScore;
	
	/**
	 * If a score line, whether or not the player has landed on it
	 */
	public boolean mActive;
	
	/**
	 * Creates the cliff line
	 * @param sx The start x position
	 * @param sy The start y position
	 * @param ex The end x position
	 * @param ey The end y position
	 * @param lineType The type the cliff line is
	 * @param random A random generator used for the score
	 */
	public DataCliffLine(int sx, int sy, int ex, int ey, int lineType, Random random) {
		
		if (mScorePaint == null) {
			mScorePaint = new Paint();
			mScorePaint.setColor(Color.BLACK);
			mScorePaint.setTextSize(30 * GameThread.mScaleY);
		}
		
		mStart = new ParcelablePoint(sx,sy);
		mEnd = new ParcelablePoint(ex, ey);
		mLineType = lineType;
		mActive = true;
		
		//Create random score
		switch (random.nextInt(10)) {
		case 0:
		case 1:
		case 2:
		case 3:
			mScore = 25;
			break;
		case 4:
		case 5:
		case 6:
			mScore = 35;
			break;
		case 7:
		case 8:
			mScore = 45;
			break;
		case 9:
			mScore = 50;
			break;
		}

		//Create path here
		path = updatePath();
		
		updateTextPosition();
	}
	
	/**
	 * Landing lines don't update, so only need to render them once
	 * This speeds up the drawing loop as less pixels are being drawn each time
	 * @param canvas The canvas to draw to
	 * @param landingPaint The paint used to draw the landing line
	 */
	public void renderInitial(Canvas canvas,  Paint landingPaint) {
		mLandingPaintWidth = (int) landingPaint.getStrokeWidth();
		if (mLineType == DataCliffLine.LINETYPE_FLAT) {
			int offsetY = (int)(landingPaint.getStrokeWidth()) >> 1;
			canvas.drawLine(mStart.x * GameThread.mScaleX, (mStart.y* GameThread.mScaleY + offsetY) , mEnd.x * GameThread.mScaleX, (mEnd.y* GameThread.mScaleY + offsetY) , landingPaint);
		}
	}
	
	/**
	 * Since the score of the line can either exist (if not collected) or not be shown (if collected) this must be rendered each loop
	 * @param canvas The canvas to draw to
	 */
	public void render(Canvas canvas) {
		//Highlight landing and draw its score
		if (mLineType == DataCliffLine.LINETYPE_FLAT)
			if (mActive) 
				ToolsTextDrawer.drawSingleLineText(canvas, String.valueOf(mScore), mScorePaint, (int) (GameThread.mScaleX * (mStart.x + ((mEnd.x - mStart.x)>>1))), (mLandingPaintWidth >> 1) + (int)(GameThread.mScaleY * (mEnd.y)));
	}
	
	/**
	 * Used to mark the cliff line landing zone as landed on, thus the player cannot recieve any more score points from it
	 */
	public void consumeScore() {
		mActive = false;
		updateTextPosition();
	}
	
	private void updateTextPosition() {
		//mScorePositionX = (int) ((mStart.x + ((mEnd.x - mStart.x)>>1)) * GameThread.mScaleX - ((int)mScorePaint.measureText(String.valueOf(mScore))>>1));
		//mScorePositionY = (int)((mStart.y + ((mEnd.y - mStart.y)>>1)) * GameThread.mScaleY ) + (int)mScorePaint.getStrokeWidth() + 
	}
	
	/**
	 * Updates the polygon used to render the path - must be used whenever the cliff line changes
	 * @return
	 */
	public SerializablePath updatePath() {
		//Scale the values according to resolution
		float scaledStartX = (float)mStart.x * GameThread.mScaleX;
		float scaledStartY = (float)mStart.y * GameThread.mScaleY;
		float scaledEndX = (float)mEnd.x * GameThread.mScaleX;
		float scaledEndY = (float)mEnd.y * GameThread.mScaleY;
		
		//Generate the path starting from top left and moving clockwise
		SerializablePath path = new SerializablePath();
		path.moveTo((float)scaledStartX, (float)scaledStartY);
		path.lineTo((float)scaledEndX, (float)scaledEndY);
		path.lineTo((float)scaledEndX, GameActivity.mScreenHeight);
		path.lineTo((float)scaledStartX, GameActivity.mScreenHeight);
		path.lineTo((float)scaledStartX, (float)scaledStartY);
		
		return path;
	}
	
	/**
	 * Restarts the cliff line to how it was at the start of the level
	 */
	public void restart() {
		mActive = true;
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
		dest.writeInt(mLineType);
		dest.writeParcelable(mStart,flags);
		dest.writeParcelable(mEnd, flags);
		dest.writeSerializable(path);
		dest.writeInt(mScore);
		dest.writeInt(mScorePositionX);
		dest.writeInt(mScorePositionY);
		dest.writeValue(mActive);	
	}
	
	/**
	 * Converts the cliff line to a vector for use with collision checking
	 * @return The vector
	 */
	public DataVector2D toVector2D() {
		return new DataVector2D(mStart.x, mStart.y, mEnd.x, mEnd.y);
	}

	/**
	 * Gets the squared mahattan distance of the cliff line
	 * @return The squared manhattan distance
	 */
	public int getSquaredLength() {
		return (mEnd.x - mStart.x)*(mEnd.x - mStart.x) + (mEnd.y-mStart.y)*(mEnd.y-mStart.y);
	}
}
