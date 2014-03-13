package com.jtilley.nextrip;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StoresFragment extends Fragment{
ListView storesList;
TextView noStores;
String storesString;
ArrayList<String> storesArray;

	public interface OnStoresListClicked{
		void displaySearch(Boolean tabSearch);
	}
	
	private OnStoresListClicked parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnStoresListClicked){
			parentActivity = (OnStoresListClicked) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnStoresListClicked");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_main, container, false);
		
		storesList = (ListView) view.findViewById(R.id.storeList);
		noStores = (TextView) view.findViewById(R.id.noStores);
	
		displayStoresList();
		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		parentActivity.displaySearch(false);
		displayStoresList();
	}

	public void displayStoresList(){
		SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", 0);
		storesString = prefs.getString("saved_stores", null);
		if(storesString != null){
			try {
				storesArray = new ArrayList<String>();
				JSONArray stores = new JSONArray(storesString);
				for(int i=0; i< stores.length();i++){
					JSONObject storeObj = (JSONObject) stores.get(i);
					String storeName = storeObj.getString("name");
					storesArray.add(storeName);
				}
				noStores.setVisibility(View.INVISIBLE);
				storesList.setVisibility(View.VISIBLE);
				storesList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, storesArray));
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			noStores.setVisibility(View.VISIBLE);
			storesList.setVisibility(View.GONE);
		}
	}
	
}
