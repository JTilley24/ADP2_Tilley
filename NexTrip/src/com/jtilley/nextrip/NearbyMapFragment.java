package com.jtilley.nextrip;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyMapFragment extends MapFragment {
	GoogleMap map;
	Location location;
	Context mContext;
		
		public interface OnPlacesClicked{
			Location getLocation();
			void displaySearch(Boolean tabSearch);
		}
		
		private OnPlacesClicked parentActivity;
		
		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
			if(activity instanceof OnPlacesClicked){
				parentActivity = (OnPlacesClicked) activity;
			}else{
				throw new ClassCastException(activity.toString() + "must implement OnPlacesClicked");
			}
		}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		parentActivity.displaySearch(true);
		map = this.getMap();
		location = parentActivity.getLocation();
		if(location != null){
			LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
			@SuppressWarnings("unused")
			Marker current = map.addMarker(new MarkerOptions().position(position).title("HERE"));
		}
			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
