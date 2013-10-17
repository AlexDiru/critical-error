package com.alexdiru.criticalerror;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public abstract class GameUpgrades {
	public static int mFuelLevel = 1;
	public static int mLivesLevel = 1;
	public static int mScoreToMoneyMultiplierLevel = 1;
	public static int mMaxLevel = 5;
	public static int mMoney;
	
	/**
	 * Saves the current data to a file
	 * @throws IOException
	 */
	public static void saveData() throws IOException {
		FileOutputStream file;
		file = GameThread.mContext.openFileOutput("data.txt", Context.MODE_PRIVATE);
		file.write((String.valueOf(mFuelLevel) + "\n").getBytes());
		file.write((String.valueOf(mLivesLevel) + "\n").getBytes());
		file.write((String.valueOf(mScoreToMoneyMultiplierLevel) + "\n").getBytes());
		file.write((String.valueOf(mMoney) + "\n").getBytes());
		file.close();
	}
	
	/**
	 * Erases the file which stores the player's data
	 */
	public static void eraseData() throws IOException {
		File file = new File("data.txt");
		file.delete();
		
		//Reset stats
		mFuelLevel = 1;
		mLivesLevel = 1;
		mScoreToMoneyMultiplierLevel = 1;
		mMoney = 0;
	}
	
	/**
	 * Loads the player's data from a file
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void loadData() throws IOException, NumberFormatException {
		FileInputStream file;
		file = GameThread.mContext.openFileInput("data.txt");
		
		String fileContents = "";
		
		int content;
		while ((content = file.read()) != -1)
			fileContents += (char)content;
		
		file.close();
		
		String[] lines = fileContents.split("[\\r\\n]+");
		
		mFuelLevel = Integer.parseInt(lines[0]);
		mLivesLevel = Integer.parseInt(lines[1]);
		mScoreToMoneyMultiplierLevel = Integer.parseInt(lines[2]);
		mMoney = Integer.parseInt(lines[3]);
		
	}
	
	/**
	 * Retrieves the multiplier from the current level
	 * @return The multiplier
	 */
	public static float getScoreToMoneyMultiplier() {
		switch (mScoreToMoneyMultiplierLevel) {
		case 1:
			return 1.0f;
		case 2:
			return 1.25f;
		case 3:
			return 1.50f;
		case 4:
			return 1.75f;
		case 5:
			return 2.0f;
		default:
			return 0.0f;
		}
	}
	
	public static int getLevelCost(int level) {
		switch (level) {
		case 2:
			return 1000;
		case 3:
			return 2000;
		case 4:
			return 5000;
		case 5:
			return 10000;
		default:
			return 0;
		}
	}
	
	public static int getMaximumFuel() {
		switch (mFuelLevel) {
		default:
		case 1:
			return 500;
		case 2:
			return 625;
		case 3:
			return 750;
		case 4:
			return 875;
		case 5:
			return 1000;
		}
	}
	
	public static int getMaximumLives() {
		switch (mLivesLevel) {
		default:
		case 1:
			return 3;
		case 2:
			return 4;
		case 3:
			return 5;
		case 4:
			return 6;
		case 5:
			return 8;
		}
	}
}
