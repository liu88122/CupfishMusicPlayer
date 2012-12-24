package com.cupfish.musicplayer.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cupfish.musicplayer.R;
import com.cupfish.musicplayer.bean.MusicFolder;
import com.cupfish.musicplayer.ui.adapter.FolderAdapter;
import com.cupfish.musicplayer.utils.LocalManager;

public class LocalFolderActivity extends FragmentActivity {

	private ListView mListView;
	private View mScanning;
	private FolderAdapter mAdapter;
	private List<MusicFolder> folders = new ArrayList<MusicFolder>();
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			mScanning.setVisibility(View.GONE);
			if(mAdapter!= null){
				mListView.setVisibility(View.VISIBLE);
				mAdapter.notifyDataSetChanged();
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_folder);
		
		mScanning = findViewById(R.id.scanning);
		mListView = (ListView) findViewById(R.id.folders);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MusicFolder folder = (MusicFolder) mAdapter.getItem(position);
				Toast.makeText(LocalFolderActivity.this, folder.getCount()+" ", 0).show();
				
			}
		});
		mAdapter = new FolderAdapter(this, folders);
		mListView.setAdapter(mAdapter);
		new ScanThread().start();
		
	}
	
	public class ScanThread extends Thread{
		@Override
		public void run() {
			File dir = Environment.getExternalStorageDirectory();
			LocalManager.searchMusicFolder(dir, folders);
			handler.sendEmptyMessage(0);
		}
	}
}
