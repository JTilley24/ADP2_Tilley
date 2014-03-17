package com.jtilley.nextrip;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AddItemFragment extends Fragment{
ArrayList<String> storesArray;
Spinner storeSpinner;
TextView storeNameLabel;
EditText storeNameInput;
LinearLayout storeNameView;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_add_item, container);
		
		storeSpinner = (Spinner) view.findViewById(R.id.storeSpinner);
		storeNameLabel = (TextView) view.findViewById(R.id.storeNameLabel);
		storeNameInput = (EditText) view.findViewById(R.id.storeName);
		storeNameView = (LinearLayout) view.findViewById(R.id.storeNameView);
		
		
		getStores();
		return view;
	}

	public void getStores(){
		SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", 0);
		String storesString = prefs.getString("saved_stores", null);
		storesArray = new ArrayList<String>();
		
		if(storesString != null){
			try {
				JSONArray storesJSON = new JSONArray(storesString);
				for(int i=0;i < storesJSON.length(); i++){
					JSONObject store = storesJSON.getJSONObject(i);
					String storeName = store.getString("name");
					Log.i("STORE", storeName);
					storesArray.add(storeName);	
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		storesArray.add("....New");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, storesArray);
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		
		storeSpinner.setAdapter(adapter);
		storeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(position == (storesArray.size() - 1)){
					storeNameView.setVisibility(View.VISIBLE);
					//storeNameLabel.setVisibility(View.VISIBLE);
					//storeNameInput.setVisibility(View.VISIBLE);
				}else{
					storeNameView.setVisibility(View.GONE);
					//storeNameInput.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
