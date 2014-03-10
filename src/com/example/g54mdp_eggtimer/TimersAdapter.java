package com.example.g54mdp_eggtimer;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimersAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<TimerData> listData;

	protected LayoutInflater inflater;

	public TimersAdapter(Context context, ArrayList<TimerData> data) {
		this.listData = data;
		this.mContext = context;
		this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView;

		if (convertView == null) {
			this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.list_item, parent, false);
		}
		else {
			itemView = convertView;
		}

		TextView timerName = (TextView) itemView.findViewById(R.id.timerName);
		TextView secondsLeft = (TextView) itemView.findViewById(R.id.secondsLeft);

		String name = listData.get(position).getName();
		long seconds = listData.get(position).getSeconds();
		long minutes = seconds / 60;
		long hours = minutes / 60;

		minutes -= hours * 60;
		seconds -= (minutes * 60) + (hours * 60 * 60);
		timerName.setText(name);
		secondsLeft.setText(((hours > 0) ? hours + " hours " : "") + ((minutes > 0) ? minutes + " minutes " : "")
				+ seconds + " seconds");

		return itemView;
	}

	public void updateData(ArrayList<TimerData> data) {
		listData = data;
		notifyDataSetChanged();
	}

}
