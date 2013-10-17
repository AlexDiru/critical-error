package com.alexdiru.criticalerror;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private volatile GameThread thread;

	private SensorEventListener sensorAccelerometer;

	public GameActivity mGameActivity;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}
	
	//Used to release any resources.
	public void cleanup() {
		thread.mRun = false;
		thread.cleanup();
		
		removeCallbacks(thread);
		thread = null;
		
		setOnTouchListener(null);
		sensorAccelerometer = null;
		
		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}
	
	/*
	 * Setters and Getters
	 */

	public void setThread(GameThread newThread) {

		thread = newThread;

		setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if(thread!=null) {
					return thread.onTouch(event);
				}
				else return false;
			}

		});

		setClickable(true);
		setFocusable(true);
	}
	
	public GameThread getThread() {
		return thread;

	}
	
	/*
	 * Screen functions
	 */
		
	//ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(thread!=null) {
			if (!hasWindowFocus)
				thread.pause();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if(thread!=null) {
			thread.mRun = true;
			
			if(thread.getState() == Thread.State.NEW){
				//Just start the new thread
				thread.start();
			}
			else {
				if(thread.getState() == Thread.State.TERMINATED){
					//Set up and start a new thread with the old thread as seed
					thread = new GameThread(this, thread, mGameActivity);
					thread.mRun = true;
					thread.start();
				}
			}
		}
	}
	
	//Always called once after surfaceCreated. Tell the GameThread the actual size
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused on even stopped.
	 */
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		boolean retry = true;
		if(thread!=null) {
			thread.mRun = false;
		}
		
		//join the thread with this thread
		while (retry) {
			try {
				if(thread!=null) {
					thread.join();
				}
				retry = false;
			} 
			catch (InterruptedException e) {
				//naugthy, ought to do something...
			}
		}
	}
	
	/*
	 * Accelerometer
	 */

	public void startSensor(SensorManager sm) {
		sm.registerListener(this.sensorAccelerometer, 
				sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),	
				SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void removeSensor(SensorManager sm) {
		sm.unregisterListener(this.sensorAccelerometer);
		this.sensorAccelerometer = null;
	}
	
}
