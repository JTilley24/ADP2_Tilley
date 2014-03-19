package com.jtilley.nextrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter {
private Activity activity;
private static JSONArray historyArray;
private static LayoutInflater inflater = null;

	public HistoryListAdapter(Activity act, JSONArray array){
		activity = act;
		historyArray = array;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return historyArray.length();
	}

	@Override
	public Object getItem(int posistion) {
		// TODO Auto-generated method stub
		return posistion;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = itemView;
		if(view == null){
			view = inflater.inflate(R.layout.history_list_row, null);
		}
		
		TextView historyName = (TextView) view.findViewById(R.id.historyName);
		TextView historyStore = (TextView) view.findViewById(R.id.historyStore);
		TextView historyDate = (TextView) view.findViewById(R.id.historyDate);
		
		try {
			JSONObject itemObj = historyArray.getJSONObject(position);
			historyName.setText(itemObj.getString("name"));
			historyStore.setText("at: " + itemObj.getString("store"));
			historyDate.setText(itemObj.getString("day"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;
	}

}
