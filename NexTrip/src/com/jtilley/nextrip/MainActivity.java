package com.jtilley.nextrip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;


public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener, StoresFragment.OnStoresListClicked, StoresMapFragment.OnStoresMapClicked, NearbyMapFragment.OnPlacesClicked, OnQueryTextListener, OnAddGeofencesResultListener{
ActionBar aBar;
Location location;
String data;
Menu abMenu;
SearchView searchField;
MenuItem addAction;
LocationClient myclient;
ArrayList<Geofence> storesFences;

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
		
		
		
		//Check for saved stores
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		String first = prefs.getString("first_alert", null);
		if(first == null){
			SharedPreferences.Editor editPrefs = prefs.edit();
			editPrefs.putString("first_alert", "help_displayed");
			editPrefs.commit();
			displayHelp();
		}
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
		}else if(item.getItemId() == R.id.action_help){
			displayHelp();
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
			setGeofences();
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
		setGeofences();
		
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
		
		if(myclient == null){
			//Google Play Services Location
			myclient = new LocationClient(this, this, this);
			if(!myclient.isConnected()){
				myclient.connect();	
			}
		}
	}

	//Convert Stores to Geofences and Add to Location Client
	public void setGeofences(){
		SharedPreferences prefs = getSharedPreferences("user_prefs", 0);
		String storesString = prefs.getString("saved_stores", null);
		if(storesString != null){
			try {
				storesFences = new ArrayList<Geofence>();
				JSONArray storesJSON = new JSONArray(storesString);
				for(int i=0;i < storesJSON.length(); i++){
					JSONObject storeObj = storesJSON.getJSONObject(i);
					Geofence geo = new Geofence.Builder().setRequestId(storeObj.getString("name")).setCircularRegion(storeObj.getDouble("lat"), storeObj.getDouble("lng"), 100).setExpirationDuration(Geofence.NEVER_EXPIRE).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build();
					storesFences.add(geo);
				}
				Intent intent = new Intent(this, GeofenceReciever.class);
				intent.setAction("ACTION_RECEIVE_GEOFENCE");
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
				myclient.addGeofences(storesFences, pIntent, this);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	//Results of Geofences
	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		// TODO Auto-generated method stub
		if(LocationStatusCodes.SUCCESS == arg0){
			Log.i("GEOFENCES", arg1.toString());
		}
	}

	//Display Help Dialog
	public void displayHelp(){
		DialogFragment helpDialog = new HelpDialog();
		helpDialog.show(getFragmentManager(), "help_dialog");
	}
	
	public static class HelpDialog extends DialogFragment{

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog));;
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.help_dialog_fragment, null);
			
			TextView helpStoreMap = (TextView) view.findViewById(R.id.helpMap);
			helpStoreMap.setText("Click and Hold the point \n on the map where you would \n like the new store.");
			helpStoreMap.setTextColor(Color.CYAN);
			
			TextView helpStoreItem = (TextView) view.findViewById(R.id.helpItem);
			helpStoreItem.setText("Select 'New...' in the Store \n option and a new input \n will appear below.");
			helpStoreItem.setTextColor(Color.CYAN);
			
			Button helpGPSButton = (Button) view.findViewById(R.id.helpGPSButton);
			helpGPSButton.setOnClickListener(new OnClickListener() {
				//Intent to Location Settings
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent setttingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(setttingsIntent);
				}
			});
			
			builder.setView(view).setPositiveButton(R.string.help_close_button, new DialogInterface.OnClickListener() {
				//Close Dialog
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					getDialog().dismiss();
				}
			}).setTitle("Help").setIcon(R.drawable.ic_action_help);
			
			return builder.create();
		}	
	}
}
