package com.jtilley.nextrip;



import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class StoreDetailsFragment extends Fragment{
ListView itemList;
TextView noItems;
ItemListAdapter adapter;

	public interface OnItemSelected{
		JSONArray getItems();
		void displayItemsContextual();
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
		noItems = (TextView) view.findViewById(R.id.noItems);
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		displayItems();
	}
	
	public void displayItems(){
		if(parentActivity.getItems().length() == 0){
			itemList.setVisibility(View.GONE);
			noItems.setVisibility(View.VISIBLE);
		}else{
			itemList.setVisibility(View.VISIBLE);
			noItems.setVisibility(View.GONE);
			adapter = new ItemListAdapter(getActivity(), parentActivity.getItems());
			itemList.setAdapter(adapter);
		}
	}
	
	public ArrayList<String> getSelectedItems(){
		ArrayList<String> selecteditems = adapter.getSelectedItems();
		
		return selecteditems;
	}
	public void setActionBar(){
		parentActivity.displayItemsContextual();
	}

}
