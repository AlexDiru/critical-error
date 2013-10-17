package com.alexdiru.criticalerror;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class ToolsTextDrawer {

	public static float mTextSize;
	private static Paint mDefaultPaint = null;
	
	public static void drawSingleLineText(Canvas canvas, String text, Paint paint, int x, int y) {
		drawMultiLineText(canvas,text +"\n", paint, x, y);
	}
	
	public static void drawMultiLineText(Canvas canvas, String text, Paint paint, int x, int y) {
		
		if (mDefaultPaint == null) {
			mDefaultPaint = new Paint();
			mDefaultPaint.setColor(Color.WHITE);
			mTextSize =30 * GameThread.mScaleY;
			mDefaultPaint.setTextSize(mTextSize);
		}
		
		if (paint == null)
			paint = mDefaultPaint;
		
		String[] lines = text.split("[\\r\\n]+");
		
		for (String line : lines) {
			int lineWidth = (int)paint.measureText(line);
			canvas.drawText(line, x - (lineWidth >> 1), y + paint.descent(), paint);
			y -= paint.ascent() - paint.descent();
		}
	}

}
