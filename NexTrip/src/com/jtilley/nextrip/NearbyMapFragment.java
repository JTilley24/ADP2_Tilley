package com.jtilley.nextrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyMapFragment extends MapFragment {
	GoogleMap map;
	Location location;
	Context mContext;
	MarkerOptions current;
	
		public interface OnPlacesClicked{
			Location getLocation();
			void displaySearch(Boolean tabSearch);
			void displayPlaces(JSONObject json);
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
			current = new MarkerOptions().position(position).title("HERE");
			map.addMarker(current);
		}
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.i("MARKER", marker.getTitle().toString());
				return false;
			}
		});
			
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void displayPlaces(JSONObject json){
		map.clear();
		if(current != null){
			map.addMarker(current);
		}
		try {
			JSONArray results = json.getJSONArray("results");
			String status = json.getString("status");
			if(status.equals("OK")){
				for(int i = 0; i < results.length(); i++){
					JSONObject place = new JSONObject(results.get(i).toString());
					String name = place.getString("name");
					JSONObject placeLoc = place.getJSONObject("geometry").getJSONObject("location");
					LatLng placePos = new LatLng(placeLoc.getDouble("lat"), placeLoc.getDouble("lng"));
					map.addMarker(new MarkerOptions().position(placePos).title(name));		
					Log.i("Place", name + " : " + placeLoc.getString("lng") + " , " + placeLoc.getString("lat"));
				}
			}else{
				Toast.makeText(getActivity(), "No Places Available. Please Try Again.", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
