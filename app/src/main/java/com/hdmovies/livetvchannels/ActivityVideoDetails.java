package com.hdmovies.livetvchannels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdmovies.livetvchannels.adapter.CommentAdapter;
import com.hdmovies.livetvchannels.adapter.RelatedAdapter;
import com.hdmovies.livetvchannels.dailymotion.DailyMotionPlay;
import com.hdmovies.livetvchannels.dailymotion.DailyMotionPlayNoPip;
import com.hdmovies.livetvchannels.favorite.DatabaseHelper;
import com.hdmovies.livetvchannels.item.ItemComment;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.hdmovies.livetvchannels.vimeo.Vimeo;
import com.hdmovies.livetvchannels.vimeo.VimeoNoPip;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityVideoDetails extends AppCompatActivity {

    Toolbar toolbar;
    WebView webViewDesc;
    TextView textViewTitle, text_time, text_view_all, txt_comment_no;
    ImageView imageViewVideo, imageViewPlay;
    LinearLayout linearLayoutMain, premium;
    ProgressBar progressBar;
    ArrayList<ItemLatest> mVideoList, mVideoListRelated;
    ItemLatest itemVideo;
    Menu menu;
    LinearLayout adLayout;
    boolean isWhichScreenNotification;
    JsonUtils jsonUtils;
    DatabaseHelper databaseHelper;
    RelatedAdapter relatedAdapter;
    ArrayList<ItemComment> mCommentList;
    CommentAdapter commentAdapter;
    RecyclerView recyclerViewRelatedVideo, recyclerViewCommentVideo;
    MyApplication myApplication;
    LinearLayout lay_detail;

    //rajan
    //musical
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private PrefManager prf;

    private ThinDownloadManager downloadManager;
    public DonutProgress circle_progress;
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        //musical
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        prf= new PrefManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        mVideoList = new ArrayList<>();
        mVideoListRelated = new ArrayList<>();
        mCommentList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(ActivityVideoDetails.this);
        myApplication = MyApplication.getInstance();

        webViewDesc = findViewById(R.id.web_desc);
        textViewTitle = findViewById(R.id.text);
        imageViewPlay = findViewById(R.id.image_play);
        imageViewVideo = findViewById(R.id.image);
        linearLayoutMain = findViewById(R.id.lay_main);
        premium = findViewById(R.id.premium);
        text_time = findViewById(R.id.text_time);
        progressBar = findViewById(R.id.progressBar);
        adLayout = findViewById(R.id.ad_view);
        recyclerViewRelatedVideo = findViewById(R.id.rv_most_video);
        recyclerViewRelatedVideo.setHasFixedSize(false);
        recyclerViewRelatedVideo.setNestedScrollingEnabled(false);
        recyclerViewRelatedVideo.setLayoutManager(new LinearLayoutManager(ActivityVideoDetails.this, LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(ActivityVideoDetails.this, R.dimen.item_offset);
        recyclerViewRelatedVideo.addItemDecoration(itemDecoration);
        text_view_all = findViewById(R.id.txt_comment_all);
        txt_comment_no = findViewById(R.id.txt_comment_no);
        lay_detail = findViewById(R.id.lay_detail);

        circle_progress = (DonutProgress) findViewById(R.id.circle_progress);

        recyclerViewCommentVideo = findViewById(R.id.rv_comment_video);
        recyclerViewCommentVideo.setHasFixedSize(false);
        recyclerViewCommentVideo.setNestedScrollingEnabled(false);
        recyclerViewCommentVideo.setLayoutManager(new LinearLayoutManager(ActivityVideoDetails.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCommentVideo.addItemDecoration(itemDecoration);

        if (getResources().getString(R.string.isRTL).equals("true")) {
            lay_detail.setBackgroundResource(R.drawable.home_title_gradient_right);
        } else {
            lay_detail.setBackgroundResource(R.drawable.home_title_gradient);
        }

        Intent intent = getIntent();
        isWhichScreenNotification = intent.getBooleanExtra("isNotification", false);
        if (!isWhichScreenNotification) {
//            if (JsonUtils.personalization_ad) {
                JsonUtils.showPersonalizedAds(adLayout, ActivityVideoDetails.this);
//            } else {
//                JsonUtils.showNonPersonalizedAds(adLayout, ActivityVideoDetails.this);
//            }

        }


        if (JsonUtils.isNetworkAvailable(ActivityVideoDetails.this)) {
            new getVideoDetail().execute(Constant.SINGLE_VIDEO_URL + Constant.LATEST_IDD);
        }

        downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);


    }

    private void downloadvideo(String videoUrl) {
        circle_progress.setVisibility(View.VISIBLE);
        Uri downloadUri = Uri.parse(videoUrl);
        final File file = new File(getApplicationContext().getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder));

        if (!file.exists())
        {
            file.mkdirs();
        }
        System.out.println("Rajan_filepath"+file.getPath());
        String filename = extractFilename(videoUrl);
        System.out.println("Rajan_filename"+filename);
        final Uri destinationUri = Uri.parse(file+ File.separator + filename);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadContext("downloader")//Optional
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Toast.makeText(ActivityVideoDetails.this,"Video Downloaded", Toast.LENGTH_LONG).show();
                        circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");

//                        //scan file
//                        File yourFile = new File(String.valueOf(destinationUri));
//
//                        try {
//                            scanFile(yourFile.getAbsolutePath());
//                        } catch (Exception e) {
//                            System.out.println("filescan"+e);
//                        }

                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        System.out.println("Rajan_download_error"+errorCode+errorMessage);
                        Toast.makeText(ActivityVideoDetails.this,"Download Failed", Toast.LENGTH_LONG).show();
                        circle_progress.setVisibility(View.GONE);
                        circle_progress.setProgress((float) 0);
                        circle_progress.setText(String.valueOf(0) + "%");
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
//                        System.out.println("Meera_onProgress"+ progress);
                        circle_progress.setProgress((float) progress);
                        circle_progress.setText(String.valueOf(progress) + "%");
                    }
                });

        downloadManager.add(downloadRequest);
    }

    public static String extractFilename(String path) {
        Matcher matcher = Pattern.compile("^[/\\\\]?(?:.+[/\\\\]+?)?(.+?)[/\\\\]?$").matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public void storagepermission(String videoUrl) {
        //imei
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(ActivityVideoDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityVideoDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVideoDetails.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(ActivityVideoDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVideoDetails.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(ActivityVideoDetails.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
                editor.commit();


            } else {
                //You already have the permission, just go ahead.
                downloadvideo(itemVideo.getLatestVideoUrl());
            }
        }
        else{
            downloadvideo(itemVideo.getLatestVideoUrl());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_CONSTANT : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    downloadvideo(itemVideo.getLatestVideoUrl());
//					Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();

                } else {

//					Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getVideoDetail extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutMain.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            linearLayoutMain.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemLatest objItem = new ItemLatest();

                        objItem.setLatestId(objJson.getString(Constant.LATEST_ID));
                        objItem.setLatestCategoryName(objJson.getString(Constant.LATEST_CAT_NAME));
                        objItem.setLatestCategoryId(objJson.getString(Constant.LATEST_CATID));
                        objItem.setLatestVideoUrl(objJson.getString(Constant.LATEST_VIDEO_URL));
                        objItem.setLatestVideoPlayId(objJson.getString(Constant.LATEST_VIDEO_ID));
                        objItem.setLatestVideoName(objJson.getString(Constant.LATEST_VIDEO_NAME));
                        objItem.setLatestDuration(objJson.getString(Constant.LATEST_VIDEO_DURATION));
                        objItem.setLatestDescription(objJson.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                        objItem.setLatestVideoImgBig(objJson.getString(Constant.LATEST_IMAGE_URL));
                        objItem.setLatestVideoType(objJson.getString(Constant.LATEST_TYPE));
                        objItem.setLatestVideoRate(objJson.getString(Constant.LATEST_RATE));
                        objItem.setLatestVideoView(objJson.getString(Constant.LATEST_VIEW));
                        objItem.setLatestPremium(objJson.getString(Constant.TAG_PREMIUM));
                        objItem.setLatestResolution(objJson.getString(Constant.TAG_RESOLUTION));

                        mVideoList.add(objItem);

                        JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_ARRAY);

                        for (int j = 0; j < jsonArrayChild.length(); j++) {
                            JSONObject objChild = jsonArrayChild.getJSONObject(j);
                            ItemLatest objItem2 = new ItemLatest();

                            objItem2.setLatestId(objChild.getString(Constant.LATEST_ID));
                            objItem2.setLatestCategoryName(objChild.getString(Constant.LATEST_CAT_NAME));
                            objItem2.setLatestCategoryId(objChild.getString(Constant.LATEST_CATID));
                            objItem2.setLatestVideoUrl(objChild.getString(Constant.LATEST_VIDEO_URL));
                            objItem2.setLatestVideoPlayId(objChild.getString(Constant.LATEST_VIDEO_ID));
                            objItem2.setLatestVideoName(objChild.getString(Constant.LATEST_VIDEO_NAME));
                            objItem2.setLatestDuration(objChild.getString(Constant.LATEST_VIDEO_DURATION));
                            objItem2.setLatestDescription(objChild.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                            objItem2.setLatestVideoImgBig(objChild.getString(Constant.LATEST_IMAGE_URL));
                            objItem2.setLatestVideoType(objChild.getString(Constant.LATEST_TYPE));
                            objItem2.setLatestVideoRate(objChild.getString(Constant.LATEST_RATE));
                            objItem2.setLatestVideoView(objChild.getString(Constant.LATEST_VIEW));
                            objItem2.setLatestPremium(objChild.getString(Constant.TAG_PREMIUM));

                            mVideoListRelated.add(objItem2);
                        }

                        JSONArray jsonArrayCmt = objJson.getJSONArray(Constant.COMMENT_ARRAY);

                        int k = jsonArrayCmt.length() >= 3 ? 3 : jsonArrayCmt.length();
                        for (int j = 0; j < k; j++) {
                            JSONObject objComment = jsonArrayCmt.getJSONObject(j);
                            ItemComment itemComment = new ItemComment();

                            itemComment.setCommentId(objComment.getString(Constant.COMMENT_ID));
                            itemComment.setCommentName(objComment.getString(Constant.COMMENT_NAME));
                            itemComment.setCommentMsg(objComment.getString(Constant.COMMENT_MSG));

                            mCommentList.add(itemComment);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResultSlider();
            }
        }
    }

    private void setResultSlider() {

        itemVideo = mVideoList.get(0);

        if(itemVideo.getLatestVideoType().contains("server_url") || itemVideo.getLatestVideoType().contains("local")) {
            toolbar.getMenu().findItem(R.id.menu_share).setVisible(true);
        }
        textViewTitle.setText(itemVideo.getLatestVideoName());
        webViewDesc.setBackgroundColor(0);
        webViewDesc.setFocusableInTouchMode(false);
        webViewDesc.setFocusable(false);
        WebSettings webSettings = webViewDesc.getSettings();
        webSettings.setDefaultFontSize(14);
        webViewDesc.getSettings().setDefaultTextEncodingName("UTF-8");

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = itemVideo.getLatestDescription();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/Poppins-Medium_0.ttf\")}body{font-family: MyFont;color: #666666;line-height:1.4}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webViewDesc.loadDataWithBaseURL(null, text, mimeType, encoding, null);
        text_time.setText(itemVideo.getLatestDuration());

        if (itemVideo.getLatestPremium().equalsIgnoreCase("Y")) {
            premium.setVisibility(View.VISIBLE);
        }

        switch (itemVideo.getLatestVideoType()) {
            case "local":
                Glide.with(ActivityVideoDetails.this).load(itemVideo.getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
            case "server_url":
                Glide.with(ActivityVideoDetails.this).load(itemVideo.getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
            case "youtube":
                Glide.with(ActivityVideoDetails.this).load(Constant.YOUTUBE_IMAGE_FRONT + itemVideo.getLatestVideoPlayId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
            case "dailymotion":
                Glide.with(ActivityVideoDetails.this).load(Constant.DAILYMOTION_IMAGE_PATH + itemVideo.getLatestVideoPlayId()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
            case "vimeo":
                Glide.with(ActivityVideoDetails.this).load(itemVideo.getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
            case "embed":
                Glide.with(ActivityVideoDetails.this).load(itemVideo.getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewVideo);
                break;
        }

        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (myApplication.getIsLogin()) {
                    if(itemVideo.getLatestPremium().equalsIgnoreCase("Y")) {
//                    Toast.makeText(ActivityVideoDetails.this, "Video is premium", Toast.LENGTH_SHORT).show();

//            System.out.println("Rajan_login_user_id" + loginobject.getString(Constant.TAG_USER_ID));
//            prf.setString(Constant.TAG_USER_ID, loginobject.getString(Constant.TAG_USER_ID));
//            prf.setString(Constant.TAG_USER_NAME, editText_email.getText().toString());
//            prf.setString(Constant.TAG_PLANID, loginobject.getString(Constant.TAG_PLANID));
//            prf.setString(Constant.TAG_PLANACTIVE, loginobject.getString(Constant.TAG_PLANACTIVE));
//            prf.setString(Constant.TAG_PLANDAYS, loginobject.getString(Constant.TAG_PLANDAYS));
//            prf.setString(Constant.TAG_PLANSTART, loginobject.getString(Constant.TAG_PLANSTART));
//            prf.setString(Constant.TAG_PLANEND, loginobject.getString(Constant.TAG_PLANEND));

                        //Input date in String format
                        System.out.println("Rajan_planend"+prf.getString(Constant.TAG_PLANEND));
                        String input = prf.getString(Constant.TAG_PLANEND);
                        System.out.println("Rajan_detailvideo_enddate"+input);
                        //Date/time pattern of input date
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //Date/time pattern of desired output date
                        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                        Date date;
                        String output = null;
                        try {
                            //Conversion of input String to date
                            date = df.parse(input);
                            Date date1 = Calendar.getInstance().getTime();
                            System.out.println("Rajan_Current time => " + date1);

                            if (date.compareTo(date1) > 0) {
                                System.out.println("Rajan_app_Date is after Date1");
                                playvideo(view);
                            } else if (date.compareTo(date1) < 0) {
                                System.out.println("Rajan_app_Date is before Date1");
                                Toast.makeText(ActivityVideoDetails.this, "Video is premium...Active a Plan", Toast.LENGTH_SHORT).show();

                                Intent subcription = new Intent(ActivityVideoDetails.this, SubcriptionActivity.class);
                                startActivity(subcription);

                            } else if (date.compareTo(date1) == 0) {
                                System.out.println("app_Date is equal to Date1");
                                Toast.makeText(ActivityVideoDetails.this, "Video is premium...Active a Plan", Toast.LENGTH_SHORT).show();

                                Intent subcription = new Intent(ActivityVideoDetails.this, SubcriptionActivity.class);
                                startActivity(subcription);
                            }
                            //old date format to new date format
//                output = outputformat.format(date);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }

                    } else {
                        playvideo(view);
                    }
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), ActivitySignInNew.class);
//                    startActivity(intent);
//                    finish();
//                }
            }
        });


        relatedAdapter = new RelatedAdapter(ActivityVideoDetails.this, mVideoListRelated);
        recyclerViewRelatedVideo.setAdapter(relatedAdapter);

        if (mCommentList.size() == 0) {
            txt_comment_no.setVisibility(View.VISIBLE);
        }
        commentAdapter = new CommentAdapter(ActivityVideoDetails.this, mCommentList);
        recyclerViewCommentVideo.setAdapter(commentAdapter);

        text_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.LATEST_CMT_IDD = itemVideo.getLatestId();
                skipActivity(ActivityComment.class);
            }
        });

    }

    private void playvideo(View view) {
        switch (itemVideo.getLatestVideoType()) {
            case "local": {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Do something for lollipop and above versions
                    Intent intent = new Intent(ActivityVideoDetails.this,PlayerActivity.class);
                    intent.putExtra("id","1");
                    if (itemVideo.getLatestVideoUrl().contains("m3u8") || itemVideo.getLatestVideoUrl().contains("M3U8")) {
                        intent.putExtra("isLive", true);
                        intent.putExtra("type","m3u8");
                    } else {
                        intent.putExtra("type","mp4");
                    }
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
                    intent.putExtra("resolution",itemVideo.getLatestResolution());

                    try {
                        String filename = extractFilename(itemVideo.getLatestVideoUrl());

                        final File file = new File(getApplicationContext().getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder) + File.separator + filename);

                        if (file.exists()) {
                            intent.putExtra("url", getApplicationContext().getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder) + File.separator + filename);
                        } else {
                            intent.putExtra("url", itemVideo.getLatestVideoUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        intent.putExtra("url", itemVideo.getLatestVideoUrl());
                    }
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityVideoDetails.this,PlayerActivity.class);
                    intent.putExtra("id","1");
                    intent.putExtra("url",itemVideo.getLatestVideoUrl());
                    if (itemVideo.getLatestVideoUrl().contains("m3u8") || itemVideo.getLatestVideoUrl().contains("M3U8")) {
                        intent.putExtra("isLive", true);
                        intent.putExtra("type","m3u8");
                    } else {
                        intent.putExtra("type","mp4");
                    }
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
                    intent.putExtra("resolution",itemVideo.getLatestResolution());
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                break;
            }
            case "server_url": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(ActivityVideoDetails.this,PlayerActivity.class);
                    intent.putExtra("id","1");

                    try {
                        String filename = extractFilename(itemVideo.getLatestVideoUrl());

                        final File file = new File(getApplicationContext().getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder) + File.separator + filename);

                        if (file.exists()) {
                            intent.putExtra("url", getApplicationContext().getFilesDir() + File.separator + getResources().getString(R.string.downloadfolder) + File.separator + filename);
                        } else {
                            intent.putExtra("url", itemVideo.getLatestVideoUrl());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        intent.putExtra("url", itemVideo.getLatestVideoUrl());
                    }

                    if (itemVideo.getLatestVideoUrl().contains("m3u8") || itemVideo.getLatestVideoUrl().contains("M3U8")) {
                        intent.putExtra("isLive", true);
                        intent.putExtra("type","m3u8");
                    } else {
                        intent.putExtra("type","mp4");
                    }
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
                    intent.putExtra("resolution",itemVideo.getLatestResolution());
                    startActivity(intent);

//                    // Do something for lollipop and above versions
//                    Intent i = new Intent();
////                    i.setClass(ActivityVideoDetails.this, PipServerActivity.class);
//                    i= new Intent(ActivityVideoDetails.this,SimpleExoPlayerActivity.class);
//                    i.putExtra("duration", "1");
//                    i.putExtra("local","");
//
//                    i.putExtra("id", itemVideo.getLatestVideoUrl());
//                    i.putExtra("title", itemVideo.getLatestVideoName());
//                    i.putExtra("resolution",itemVideo.getLatestResolution());
////                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActivityVideoDetails.this,PlayerActivity.class);
                    intent.putExtra("id","1");
                    intent.putExtra("url",itemVideo.getLatestVideoUrl());
                    if (itemVideo.getLatestVideoUrl().contains("m3u8") || itemVideo.getLatestVideoUrl().contains("M3U8")) {
                        intent.putExtra("isLive", true);
                        intent.putExtra("type","m3u8");
                    } else {
                        intent.putExtra("type","mp4");
                    }
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
                    intent.putExtra("resolution",itemVideo.getLatestResolution());
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            }
            case "youtube": {
                Intent i = new Intent(ActivityVideoDetails.this, PictureInPictureActivity.class);
//                Intent i = new Intent(ActivityVideoDetails.this, YoutubePlay.class);
                i.putExtra("id", itemVideo.getLatestVideoPlayId());
                startActivity(i);
                break;
            }
            case "dailymotion": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Do something for lollipop and above versions
                    Intent i = new Intent(ActivityVideoDetails.this, DailyMotionPlay.class);
                    i.putExtra("id", itemVideo.getLatestVideoPlayId());
                    startActivity(i);
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ActivityVideoDetails.this, DailyMotionPlayNoPip.class);
                    i.putExtra("id", itemVideo.getLatestVideoPlayId());
                    startActivity(i);
                }
                break;
            }
            case "vimeo": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Do something for lollipop and above versions
                    Intent i = new Intent(ActivityVideoDetails.this, Vimeo.class);
                    i.putExtra("id", itemVideo.getLatestVideoPlayId());
                    startActivity(i);
                } else {
                    // do something for phones running an SDK before lollipop
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ActivityVideoDetails.this, VimeoNoPip.class);
                    i.putExtra("id", itemVideo.getLatestVideoPlayId());
                    startActivity(i);
                }
                break;
            }
            case "embed": {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(ActivityVideoDetails.this,EmbedActivity.class);
                    intent.putExtra("id","1");
                    intent.putExtra("url",itemVideo.getLatestVideoUrl());
                    intent.putExtra("isLive", false);
                    intent.putExtra("type","website");
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
                    startActivity(intent);

//                    // Do something for lollipop and above versions
//                    Intent i = new Intent();
////                    i.setClass(ActivityVideoDetails.this, PipServerActivity.class);
//                    i= new Intent(ActivityVideoDetails.this,SimpleExoPlayerActivity.class);
//                    i.putExtra("duration", "1");
//                    i.putExtra("local","");
//
//                    i.putExtra("id", itemVideo.getLatestVideoUrl());
//                    i.putExtra("title", itemVideo.getLatestVideoName());
//                    i.putExtra("resolution",itemVideo.getLatestResolution());
////                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                } else {
                    // do something for phones running an SDK before lollipop
                    Intent intent = new Intent(ActivityVideoDetails.this,EmbedActivity.class);
                    intent.putExtra("id","1");
                    intent.putExtra("url",itemVideo.getLatestVideoUrl());
                    intent.putExtra("isLive", false);
                    intent.putExtra("type","website");
                    intent.putExtra("image",itemVideo.getLatestVideoImgBig());
                    intent.putExtra("title",itemVideo.getLatestVideoName());
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            }
        }
    }

    private void skipActivity(Class<?> classOf) {
        Intent intent = new Intent(getApplicationContext(), classOf);
        startActivity(intent);
    }

    public void showToast(String msg) {
        Toast.makeText(ActivityVideoDetails.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        isFavourite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_fav:
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Constant.LATEST_IDD)) {
                    databaseHelper.removeFavouriteById(Constant.LATEST_IDD);
                    menu.getItem(0).setIcon(R.drawable.ic_hearts_white);
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, Constant.LATEST_IDD);
                    fav.put(DatabaseHelper.KEY_TITLE, itemVideo.getLatestVideoName());
                    fav.put(DatabaseHelper.KEY_IMAGE, itemVideo.getLatestVideoImgBig());
                    fav.put(DatabaseHelper.KEY_VIEW, itemVideo.getLatestVideoView());
                    fav.put(DatabaseHelper.KEY_TYPE, itemVideo.getLatestVideoType());
                    fav.put(DatabaseHelper.KEY_PID, itemVideo.getLatestVideoPlayId());
                    fav.put(DatabaseHelper.KEY_TIME, itemVideo.getLatestDuration());
                    fav.put(DatabaseHelper.KEY_CNAME, itemVideo.getLatestCategoryName());
                    fav.put(DatabaseHelper.KEY_PREMIUM, itemVideo.getLatestPremium());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    menu.getItem(0).setIcon(R.drawable.ic_hearts_filled_white);
                    Toast.makeText(ActivityVideoDetails.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_share:

//                if (myApplication.getIsLogin()) {
                    if(itemVideo.getLatestPremium().equalsIgnoreCase("Y")) {
//                    Toast.makeText(ActivityVideoDetails.this, "Video is premium", Toast.LENGTH_SHORT).show();

//            System.out.println("Rajan_login_user_id" + loginobject.getString(Constant.TAG_USER_ID));
//            prf.setString(Constant.TAG_USER_ID, loginobject.getString(Constant.TAG_USER_ID));
//            prf.setString(Constant.TAG_USER_NAME, editText_email.getText().toString());
//            prf.setString(Constant.TAG_PLANID, loginobject.getString(Constant.TAG_PLANID));
//            prf.setString(Constant.TAG_PLANACTIVE, loginobject.getString(Constant.TAG_PLANACTIVE));
//            prf.setString(Constant.TAG_PLANDAYS, loginobject.getString(Constant.TAG_PLANDAYS));
//            prf.setString(Constant.TAG_PLANSTART, loginobject.getString(Constant.TAG_PLANSTART));
//            prf.setString(Constant.TAG_PLANEND, loginobject.getString(Constant.TAG_PLANEND));

                        //Input date in String format
                        System.out.println("Rajan_planend"+prf.getString(Constant.TAG_PLANEND));
                        String input = prf.getString(Constant.TAG_PLANEND);
                        System.out.println("Rajan_detailvideo_enddate"+input);
                        //Date/time pattern of input date
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //Date/time pattern of desired output date
                        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                        Date date;
                        String output = null;
                        try {
                            //Conversion of input String to date
                            date = df.parse(input);
                            Date date1 = Calendar.getInstance().getTime();
                            System.out.println("Rajan_Current time => " + date1);

                            if (date.compareTo(date1) > 0) {
                                System.out.println("Rajan_app_Date is after Date1");
                                storagepermission(itemVideo.getLatestVideoUrl());
                            } else if (date.compareTo(date1) < 0) {
                                System.out.println("Rajan_app_Date is before Date1");
                                Toast.makeText(ActivityVideoDetails.this, "Video is premium...Active a Plan", Toast.LENGTH_SHORT).show();

                                Intent subcription = new Intent(ActivityVideoDetails.this, SubcriptionActivity.class);
                                startActivity(subcription);

                            } else if (date.compareTo(date1) == 0) {
                                System.out.println("app_Date is equal to Date1");
                                Toast.makeText(ActivityVideoDetails.this, "Video is premium...Active a Plan", Toast.LENGTH_SHORT).show();

                                Intent subcription = new Intent(ActivityVideoDetails.this, SubcriptionActivity.class);
                                startActivity(subcription);
                            }
                            //old date format to new date format
//                output = outputformat.format(date);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }

                    } else {
                        storagepermission(itemVideo.getLatestVideoUrl());
                    }
//                } else {
//                    Intent intent = new Intent(getApplicationContext(), ActivitySignIn.class);
//                    startActivity(intent);
//                    finish();
//                }
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Constant.LATEST_IDD)) {
            menu.getItem(0).setIcon(R.drawable.ic_hearts_filled_white);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_hearts_white);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isWhichScreenNotification) {
            super.onBackPressed();

        } else {
            Intent intent = new Intent(ActivityVideoDetails.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


}
