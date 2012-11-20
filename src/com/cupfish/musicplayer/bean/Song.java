package com.cupfish.musicplayer.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Song implements Serializable {

	private String id;
	private String title;
	private String album;
	private String albumId;
	private ArrayList<Artist> authorList = new ArrayList<Artist>();
	private String url;
	private String albumCover;
	private String lrcUrl;
	private long duration;
	private boolean isLocal = false;

	public Song() {
	}

	public Song(String id, String title, String album, String albumId, ArrayList<Artist> authorList, String url, String albumCover, String lrcUrl,
			long duration, boolean isLocal) {
		super();
		this.id = id;
		this.title = title;
		this.album = album;
		this.albumId = albumId;
		this.authorList = authorList;
		this.url = url;
		this.albumCover = albumCover;
		this.lrcUrl = lrcUrl;
		this.duration = duration;
		this.isLocal = isLocal;
	}

	public ArrayList<Artist> getAuthorList() {
		return authorList;
	}

	public void setAuthorList(ArrayList<Artist> authorList) {
		this.authorList = authorList;
	}

	public String getLrcUrl() {
		return lrcUrl;
	}

	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}

	public String getAlbumCover() {
		return albumCover;
	}

	public void setAlbumCover(String albumCover) {
		this.albumCover = albumCover;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
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

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	@Override
	public boolean equals(Object o) {
		Song temp = (Song) o;
		if(this.title.equals(temp.title) && this.id == temp.id){
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return "Song [id=" + id + ", title=" + title + ", album=" + album + ", albumId=" + albumId + ", authorListSize=" + authorList.size()
				+ ", url=" + url + ", albumCover=" + albumCover + ", lrcUrl=" + lrcUrl + ", duration=" + duration + ", isLocal=" + isLocal + "]";
	}

}
