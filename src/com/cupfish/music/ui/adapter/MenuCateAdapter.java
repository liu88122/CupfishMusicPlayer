package com.cupfish.music.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cupfish.music.R;

public class MenuCateAdapter extends BaseAdapter {
	
	private int[] sectionTitles = {R.string.love_music, R.string.tools};
	private int[] menuIcons = {R.drawable.logo, R.drawable.logo, R.drawable.baidu, R.drawable.songtaste};
	private String[] menuCates;
	private String[] tools;
	private Context context;
	
	public MenuCateAdapter(Context context){
		this.context = context;
		menuCates = context.getResources().getStringArray(R.array.menu_cate);
		tools = context.getResources().getStringArray(R.array.tools);
	}

	@Override
	public int getCount() {
		return menuCates.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		int viewType = getItemViewType(position);
		switch(viewType){
		case 0:
			
			break;
		case 1:
			
			break;
		}
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		int viewType = getItemViewType(position);
		View view = null;
//		switch(viewType){
//		case 0:
//			view = View.inflate(context, R.layout.music_list_item_section_title, null);
//			TextView  tv = (TextView) view.findViewById(R.id.tv_section_title);
//			if(position == 0){
//				tv.setText(sectionTitles[0]);
//			} else {
//				tv.setText(sectionTitles[1]);
//			}
//			break;
//		case 1:
			view = View.inflate(context, R.layout.menu_cate_item, null);
			TextView tv1 = (TextView) view.findViewById(R.id.title);
			if(position <= menuCates.length){
				tv1.setText(menuCates[position]);
			} else {
				tv1.setText(tools[position - menuCates.length]);
			}
//			break;
//		}
		return view;
	}

	@Override
	public int getItemViewType(int position) {
		if(position == 0 || position == menuCates.length + 1){
			return 0;
		}else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	

}
