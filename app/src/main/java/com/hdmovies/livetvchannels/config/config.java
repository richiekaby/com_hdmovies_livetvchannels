package com.hdmovies.livetvchannels.config;

import com.hdmovies.livetvchannels.BuildConfig;

public class config {

    public static final String main = "http://www.battleworld.in/netklix";
    //    public static String mainurl = "http://www.battleworld.in/netklix/play/";
    public static final String mainurl = "http://www.battleworld.in/netklix/";

    public static final String privacypolicy = "https://sites.google.com/view/primeflixapp/home";

    public static final String paytmchecksum = mainurl + "paytm/";

    public static final String appurl = "http://www.battleworld.in/netklix/app.apk";

    // Envato codecanyon purchase code
    public static final String PURCHASE_CODE = BuildConfig.PURCHASE_CODE;

    public static final String currency = "INR";

    public static final boolean screenrecording = false;

    //Demo app
    //if yes type "true", for no type "false"
    public static final boolean demo = true;

    //Payment Gateway
    //Type true to visible
    //Type false to invisible
    public static final boolean paytm = true;
    public static final boolean paypal = true;
    public static final boolean instamojo = true;
    public static final boolean razorpay = true;
    public static final boolean google = true;
    public static final boolean paykun = false;
    public static final boolean traknpay = false;
    public static final boolean flutterwave = true;

    // Paytm
    // Test API Details
//    public static String MID = "EJHiMn10015192456115";
//    public static String WEBSITE = "WEBSTAGING";
//    public static String INDUSTRY_TYPE_ID = "Retail";
//    public static String CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";

    // Production
    public static final String MID = "cHQrqq17392877779909";
    public static final String WEBSITE = "DEFAULT";
    public static final String INDUSTRY_TYPE_ID = "Retail";
    public static final String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";

    // Razorpay
    public static String ApiKey = "rzp_live_IEZg8qb41ELHfZ";
    public static String Razorpay_currency = "INR";

    // Googlepay
    public static String UPI = "rajan@ok";
    public static String MERCHANTNAME = "Rajan";

//    // Paykun
//    public static String merchantIdLive="114479973849978"; // merchant id for live mode application id  = com.paykunsandbox.live
//    public static String accessTokenLive="8F070CD9FF24BEC8FC77F1FD9CAA1A40"; // access token for live mode application id  = com.paykunsandbox.live
//
//    // Traknpay
//    public static String PG_API_KEY = "xxxxx-xxxxx-xxxxx-xxxxx-xxxxx";

    // FlutterWave
    public static final String publicKey = "FLWPUBK_TEST-d79b7c339e4aa35d437a29217be711ac-X";
    public static final String encryptionKey = "FLWSECK_TEST8ed2b2763a23";

}
