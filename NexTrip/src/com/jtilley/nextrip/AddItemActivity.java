package com.jtilley.nextrip;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AddItemActivity extends Activity implements AddItemFragment.OnSaveItem {
Double lat;
Double lng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_item);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		lat = extras.getDouble("lat");
		lng = extras.getDouble("lng");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_item, menu);
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
		}else if(id == R.id.action_accept){
			AddItemFragment frag = (AddItemFragment) getFragmentManager().findFragmentById(R.id.add_item_frag);
			frag.saveItem();
		}
		return super.onOptionsItemSelected(item);
	}
	
	public LatLng getLocation(){
		LatLng location = new LatLng(lat, lng);
		
		return location;
		
	}
	
	public void onClick(View view){
		InputMethodManager immanager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		immanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
