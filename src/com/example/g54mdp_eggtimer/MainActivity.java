package com.example.g54mdp_eggtimer;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MainActivity extends Activity {
	private int numberOfTimers = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("MainActivity", "Oncreate MainActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setNumberPickersBounds();

		final EditText timerNameET = (EditText) findViewById(R.id.timerNameEditText);

		timerNameET.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timerNameET.setText("");
			}
		});

		this.bindService(new Intent(this, TimerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Messenger messenger;

	public void startEggTimer(View v) {
		final EditText timerNameET = (EditText) findViewById(R.id.timerNameEditText);
		NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);

		String name = timerNameET.getText().toString();
		int seconds = (hoursNP.getValue() * 60 * 60) + (minutesNP.getValue() * 60);

		if (seconds == 0)
			alertWrongInput();

		else {
			Message message = Message.obtain(null, TimerService.START_EGGTIMER, 0, 0);

			MyParcelable parcel = new MyParcelable();
			parcel.eggTimerName = name;
			parcel.seconds = seconds;

			Bundle bundle = new Bundle();
			bundle.putParcelable("startTimeParcel", (Parcelable) parcel);
			message.setData(bundle);

			try {
				Log.d("MainActivity", "StartEggTimer message sent");
				messenger.send(message);
				numberOfTimers++;
			}
			catch (RemoteException e) {
				Log.d("MainActivity", "StartEggTimer RemoteException");
				e.printStackTrace();
			}
		}

	}

	public void stopEggTimer(View v) {
		if (numberOfTimers != 0) {
			Message message = Message.obtain(null, TimerService.STOP_EGGTIMER, 0, 0);

			MyParcelable parcel = new MyParcelable();
			parcel.eggTimerName = "tai";
			parcel.seconds = 0;

			Bundle bundle = new Bundle();
			bundle.putParcelable("stopTimerParcel", (Parcelable) parcel);
			message.setData(bundle);

			try {
				Log.d("MainActivity", "StopEggTimer message sent");
				messenger.send(message);
				numberOfTimers--;
			}
			catch (RemoteException e) {
				Log.d("MainActivity", "StopEggTimer RemoteException");
				e.printStackTrace();
			}
		}

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
		Log.d("MainActivity", "SetNumberPickerBounds MainActivity");
		NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		hoursNP.setMaxValue(23);
		hoursNP.setMinValue(0);
		hoursNP.setWrapSelectorWheel(true);

		NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		minutesNP.setMaxValue(59);
		minutesNP.setMinValue(0);
		minutesNP.setWrapSelectorWheel(true);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.d("MainActivity", "onServiceConnected");
			messenger = new Messenger(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.d("MainActivity", "onServiceDisconnected");
			messenger = null;

		}

	};
}
