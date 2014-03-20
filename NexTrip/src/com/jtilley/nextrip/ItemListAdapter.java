package com.jtilley.nextrip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends BaseAdapter{
private Activity activity;
private static JSONArray itemsArray;
private static LayoutInflater inflater = null;
ArrayList<String> selectedItems = new ArrayList<String>();

public ItemListAdapter(Activity act, JSONArray array){
	activity = act;
	itemsArray = array;
	inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemsArray.length();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View itemView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = itemView;
		if(view == null){
			view = inflater.inflate(R.layout.item_list_row, null);
		}
		
		final String index = String.valueOf(position);
		
		TextView nameText = (TextView) view.findViewById(R.id.itemName);
		TextView priceText = (TextView) view.findViewById(R.id.itemPrice);
		TextView detailsText = (TextView) view.findViewById(R.id.itemDetails);
		ImageView itemImg = (ImageView) view.findViewById(R.id.itemImage);
		CheckBox check = (CheckBox) view.findViewById(R.id.checkBox1);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					selectedItems.add(index);
					if(!isItemsSelected()){
						StoreDetailsFragment frag = (StoreDetailsFragment) activity.getFragmentManager().findFragmentById(R.id.store_details_frag);
						frag.setActionBar();
					}
										
				}else{
					selectedItems.remove(index);
				}
			}
		});
		
		
		try {
			JSONObject itemObj = itemsArray.getJSONObject(position);
			nameText.setText(itemObj.getString("name"));
			priceText.setText("Price:\n" + itemObj.getString("price"));
			detailsText.setText("Details:\n " + itemObj.getString("details"));
			String imageString = itemObj.getString("image");
			if(imageString.length() != 0){
				File path = Environment.getExternalStoragePublicDirectory("/NexTrip/");
				File file = new File(path, imageString);
				FileInputStream input = new FileInputStream(file);
				Bitmap image = BitmapFactory.decodeStream(input);
				itemImg.setImageBitmap(image);
				input.close();
			}else{
				Bitmap image = BitmapFactory.decodeResource(parent.getResources(), R.drawable.itempic);
				itemImg.setImageBitmap(image);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;
	}
	ArrayList<String> getSelectedItems(){
		return selectedItems;
	}
	
	Boolean isItemsSelected(){
		if(selectedItems.size() == 1){
			return false;
		}
		return true;
	}
	
}
