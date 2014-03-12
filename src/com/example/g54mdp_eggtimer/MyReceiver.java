package com.example.g54mdp_eggtimer;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

/**
 * This class receives the intents sent by the broadcasters in order to process the information and act in consequence,
 * either playing the alarm or removing a finished timer
 * 
 * @author Tai Nguyen Bui (psytn2)
 */
public class MyReceiver extends BroadcastReceiver {
	private ArrayList<TimerData> timerDataArr;

	private TimersAdapter timersAdapter;

	public MyReceiver(ArrayList<TimerData> timerDataArr, TimersAdapter timersAdapter) {
		this.timerDataArr = timerDataArr;
		this.timersAdapter = timersAdapter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String timerName = intent.getStringExtra("TIMER_NAME");
		Long timeLeft = intent.getLongExtra("SECONDS_LEFT", 0);

		if (intent.getAction().equals("UPDATE_TIMER_INFO")) {

			int index = findIndex(timerName);

			if (timeLeft == 0) {
				timerDataArr.remove(index);
				ringAlarm(context, timerName);
			}
			else {
				if (index == -1) {
					timerDataArr.add(new TimerData(timerName, timeLeft));
				}
				else {
					timerDataArr.get(index).setSeconds(timeLeft);
				}
			}

		}

		timersAdapter.updateData(timerDataArr);

		Log.d("MyReceiver", "onReceive " + timerName + " " + timeLeft);
	}

	/**
	 * Ring the alarm if not in mute and turn on the vibrator when the timer finishes
	 * 
	 * @param context
	 * @param timerName name of the timer
	 */
	private void ringAlarm(Context context, String timerName) {
		RingtoneManager ringtoneManager = new RingtoneManager(context);

		// find the alarm to play, if alarm is not available then try notification and ringtone
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		final Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alert);
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		final android.os.Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		long[] pattern = { 200, 1000, 300, 1000, 200, 1000, 300, 1000 };

		// play the ringtone if not mute or vibrator
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			ringtoneManager.stopPreviousRingtone();
			ringtone.play();
		}

		// vibrate on alarm
		vibrator.vibrate(pattern, 1);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Timer: " + timerName + " finished");

		alertDialogBuilder.setMessage("Stop alarm").setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						vibrator.cancel();
						ringtone.stop();
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	/**
	 * This method finds the index of the given timer in the ArrayList
	 * 
	 * @param timerName name of the timer
	 * @return the index of the timer
	 */
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
