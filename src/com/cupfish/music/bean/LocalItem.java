package com.cupfish.music.bean;

import java.util.ArrayList;


public class LocalItem {

	private String name;
	private int foregroundCover;
	private int backgroundCover;
	private int itemNum;
	private Class<?> target;
	private ArrayList data;
	public int getItemNum() {
		return itemNum;
	}

	public LocalItem(String name, int foregroundCover, int backgroundCover) {
		this.name = name;
		this.foregroundCover = foregroundCover;
		this.backgroundCover = backgroundCover;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	public String getName() {
		return name;
	}

	public int getForegroundCover() {
		return foregroundCover;
	}

	public int getBackgroundCover() {
		return backgroundCover;
	}

	public void setBackgroundCover(int backgroundCover) {
		this.backgroundCover = backgroundCover;
	}

	public Class<?> getTarget() {
		return target;
	}

	public void setTarget(Class<?> target) {
		this.target = target;
	}

	public ArrayList getData() {
		return data;
	}

	public void setData(ArrayList data) {
		this.data = data;
	}
	
}
