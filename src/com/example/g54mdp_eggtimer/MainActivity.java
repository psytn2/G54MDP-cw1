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
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

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
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		listView = (ListView) findViewById(R.id.timerListView);
		timerDataArr = new ArrayList<TimerData>();
		timersAdapter = new TimersAdapter(MainActivity.this, timerDataArr);

		myReceiver = new MyReceiver(this.timerDataArr, timersAdapter);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TimerService.UPDATE_TIMER_INFO);
		registerReceiver(myReceiver, intentFilter);

		setListViewProperties();

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
	protected void onDestroy() {
		unregisterReceiver(myReceiver);
		super.onDestroy();
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
		final NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		final NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		final NumberPicker secondsNP = (NumberPicker) findViewById(R.id.secondsNumberPicker);

		// Hide keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(timerNameET.getWindowToken(), 0);

		String name = timerNameET.getText().toString();
		int seconds = (hoursNP.getValue() * 60 * 60) + (minutesNP.getValue() * 60) + secondsNP.getValue();

		if (seconds == 0 || dataHashMap.containsKey(name) || name.equals(""))
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
			resetInputValues();
		}

	}

	private void resetInputValues() {
		final EditText timerNameET = (EditText) findViewById(R.id.timerNameEditText);
		final NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		final NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		final NumberPicker secondsNP = (NumberPicker) findViewById(R.id.secondsNumberPicker);
		timerNameET.setText("");
		hoursNP.setValue(0);
		minutesNP.setValue(0);
		secondsNP.setValue(0);
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

		NumberPicker hoursNP = (NumberPicker) findViewById(R.id.hoursNumberPicker);
		hoursNP.setMaxValue(23);
		hoursNP.setMinValue(0);
		hoursNP.setWrapSelectorWheel(true);

		NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		minutesNP.setMaxValue(59);
		minutesNP.setMinValue(0);
		minutesNP.setWrapSelectorWheel(true);

		NumberPicker secondsNP = (NumberPicker) findViewById(R.id.secondsNumberPicker);
		secondsNP.setMaxValue(59);
		secondsNP.setMinValue(0);
		secondsNP.setWrapSelectorWheel(true);
		Log.d("MainActivity", "SetNumberPickerBounds MainActivity");
	}

	private void setListViewProperties() {
		TextView listViewTitle = new TextView(getBaseContext());
		listViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		listViewTitle.setTextColor(getResources().getColor(R.color.black));
		listViewTitle.setText("Timers");
		listView.addHeaderView(listViewTitle);
		listView.setAdapter(timersAdapter);
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
