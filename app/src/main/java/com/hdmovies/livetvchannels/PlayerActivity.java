package com.hdmovies.livetvchannels;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.player.PlayerFragment;


public class PlayerActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private PlayerFragment playerFragment;
    private String videoUrl;
    private Boolean isLive = false;
    private String videoType;
    private String videoTitle;
    private String videoImage;
    private int vodeoId;
    private String videoResolution;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if (scaleGestureDetector.getScaleFactor()>1){
                PlayerFragment myFragment = (PlayerFragment)getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                myFragment.setFull();
            }
            if (scaleGestureDetector.getScaleFactor()<1){
                PlayerFragment myFragment = (PlayerFragment)getSupportFragmentManager().findFragmentByTag("PlayerFragment");
                myFragment.setNormal();

            }
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            if (config.screenrecording) {
                //screen capture off
                window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_player);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        Bundle bundle = getIntent().getExtras();
        try {
            vodeoId = bundle.getInt("id",0);
            videoUrl = bundle.getString("url");
            isLive = bundle.getBoolean("isLive");
            videoType = bundle.getString("type");
            videoTitle = bundle.getString("title");
            videoImage = bundle.getString("image");
            videoResolution = bundle.getString("resolution","N");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState == null) {
            playerFragment =
                    PlayerFragment.newInstance(getVideoUrl(),isLive,videoType,videoTitle,videoImage,vodeoId,videoResolution);
            launchFragment(playerFragment);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }

    private void launchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_container, fragment, "PlayerFragment");
        fragmentTransaction.commit();


    }

    private String getVideoUrl() {
        return videoUrl;
    }

}
