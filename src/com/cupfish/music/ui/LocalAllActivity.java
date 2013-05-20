package com.cupfish.music.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.SubMenu;
import com.cupfish.music.R;
import com.cupfish.music.bean.Song;
import com.cupfish.music.cache.ImageFetcher;
import com.cupfish.music.cache.NewImageCache;
import com.cupfish.music.common.Constants;
import com.cupfish.music.ui.adapter.LocalAllAdapter;
import com.cupfish.music.ui.view.AlphabetSideBar;
import com.cupfish.music.ui.view.AlphabetSideBar.AlphabetClickListener;
import com.cupfish.music.ui.view.PinnedHeaderListView;
import com.cupfish.music.utils.LocalMediaUtils;

public class LocalAllActivity extends SherlockActivity {

	private static final String CACHE_DIR = "cache";
	
	private PinnedHeaderListView mSongListView;
	private AlphabetSideBar mSideBar;
	private LocalAllAdapter mAdapter;
	private TextView mCurrentSection;
	private List<Song> songs;
	private ImageFetcher mImageFetcher;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.local);
		
		mImageFetcher = new ImageFetcher(this);
		mImageFetcher.setLoadingImage(R.drawable.no_art_small);
		NewImageCache imageCache = new NewImageCache(this, CACHE_DIR);
		mImageFetcher.setImageCache(imageCache);
		songs = LocalMediaUtils.getLocalSongs(this);
		
		setupLayout();
		setupListener();
		
	}
	
	private void setupLayout(){
		mSongListView = (PinnedHeaderListView) findViewById(R.id.lv_local);
		mSideBar = (AlphabetSideBar) findViewById(R.id.side_bar);
		mCurrentSection = (TextView) findViewById(R.id.tv_current_section);
		mAdapter = new LocalAllAdapter(this, mImageFetcher, songs);
		mSongListView.setAdapter(mAdapter);
		mSongListView.setOnScrollListener(mAdapter);
		mSongListView.setHeaderView(getLayoutInflater().inflate(R.layout.music_list_item_section_title, mSongListView, false));
		mSideBar.setListView(mSongListView);
		
	}
	
	private void setupListener(){
		mSongListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Animation animation = AnimationUtils.loadAnimation(LocalAllActivity.this, R.anim.fade_out);
				animation.setDuration(300);
				view.startAnimation(animation);

				Song song = (Song) mSongListView.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", song);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.putExtra("flag", Constants.FLAG_LOCAL_MUSIC);
				intent.setAction(Constants.ACTION_PLAY);
				sendBroadcast(intent);
				
			}
		});
		
		mSideBar.setAlphabetClickListener(new AlphabetClickListener() {
			
			@Override
			public void onReleased() {
				mCurrentSection.setVisibility(View.GONE);
			}
			
			@Override
			public void onPressed(char c) {
				mCurrentSection.setVisibility(View.VISIBLE);
				mCurrentSection.setText(Character.toString(c));
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.action_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	 @Override
	    public void onResume() {
	        super.onResume();
	        mImageFetcher.setExitTasksEarly(false);
	        mAdapter.notifyDataSetChanged();
	        
			getSupportActionBar().setHomeButtonEnabled(true);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        mImageFetcher.setExitTasksEarly(true);
	        mImageFetcher.flushCache();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        mImageFetcher.closeCache();
	    }
	
}
