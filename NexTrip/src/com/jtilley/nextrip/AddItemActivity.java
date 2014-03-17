package com.jtilley.nextrip;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AddItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_item);

		
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
			Log.i("SAVE", "Item will be Saved!");
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick(View view){
		InputMethodManager immanager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		immanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
