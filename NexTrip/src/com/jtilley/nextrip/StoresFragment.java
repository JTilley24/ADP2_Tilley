package com.jtilley.nextrip;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoresFragment extends Fragment{

	public interface OnStoresListClicked{
		void displaySearch(Boolean tabSearch);
	}
	
	private OnStoresListClicked parentActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if(activity instanceof OnStoresListClicked){
			parentActivity = (OnStoresListClicked) activity;
		}else{
			throw new ClassCastException(activity.toString() + "must implement OnStoresListClicked");
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
		super.onResume();
		parentActivity.displaySearch(false);
	}


}
