package com.alexdiru.criticalerror;

import android.graphics.Rect;
import android.view.MotionEvent;

public abstract class ToolsTouchHelper {
	public static boolean isTouchInsideBoundingBox(MotionEvent e, Rect rect) {
		if (rect == null)
			return false;
		
		return ((int)(e.getX()) <= rect.right && (int)(e.getX()) >= rect.left &&
			(int)(e.getY()) <= rect.bottom && (int)(e.getY()) >= rect.top);
	}
}