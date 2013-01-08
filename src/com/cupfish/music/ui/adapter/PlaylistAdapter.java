package com.cupfish.music.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.global.Constants;

public class PlaylistAdapter extends BaseAdapter {

	private static final String TAG = "PlaylistAdapter";
	private List<Song> mPlaylist;
	private Context mContext;
	
	public PlaylistAdapter(Context context, List<Song> playlist){
		mContext = context;
		mPlaylist = playlist;
		
	}
	
	
	@Override
	public int getCount() {
		Log.i(TAG, "" + mPlaylist.size());
		return mPlaylist.size();
	}

	@Override
	public Object getItem(int position) {
		return mPlaylist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.play_list_item, null);
			TextView mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			TextView mArtist = (TextView) convertView.findViewById(R.id.tv_artist);
			TextView mIndex = (TextView) convertView.findViewById(R.id.tv_item_index);
			
			ImageView mRemoveBtn = (ImageView) convertView.findViewById(R.id.iv_remove_btn);
			
				
			
			holder.title = mTitle;
			holder.artist = mArtist;
			holder.index = mIndex;
			holder.remove = mRemoveBtn;
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		/*Log.i(TAG, mPlaylist.get(position).getTitle());
		Log.i(TAG, mPlaylist.get(position).getArtist());
		*/
		Song song = mPlaylist.get(position);
		holder.index.setText(String.valueOf(position + 1));
		holder.title.setText(song.getTitle());
		//TODO 需要将所有的artist都显示
		if(song.getArtists() != null && song.getArtists().size() > 0){
			holder.artist.setText(song.getArtists().get(0).getName());
		}
		holder.remove.setOnClickListener(new OnClickListener() {
			int mPosition = position;
			@Override
			public void onClick(View v) { 
				Log.i(TAG, "REMOVE:" + mPosition);
				mPlaylist.remove(mPosition);
				notifyDataSetChanged();
				Intent intent = new Intent();
				intent.setAction(Constants.ACTION_PLAYLIST_REFRESH);
				mContext.sendBroadcast(intent);
			}
		});
		return convertView;
		
	}
	
	private static class ViewHolder{
		public TextView index;
		public TextView title;
		public TextView artist;
		public ImageView remove;
	}

}
