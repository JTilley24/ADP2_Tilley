package com.jtilley.nextrip;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class RecentHistoryActivity extends Activity implements RecentHistoryFragment.OnHistoryItemsSaved {
SharedPreferences prefs;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_recent_history);
		
		prefs = getSharedPreferences("user_prefs", 0);
		String history = prefs.getString("history", null);
		if(history != null){
			Log.i("HISTORY", history);
		}
		SharedPreferences.Editor editPrefs = prefs.edit();
		editPrefs.putString("history", "");
		editPrefs.commit();
		
	}

	public JSONArray getHistory(){
		prefs = getSharedPreferences("user_prefs", 0);
		String history = prefs.getString("history", null);
		JSONArray historyArray = new JSONArray();
		if(history != null){
			Log.i("HISTORY", history);
			try {
				historyArray = new JSONArray(history);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return historyArray;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_history, menu);
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
			
		}else if(id == R.id.action_clear){
			SharedPreferences.Editor editPrefs = prefs.edit();
			editPrefs.putString("history", "");
			editPrefs.commit();
			RecentHistoryFragment frag = (RecentHistoryFragment) getFragmentManager().findFragmentById(R.id.historyFrag);
			frag.displayHistory();	
		}
		return super.onOptionsItemSelected(item);
	}

	
}
