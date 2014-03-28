package com.jtilley.nextrip;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoresMapFragment extends MapFragment{
GoogleMap map;
Location location;
String storesString;
ArrayList<String> storesArray;
MarkerOptions current;

	public interface OnStoresMapClicked{
		Location getLocation();
		void displaySearch(Boolean tabSearch);
		void showDialog(String name, Double lat, Double lng);
		void openStoreDetails(String store);
	}
	
	private OnStoresMapClicked parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnStoresMapClicked){
			parentActivity = (OnStoresMapClicked) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnStoresMapClicked");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		parentActivity.displaySearch(false);
		map = this.getMap();
		map.setMyLocationEnabled(true);
		if(location == null){
			location = parentActivity.getLocation();
		}
		if(location != null){
			LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
			
			
			//Save Selected Location as new Store
			map.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng position) {
					// TODO Auto-generated method stub
					String name = "";
					parentActivity.showDialog(name, position.latitude, position.longitude);
					Log.i("LATLNG", position.toString());
				}
			});
			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {
					// TODO Auto-generated method stub
					String store = marker.getTitle().toString();
					Log.i("STORE", store);
					parentActivity.openStoreDetails(store);
				}
			});
			
			displayStoresMarkers();
		}else{
			Toast.makeText(getActivity(), "Please turn on GPS and try again.", Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}
	
	//Display Markers For Saved Stores
	public void displayStoresMarkers(){
		map.clear();
		SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", 0);
		storesString = prefs.getString("saved_stores", null);
		if(storesString != null){
			try {
				
				JSONArray stores = new JSONArray(storesString);
				for(int i=0; i< stores.length();i++){
					JSONObject storeObj = (JSONObject) stores.get(i);
					String storeName = storeObj.getString("name");
					Double lat = storeObj.getDouble("lat");
					Double lng = storeObj.getDouble("lng");
					LatLng storePos = new LatLng(lat, lng);
					map.addMarker(new MarkerOptions().position(storePos).title(storeName));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Toast.makeText(getActivity(), "No Saved Stores to Display", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

}
