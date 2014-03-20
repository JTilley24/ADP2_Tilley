package com.jtilley.nextrip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AddItemActivity extends Activity implements AddItemFragment.OnSaveItem {
Double lat;
Double lng;
Bitmap image;

private static final int CAMERA_REQUEST = 1888;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_item);
		//Get Current Location
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		lat = extras.getDouble("lat");
		lng = extras.getDouble("lng");	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_item, menu);
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
		}else if(id == R.id.action_accept){
			//Add Item to Selected Store
			AddItemFragment frag = (AddItemFragment) getFragmentManager().findFragmentById(R.id.add_item_frag);
			frag.saveItem();
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	//Get Location for New Store
	public LatLng getLocation(){
		LatLng location = new LatLng(lat, lng);
		
		return location;
	}
	//Open Camera Intent
	public void openCamera(){
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camera, CAMERA_REQUEST);
	}
	
	//Receive image data from Camera
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			if(requestCode == CAMERA_REQUEST){
				image = (Bitmap) data.getExtras().get("data");
				AddItemFragment frag = (AddItemFragment) getFragmentManager().findFragmentById(R.id.add_item_frag);
				frag.setImage(image, saveImage());
			}
		}
	}
	
	//Save image to External Storage
	public String saveImage(){
		File path = Environment.getExternalStoragePublicDirectory("/NexTrip/");
		if(!path.exists()){
			path.mkdir();
		}
		String time = String.valueOf(Calendar.getInstance().getTime().getTime());
		String fileName = time + ".jpeg";
		
		File file = new File(path, fileName);
		try {
			OutputStream fos = new FileOutputStream(file);
			Bitmap bitmap = image;
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileName;
	}
	
	//Hide Keyboard when User clicks out of EditText
	public void onClick(View view){
		InputMethodManager immanager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		immanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
