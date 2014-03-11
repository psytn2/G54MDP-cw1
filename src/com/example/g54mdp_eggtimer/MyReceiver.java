package com.example.g54mdp_eggtimer;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

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

			timersAdapter.updateData(timerDataArr);
		}

		Log.d("MyReceiver", "onReceive " + timerName + " " + timeLeft);
	}

	private void ringAlarm(Context context, String timerName) {

		// find the alarm to play, if alarm is not available then try notification and ringtone
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				// alert backup is null, using 2nd backup
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alert);
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		android.os.Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		long[] pattern = { 200, 1000, 300, 1000, 200, 1000, 300, 1000 };

		// play the ringtone if not mute or vibrator
		if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			ringtone.play();
		}

		// vibrate on alarm
		vibrator.vibrate(pattern, 1);
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
