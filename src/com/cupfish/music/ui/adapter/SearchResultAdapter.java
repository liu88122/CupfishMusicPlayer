package com.cupfish.music.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.global.Constants;

public class SearchResultAdapter extends BaseAdapter {

	private List<Song> mSongs;
	private Context mContext;
	
	public SearchResultAdapter(Context context, List<Song> songs){
		mContext = context;
		mSongs = songs;
	}
	
	@Override
	public int getCount() {
		return mSongs.size();
	}

	@Override
	public Object getItem(int position) {
		return mSongs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, null);
			TextView mIndex = (TextView) convertView.findViewById(R.id.tv_item_index);
			TextView mTitle = (TextView) convertView.findViewById(R.id.tv_title);
			TextView mArtist = (TextView) convertView.findViewById(R.id.tv_artist);
			TextView mAlbum = (TextView) convertView.findViewById(R.id.tv_album);
			View mDownload = convertView.findViewById(R.id.ll_download);
			holder = new ViewHolder();
			holder.index = mIndex;
			holder.title = mTitle;
			holder.artist = mArtist;
			holder.album = mAlbum;
			holder.download = mDownload;
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.index.setText(String.valueOf(position + 1));
		Song song = mSongs.get(position);
		if(song.getTitle()!=null){
			holder.title.setText(song.getTitle());
		}
		//TODO 需要将所有的artist都显示
//		if(song.getArtist() != null	){
//			holder.artist.setText(song.getArtist());
//		}
		if(song.getAlbum() != null){
			holder.album.setText(song.getAlbum().getTitle());
		}
		holder.download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", mSongs.get(position));
				intent.putExtras(bundle);
				intent.setAction(Constants.ACTION_ADD_DOWNLOAD_TASK);
				mContext.sendBroadcast(intent);
				Toast.makeText(mContext, "开始下载:" +mSongs.get(position).getTitle(), 0).show();
			}
		});
		return convertView;
	}

	private static class ViewHolder{
		public TextView index;
		public TextView title;
		public TextView artist;
		public TextView album;
		public View download;
	}
	
}
