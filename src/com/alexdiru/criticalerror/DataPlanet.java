package com.alexdiru.criticalerror;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;

public class DataPlanet implements Parcelable {

	public static final int PLANETTYPE_DEIMOS = 0;
	public static final int PLANETTYPE_NEPTUNE = 1;
	public static final int PLANETTYPE_HELL = 2;
	
	private static Random random = new Random();
	
	/**
	 * Single bitmap which all the cliffs are rendered in initially
	 */
	public static Bitmap mCliffs;
	
	/**
	 * All the cliff lines of the planet
	 */
	public DataCliffLine[] mCliffLines;
	
	public int mPlanetType;
	public int mLandingZoneCount;
	public int mLandingZonesCompleted;
	public ArrayList<DataStar> mStars = new ArrayList<DataStar>();
	public int mStartPositionX;
	public int mStartPositionY;
	
	//Star art from opengameart.org/content/stars-set-01
	public Bitmap mStarBitmap;

	public DataPlanet() {
	}
	
	/**
	 * Generates a new map
	 */
	public void generate(int seed) {
		
		mStartPositionX = GameThread.mWorldWidth / 2;
		mStartPositionY = GameThread.mWorldHeight / 2 - 300;
		
		//Generate the lines for the cliffs
		if (seed != -1)
			mCliffLines = DataCliffGenerator.generate(630,360, 5, 5, seed);
		else
			mCliffLines = DataCliffGenerator.generate(630, 360, 5, 5, random.nextInt(Integer.MAX_VALUE - 1));
		
		mLandingZoneCount = 0;
		mLandingZonesCompleted = 0;
		
		//Random planet type
		mPlanetType = random.nextInt(3);
		
		//Get the cliff tile and background depending on the planet type
		
		int cliffTileId = 0;
		int backgroundId = 0;
		int starId = 0;
		
		switch (mPlanetType) {
		case PLANETTYPE_DEIMOS:
			cliffTileId = R.drawable.deimos_cliff_tile;
			backgroundId = R.drawable.background;
			starId = R.drawable.deimos_star;
			break;
		case PLANETTYPE_NEPTUNE:
			cliffTileId = R.drawable.neptune_cliff_tile;
			backgroundId = R.drawable.neptune_background;
			starId = R.drawable.neptune_star;
			break;
		case PLANETTYPE_HELL:
			cliffTileId = R.drawable.hell_cliff_tile;
			backgroundId = R.drawable.hell_background;
			starId = R.drawable.hell_star;
			break;
		}
		
		//Idea from http://stackoverflow.com/questions/8835727/drawing-bitmaps-with-alpha-channel-please-advise-some-solutions-and-speed-i
		//Use BitmapShader to tiled images to cropped bitmaps
		//The BitmapShader is applied to fillPaint, which is then used to scroll the texture of the bitmap
		Paint fillPaint = new Paint();  
		fillPaint.setColor(0xFFFFFFFF);  
		fillPaint.setStyle(Paint.Style.FILL);
		
		//This is the bitmap where the background and cliffs will be rendered to
		Bitmap clippedData;
		
		//This canvas will be temporarily used to render the background onto clippedData, and then overlay the static parts of the cliffs on top
		//Therefore the canvas must be the same size as the screen
		Canvas canvas = new Canvas(clippedData = Bitmap.createBitmap(GameActivity.mScreenWidth, GameActivity.mScreenHeight, Bitmap.Config.ARGB_8888));
		
		//Draw the background to the canvas first
		Bitmap backgroundTileBitmap = ToolsBitmapHelper.loadBitmap(backgroundId);
		//Get fillPaint to use a BitmapShader to scroll the texture across the desired area
		fillPaint.setShader(new BitmapShader(backgroundTileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		//Scroll the background across the whole screen
		canvas.drawRect(new Rect(0, 0, GameActivity.mScreenWidth, GameActivity.mScreenHeight), fillPaint);
		
		Bitmap originalData = ToolsBitmapHelper.loadBitmap(cliffTileId);
		
		
		//Draw the cliffs
		fillPaint.setShader(new BitmapShader(originalData, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
		for (DataCliffLine cliffLine : mCliffLines) {
			canvas.drawPath(cliffLine.path, fillPaint);

			Paint landingPaint = new Paint();
			landingPaint.setColor(Color.GREEN);
			landingPaint.setStrokeWidth(30*GameThread.mScaleY);
			cliffLine.renderInitial(canvas, landingPaint);
			
			if (cliffLine.mLineType == DataCliffLine.LINETYPE_FLAT)
				mLandingZoneCount++;
		}
		
		mCliffs = clippedData;
		
		//Generate stars
		mStarBitmap = ToolsBitmapHelper.loadBitmap(starId);
		
		mStars.clear();
		int numStars = random.nextInt(4);
		for (int i = 0; i < numStars; i++)
			mStars.add(new DataStar(random.nextInt(GameThread.mWorldWidth - (int)(mStarBitmap.getWidth()/GameThread.mScaleX)), random.nextInt(GameThread.mWorldHeight - 360 - (int)(mStarBitmap.getHeight()/GameThread.mScaleY)), 100));
	}
	
	public void restart() {
		for (DataStar star : mStars)
			star.restart();
		
		for (DataCliffLine cliffLine : mCliffLines)
			cliffLine.restart();
		
		mLandingZonesCompleted = 0;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(mCliffLines, flags);
		dest.writeInt(mPlanetType);
		dest.writeParcelable(mCliffs, flags);
	}

	public void render(Canvas canvas) {
		if (mCliffs != null)
			canvas.drawBitmap(mCliffs, 0,0,null);
		
		//Draw the cliff lines
		if (mCliffLines != null)
			for (DataCliffLine cliffLine : mCliffLines)
				cliffLine.render(canvas);
		
		//Draw stars
		for (DataStar star : mStars)
			if (star.mActive)
				star.render(canvas, mStarBitmap);
	}

}
