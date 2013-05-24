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

public class LeftMenuFragment extends Fragment implements OnItemClickListener {

	private ListView MenuItems;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.menu_layout, null);
		MenuItems = (ListView) view.findViewById(R.id.list_view);
		setListeners();
		setAdapters();
		return view;
	}
	
	private void setListeners(){
		MenuItems.setOnItemClickListener(this);
	}
	
	private void setAdapters(){
		MenuItems.setAdapter(new MenuCateAdapter(getActivity()));
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}
	
}
