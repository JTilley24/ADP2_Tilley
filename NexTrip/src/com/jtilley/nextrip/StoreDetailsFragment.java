package com.jtilley.nextrip;



import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StoreDetailsFragment extends Fragment{
ListView itemList;
ItemListAdapter adapter;

	public interface OnItemSelected{
		JSONArray getItems();
		
	}
	
	private OnItemSelected parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnItemSelected){
			parentActivity = (OnItemSelected) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnItemSelected");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_store_details, container);
		
		itemList = (ListView) view.findViewById(R.id.itemsList);
		
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		displayItems();
	}
	
	public void displayItems(){
		adapter = new ItemListAdapter(getActivity(), parentActivity.getItems());
		itemList.setAdapter(adapter);
	}
	
	public ArrayList<String> getSelectedItems(){
		ArrayList<String> selecteditems = adapter.getSelectedItems();
		
		if(selecteditems.size() > 0){
			Log.i("Selected", selecteditems.get(1));
		}
		return selecteditems;
	}

}
