杯里鱼音乐播放器 <br/>
字体下载地址：http://www.cupfish.com/files/xujinglei_font.ttf (暂时无效) <br/>

v2014.02.21 <br/>
1.在线听时返回播放界面歌曲信息不全<br/>
APK下载地址:http://pan.baidu.com/s/1qWNgCQo

v2014.02.20 <br/>
1.修正无法播放歌曲历史遗留问题<br/>
2.修正百度音乐地址获取问题<br/>

TODO:
1.下载引擎弱弱好像不工作了呀，检查一下<br/>
2014，加油~！<br/>


v2013.05.24 <br/>

1.引入SlidingMenu库, 加入SlidingMenu，需要继续完善<br/> 

v2013.05.20 <br/>

1.引入ActionBarSherlock库， 目前只用在LocalAllActivity<br/>
	http://actionbarsherlock.com/<br/>
	https://github.com/JakeWharton/ActionBarSherlock<br/>

v2013.05.17 <br/>

1.加入部分图标，及动画

v2013.05.16 <br/>

1.加入图片缓存，但用户体验不是太好，加载不是太快，继续研究有没有优化方案

v2013.05.15 <br/>
1.继续调整UI,引入lastFm API

TODO:
1.搞定图片缓存

v2013.05.14 <br/>
1.重新调整UI及架构


v2013.01.11 <br/>
1.更新MusicDao,已基本满足现阶段的查询任务 <br/>
2.文件夹模块目前已经实现，但查询效率有待提高，用户体验不是太好<br/>
3.更换歌曲默认封面<br/>
4.更新 回到MainActivity时默认回到播放界面,目前体验不是很流畅<br/>
5.在Song中加入rank字段<br/>


v2013.01.10 <br/>
1.增加 musicScanner相关模块，继续完善MusicDao<br/>

v2013.01.09 <br />
1.MusicDao继续编写，简单重构Album<br/>

v2013.01.08 <br/>
1.重构包名<br/>
2.MusicDao的编写，正在进行...<br/>

v2013.01.07<br/>
1.完美解决歌词显示 seek to问题<br/>
2.部分代码重构<br/>

v2013.01.06<br/>
1.修正 如果歌词长度过长导致换行后 拖动快进时时间不准确的bug<br/>

v2012.12.25<br/>
1.修改 splash界面背景及延迟时间<br/>
2.修改版本号<br/>

v2012.12.24<br />
WOW, HAPPY CHRISTMAS EVE!<br/>
1.LrcView终于可以按需求进行工作了，哈哈，后期继续完善<br/>
TODO:<br/>
1.下一个目标：本地文件夹管理


v2012.12.17<br />
1.增加 LocalManager，对本地听进行管理，功能有待完善<br/>
2.将原来的一个主activity重构为3个fragment构成，修改本地听相关方法，增加字母索引View，增加PinnedHeaderListView，重写LrcView(还未完成)<br/>
3.更新 相关bean类，加入titlePinyin字段 ,更新按照字母排序的方法<br/>
4.增加命令，当播放器主界面resume时获取当前播放的歌曲信息，用于刷新播放器界面<br/>
5.目前本地所有歌曲列表采用PinnedHeaderListView和字母索引方式展示，歌曲头像显示目前不可用<br/>

v2012.11.22<br />

1.LRCView功能完善,优化歌词滚动<br />
2.修正单行歌词与全屏歌词切换问题<br />


v2012.11.21<br />

1.LRCView功能基本实现,解决歌词滚动问题<br />

TODO:<br/>
1.实现LRCView<br />

v2012.11.20<br/>
1.下载引擎直接指定下载文件名称<br/>
2.恢复歌词显示字体，目前需要手动下载歌词文件到SDCARD根目录<br/>
3.修正 专辑封面显示过小问题<br/>

TODO:<br/>
1.本地歌曲下载歌词、封面<br/>
2.重写Playlist<br/>
bug:<br/>
1.歌词下载逻辑问题，目前出来空指针异常，应该是歌词还没有下载准备好就试图获取歌词语句造成(修复)<br/>
2.同名歌词保存冲突<br/>
3.同歌曲名专辑封面保存冲突<br/>
4.声音调节自定义窗口在xhdpi下显示不全<br/>

v2012.11.19<br/>
1.实现断点下载功能 ,基本功能已经完成<br/>
2.实现边下边播功能，基本功能已经完成，只需要将缓冲进度添加到播放进度中即OK<br/>
3.实现歌词全屏显示<br/>

BUG<br/>
1.splash界面界面过度问题,更新提示框点击对话框外 对话框消失(修复)<br/>
2.多线程下载(修复)<br/>
