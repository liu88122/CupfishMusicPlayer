package com.cupfish.music.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.cupfish.music.common.Constants;

/**
 * 新的歌词LRC类
 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
 * @2012-11-19下午4:52:32
 */
public class LRC2 {
	
	/* 由lrc文件解析出来的歌词map, key是对应的时间点，value为歌词*/
	private TreeMap<Long, String> lrcs;
	
	public LRC2(TreeMap<Long, String> lrcs){
		this.lrcs = lrcs;
	}
	
	/**
	 * 获取time对应的歌词
	 * @param time 时间点
	 * @return
	 * String 对应歌词
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午4:53:59
	 */
	public String getStatement(long time){
		long key =  lrcs.floorKey(time);
		return lrcs.get(key);
	}
	
	/**
	 * 根据当前时间获取歌词出现的下一个时间点
	 * @param time 当前时间
	 * @return
	 * long 歌词出现的下一个时间点
	 * @author <a href="liu88122@gmail.com">LiuZhongde</a>
	 * @2012-11-19 下午4:55:19
	 */
	public long getNextTimeline(long time){
		Long result = lrcs.ceilingKey(time);
		if( result != null){
			return result;
		}
		return Long.MAX_VALUE;
	}
	
	public long getCurrentTimeLine(long time){
		Long result = lrcs.floorKey(time);
		if( result != null){
			return result;
		}
		return Long.MAX_VALUE;
	}
	
	@Deprecated
	public ArrayList<String> getStatementList(){
		ArrayList<String> result = new ArrayList<String>();
		if (lrcs != null && lrcs.size() > 0) {
			for (Map.Entry<Long, String> entry : lrcs.entrySet()) {
				result.add(entry.getValue().replace(Constants.LRC_AD, "\n"));
			}
		}
		return result;
		
	}
	
	public TreeMap<Long, String> getLrcsMap(){
		return lrcs;
	}
}
