# TouristSharePlatformAndroidClient
芳草寻源旅游分享平台Android客户端

解决的主要问题：设计一个融入社交元素与资源分享功能的多方面旅游平台，用户能随时以游记、相册等形式分享旅游心得，同时提供目的地分类、个人喜好推荐、AR等功能，满足用户旅行各方面需求。具体要求：（1）、实现旅行游记、相册、旅行装备、摄影资源共享、目的地分类与检索、LBS、用户社交、上传下载、内容分享、数据挖掘与分析等核心模块功能。
（2）、基于构件与框架进行主体设计，融入设计模式，注重性能优化，基于异步思想构建，保持系统稳定，注重应用程序的界面美观，提高实用性与友好性。

1、服务器端：服务器端使用Struts2+Spring+Hibernate框架进行整合开发。遵循MVC开发模式。
2、网页端：前端业务逻辑采用AngularJS+ Grunt进行设计，使用Bootstrap+ CSS进行界面设计。
3、Android端：采用Okhttp+RxJava进行搭建，界面遵循Material Design设计规范，遵循MVC开发模式。
4、数据库：编写触发器减轻服务器压力。

重点：
1、服务器端多文件与图片的上传与下载。
2、数据库表与表之间多对多关系的处理。
3、服务器端数据分析的代码实现。
4、数据库触发器的性能优化。
5、Android客户端安全性与内存性能的优化。
6、Android响应式编程数据的调度处理。
7、LBS与AR功能的集成。
8、Android与Web界面的兼容性处理。

根据前期的需求分析与概要设计，设计出基本框架，基于构件进行开发，每一部分构件完成后进行反复测试。直到最终实现全部功能。
拟实现的系统功能模块如下：
游记攻略、相册：支持网页端与Android的编辑，用户可以撰写或上传图文并茂的游记攻略与相册集到服务器当中，其他用户可以收藏，下载与评论。
轻游记：支持以小视频，短文本的形式发表旅游信息。
目的地：实现各城市美食、名胜古迹、生活信息、交通的查找。
旅行装备：用户可以发布二手或全新旅行商品，可以在线支付与商品收藏。
沙发客：包括旅行摄影技巧的分享，互赠明信片模块。
附近：基于LBS实现定位以及身边的人功能，融入增强虚拟现实。
用户空间：融入社交元素，用户之间可以互相关注，主页显示好友动态，时间轴等功能。

开发工具与平台选择：
服务器端使用java EE+ Tomcat实现，数据库使用微软 SQL Server数据库，Android使用Android Studio构建。


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/local_index.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/activity_friend.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/album.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/album_activity.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/album_detail.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/album_detail2.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/cellphone.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/jingxuan_strategy.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/jingxuan_topic.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/jingxuan_topic_detail_2.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/main_category.png?raw=true)


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/setting.png?raw=true)


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/share.png?raw=true)


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/strategy.png?raw=true)


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/strategy_detail.png?raw=true)


![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/strategy_detail2.png?raw=true)

![image](https://github.com/King-Of-Overload/TouristSharePlatformAndroidClient/blob/master/TouristSharePlatformMobile/app/src/main/assets/captures/trip_equip.png?raw=tr