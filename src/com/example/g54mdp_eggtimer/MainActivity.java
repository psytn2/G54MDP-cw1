package com.example.g54mdp_eggtimer;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("MainActivity", "Oncreate MainActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setNumberPickersBounds();

		Button startTimerButton = (Button) findViewById(R.id.startTimerButton);
		startTimerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
				NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
				int seconds = (hoursNP.getValue() * 60 * 60) + (minutesNP.getValue() * 60);
				if (seconds == 0)
					alertWrongInput();

			}
		});
		
		final EditText timerNameET = (EditText) findViewById(R.id.timerNameEditText);
		timerNameET.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				timerNameET.setText("");
				
			}
		});
	}

	protected void alertWrongInput() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Wrong input");

		alertDialogBuilder.setMessage("The time inserted is invalid").setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	private void setNumberPickersBounds() {
		Log.i("MainActivity", "SetNumberPickerBounds MainActivity");
		NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		hoursNP.setMaxValue(23);
		hoursNP.setMinValue(0);
		hoursNP.setWrapSelectorWheel(true);

		NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		minutesNP.setMaxValue(59);
		minutesNP.setMinValue(0);
		minutesNP.setWrapSelectorWheel(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
