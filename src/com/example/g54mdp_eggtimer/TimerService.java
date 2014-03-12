package com.example.g54mdp_eggtimer;

import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class is in charge of receiving and organising the information.
 * 
 * @author Tai Nguyen Bui (psytn2)
 */
public class TimerService extends Service {

	private Messenger messenger;

	HashMap<String, EggTimer> timers = new HashMap<String, EggTimer>();

	private static final int SECOND_IN_MILLIS = 1000;

	private static final int COUNTDOWN_INTERVAL = 1000;

	public static final int START_EGGTIMER = 0;

	public static final int RECEIVE_TIMER_DATA = 1;

	public static final int CANCEL_EGGTIMER = 2;

	public static final int PAUSE_EGGTIMER = 3;

	public static final int RESUME_EGGTIMER = 4;

	public static final int FINISHED_EGGTIMER = 5;

	public static final String UPDATE_TIMER_INFO = "UPDATE_TIMER_INFO";

	public static final String STOP_TIMER = "STOP_TIMER";

	public static final String TIMER_NAME = "TIMER_NAME";

	public static final String SECONDS_LEFT = "SECONDS_LEFT";

	public static final String REMOVE_EGGTIMER = "REMOVE_EGGTIMER";

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			MyParcelable parcel;
			Intent intent;
			switch (msg.what) {

			case START_EGGTIMER:
				parcel = msg.getData().getParcelable("startTimeParcel");
				startEggTimer(parcel.eggTimerName, parcel.seconds);
				Log.d("TimerService", "MyHandler startEggTimer " + parcel.eggTimerName + " " + parcel.seconds);

				// Display toast when timer starts
				Toast toast = Toast.makeText(getApplicationContext(), "Timer: " + parcel.eggTimerName + " started",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();

				break;

			case RECEIVE_TIMER_DATA:
				parcel = msg.getData().getParcelable("timerInfo");

				intent = new Intent();
				intent.setAction(UPDATE_TIMER_INFO);
				intent.putExtra(TIMER_NAME, parcel.eggTimerName);
				intent.putExtra(SECONDS_LEFT, parcel.seconds);
				sendBroadcast(intent);

				Log.d("TimerService", "MyHandler RECEIVE_TIMER_DATA " + parcel.eggTimerName + " " + parcel.seconds);
				break;

			case CANCEL_EGGTIMER:
				parcel = msg.getData().getParcelable("cancelTimer");
				timers.get(parcel.eggTimerName).cancel();
				timers.remove(parcel.eggTimerName);
				break;

			case PAUSE_EGGTIMER:
				break;

			case RESUME_EGGTIMER:
				break;
			case FINISHED_EGGTIMER:
				parcel = msg.getData().getParcelable("finishedTimer");

				// remove finished eggtimer from hashmap
				timers.remove(parcel.eggTimerName);
				intent = new Intent();
				intent.setAction(UPDATE_TIMER_INFO);
				intent.putExtra(TIMER_NAME, parcel.eggTimerName);
				intent.putExtra(SECONDS_LEFT, parcel.seconds);
				sendBroadcast(intent);

				Log.d("TimerService", "MyHandler FINISHED_EGGTIMER ");
				break;
			default:
				super.handleMessage(msg);
			}
		}

	}

	@Override
	public void onCreate() {
		messenger = new Messenger(new MyHandler());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	public void startEggTimer(String eggTimerName, long seconds) {
		timers.put(eggTimerName, new EggTimer(eggTimerName, seconds * SECOND_IN_MILLIS, COUNTDOWN_INTERVAL,
				this.messenger));
	}

	@Override
	public void onDestroy() {
		Log.d("TimerService", "onDestroy");
		timers.clear();
		timers = null;
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d("TimerService", "onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("TimerService", "onUnbind");
		return super.onUnbind(intent);
	}
}
