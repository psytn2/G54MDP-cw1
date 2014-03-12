package com.example.g54mdp_eggtimer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class acts as the timer of the Application, it extends the CountDownTimer, allowing multiple timers running on
 * the background at the same time
 * 
 * @author Tai Nguyen Bui (psytn2)
 */
public class EggTimer extends CountDownTimer {

	private static final int SECOND_IN_MILLIS = 1000;

	public static final String TIMER_FINISHED = "TIMER_FINISHED";

	private Messenger messenger;

	String name;

	public EggTimer(String eggTimerName, long millisInFuture, long countDownInterval, Messenger messenger) {
		super(millisInFuture, countDownInterval);
		this.name = eggTimerName;
		this.messenger = messenger;
		this.start();

		Log.d("EggTimer", this.name + " EggTimer started");
	}

	@Override
	public void onFinish() {
		Message message = Message.obtain(null, TimerService.FINISHED_EGGTIMER, 0, 0);

		MyParcelable parcel = new MyParcelable();
		parcel.eggTimerName = this.name;
		parcel.seconds = 0;

		Bundle bundle = new Bundle();
		bundle.putParcelable("finishedTimer", (Parcelable) parcel);
		message.setData(bundle);
		try {
			Log.d("EggTimer", this.name + " EggTimer finished");
			messenger.send(message);
		}
		catch (RemoteException e) {
			Log.d("EggTimer", "onTick RemoteException");
			e.printStackTrace();
		}

	}

	@Override
	public void onTick(long millisUntilFinished) {
		Message message = Message.obtain(null, TimerService.RECEIVE_TIMER_DATA, 0, 0);

		MyParcelable parcel = new MyParcelable();
		parcel.eggTimerName = this.name;
		parcel.seconds = millisUntilFinished / SECOND_IN_MILLIS;

		Bundle bundle = new Bundle();
		bundle.putParcelable("timerInfo", (Parcelable) parcel);
		message.setData(bundle);

		try {
			Log.d("EggTimer", this.name + ": onTick sending data to Service");
			messenger.send(message);
		}
		catch (RemoteException e) {
			Log.d("EggTimer", "onTick RemoteException");
			e.printStackTrace();
		}
	}

}
