package com.cupfish.music.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

import com.cupfish.music.R;
import com.cupfish.music.ui.adapter.MenuCateAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class LeftMenuFragment extends Fragment implements OnItemClickListener {

	private ListView mMenuItemsListView;
	private String[] mCateTitles;
	private SlidingMenu mSlidingMenu;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mCateTitles = getResources().getStringArray(R.array.menu_cate);
		
		View view = inflater.inflate(R.layout.menu_layout, null);
		mMenuItemsListView = (ListView) view.findViewById(R.id.list_view);
		setListeners();
		setAdapters();
		return view;
	}
	
	private void setListeners(){
		mMenuItemsListView.setOnItemClickListener(this);
	}
	
	private void setAdapters(){
		mMenuItemsListView.setAdapter(new MenuCateAdapter(getActivity()));
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		Fragment mFragment = null;
		
		if(position < mCateTitles.length){
			switch(position){
			case SlidingMenuItem.PLAYING:
				mFragment = new MusicPlayerFragment();
				break;
			case SlidingMenuItem.LOCAL:
				mFragment = new LocalMusicFragment();
				break;
			case SlidingMenuItem.BAIDU:
				mFragment = new OnlineMusicFragment();
				break;
			case SlidingMenuItem.SONGTASTE:
				
				break;
			}
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content, mFragment).commit();
			toggleSlidingMenu();
		}
	}
	
	public void setSlidingMenu(SlidingMenu slidingMenu){
		mSlidingMenu = slidingMenu;
	}
	
	private void toggleSlidingMenu(){
		if(mSlidingMenu != null){
			mSlidingMenu.toggle(true);
		}
	}
	
	
	
	private static final class SlidingMenuItem{
		public static final int PLAYING = 0;
		public static final int LOCAL = 1;
		public static final int BAIDU = 2;
		public static final int SONGTASTE = 3;
	}
	
}
