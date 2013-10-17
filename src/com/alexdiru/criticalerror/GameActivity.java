package com.alexdiru.criticalerror;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    private static GameThread mGameThread;
    private GameView mGameView;	
    
    public static int mScreenWidth;
    public static int mScreenHeight;
    
    public static Typeface mTypefaceSevenEight;
    
    public static AlertDialog.Builder mShopDialog;
	
	//http://developer.android.com/guide/practices/screens_support.html#dips-pels
	//public static int mGestureThreshold;
    
    public void updateScreenDimensions() {
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
    }
    
	/** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_main);
        
        ToolsActivity.setActivity(this, getBaseContext()); 
        
        mGameView = (GameView)findViewById(R.id.gamearea);
        mGameView.mGameActivity = this;
        mTypefaceSevenEight = Typeface.createFromAsset(this.getAssets(), "fonts/futuraltbtlight.ttf");
        
        updateScreenDimensions();
              	
        startGame(mGameView, null, savedInstanceState);
        
        Log.d("hi","hi");
    }
    
    public void showShopDialog() {
    	new GUIDialogEraseData(this).createAndShow();
    }
    
    public void showInsufficientFundsDialog(int cost, int currentMoney) {
    	new GUIDialogInsufficientFunds(this, cost, currentMoney).createAndShow();
    }
    
    
	private void startGame(GameView gView, GameThread gThread, Bundle savedInstanceState) {    	
        if (savedInstanceState == null) {
            // we were just launched: set up a new game
        	mGameThread = new GameThread(mGameView, getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), this);
        	mGameView.setThread(mGameThread);
        } 
        else {
        	if(mGameThread != null) {
        		//Thread is lives, just restart it!
        		mGameThread.restoreState(savedInstanceState);
        		if(ToolsGameState.mGameState == ToolsGameState.STATE_RUNNING) {
        			mGameThread.setState(ToolsGameState.STATE_PAUSE);
        		}
        	}
        	else {
        		//make a new thread with the values from savedInstanceState
        		gThread = new GameThread(mGameView, getWindowManager().getDefaultDisplay().getWidth(),  getWindowManager().getDefaultDisplay().getHeight(), this);
        		mGameView.setThread(gThread);
        		mGameThread = mGameView.getThread();
        		mGameThread.restoreState(savedInstanceState);
        	}
        }
        
        mGameView.startSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
    }
    
	/*
	 * Activity state functions
	 */
	
    @Override
    protected void onPause() {
        super.onPause();
        mGameThread.mRun = false;
        mGameThread.setState(ToolsGameState.STATE_PAUSE);
        
    }
    
    @Override
    protected void onResume() {
    	Log.d("onResume", "RESUMED");
    	super.onResume();
    	
    	updateScreenDimensions();
    	if (ToolsGameState.mGameState == ToolsGameState.STATE_PAUSE)
    		ToolsGameState.mGameState = ToolsGameState.STATE_RUNNING;
    	mGameThread.mRun = true;
    }

    
    @Override
	protected void onDestroy() {
		super.onDestroy();
    	
    	mGameView.cleanup();
        mGameView.removeSensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        
        mGameThread = null;
        mGameView = null;
	}    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);

        if(mGameThread != null) {
        	mGameThread.saveState(outState);
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
    	super.onRestoreInstanceState(inState);
    	
    	if (mGameThread != null) {
    		mGameThread.restoreState(inState);
    	}
    }
}