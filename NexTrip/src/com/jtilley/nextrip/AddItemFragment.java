package com.jtilley.nextrip;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AddItemFragment extends Fragment{
ArrayList<String> storesArray;
SharedPreferences prefs;
ImageView itemPic;
String imageFile;
Spinner storeSpinner;
EditText storeNameInput;
LinearLayout storeNameView;
EditText nameInput;
EditText priceInput;
EditText detailsInput;
String selectedStore;
JSONArray storesJSON;

	public interface OnSaveItem{
		LatLng getLocation();
		void openCamera();
	}

	private OnSaveItem parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnSaveItem){
			parentActivity = (OnSaveItem) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnSaveItem");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_add_item, container);
		
		storeSpinner = (Spinner) view.findViewById(R.id.storeSpinner);
		storeNameInput = (EditText) view.findViewById(R.id.storeName);
		storeNameView = (LinearLayout) view.findViewById(R.id.storeNameView);
		
		itemPic = (ImageView) view.findViewById(R.id.itemPic);
		itemPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				parentActivity.openCamera();
			}
		});
		
		nameInput = (EditText) view.findViewById(R.id.nameInput);
		nameInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//Validate Name Input
				if(nameInput.getText().length() == 0){
					nameInput.setError("Enter Name of Item!");
				}else{
					nameInput.setError(null);
				}
			}
		});
		priceInput = (EditText) view.findViewById(R.id.priceInput);
		detailsInput = (EditText) view.findViewById(R.id.detailsInput);
		
		prefs = getActivity().getSharedPreferences("user_prefs", 0);
		
		getStores();
		
		imageFile = "";
		
		return view;
	}
	
	//Get Stores Array for Spinner
	public void getStores(){
		String storesString = prefs.getString("saved_stores", null);
		storesArray = new ArrayList<String>();
		storesJSON = new JSONArray();
		if(storesString != null){
			try {
				storesJSON = new JSONArray(storesString);
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
		storesArray.add("New...");
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
				}else{
					storeNameView.setVisibility(View.GONE);
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//Save Item to Selected Store
	public void saveItem(){
		Log.i("SAVE", "Item will be Saved!");
		if(nameInput.getText().length() == 0){
			nameInput.setError("Enter Name of Item!");
		}else if(!(storeSpinner.getSelectedItem().toString().equalsIgnoreCase("New..."))){
			selectedStore = storeSpinner.getSelectedItem().toString();
			int index = 0;
			JSONObject saveStore = null;
			for(int i=0; i < storesJSON.length(); i++){
				try {
					JSONObject tempStore = storesJSON.getJSONObject(i);
					if(tempStore.getString("name").equalsIgnoreCase(selectedStore)){
						index = i;
						saveStore = tempStore;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(saveStore != null){
				JSONObject itemObj = new JSONObject();
				try {
					itemObj.put("name", nameInput.getText().toString());
					itemObj.put("price", priceInput.getText().toString());
					itemObj.put("details", detailsInput.getText().toString());
					itemObj.put("image", imageFile);
					JSONArray itemArray = new JSONArray();
					if(saveStore.has("items")){
						itemArray = saveStore.getJSONArray("items");
					}
					itemArray.put(itemObj);
					saveStore.put("items", itemArray);
					storesJSON.put(index, saveStore);
					SharedPreferences.Editor editPrefs = prefs.edit();
					editPrefs.putString("saved_stores", storesJSON.toString());
					editPrefs.commit();
					Log.i("JSON", storesJSON.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}else{
			if(storeNameInput.getText().length() == 0){
				storeNameInput.setError("Please Enter Store Name!");
			}else{
				storeNameInput.setError(null);
				JSONObject saveStore = new JSONObject();
				String newStoreName = storeNameInput.getText().toString();
				JSONObject itemObj = new JSONObject();
				try {
					itemObj.put("name", nameInput.getText().toString());
					itemObj.put("price", priceInput.getText().toString());
					itemObj.put("details", detailsInput.getText().toString());
					itemObj.put("image", imageFile);
					JSONArray itemArray = new JSONArray();
					itemArray.put(itemObj);
					saveStore.put("name", newStoreName);
					saveStore.put("items", itemArray);
					LatLng location = parentActivity.getLocation();
					saveStore.put("lat", location.latitude);
					saveStore.put("lng", location.longitude);
					storesJSON.put(saveStore);
					SharedPreferences.Editor editPrefs = prefs.edit();
					editPrefs.putString("saved_stores", storesJSON.toString());
					editPrefs.commit();
					Log.i("JSON", storesJSON.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	//Set Image from Camera to ImageView
	public void setImage(Bitmap image, String fileName){
		if(image != null){
			itemPic.setImageBitmap(image);
			if(fileName != null){
				imageFile = fileName;
			}
		}
	}

}
