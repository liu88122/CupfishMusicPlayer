package com.cupfish.music.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cupfish.music.R;
import com.cupfish.music.bean.LocalItem;
import com.cupfish.music.utils.LocalCategoryFactory;

public class LocalCategoryAdapter extends BaseAdapter {

	private List<LocalItem> items;
	private LayoutInflater mInflater;
	
	public LocalCategoryAdapter(Context context){
		items = LocalCategoryFactory.getAllCategoris(context);
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.local_category_item, null);
			holder.bg = (ImageView) convertView.findViewById(R.id.background_cover);
			holder.fg = (ImageView) convertView.findViewById(R.id.foreground_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.num = (TextView) convertView.findViewById(R.id.number);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		LocalItem item = items.get(position);
		holder.name.setText(item.getName());
		holder.num.setText(String.valueOf(item.getItemNum()));
		holder.bg.setBackgroundResource(item.getBackgroundCover());
		holder.fg.setBackgroundResource(item.getForegroundCover());
		
		return convertView;
	}
	
	private class ViewHolder{
		public TextView name;
		public TextView num;
		public ImageView bg;
		public ImageView fg;
	}

}
