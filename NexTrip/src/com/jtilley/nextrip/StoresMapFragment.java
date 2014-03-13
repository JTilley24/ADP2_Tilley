package com.jtilley.nextrip;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class StoresMapFragment extends MapFragment{
GoogleMap map;
Location location;

	public interface OnStoresMapClicked{
		Location getLocation();
		void displaySearch(Boolean tabSearch);
		void showDialog(String name, Double lat, Double lng);
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
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		parentActivity.displaySearch(false);
		map = this.getMap();
		location = parentActivity.getLocation();
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng position) {
				// TODO Auto-generated method stub
				String name = "";
				parentActivity.showDialog(name, position.latitude, position.longitude);
				Log.i("LATLNG", position.toString());
			}
		});
		
		map.addMarker(new MarkerOptions().position(position).title("HERE"));
		
		super.onResume();
	}

}
