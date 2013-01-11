package com.cupfish.music.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cupfish.music.R;
import com.cupfish.music.bean.MusicFolder;
import com.cupfish.music.bean.Song;
import com.cupfish.music.dao.MusicDao;
import com.cupfish.music.ui.adapter.FolderAdapter;

public class LocalFolderActivity extends FragmentActivity {

	private ListView mListView;
	private FolderAdapter mAdapter;
	private ArrayList<MusicFolder> folders;
	private MusicDao mMusicDao;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_folder);
		
		mMusicDao = new MusicDao(this);
		
		Intent intent = getIntent();
		folders = (ArrayList<MusicFolder>) intent.getSerializableExtra("data");

		mListView = (ListView) findViewById(R.id.folders);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MusicFolder folder = (MusicFolder) mAdapter.getItem(position);
				System.out.println(System.currentTimeMillis());
				ArrayList<Song> songs = mMusicDao.queryLocalSongsByFolder(folder.getPath());
				System.out.println(System.currentTimeMillis());
				if (songs != null && songs.size() > 0) {
					Intent intent = new Intent(LocalFolderActivity.this, LocalAllActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("data", songs);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		mAdapter = new FolderAdapter(this, folders);
		mListView.setAdapter(mAdapter);
		
	}
}
