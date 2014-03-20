package com.jtilley.nextrip;

import org.json.JSONArray;
import org.json.JSONException;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class RecentHistoryActivity extends Activity implements RecentHistoryFragment.OnHistoryItemsSaved {
SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_recent_history);
		
	}

	//Get History Data
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
				//Display Dialog
				HistoryDialog dialog = new HistoryDialog();
				dialog.show(getFragmentManager(), "history_dialog");
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Confirmation Dialog for Clear History 
	public static class HistoryDialog extends DialogFragment{
		
		static HistoryDialog newInstance(){
			return new HistoryDialog();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.history_dialog, container);
			getDialog().setTitle("Are You Sure?");
			Button okButton = (Button) view.findViewById(R.id.historyOK);
			okButton.setOnClickListener(new OnClickListener() {
				//Clear All Recent History
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", 0);
					SharedPreferences.Editor editPrefs = prefs.edit();
					editPrefs.putString("history", "");
					editPrefs.commit();
					RecentHistoryFragment frag = (RecentHistoryFragment) getFragmentManager().findFragmentById(R.id.historyFrag);
					frag.displayHistory();
					dismiss();
				}
			});
			Button cancelButton = (Button) view.findViewById(R.id.historyCancel);
			cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
			return view;
		}
	}	
}
