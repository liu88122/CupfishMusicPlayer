杯里鱼音乐播放器 <br/>
字体下载地址：http://www.cupfish.com/files/xujinglei_font.ttf  <br/>


v2012.12.25<br/>
1.修改 splash界面背景及延迟时间<br/>
2.修改版本号<br/>

git i love u, wow!<br/>

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
