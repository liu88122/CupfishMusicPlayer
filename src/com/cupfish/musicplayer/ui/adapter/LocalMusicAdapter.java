package com.cupfish.musicplayer.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.Song;
import com.cupfish.musicplayer.utils.LocalMediaUtil;
import com.cupfish.musicplayer.utils.TextFormatUtils;

public class LocalMusicAdapter extends BaseAdapter {

	private List<Song> mLocalSongs;
	private Context mContext;
	
	public LocalMusicAdapter(Context context, List<Song> mLocalSongs){
		mContext = context;
		this.mLocalSongs = mLocalSongs;
	}
	
	public void setData(List<Song> songs){
		mLocalSongs = songs;
	}
	
	@Override
	public int getCount() {
		if(mLocalSongs!=null){
			return mLocalSongs.size();
		}
		return 0;
	}

	@Override
	public Song getItem(int position) {
		if(mLocalSongs!=null){
			return mLocalSongs.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.music_list_item, null);
			TextView mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			TextView mArtist = (TextView) convertView.findViewById(R.id.tv_artist);
			TextView mDuration = (TextView) convertView.findViewById(R.id.tv_duration);
			
			holder = new ViewHolder();
			holder.title = mTitle;
			holder.artist = mArtist;
			holder.duration = mDuration;
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(mLocalSongs.get(position).getTitle());
		//TODO 需要将所有的artist都显示出来
		holder.artist.setText(mLocalSongs.get(position).getAuthorList().get(0).name);
		holder.duration.setText(TextFormatUtils.getPrettyFormatDuration(mLocalSongs.get(position).getDuration()));
		return convertView;
	}
	
	static class ViewHolder{
		public TextView title;
		public TextView artist;
		public TextView duration;
	}

}
