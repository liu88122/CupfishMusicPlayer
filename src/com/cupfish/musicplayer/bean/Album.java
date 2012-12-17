package com.cupfish.musicplayer.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cupfish.musicplayer.utils.PinyinUtil;

public class Album implements Serializable, Comparable<Album>{

	private static final long serialVersionUID = -4909477253560059312L;
	
	private String id;
	private String title;
	private String titlePinyin;
	private List<Artist> authorList = new ArrayList<Artist>();
	private String coverImg;
	private String desc;
	private HashMap<String, String> songs;

	public Album(){
		
	}
	
	public Album(String id, String title, List<Artist> authorList, String coverImg, String desc,
			HashMap<String, String> songs) {
		super();
		this.id = id;
		this.title = title;
		this.titlePinyin = PinyinUtil.toPinyinString(title);
		this.authorList = authorList;
		this.coverImg = coverImg;
		this.desc = desc;
		this.songs = songs;
	}

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
		this.titlePinyin = PinyinUtil.toPinyinString(title);
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

	@Override
	public int compareTo(Album another) {
		String thisPinyin = PinyinUtil.toPinyinString(this.title);
		String anotherPinyin = PinyinUtil.toPinyinString(another.title);
		return thisPinyin.compareTo(anotherPinyin);
	}
	
}