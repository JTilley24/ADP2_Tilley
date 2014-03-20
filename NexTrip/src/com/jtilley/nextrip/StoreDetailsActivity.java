package com.jtilley.nextrip;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class StoreDetailsActivity extends Activity implements StoreDetailsFragment.OnItemSelected {
String store;
JSONObject storeObj;
JSONArray storesArray;
JSONArray itemsArray;

	protected Object actionMode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_store_details);
		Intent intent = getIntent();
		store = intent.getExtras().getString("store");
		
		setTitle(store);
		setItems();
	}

	//Get Items for Selected Store
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
	//Return Items for Custom Adapter
	public JSONArray getItems(){
		setItems();
		JSONArray items = new JSONArray();
		if(itemsArray != null){
			items = itemsArray;
		}
		return items;
	}
	
	//Delete Selected Items from Store Object
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
	
	//Check for Item's Checkbox
	public Boolean isChecked(ArrayList<String> selectedItems, int index){
		for(int i=0; i< selectedItems.size();i++){
			if(Integer.valueOf(selectedItems.get(i)) == index){
				return false;
			}
		}
		return true;
	}
	
	//Save Selected Items to Recent History
	public void saveItemHistory(ArrayList<String> selectedItems){
		JSONArray historyArray = new JSONArray();
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		String historyString = prefs.getString("history", null);
		if(historyString.length() != 0){
			try {
				historyArray = new JSONArray(historyString);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i=0; i<itemsArray.length();i++){
			if(!isChecked(selectedItems, i)){
				try {
					JSONObject tempObject = itemsArray.getJSONObject(i);
					Calendar date = Calendar.getInstance();
					String today = String.valueOf(date.get(Calendar.MONTH) + "-" + date.get(Calendar.DATE) + "-" + date.get(Calendar.YEAR) );
					tempObject.put("day", today);
					tempObject.put("store", store);
					historyArray.put(itemsArray.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		SharedPreferences.Editor editPrefs = prefs.edit();
		editPrefs.putString("history", historyArray.toString());
		editPrefs.commit();
		
		Log.i("HISTORY", historyArray.toString());
	}
	
	//Display Contextual Action Bar 
	public void displayItemsContextual(){
		actionMode = StoreDetailsActivity.this.startActionMode(aMode);
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
		}else if (id == R.id.action_history){
			//Open Recent History
			Intent history = new Intent(this, RecentHistoryActivity.class);
			startActivity(history);
		}
		return super.onOptionsItemSelected(item);
	}

	//Contextual Action Bar
	private ActionMode.Callback aMode = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			actionMode = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.items_contextual, menu);
			return true;
		}
		
		//Check for Delete or Save to History
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			if(item.getItemId() == R.id.action_discard){
				StoreDetailsFragment frag = (StoreDetailsFragment) getFragmentManager().findFragmentById(R.id.store_details_frag);
				ArrayList<String> selectedItems = frag.getSelectedItems();
				deleteItems(selectedItems);
				mode.finish();
			}else if(item.getItemId() == R.id.action_item_accept){
				StoreDetailsFragment frag = (StoreDetailsFragment) getFragmentManager().findFragmentById(R.id.store_details_frag);
				ArrayList<String> selectedItems = frag.getSelectedItems();
				saveItemHistory(selectedItems);
				deleteItems(selectedItems);
				mode.finish();
			}
			return true;
		}
	};
	
}
