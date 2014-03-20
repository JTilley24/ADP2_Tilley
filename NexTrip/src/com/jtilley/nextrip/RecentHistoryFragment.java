package com.jtilley.nextrip;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RecentHistoryFragment extends Fragment{
ListView historyList;
TextView noHistory;
HistoryListAdapter adapter;

	public interface OnHistoryItemsSaved{
		JSONArray getHistory();
	}
	
	private OnHistoryItemsSaved parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnHistoryItemsSaved){
			parentActivity = (OnHistoryItemsSaved) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnHistoryItemsSaved");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_recent_history, container);
		
		historyList = (ListView) view.findViewById(R.id.historyList);
		noHistory = (TextView) view.findViewById(R.id.noHistory);
	
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		displayHistory();
	}
	
	//Display History data in ListView
	public void displayHistory(){
		if(parentActivity.getHistory().length() == 0){
			historyList.setVisibility(View.GONE);
			noHistory.setVisibility(View.VISIBLE);
		}else{
			historyList.setVisibility(View.VISIBLE);
			noHistory.setVisibility(View.GONE);
			adapter = new HistoryListAdapter(getActivity(), parentActivity.getHistory());
		
			historyList.setAdapter(adapter);
		}
	}
}
