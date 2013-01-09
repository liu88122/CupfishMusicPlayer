package com.cupfish.music.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cupfish.music.utils.PinyinUtil;

public class Album implements Serializable, Comparable<Album>{

	private static final long serialVersionUID = -4909477253560059312L;
	
	private String id;
	private String title;
	private String titlePinyin;
	private List<Artist> artists = new ArrayList<Artist>();
	private String coverPath;
	private String coverUrl;
	private String coverHdUrl;
	private String desc;
	private List<Song> songs;

	public Album(){
		
	}
	
	public Album(String id, String title, List<Artist> authorList, String coverPath, String coverUrl, String coverHdUrl,
			String desc, List<Song> songs) {
		super();
		this.id = id;
		this.title = title;
		this.titlePinyin = PinyinUtil.toPinyinString(title);
		this.artists = authorList;
		this.coverPath = coverPath;
		this.coverUrl = coverUrl;
		this.coverHdUrl = coverHdUrl;
		this.desc = desc;
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
		this.titlePinyin = PinyinUtil.toPinyinString(title);
	}

	public String getTitlePinyin() {
		return titlePinyin;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getCoverHdUrl() {
		return coverHdUrl;
	}

	public void setCoverHdUrl(String coverHdUrl) {
		this.coverHdUrl = coverHdUrl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void setSongs(List<Song> songs) {
		this.songs = songs;
	}

	@Override
	public int compareTo(Album another) {
		String thisPinyin = PinyinUtil.toPinyinString(this.title);
		String anotherPinyin = PinyinUtil.toPinyinString(another.title);
		return thisPinyin.compareTo(anotherPinyin);
	}
	
}