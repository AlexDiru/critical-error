package com.alexdiru.criticalerror;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class GamePostLevelScreen {

	public static void construct() {
	}
	
	public static void render(Canvas canvas, long score, int moneyEarned) {
		Paint paint = new Paint();
		paint.setTypeface(GameActivity.mTypefaceSevenEight);
		paint.setColor(Color.WHITE);
		paint.setTextSize(ToolsTextDrawer.mTextSize);
		
		ToolsTextDrawer.drawMultiLineText(canvas, "You crashed!\nScore: " + score + " Total Money Earned: " + moneyEarned, paint, GameActivity.mScreenWidth >> 1, GameActivity.mScreenHeight >> 1);
		
	}
	
	/**
	 * Render win level
	 * @param canvas
	 * @param score
	 * @param levelMoneyEarned
	 * @param moneyEarned
	 */
	public static void render(Canvas canvas, long score, int levelMoneyEarned, int moneyEarned, int levelJustBeat) {
		Paint paint = new Paint();
		paint.setTypeface(GameActivity.mTypefaceSevenEight);
		paint.setColor(Color.WHITE);
		paint.setTextSize(ToolsTextDrawer.mTextSize);
		
		ToolsTextDrawer.drawMultiLineText(canvas, "Congratulations you beat Level " + levelJustBeat + "\nScore: " + score + " Money Earned: " + moneyEarned + " Total Money Earned: " + moneyEarned, paint, GameActivity.mScreenWidth >> 1, GameActivity.mScreenHeight >> 1);
	}
	
	public static void destruct() {
	}
	
}
