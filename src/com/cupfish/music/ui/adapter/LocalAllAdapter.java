package com.cupfish.music.ui.adapter;

import static com.cupfish.music.Constants.*;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.cache.ImageInfo;
import com.cupfish.music.cache.ImageProvider;
import com.cupfish.music.ui.view.PinnedHeaderListView;
import com.cupfish.music.ui.view.PinnedHeaderListView.PinnedHeaderAdapter;

public class LocalAllAdapter extends BaseAdapter implements PinnedHeaderAdapter, SectionIndexer, OnScrollListener{

	private List<Song> mLocalSongs;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<Character> mSections;
	private ImageProvider mImageProvider;


	public LocalAllAdapter(Context context, List<Song> mLocalSongs){
		this.mLocalSongs = mLocalSongs;
		mInflater = LayoutInflater.from(context);
		buildSections(mLocalSongs);
		mImageProvider = ImageProvider.getInstance((Activity)context);
	}
	
	private void buildSections(List<Song> mLocalSongs){
		mSections = new ArrayList<Character>();
		boolean flag = false;
		for(Song song: mLocalSongs){
			if(TextUtils.isEmpty(song.getTitlePinyin())){
				continue;
			}
			char firstLetter = song.getTitlePinyin().charAt(0);
			if(firstLetter == '#'){
				flag = true;
				continue;
			}
			Character upLetter = Character.toUpperCase(firstLetter);
			if(!mSections.contains(upLetter)){
				mSections.add(upLetter);
			}
		}
		if(flag){
			mSections.add('#');
		}
	}


	public void setData(List<Song> songs){
		mLocalSongs = songs;
		buildSections(songs);
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
			convertView = mInflater.inflate(R.layout.music_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.artist = (TextView) convertView.findViewById(R.id.tv_artist);
			holder.albumCover = (ImageView) convertView.findViewById(R.id.iv_album_cover);
			holder.section = (TextView) convertView.findViewById(R.id.tv_section_title);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
			holder.albumCover.setImageResource(R.drawable.no_art_small);
		}
		Song song = mLocalSongs.get(position);
		holder.title.setText(song.getTitle());
		
		if(!TextUtils.isEmpty(song.getArtist())){
			holder.artist.setText(song.getArtist());
		}
		
		if(!TextUtils.isEmpty(song.getTitlePinyin())){
			char firstChar = song.getTitlePinyin().charAt(0);
			int p = getPositionForSection(firstChar);
			if(position == p){
				holder.section.setText(Character.toString(firstChar).toUpperCase());
				holder.section.setVisibility(View.VISIBLE);
			}else{
				holder.section.setVisibility(View.GONE);
			}
		}

		ImageInfo mInfo = new ImageInfo();
		mInfo.type = TYPE_ARTIST;
		mInfo.size = SIZE_THUMB;
		mInfo.source = SRC_LASTFM;
		mInfo.data = new String[] { song.getArtist() };

		mImageProvider.loadImage(holder.albumCover, mInfo);
		return convertView;
	}


	static class ViewHolder{
		public TextView title;
		public TextView artist;
		public ImageView albumCover;
		public TextView section;
	}


	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		
		if (realPosition < 0) {
			return PinnedHeaderAdapter.PINNED_HEADER_GONE;
		}
		
		int section = getSectionForPosition(realPosition);// 得到此item所在的分组位置
		if (section < mSections.size() - 1) {
			int nextSectionPosition = getPositionForSection(mSections.get(section + 1));// 得到下一个分组的位置
			System.out.println("section:" + section + " next:" + nextSectionPosition);
			if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
				return PINNED_HEADER_PUSHED_UP;
			}
		}
		return PINNED_HEADER_VISIBLE;
	}


	@Override
	public void configurePinnedHeader(View view, int position) {
		int section = getSectionForPosition(position);
		TextView tv = (TextView) view.findViewById(R.id.tv_section_title);
		if (section >= 0) {
			tv.setText(Character.toString(mSections.get(section)));
		}
	}


	@Override
	public Object[] getSections() {
		return mSections.toArray();
	}


	@Override
	public int getPositionForSection(int section) {
		char c = (char) section;
		c = Character.toUpperCase(c);
		Song song;
		for(int i=0; i<mLocalSongs.size(); i++){
			song  = mLocalSongs.get(i);
			if(TextUtils.isEmpty(song.getTitlePinyin())){
				continue;
			}
			char curChar = song.getTitlePinyin().charAt(0);
			if(c == Character.toUpperCase(curChar)){
				return i;
			}
		}
		return -1;
	}


	@Override
	public int getSectionForPosition(int position) {
		Song song = mLocalSongs.get(position);
		if(song!= null && !TextUtils.isEmpty(song.getTitlePinyin())){
			char c = song.getTitlePinyin().charAt(0);
			c = Character.toUpperCase(c);
			return mSections.indexOf(c);
		}
		return -1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(view instanceof PinnedHeaderListView) {
        	((PinnedHeaderListView)view).adjustHeaderView(firstVisibleItem);
        }
		
	}

}
