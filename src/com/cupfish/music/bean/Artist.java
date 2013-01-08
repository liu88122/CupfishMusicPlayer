package com.cupfish.music.bean;

import java.io.Serializable;

import android.R.array;

import com.cupfish.music.utils.PinyinUtil;

public class Artist implements Serializable, Comparable<Artist>{

	private static final long serialVersionUID = 6832612502063972570L;
	private String id;
	private String name;
	private String namePinyin;
	
	public Artist(){
		
	}
	
	public Artist(String id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.namePinyin = PinyinUtil.toPinyinString(name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.namePinyin = PinyinUtil.toPinyinString(name);
	}

	@Override
	public int compareTo(Artist another) {
		
		return this.namePinyin.compareTo(another.namePinyin);
	}
	
}
