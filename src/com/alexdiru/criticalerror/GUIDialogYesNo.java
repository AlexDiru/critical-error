package com.alexdiru.criticalerror;

import android.app.AlertDialog;
import android.content.DialogInterface;

public abstract class GUIDialogYesNo implements GUIDialog {
	protected GameActivity mGameActivity;
	protected String mYes;
	protected String mNo;

	public GUIDialogYesNo(GameActivity gameActivity) {
		mYes = gameActivity.getBaseContext().getString(R.string.yes);
		mNo = gameActivity.getBaseContext().getString(R.string.no);
		mGameActivity = gameActivity;
	}

	public void createAndShow(final String dialogTitle, final String dialogMessage) {
		//The dialog must be run on the UI thread
		mGameActivity.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(mGameActivity);
				
				//Yes button - perform the positive action which must be defined in the subclass
				builder.setPositiveButton(mYes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						positiveAction();
					}
				});
				
				//No button - perform the negative action which must be defined in the subclass
				builder.setNegativeButton(mNo, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						negativeAction();
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

	public void neutralAction() {
	}
}
