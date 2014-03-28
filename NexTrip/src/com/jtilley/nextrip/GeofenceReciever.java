package com.jtilley.nextrip;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GeofenceReciever extends BroadcastReceiver{
Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		List<Geofence> locs = LocationClient.getTriggeringGeofences(intent);
		Log.i("TRIGGER", locs.toString());
		
		if(hasItems(locs.get(0).getRequestId().toString())){
			Intent storeIntent = new Intent(context, StoreDetailsActivity.class);
			storeIntent.putExtra("store", locs.get(0).getRequestId().toString());
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, storeIntent, 0);
			
			//Send notification on trigger
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(locs.get(0).getRequestId())
					.setContentText("You have saved item(s) at this store!")
					.setContentIntent(pIntent);
			
			NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			nManager.notify(0, builder.build());
		}
	}

	//Check if store has saved items
	public Boolean hasItems(String store){
		JSONArray storesArray = new JSONArray();
		JSONObject storeObj = new JSONObject();
		SharedPreferences prefs = mContext.getSharedPreferences("user_prefs", 0);
		try {
			storesArray = new JSONArray(prefs.getString("saved_stores", null));
			for(int i=0; i< storesArray.length(); i++){
				JSONObject tempObj = storesArray.getJSONObject(i);
				if(tempObj.getString("name").equalsIgnoreCase(store)){
					Log.i("STORE", tempObj.toString());
					storeObj = tempObj;
				}
			}
			if(storeObj != null){
				if(storeObj.has("items")){
					if(storeObj.getJSONArray("items").length() != 0){
						return true;
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
