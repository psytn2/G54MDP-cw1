package com.example.g54mdp_eggtimer;

import java.util.ArrayList;
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
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listView;

	private TimersAdapter timersAdapter;

	private ArrayList<TimerData> timerDataArr;

	private MyReceiver myReceiver;

	private Messenger messenger;

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

		if (seconds == 0 || findIndex(name) != -1 || name.equals(""))
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

	public int findIndex(String timerName) {
		int index = -1;
		for (int i = 0; i < timerDataArr.size(); i++) {
			if (timerDataArr.get(i).getName().equals(timerName)) {
				index = i;
			}
		}
		return index;
	}

	public void clearNameEditText(View v) {
		final EditText timerNameET = (EditText) findViewById(R.id.timerNameEditText);
		timerNameET.setText("");
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
		hoursNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		NumberPicker minutesNP = (NumberPicker) findViewById(R.id.minutesNumberPicker);
		minutesNP.setMaxValue(59);
		minutesNP.setMinValue(0);
		minutesNP.setWrapSelectorWheel(true);
		minutesNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		NumberPicker secondsNP = (NumberPicker) findViewById(R.id.secondsNumberPicker);
		secondsNP.setMaxValue(59);
		secondsNP.setMinValue(0);
		secondsNP.setWrapSelectorWheel(true);
		secondsNP.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		Log.d("MainActivity", "SetNumberPickerBounds MainActivity");
	}

	private void setListViewProperties() {
		TextView listViewTitle = new TextView(getBaseContext());
		listViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		listViewTitle.setTextColor(getResources().getColor(R.color.black));
		listViewTitle.setText("Timers");
		listView.addHeaderView(listViewTitle);
		listView.setAdapter(timersAdapter);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long arg3) {
				final String timerName = ((TextView) view.findViewById(R.id.timerName)).getText().toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Do you want to stop the timer?").setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								int index = findIndex(timerName);
								if (index == -1) {
									dialog.cancel();
								}
								else {
									Message message = Message.obtain(null, TimerService.CANCEL_EGGTIMER, 0, 0);

									MyParcelable parcel = new MyParcelable();
									parcel.eggTimerName = timerName;
									parcel.seconds = 0;

									Bundle bundle = new Bundle();
									bundle.putParcelable("cancelTimer", (Parcelable) parcel);
									message.setData(bundle);

									try {
										Log.d("MainActivity", "CancelEggTimer");
										messenger.send(message);
									}
									catch (RemoteException e) {
										Log.d("MainActivity", "CancelEggTimer RemoteException");
										e.printStackTrace();
									}

									timerDataArr.remove(index);
									timersAdapter.updateData(timerDataArr);

									// Display toast when timer is cancelled
									Toast toast = Toast.makeText(getApplicationContext(), "Timer: " + timerName
											+ " was cancelled", Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.BOTTOM, 0, 0);
									toast.show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
				return false;
			}
		});
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d("MainActivity", "onServiceConnected");
			messenger = new Messenger(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("MainActivity", "onServiceDisconnected");
			messenger = null;

		}

	};

}
