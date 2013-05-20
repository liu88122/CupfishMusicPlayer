package com.cupfish.music.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;

import com.cupfish.music.R;
import com.cupfish.music.ui.LocalAllActivity;
import com.cupfish.music.ui.anim.RotateAnimation;

public class LocalMusicFragment extends Fragment implements OnClickListener {

	private View mLocalContent;
	private ViewGroup mItemSongs;
	private ViewGroup mItemArtist;
	private ViewGroup mItemAlbum;
	private ViewGroup mItemRecent;
	private ViewGroup mItemPlaylist;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLocalContent = inflater.inflate(R.layout.local5, container, false);
		setupLayout();
		setupListener();
		return mLocalContent;
	}

	private void setupLayout() {
		mItemSongs = (ViewGroup) mLocalContent.findViewById(R.id.rl_item_songs);
		mItemArtist = (ViewGroup) mLocalContent.findViewById(R.id.rl_item_artist);
		mItemAlbum = (ViewGroup) mLocalContent.findViewById(R.id.rl_item_album);
		mItemRecent = (ViewGroup) mLocalContent.findViewById(R.id.rl_item_recent);
		mItemPlaylist = (ViewGroup) mLocalContent.findViewById(R.id.rl_item_playlist);
	}

	private void setupListener() {
		mItemSongs.setOnClickListener(this);
		mItemArtist.setOnClickListener(this);
		mItemAlbum.setOnClickListener(this);
		mItemRecent.setOnClickListener(this);
		mItemPlaylist.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		rotateView((ViewGroup) v);
		
	}

	private void rotateView(final ViewGroup view) {
		final int id = view.getId();
		final ArrayList<View> childs = new ArrayList<View>();
		for (int i = 0; i < view.getChildCount(); i++) {
			View child = view.getChildAt(i);
			child.setVisibility(View.INVISIBLE);
			childs.add(child);
		}

		AnimationSet animSet = new AnimationSet(true);
		RotateAnimation rotAnim = new RotateAnimation(view.getWidth() / 2, view.getHeight() / 2, RotateAnimation.ROTATE_DECREASE);


		animSet.addAnimation(rotAnim);
		animSet.setDuration(500);
		animSet.setZAdjustment(Animation.ZORDER_TOP);
		view.bringToFront();
		animSet.setFillAfter(false);

		view.startAnimation(animSet);

		animSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				for (View child : childs) {
					child.setVisibility(View.VISIBLE);
				}
				startNewPage(id);
			}
		});
	}

	protected void startNewPage(int id) {
		switch(id){
		case R.id.rl_item_songs:
			Intent intent = new Intent(getActivity(), LocalAllActivity.class);
			startActivity(intent);
			break;
		}
	}

}
