package com.cupfish.musicplayer.ui.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.MusicFolder;

public class FolderAdapter extends BaseAdapter {

	private List<MusicFolder> mFolders;
	private LayoutInflater inflater;
	
	public FolderAdapter(Context context, List<MusicFolder> folders){
		inflater = LayoutInflater.from(context);
		mFolders = folders;
	}
	
	public void setFolders(List<MusicFolder> folders){
		mFolders = folders;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mFolders.size();
	}

	@Override
	public Object getItem(int position) {
		return mFolders.get(position);
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
			convertView = inflater.inflate(R.layout.folder_list_item, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.path = (TextView) convertView.findViewById(R.id.path);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		String folderPath = mFolders.get(position).getPath();
		int index = folderPath.lastIndexOf(File.separator);
		if(index > 0 && index < folderPath.length()){
			String title = folderPath.substring(index);
			holder.title.setText(title);
		}else{
			holder.title.setText("?");
		}
		holder.path.setText(folderPath);
		return convertView;
	}
	
	private static class ViewHolder{
		public ImageView icon;
		public TextView title;
		public TextView path;
	}

}
