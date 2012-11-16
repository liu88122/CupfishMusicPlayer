package com.cupfish.musicplayer.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.domain.Song;
import com.cupfish.musicplayer.global.BaseApp;
import com.cupfish.musicplayer.global.Constants;
import com.cupfish.musicplayer.ui.adapter.PlaylistAdapter;

public class PlaylistActivity extends Activity implements OnClickListener {

	private Button mBackBtn;
	private ListView mPlaylistView;
	private List<Song> mPlaylist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playlist);
		
		mPlaylistView = (ListView) findViewById(R.id.lv_playlist);
		mPlaylist = ((BaseApp)getApplication()).playlist;
		mPlaylistView.setAdapter(new PlaylistAdapter(this, mPlaylist));
		mPlaylistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("song", mPlaylist.get(position));
				intent.putExtras(bundle);
				intent.setAction(Constants.ACTION_PLAY);
				sendBroadcast(intent);
			}
		});
		
		mBackBtn = (Button) findViewById(R.id.bt_back);
		mBackBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_back:
			finish();
			break;
		}
	}

	
}
