package com.jtilley.nextrip;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class StoreDetailsActivity extends Activity implements StoreDetailsFragment.OnItemSelected {
String store;
JSONObject storeObj;
JSONArray storesArray;
JSONArray itemsArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_store_details);
		Intent intent = getIntent();
		store = intent.getExtras().getString("store");
		
		setTitle(store);
		
		setItems();
	}

	public void setItems(){
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		try {
			storesArray = new JSONArray(prefs.getString("saved_stores", null));
			for(int i=0; i< storesArray.length(); i++){
				JSONObject tempObj = storesArray.getJSONObject(i);
				if(tempObj.getString("name").equalsIgnoreCase(store)){
					Log.i("STORE", tempObj.toString());
					storeObj = tempObj;
				}
			}
			if(storeObj != null){
				if(storeObj.has("items")){
					itemsArray = storeObj.getJSONArray("items");
					for(int i=0; i < itemsArray.length(); i++){
						JSONObject tempItem = itemsArray.getJSONObject(i);
						Log.i("ITEM",tempItem.getString("name"));
					}
				}
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public JSONArray getItems(){
		setItems();
		JSONArray items = new JSONArray();
		if(itemsArray != null){
			items = itemsArray;
		}
		return items;
	}
	
	public void deleteItems(ArrayList<String> selectedItems){
		JSONArray tempArray = new JSONArray();
		for(int i=0; i<itemsArray.length();i++){
			if(isChecked(selectedItems, i)){
				try {
					tempArray.put(itemsArray.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			storeObj.put("items", tempArray);
			SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
			SharedPreferences.Editor editPrefs = prefs.edit();
			for(int i=0; i< storesArray.length(); i++){
				JSONObject tempObj = storesArray.getJSONObject(i);
				if(tempObj.getString("name").equalsIgnoreCase(store)){
					storesArray.put(i, storeObj);
				}
			}
			editPrefs.putString("saved_stores", storesArray.toString());
			editPrefs.commit();
			StoreDetailsFragment frag = (StoreDetailsFragment) getFragmentManager().findFragmentById(R.id.store_details_frag);
			frag.displayItems();
			Log.i("STORES", storesArray.toString());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Log.i("ITEMS", tempArray.toString());
	}
	
	public Boolean isChecked(ArrayList<String> selectedItems, int index){
		for(int i=0; i< selectedItems.size();i++){
			if(Integer.valueOf(selectedItems.get(i)) == index){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.store_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if(id == R.id.action_discard){
			StoreDetailsFragment frag = (StoreDetailsFragment) getFragmentManager().findFragmentById(R.id.store_details_frag);
			ArrayList<String> selectedItems = frag.getSelectedItems();
			deleteItems(selectedItems);
		}
		return super.onOptionsItemSelected(item);
	}

}
