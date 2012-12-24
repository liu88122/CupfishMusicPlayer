package com.cupfish.musicplayer.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cupfish.musicplayer.bean.MusicFolder;
import com.cupfish.musicplayer.ui.adapter.FolderAdapter;
import com.cupfish.musicplayer.utils.LocalManager;

public class FolderFragment extends ListFragment {

	private ListView mListView;
	private FolderAdapter mAdapter;
	private List<MusicFolder> folders = new ArrayList<MusicFolder>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mListView = getListView();
		mAdapter = new FolderAdapter(getActivity(), folders);
		mListView.setAdapter(mAdapter);
		new ScanThread().start();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public class ScanThread extends Thread{
		@Override
		public void run() {
			File dir = Environment.getExternalStorageDirectory();
			LocalManager.searchMusicFolder(dir, folders);
			mAdapter.setFolders(folders);
		}
	}
}
