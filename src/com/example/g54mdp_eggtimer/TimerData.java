package com.example.g54mdp_eggtimer;

/**
 * This class was created to gather information about a Timer in order to facilitate the transaction of data
 * 
 * @author Tai Nguyen Bui (psytn2)
 */
public class TimerData {
	String name;

	long seconds;

	public TimerData(String name, long seconds) {
		this.name = name;
		this.seconds = seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	public String getName() {
		return this.name;
	}

	public long getSeconds() {
		return this.seconds;
	}
}
