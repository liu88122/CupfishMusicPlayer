package com.cupfish.music.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.cupfish.music.R;
import com.cupfish.music.bean.LocalItem;
import com.cupfish.music.ui.adapter.LocalCategoryAdapter;

public class LocalMusicFragment extends Fragment implements OnItemClickListener {

	private View mLocalContent;
	private GridView mCategoriesView;
	private LocalCategoryAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLocalContent = inflater.inflate(R.layout.local2, container, false);
		setupLayout();
		setupListener();
		return mLocalContent;
	}
	private void setupLayout() {
		mCategoriesView = (GridView) mLocalContent.findViewById(R.id.gv_categories);
		mAdapter = new LocalCategoryAdapter(getActivity());
		mCategoriesView.setAdapter(mAdapter);
	}
	
	private void setupListener(){
		mCategoriesView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LocalItem item = (LocalItem) mAdapter.getItem(position);
		if(item.getTarget() != null){
			ArrayList data = item.getData();
			if(data != null && data.size() > 0){
				Intent intent = new Intent(getActivity(), item.getTarget());
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", data);
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		}
	}
	
	
	
}
