package com.alexdiru.criticalerror;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class DataVector2D {
	public PointF mStart;
	public PointF mEnd;

	public DataVector2D(float sx, float sy, float ex, float ey) {
		mStart = new PointF(sx, sy);
		mEnd = new PointF(ex, ey);
	}
	
	public static DataVector2D subtract(DataVector2D a, DataVector2D b) {
		return new DataVector2D(a.mStart.x - b.mStart.x, a.mStart.y - b.mStart.y, a.mEnd.x - b.mEnd.x, a.mEnd.y - b.mEnd.y);
	}
	
	/**
	 * Converts the 4 edges of a rectangle to 4 vectors
	 * @param rectangle
	 * @return
	 */
	public static DataVector2D[] convertRectangle(RectF rectangle) {
		DataVector2D[] vectors = new DataVector2D[4];
		
		vectors[0] = new DataVector2D(rectangle.top, rectangle.left, rectangle.top, rectangle.right);
		vectors[1] = new DataVector2D(rectangle.top, rectangle.right, rectangle.bottom, rectangle.right);
		vectors[2] = new DataVector2D(rectangle.bottom, rectangle.right, rectangle.bottom, rectangle.left);
		vectors[3] = new DataVector2D(rectangle.bottom, rectangle.left, rectangle.top, rectangle.left);
		
		return vectors;
	}

	// http://www.blitzbasic.com/codearcs/codearcs.php?code=1855
	public PointF intersectsWith(DataVector2D other) {
		//Points of one vector
		float x1 = mStart.x;
		float y1 = mStart.y;
		float x2 = mEnd.x;
		float y2 = mEnd.y;
		
		//Points of the 
		float x3 = other.mStart.x;
		float y3 = other.mStart.y;
		float x4 = other.mEnd.x;
		float y4 = other.mEnd.y;

		float nA, nB, d;

		nA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
		nB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
		d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

		if (d != 0) {
			float ua, ub;
			ua = nA/d;
			ub = nB/d;
			
			boolean r1, r2;
			r1 = ua >= 0.0 && ua <= 1.0;
			r2 = ub >= 0.0 && ub <= 1.0;
			
			if (r1 && r2)
			{
				return new PointF(x1 + ua*(x2-x1) , y1 + ua*(y2-y1));
			}
		}
		
		return null;
	}
	
	public PointF intersectsWith(RectF rectangle) {
		DataVector2D[] vectors = convertRectangle(rectangle);
		PointF intersection;
		
		for (DataVector2D vector : vectors)
			if ((intersection = intersectsWith(vector)) != null)
				return intersection;
		
		return null;
	}
	
	public void render(Canvas canvas, Paint paint) {
		canvas.drawLine(mStart.x * GameThread.mScaleX, mStart.y * GameThread.mScaleY, mEnd.x * GameThread.mScaleX, mEnd.y * GameThread.mScaleY, paint);
	}
}
