package com.alexdiru.criticalerror;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class DataStar {
	
	private static final int mStarHeight = 64;
	private static final int mStarWidth = 64;
	
	public float mWorldPositionX;
	public float mWorldPositionY;
	//Original position so the level can be restarted
	public float mOriginalPositionX;
	public float mOriginalPositionY;
	public int mScore;
	public boolean mActive;
	
	public float mRenderOffsetY;
	public boolean mOffsetDirectionUp;
	
	public DataStar(float worldPositionX, float worldPositionY, int score) {
		mOriginalPositionX = mWorldPositionX = worldPositionX;
		mOriginalPositionY = mWorldPositionY = worldPositionY;
		mScore = score;
		mActive = true;
		mOffsetDirectionUp = true;
	}
	
	public RectF getBoundingBox() {
		return new RectF(mWorldPositionY, mWorldPositionX, mWorldPositionY + mStarHeight, mWorldPositionX + mStarWidth); 
	}
	
	public void render(Canvas canvas, Bitmap starBitmap) {
		canvas.drawBitmap(starBitmap, mWorldPositionX * GameThread.mScaleX, (mWorldPositionY + mRenderOffsetY) * GameThread.mScaleY, null);
	}
	
	public void update(float elapsed) {
		if (Math.abs(mRenderOffsetY) > 5.0)
			mOffsetDirectionUp = !mOffsetDirectionUp;
		
		if (mOffsetDirectionUp) {
			mRenderOffsetY -= 0.5;
		} else {
			mRenderOffsetY += 0.5;
		}
	}
	
	public void restart() {
		mActive = true;
		mWorldPositionX = mOriginalPositionX;
		mWorldPositionY = mOriginalPositionY;
	}
}
