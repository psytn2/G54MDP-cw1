package com.example.g54mdp_eggtimer;

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
