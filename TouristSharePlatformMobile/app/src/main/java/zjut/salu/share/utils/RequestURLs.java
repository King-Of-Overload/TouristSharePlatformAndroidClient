package zjut.salu.share.utils;

/**API接口
 * Created by Alan on 2016/10/20.
 */

public class RequestURLs {
    public static final String MAIN_URL="http://10.248.142.6:8080/TouristSharePlatform/";
    public static final String SUBMIT_FEEDBACK_URL=MAIN_URL+"tripuser_submitUserFeedBack.do";//提交反馈意见
    public static final String SUBMIT_REGISTER_DATA=MAIN_URL+"tripuser_registerRemote.do";//手机端注册
    public static final String START_LOGIN_URL=MAIN_URL+"tripuser_loginMobile.do";//登录
    public static final String REMOVE_USER_LOGIN_CODE_OVERTIME=MAIN_URL+"tripuser_removeUserCodeOverTime.do";//因过期而消去登录状态
    public static final String CHECK_USER_LOGIN_STATUS_URL=MAIN_URL+"tripuser_checkUserLoginStatus.do";//检查登录情况

    public static final String GET_USER_PAYED_ORDER_LIST_URL=MAIN_URL+"tripuser_getUserPayedOrderList.do";//获取用户已付款订单
    public static final String GET_USER_UN_PAYED_ORDER_LIST_URL=MAIN_URL+"tripuser_getUserUnPayedOrderList.do";//取用户未付款订单

    public static final String GET_ALL_USER_STRATEGY_URL=MAIN_URL+"userstrategy_getAllUserStrategy.do";//获取所有用户攻略
    public static final String GET_BOUTIQUE_USER_STRATEGY_URL=MAIN_URL+"userstrategy_getBoutiqueUserStrategy.do";//获取精品用户攻略
    public static final String GET_HIGH_QUALITY_USER_STRATEGY_URL=MAIN_URL+"userstrategy_getHighQualityUserStrategy.do";//获取精品用户攻略

    public static final String GET_BANNER_BEAN_DATA_URL=MAIN_URL+"initialIndexBannerList.do";//获取主页轮播图数据
    public static final String GET_SKILL_ACADEMY_BANNER_DATA_URL=MAIN_URL+"skillacademy_initialSkillAcademyBannerList.do";//获取行摄攻略轮播图数据

    public static final String GET_SKILL_ACADEMY_DATA_URL=MAIN_URL+"skillacademy_getSkillAcademyByType.do";//获取技法学院数据

    public static final String GET_ALL_LOVE_POST_CARD_URL =MAIN_URL+"lovecard_getAllPostCardData.do" ;//获取所有明信片数据

    public static final String GET_CATEGORY_AND_PRODUCTS_URL=MAIN_URL+"product_getCategoryAndProducts.do";//获取旅游装备与分类

    public static final String GET_USER_SPACE_DATA_URL=MAIN_URL+"tripuser_getUserSpaceData.do";//获取用户空间数据
    public static final String UPDATE_USER_INFO_URL =MAIN_URL+"tripuser_updateUserInfo.do" ;//修改昵称
    public static final String UPLOAD_HEADER_IMAGE_MOBILE =MAIN_URL + "tripuser_uploadHeaderImageMobile.do";//上传头像

    public static final String GET_ALL_ALBUMS_URL =MAIN_URL+"photo_getAllAlbumData.do";//获取所有相册数据
    public static final String GET_ALBUM_PHOTOS_URL = MAIN_URL+"photo_getAlbumPhotos.do";//获取相册中图片

    public static final String GET_CURRENCY_LIST = "http://op.juhe.cn/onebox/exchange/list";//获取货币类型
    public static final String GET_CURRENCY_EXCHANGE = "http://op.juhe.cn/onebox/exchange/currency";//实时汇率转换
    public static final String GET_USER_FRIENDS = MAIN_URL+"tripuser_getUserFriends.do";//获取用户关注的人
    public static final String GET_USER_FULL_INFO = MAIN_URL+"tripuser_getUserCompleteInfo.do";//获取用户完整信息
    public static final String GET_ALL_BEST_CHOOSE_URL = MAIN_URL + "tripuser_getAllBestChoose.do";//获取所有精选数据
    public static final String LOCATE_CURRENT_POSITION = MAIN_URL+"tripuser_locateCurrentPosition.do";//获取当前所在城市
}
