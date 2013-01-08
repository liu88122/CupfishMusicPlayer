package com.cupfish.music.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.widget.AnalogClock;

import com.cupfish.music.utils.PinyinUtil;

public class Song implements Serializable, Comparable<Song> {

	private String _id;
	private String songId;
	private String title;
	private String titlePinyin;
	private String artist;
	private Album album;
	private String songPath;
	private String songUrl;
	private String lrcPath;
	private String lrcUrl;
	private String audioType;
	private long duration;
	private ArrayList<Artist> artists = new ArrayList<Artist>();
	
	public Song() {
		
	}

	public Song(String _id, String sId, String title, String artist, Album album, String sPath, String sUrl, String lrcPath, String lrcUrl,
			String audioType, long duration, ArrayList<Artist> artists) {
		super();
		this._id = _id;
		this.songId = sId;
		this.title = title;
		this.titlePinyin = PinyinUtil.toPinyinString(title);
		this.artist = artist;
		this.album = album;
		this.songPath = sPath;
		this.songUrl = sUrl;
		this.lrcPath = lrcPath;
		this.lrcUrl = lrcUrl;
		this.audioType = audioType;
		this.duration = duration;
		this.artists = artists;
	}



	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSongId() {
		return songId;
	}

	public void setSongId(String sId) {
		this.songId = sId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.titlePinyin = PinyinUtil.toPinyinString(title);
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String getSongPath() {
		return songPath;
	}

	public void setSongPath(String sPath) {
		this.songPath = sPath;
	}

	public String getSongUrl() {
		return songUrl;
	}

	public void setSongUrl(String sUrl) {
		this.songUrl = sUrl;
	}

	public String getLrcPath() {
		return lrcPath;
	}

	public void setLrcPath(String lrcPath) {
		this.lrcPath = lrcPath;
	}

	public String getLrcUrl() {
		return lrcUrl;
	}

	public void setLrcUrl(String lrcUrl) {
		this.lrcUrl = lrcUrl;
	}

	public String getAudioType() {
		return audioType;
	}

	public void setAudioType(String audioType) {
		this.audioType = audioType;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public ArrayList<Artist> getArtists() {
		return artists;
	}

	public void setArtists(ArrayList<Artist> artists) {
		this.artists = artists;
	}

	public String getTitlePinyin() {
		return titlePinyin;
	}

	@Override
	public int compareTo(Song another) {
		
		char thisChar = this.titlePinyin.charAt(0);
		char anotherChar = another.getTitlePinyin().charAt(0);
		if( thisChar== '#' && anotherChar != '#'){
			return 1;
		}else if(thisChar != '#' &&  anotherChar == '#'){
			return -1;
		}else if(thisChar == '#' && anotherChar == '#'){
			return 0;
		}
		return this.titlePinyin.compareTo(another.titlePinyin);
	}

}