package com.cupfish.musicplayer.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.global.Constants;

/**
 * 音乐榜单的Adapter
 * 
 * @author Liu88122
 * 
 */
public class MusicListNameAdapter extends BaseAdapter {

	private String[] mListNames;
	private Context mContext;

	public MusicListNameAdapter(Context context, String[] listNames) {
		mContext = context;
		mListNames = listNames;
	}

	@Override
	public int getCount() {
		return mListNames.length;
	}

	@Override
	public Object getItem(int position) {
		return mListNames[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.top_list_name_item, null);
			TextView mIndex = (TextView) convertView.findViewById(R.id.tv_item_index);
			TextView mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			holder = new ViewHolder();
			holder.index = mIndex;
			holder.title = mTitle;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.index.setTypeface(Typeface.SERIF);
		holder.index.setText(String.valueOf(position + 1));
		holder.title.setText(mListNames[position]);
		return convertView;
	}

	private static class ViewHolder {
		public TextView index;
		public TextView title;
	}

}
