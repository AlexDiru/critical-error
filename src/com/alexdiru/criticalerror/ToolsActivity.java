package com.alexdiru.criticalerror;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ToolsActivity {

	private static Context mContext;
	public static Activity mActivity;
	
	public static void setActivity(Activity activity, Context context) {
		mActivity = activity;
		mContext = context;
	}
	
	public static void switchActivity(Class<?> activity) {

		Intent i = new Intent(mContext, activity);
		
		//Note down the sender activity so we can go back to it
		i.putExtra("SenderActivity", mActivity.getClass().getName().toString());
		
		try {
			mActivity.startActivity(i);
		} catch (ActivityNotFoundException ex) {
			Log.d("switchActivity", "Add activity " + activity.getName() + " to AndroidManifest.xml");
		}
	}

}
