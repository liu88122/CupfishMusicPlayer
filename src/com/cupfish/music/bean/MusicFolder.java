package com.cupfish.music.bean;

public class MusicFolder {

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
	
}
