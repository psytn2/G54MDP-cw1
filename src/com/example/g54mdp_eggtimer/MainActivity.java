package com.example.g54mdp_eggtimer;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

public class MainActivity extends Activity {
	private int numberOfTimers = 0;

	private ListView listView;

	private TimersAdapter timersAdapter;

	private ArrayList<TimerData> timerDataArr;

	private HashMap<String, TimerData> dataHashMap = new HashMap<String, TimerData>();

	MyReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("MainActivity", "Oncreate MainActivity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setNumberPickersBounds();

		listView = (ListView) findViewById(R.id.timerListView);
		timerDataArr = new ArrayList<TimerData>();
		timersAdapter = new TimersAdapter(MainActivity.this, timerDataArr);

		listView.setAdapter(timersAdapter);

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
	protected void onStart() {
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TimerService.UPDATE_TIMER_INFO);
		registerReceiver(myReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(myReceiver);
		super.onStop();
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

		if (seconds == 0 || dataHashMap.containsKey(name))
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
			}
			catch (RemoteException e) {
				Log.d("MainActivity", "StartEggTimer RemoteException");
				e.printStackTrace();
			}
		}

	}

	public void stopEggTimer(View v) {
		if (dataHashMap.size() != 0) {
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

		alertDialogBuilder.setMessage("The time or name inserted is invalid").setCancelable(false)
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

	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// EditText test = (EditText) findViewById(R.id.testTextField);
			// test.setText("Timer: " + intent.getStringExtra("TIMER_NAME") + " " + intent.getLongExtra("SECONDS_LEFT",
			// 0)
			// + " seconds left");
			String timerName = intent.getStringExtra("TIMER_NAME");
			Long timeLeft = intent.getLongExtra("SECONDS_LEFT", 0);
			TimerData temptd = new TimerData(timerName, timeLeft);
			int index = findIndex(timerName);

			if (index == -1) {
				timerDataArr.add(temptd);
			}
			else {
				timerDataArr.get(index).setSeconds(timeLeft);
			}
			timersAdapter.updateData(timerDataArr);
			System.out.println(timerDataArr.size() + timerName);
			dataHashMap.put(timerName, temptd);

			Log.d("MainActivity", dataHashMap.size() + " MyReceiver " + timerName + " " + timeLeft);
		}

		public int findIndex(String timerName) {
			int index = -1;
			for (int i = 0; i < timerDataArr.size(); i++) {
				if (timerDataArr.get(i).getName().equals(timerName)) {
					index = i;
				}
			}
			return index;

		}
	}
}
