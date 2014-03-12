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


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;


public class MainActivity extends FragmentActivity implements StoresFragment.OnStoresListClicked, StoresMapFragment.OnStoresMapClicked,NearbyMapFragment.OnPlacesClicked, OnQueryTextListener{
private LocationManager lManager;
private String provider;
ActionBar aBar;
Location location;
String data;
Menu abMenu;
SearchView searchField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		aBar = getActionBar();
		aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		aBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
		
		Tab tab1 = aBar.newTab().setText("Stores")
								.setTabListener(new TabListener<StoresFragment>(this, "stores", StoresFragment.class));
		Tab tab2 = aBar.newTab().setText("Map")
								.setTabListener(new TabListener<StoresMapFragment>(this, "map", StoresMapFragment.class));
		
		Tab tab3 = aBar.newTab().setText("Nearby")
								.setTabListener(new TabListener<NearbyMapFragment>(this, "nearby", NearbyMapFragment.class));
		
		aBar.addTab(tab1);
		aBar.addTab(tab2);
		aBar.addTab(tab3);
		Location current = getLocation();
		if(current != null){
			getPlaces("hardware", current);
		}
		aBar.setSelectedNavigationItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		abMenu = menu;
		searchField = (SearchView) menu.findItem(R.id.action_search).getActionView();
		setupSearchView(searchField);
		return true;
	}
	
	
	private void setupSearchView(SearchView search){
		search.setIconifiedByDefault(false);
		search.setSubmitButtonEnabled(false);
		search.setOnQueryTextListener(this);
	}
	
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
	
	public Location getLocation(){
		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = lManager.getBestProvider(criteria, false);
		
		lManager.requestLocationUpdates(provider, 0, 1, new MyLocationListener());
		location = lManager.getLastKnownLocation(provider);
		
		return location;
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
			FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
			mFragment = Fragment.instantiate(mActivity, mClass.getName());
			if(mFragment == null){
				ft.add(android.R.id.content, mFragment, mTag);
			}else{
				ft.remove(mFragment);
			}
			ft.commit();
			
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
				ft.replace(android.R.id.content, mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if(mFragment != null){
				ft.remove(mFragment);
			}
		}

	}
	
	private final class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void getPlaces(String keyword, Location location){
		Double lat = location.getLatitude();
		Double lon = location.getLongitude();
		String urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat.toString() + "," + lon.toString() + "&radius=500&keyword="+ keyword + "&sensor=false&key=AIzaSyCEkgDX7_mDjucudKX5Y_JNWXxwHk0LRJE";
		GetPlaces places = new GetPlaces();
		try {
			data = places.execute(urlString).get();
			Log.i("DATA", data.toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
}