package com.cupfish.musicplayer.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Album implements Serializable{

	private static final long serialVersionUID = -4909477253560059312L;
	
	private String id;
	private String title;
	private List<Artist> authorList = new ArrayList<Artist>();
	private String coverImg;
	private String desc;
	private HashMap<String, String> songs;

	
	
	public List<Artist> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(List<Artist> authorList) {
		this.authorList = authorList;
	}

	public HashMap<String, String> getSongs() {
		return songs;
	}

	public void setSongs(HashMap<String, String> songs) {
		this.songs = songs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	

}