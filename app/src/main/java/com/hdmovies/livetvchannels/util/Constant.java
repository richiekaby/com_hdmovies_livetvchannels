package com.hdmovies.livetvchannels.util;


import com.hdmovies.livetvchannels.BuildConfig;

import java.io.Serializable;

public class Constant implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//server url
	public static final String SERVER_URL= BuildConfig.server_url;

	public static final String IMAGE_PATH_URL =SERVER_URL+"images/";

	public static final String CATEGORY_URL = SERVER_URL+"api.php?cat_list";

	public static final String CATEGORY_ITEM_URL = SERVER_URL+"api.php?cat_id=";

	public static final String LATEST_URL = SERVER_URL+"api.php?latest_video";

	public static final String HOME_URL = SERVER_URL+"api.php?home_videos";

	public static final String ALL_URL = SERVER_URL+"api.php?All_videos";

    public static final String SINGLE_VIDEO_URL = SERVER_URL+"api.php?video_id=";

	public static final String SEARCH_URL = SERVER_URL+"api.php?search_text=";

	public static final String ABOUT_US_URL = SERVER_URL+"api.php?settings";

	public static final String LOGIN_URL = SERVER_URL+"api.php?users_login&email=";

	public static final String LOGIN_STATUS_URL = SERVER_URL+"api.php?user_status&user_id=";

	public static final String REGISTER_URL = SERVER_URL+"api.php?user_register&name=";

	public static final String REGISTER_URL_GOOGLE = SERVER_URL+"api.php?user_register_google&name=";

	public static final String REGISTER_URL_MOBILE = SERVER_URL+"api.php?user_register_mobile&name=";

	public static final String REGISTER_URL_MOBILE_LOGIN = SERVER_URL+"api.php?user_login_mobile&name=";

	public static final String REGISTER_URL_EMAIL_LOGIN = SERVER_URL+"api.php?user_login_email&name=";

    public static final String REGISTER_URL_EMAIL_REGISTER = SERVER_URL+"api.php?user_register_email&name=";

	public static final String PROFILE_URL = SERVER_URL+"api.php?user_profile&id=";

	public static final String EARN_URL = SERVER_URL+"api.php?user_earn&id=";

	public static final String UPDATE_PROFILE_URL = SERVER_URL+"api.php?user_profile_update&user_id=";

	public static final String COMMENT_URL = SERVER_URL+"api.php?video_comment&comment_text=";

	public static final String FORGOT_URL = SERVER_URL+"api.php?forgot_pass&email=";

	public static final String YOUTUBE_IMAGE_FRONT="http://img.youtube.com/vi/";
	public static final String YOUTUBE_SMALL_IMAGE_BACK="/mqdefault.jpg";

	public static final String DAILYMOTION_IMAGE_PATH="http://www.dailymotion.com/thumbnail/video/";

	public static final String LATEST_ARRAY_NAME="ALL_IN_ONE_VIDEO";
	public static final String RELATED_ARRAY="related";
	public static final String COMMENT_ARRAY="user_comments";

	public static final String HOME_BANNER_ID="id";
	public static final String HOME_BANNER_NAME="banner_name";
	public static final String HOME_BANNER_IMAGE="banner_image";
	public static final String HOME_BANNER_LINK="banner_url";

	public static final String LATEST_ID="id";
	public static final String LATEST_CATID="cat_id";
	public static final String LATEST_CAT_NAME="category_name";
	public static final String LATEST_VIDEO_URL="video_url";
	public static final String LATEST_VIDEO_ID="video_id";
	public static final String LATEST_VIDEO_DURATION="video_duration";
	public static final String LATEST_VIDEO_NAME="video_title";
	public static final String LATEST_VIDEO_DESCRIPTION="video_description";
	public static final String LATEST_IMAGE_URL="video_thumbnail_b";
 	public static final String LATEST_TYPE="video_type";
	public static final String LATEST_VIEW="totel_viewer";
	public static final String LATEST_RATE="rate_avg";

 	public static final String CATEGORY_NAME="category_name";
	public static final String CATEGORY_CID="cid";
	public static final String CATEGORY_IMAGE="category_image";

	public static final String COMMENT_ID="video_id";
	public static final String COMMENT_NAME="user_name";
	public static final String COMMENT_MSG="comment_text";

	//for title display in CategoryItemF
	public static String CATEGORY_TITLEE;
	public static String CATEGORY_IDD;
	public static  String LATEST_IDD;
	public static  String LATEST_CMT_IDD;
	public static  int POS_ID;

	public static int GET_SUCCESS_MSG;
	public static final String MSG = "msg";
	public static final String SUCCESS = "success";
	public static final String USER_NAME = "name";
	public static final String USER_ID = "user_id";
	public static final String USER_EMAIL = "email";
	public static final String USER_PHONE = "phone";
    public static final String USER_DOB = "dob";
    public static final String USER_GENDER = "gender";

	//Rajan
	public static final String TAG_PLANID="planid";
	public static final String TAG_PLANPRICE="price";
	public static final String TAG_PLANACTIVE="planactive";
	public static final String TAG_PLANDAYS="plandays";
	public static final String TAG_PLANSTART="planstart";
	public static final String TAG_PLANEND="planend";

	public static final String TAG_PREMIUM="premium";
	public static final String TAG_RESOLUTION="resolution";

	//Gift Voucher
	public static final String TAG_VOUCHERCODE="vouchercode";

	public static final String GET_ALL_PLAN_URL =SERVER_URL+"get_all_plan.php";
	public static final String PLANID="planid";
	public static final String PLANNAME="name";
	public static final String PLANPRICE="price";
	public static final String PLANDAYS="days";

	public static final String GET_ALL_NOTIFICATION_URL =SERVER_URL+"get_all_notification.php";
	public static final String TAG_NOTIFICATION_ID="id";
	public static final String TAG_NOTIFICATION_TITLE="title";
	public static final String TAG_NOTIFICATION_MSG="msg";
	public static final String TAG_NOTIFICATION_IMAGE="image";
	public static final String TAG_NOTIFICATION_VIDEOID="videoid";
	public static final String TAG_NOTIFICATION_URL="url";
	public static final String TAG_NOTIFICATION_LOG_ENTDATE="log_entdate";

	public static final String TAG_REFERDESC="referdesc";


	public static final String APP_NAME="app_name";
	public static final String APP_IMAGE="app_logo";
	public static final String APP_VERSION="app_version";
	public static final String APP_AUTHOR="app_author";
	public static final String APP_CONTACT="app_contact";
	public static final String APP_EMAIL="app_email";
	public static final String APP_WEBSITE="app_website";
	public static final String APP_DESC="app_description";
	public static final String APP_PRIVACY="app_privacy_policy";
	public static final String APP_DEVELOP="app_developed_by";

	//public static ArrayList<ItemMostView> mList = new ArrayList<>();

	public static final String ADS_BANNER_ID="banner_ad_id";
	public static final String ADS_FULL_ID="interstital_ad_id";
	public static final String ADS_BANNER_ON_OFF="banner_ad";
	public static final String ADS_BANNER_TYPE="banner_ad_type";
	public static final String ADS_FULL_ON_OFF="interstital_ad";
	public static final String ADS_FULL_AD_TYPE="interstital_ad_type";
	public static final String ADS_PUB_ID="publisher_id";
	public static final String ADS_CLICK="interstital_ad_click";
	public static String SAVE_ADS_BANNER_ID,SAVE_ADS_FULL_ID,SAVE_ADS_BANNER_ON_OFF,SAVE_ADS_BANNER_TYPE,SAVE_ADS_FULL_ON_OFF,SAVE_ADS_FULL_TYPE,SAVE_ADS_PUB_ID,SAVE_ADS_CLICK;

}
