package com.alexdiru.criticalerror;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ToolsBitmapHelper {
	
	public static Bitmap loadBitmap(int id) {
		return loadBitmap(id, 0);
	}
	
	public static Bitmap loadBitmap(int id, int rotationDegrees) {
		Bitmap unscaled  = BitmapFactory.decodeResource(GameThread.mContext.getResources(), id);
		GameThread.mScaleMatrix.postRotate(rotationDegrees);
		Bitmap scaled = Bitmap.createBitmap(unscaled,0,0,unscaled.getWidth(), unscaled.getHeight(), GameThread.mScaleMatrix, true);
		GameThread.mScaleMatrix.postRotate(0);	
		return scaled;
	}
	
	private static Bitmap[] bitmaps;
	private static int i;
	
	//Uses multithreading to load bitmaps
	public static Bitmap[] loadBitmaps(final int[] ids) {
		
		bitmaps = new Bitmap[ids.length];
		
		//Thread for each bitmap
		Thread[] threads = new Thread[ids.length];
		
		for (i = 0; i < ids.length; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					bitmaps[i] = loadBitmap(ids[i]);
				}
			});
			threads[i].start();
		}
		
		for (int j = 0; j < ids.length; j++)
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		Bitmap[] localBitmaps = new Bitmap[ids.length];
		System.arraycopy(bitmaps, 0, localBitmaps, 0, bitmaps.length);
		bitmaps = null;
		return localBitmaps;
	}
}
