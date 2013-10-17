package com.alexdiru.criticalerror;


public class ToolsGameState {

	//The states that the game can be in
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_NEWLEVEL = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;
	public static final int STATE_SHOP = 6;
	
	public static int mGameState;
	

	public static final int GAMEMODE_CAMPAIGN = 0;
	public static final int GAMEMODE_FREEPLAY = 1;
	
	public static int mGameMode;

}
