package com.alexdiru.criticalerror;

import android.app.AlertDialog;
import android.content.DialogInterface;

public abstract class GUIDialogAlert  implements GUIDialog {
	protected GameActivity mGameActivity;
	protected String mOK;

	public GUIDialogAlert(GameActivity gameActivity) {
		mOK = gameActivity.getBaseContext().getString(R.string.ok);
		mGameActivity = gameActivity;
	}
	
	public void positiveAction() {
	}
	
	public void negativeAction() {
	}
	
	public void neutralAction() {
	}

	public void createAndShow(final String dialogTitle, final String dialogMessage) {
		//The dialog must be run on the UI thread
		mGameActivity.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(mGameActivity);
				
				builder.setNeutralButton(mOK, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				//Set the title and body of the dialog
				builder.setTitle((CharSequence) dialogTitle);
				builder.setMessage((CharSequence) dialogMessage);
				
				//Create and show the dialog
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}
}