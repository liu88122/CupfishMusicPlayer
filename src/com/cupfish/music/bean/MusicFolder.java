package com.cupfish.music.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicFolder implements Parcelable{

	private String title;
	private String path;
	private int count;
	
	public MusicFolder(){}
	
	public MusicFolder(String title, String path, int count) {
		super();
		this.title = title;
		this.path = path;
		this.count = count;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(path);
		dest.writeInt(count);
	}

	public static final Parcelable.Creator<MusicFolder> CREATOR = new Creator<MusicFolder>() {

		@Override
		public MusicFolder[] newArray(int size) {
			
			return new MusicFolder[size];
		}

		@Override
		public MusicFolder createFromParcel(Parcel source) {
			String title = source.readString();
			String path = source.readString();
			int count = source.readInt();
			MusicFolder folder = new MusicFolder(title, path, count);
			return folder;
		}
	};
	
}
