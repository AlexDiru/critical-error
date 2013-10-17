package com.alexdiru.criticalerror;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ToolsTextDrawer.mTextSize = 22* getResources().getDisplayMetrics().density + 0.5f;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.activity_menu);
        ToolsActivity.setActivity(this, getBaseContext()); 
		
		addButtonListeners();

	}
	
	private void addButtonListeners() {

		//Campaign Button
		Button campaignButton = (Button)findViewById(R.id.button1);
		campaignButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ToolsGameState.mGameMode = ToolsGameState.GAMEMODE_CAMPAIGN;
				ToolsGameState.mGameState = ToolsGameState.STATE_RUNNING;
				ToolsActivity.switchActivity(GameActivity.class);
			}
		});
		
		//Freeplay Button
		Button freeplayButton = (Button)findViewById(R.id.button2);
		freeplayButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ToolsGameState.mGameMode = ToolsGameState.GAMEMODE_FREEPLAY;
				ToolsGameState.mGameState = ToolsGameState.STATE_RUNNING;
				ToolsActivity.switchActivity(GameActivity.class);
			}
		});
		
		
		//Shop Button
		Button shopButton = (Button)findViewById(R.id.button3);
		shopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ToolsGameState.mGameState = ToolsGameState.STATE_SHOP;
				ToolsActivity.switchActivity(GameActivity.class);
			}
		});
		
		//Exit Button
		Button exitButton = (Button)findViewById(R.id.button4);
		final Activity activity = this;
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            activity.finish();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
			}
		});
	}

}
