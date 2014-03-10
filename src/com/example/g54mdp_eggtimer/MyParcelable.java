package com.example.g54mdp_eggtimer;

import android.os.Parcel;
import android.os.Parcelable;

public class MyParcelable implements Parcelable {

	String eggTimerName;

	long seconds;

	public MyParcelable() {
	}

	public MyParcelable(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		this.eggTimerName = in.readString();
		this.seconds = in.readInt();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.eggTimerName);
		out.writeLong(this.seconds);
	}

	public static final Parcelable.Creator<MyParcelable> CREATOR = new Parcelable.Creator<MyParcelable>() {

		@Override
		public MyParcelable createFromParcel(Parcel in) {
			return new MyParcelable(in);
		}

		@Override
		public MyParcelable[] newArray(int size) {
			return new MyParcelable[size];
		}
	};

}
