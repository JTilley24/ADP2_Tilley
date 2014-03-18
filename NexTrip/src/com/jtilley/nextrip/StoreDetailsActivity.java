package com.jtilley.nextrip;

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

public class StoreDetailsActivity extends Activity {
String store;
JSONObject storeObj;
JSONArray itemsArray;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_store_details);
		Intent intent = getIntent();
		store = intent.getExtras().getString("store");
		
		setTitle(store);
		
		getItems();
	}

	public void getItems(){
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		try {
			JSONArray storesArray = new JSONArray(prefs.getString("saved_stores", null));
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
		}
		return super.onOptionsItemSelected(item);
	}

}
