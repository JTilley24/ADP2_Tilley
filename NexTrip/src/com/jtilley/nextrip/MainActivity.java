package com.jtilley.nextrip;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.Menu;


public class MainActivity extends Activity{
ActionBar aBar;

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
		
		
		aBar.setSelectedNavigationItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
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
	
}
