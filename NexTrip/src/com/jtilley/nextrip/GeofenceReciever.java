package com.jtilley.nextrip;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GeofenceReciever extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		List<Geofence> locs = LocationClient.getTriggeringGeofences(intent);
		Log.i("TRIGGER", locs.toString());
	}

}
