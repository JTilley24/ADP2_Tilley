package com.jtilley.nextrip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;


public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener, StoresFragment.OnStoresListClicked, StoresMapFragment.OnStoresMapClicked, NearbyMapFragment.OnPlacesClicked, OnQueryTextListener{
ActionBar aBar;
Location location;
String data;
Menu abMenu;
SearchView searchField;
MenuItem addAction;
LocationClient myclient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Setup ActionBar and Tabs
		aBar = getActionBar();
		aBar.setTitle("NexTrip");
		aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		aBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_USE_LOGO);
		
		Tab tab1 = aBar.newTab().setText("Stores")
								.setTabListener(new TabListener<StoresFragment>(this, "stores", StoresFragment.class));
		Tab tab2 = aBar.newTab().setText("Map")
								.setTabListener(new TabListener<StoresMapFragment>(this, "map", StoresMapFragment.class));
		
		Tab tab3 = aBar.newTab().setText("Nearby")
								.setTabListener(new TabListener<NearbyMapFragment>(this, "nearby", NearbyMapFragment.class));
		
		aBar.addTab(tab1);
		aBar.addTab(tab2);
		aBar.addTab(tab3);
		
		if(savedInstanceState != null){
			int tab = savedInstanceState.getInt("tab");
			aBar.setSelectedNavigationItem(tab);
		}else{
			aBar.setSelectedNavigationItem(0);
		}
		
		//Google Play Services Location
		myclient = new LocationClient(this, this, this);
		myclient.connect();
	}
	
	//Create ActionBar Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		abMenu = menu;
		searchField = (SearchView) menu.findItem(R.id.action_search).getActionView();
		setupSearchView(searchField);
		addAction = (MenuItem) menu.findItem(R.id.action_add);
		return true;
	}
	
	//Click for Add Item button
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == addAction.getItemId()){
			Intent addItem = new Intent(this, AddItemActivity.class);
			addItem.putExtra("lat", getLocation().getLatitude());
			addItem.putExtra("lng", getLocation().getLongitude());
			startActivity(addItem);
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Save Selected Tab
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		int tab = aBar.getSelectedNavigationIndex();
		outState.putInt("tab", tab);
	}
	
	//Setup for SearchView Action
	private void setupSearchView(SearchView search){
		search.setIconifiedByDefault(false);
		search.setSubmitButtonEnabled(false);
		search.setOnQueryTextListener(this);
	}
	
	//Send User Input to Places API
	@Override
	public boolean onQueryTextSubmit(String search) {
		// TODO Auto-generated method stub
		Log.i("Search", search);
		Location current = getLocation();
		if(current != null){
			getPlaces(search, current);
		}
		searchField.clearFocus();
		return true;
	}
	
	//Display SearchView only in NearbyMap Tab
	public void displaySearch(Boolean tabSearch){
		if(abMenu != null){
			if(tabSearch == true){
				abMenu.findItem(R.id.action_search).setVisible(true);
			}else{
				abMenu.findItem(R.id.action_search).setVisible(false);
				searchField.clearFocus();
				abMenu.findItem(R.id.action_search).collapseActionView();
			}
		}
	}

	//Get Current Location
	public Location getLocation(){
		if(myclient.isConnected()){
			location = myclient.getLastLocation();
		}
		return location;
	}
	
	//Save Store to storage
	public void saveStore(String name, LatLng position){
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		SharedPreferences.Editor editPrefs = prefs.edit();
		String savedStores = prefs.getString("saved_stores", null);
		JSONArray storesArray;
		try {
			if(savedStores != null){
				storesArray = new JSONArray(savedStores.toString());
			}else{
				storesArray = new JSONArray();
			}
			JSONObject store = new JSONObject();
			store.put("name", name);
			store.put("lat", position.latitude);
			store.put("lng", position.longitude);
			storesArray.put(store);
			editPrefs.putString("saved_stores", storesArray.toString());
			editPrefs.commit();
			Log.i("SAVED", storesArray.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void openStoreDetails(String store){
		Intent details = new Intent(this, StoreDetailsActivity.class);
		details.putExtra("store", store);
		startActivity(details);
	}

	public static class TabListener<T extends Fragment>implements ActionBar.TabListener{
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		
		
		
		public TabListener(Activity activity, String tag, Class<T> clas){
			mActivity = activity;
			mTag = tag;
			mClass = clas;
			
			mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
			
			if(mFragment != null && !mFragment.isDetached()){
				FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
			
		}
		
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment == null){
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
				
			}else{
				ft.attach(mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment != null){
				ft.detach(mFragment);
			}
		}

	}
	
	//Send Location and Search Input to Places API
	public void getPlaces(String keyword, Location location){
		Double lat = location.getLatitude();
		Double lon = location.getLongitude();
		String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat.toString() + "," + lon.toString() + "&radius=3000&keyword="+ keyword + "&sensor=false&key=AIzaSyCEkgDX7_mDjucudKX5Y_JNWXxwHk0LRJE";
		GetPlaces places = new GetPlaces();
		try {
			data = places.execute(urlString).get();
			
			NearbyMapFragment nearbyMap = (NearbyMapFragment) getFragmentManager().findFragmentByTag("nearby");
			nearbyMap.displayPlaces(new JSONObject(data.toString()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//Places API
	private class GetPlaces extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			StringBuilder placesString = new StringBuilder();
			for(String urlString : params){
				HttpClient client = new DefaultHttpClient();
				try {
					HttpGet httpGet = new HttpGet(urlString);
					HttpResponse response = client.execute(httpGet);
					StatusLine status = response.getStatusLine();
					if(status.getStatusCode() == 200){
						HttpEntity entity = response.getEntity();
						InputStream input = entity.getContent();
						InputStreamReader inReader = new InputStreamReader(input);
						BufferedReader buffReader = new BufferedReader(inReader);
						String temp;
						while((temp = buffReader.readLine()) != null){
							placesString.append(temp);
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return placesString.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
	}
	
	//Display DialogFragment
	public void showDialog(String name, Double lat, Double lng){
		StoreDialog dialog = new StoreDialog();
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putDouble("lat", lat);
		args.putDouble("lng", lng);
		dialog.setArguments(args);
		dialog.show(getFragmentManager(), "dialog_fragment");
	}

	public static class StoreDialog extends DialogFragment{
		EditText textField;
		Button saveButton;
		Button cancelButton;
		
		static StoreDialog newInstance(){
			return new StoreDialog();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.dialog_fragment, container);
			getDialog().setTitle("Save Store?");
			final String name = getArguments().get("name").toString();
			final LatLng pos = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
			textField = (EditText) view.findViewById(R.id.searchField);
			saveButton = (Button) view.findViewById(R.id.saveButton);
			cancelButton = (Button) view.findViewById(R.id.cancelButton);
			
			textField.setText(name);
			
			saveButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String storeName = textField.getText().toString();
					if(storeName.length() != 0){
						((MainActivity)getActivity()).saveStore(storeName, pos);
						Log.i("STORE", storeName + " : " + String.valueOf(pos.latitude) + " " + String.valueOf(pos.longitude));
						dismiss();
						if(getActivity().getActionBar().getSelectedNavigationIndex() == 1){
							StoresMapFragment storeMap = (StoresMapFragment) getActivity().getFragmentManager().findFragmentByTag("map");
							storeMap.displayStoresMarkers();
						}
					}else{
						Toast.makeText(getActivity(), "Please enter name of Store.", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
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
	
	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void displayPlaces(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.i("connect", "connect failed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		Log.i("connect", "connected");
		getLocation();
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.i("connect", "disconnected");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//myclient.connect();
	}

	

	


	
}
