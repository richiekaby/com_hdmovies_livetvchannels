package com.hdmovies.livetvchannels;

import androidx.lifecycle.Lifecycle;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
public class PictureInPictureActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private String[] videoIds = {"6JYIGclVQdw"};

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_in_picture_example);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            Drawable background = this.getResources().getDrawable(R.drawable.statusbar_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //screen capture off
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

//            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                            WindowManager.LayoutParams.FLAG_SECURE,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
//                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                            WindowManager.LayoutParams.FLAG_SECURE |
//                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
            window.setBackgroundDrawable(background);
        }

        id = getIntent().getStringExtra("id");
        initYouTubePlayerView();
    }

    private void initYouTubePlayerView() {
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.getPlayerUIController().showFullscreenButton(true);
        youTubePlayerView.getPlayerUIController().showYouTubeButton(false);
        youTubePlayerView.getPlayerUIController().showCurrentTime(true);
        youTubePlayerView.getPlayerUIController().showDuration(true);
        youTubePlayerView.getPlayerUIController().showBufferingProgress(true);
        youTubePlayerView.getPlayerUIController().showMenuButton(false);
        youTubePlayerView.getPlayerUIController().showVideoTitle(true);
        youTubePlayerView.enterFullScreen();

        getLifecycle().addObserver(youTubePlayerView);
        initPictureInPicture(youTubePlayerView);

        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(@NonNull final YouTubePlayer youTubePlayer) {
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        loadVideo(youTubePlayer, id);
                    }
                });
            }
        }, true);
    }

    private void initPictureInPicture(YouTubePlayerView youTubePlayerView) {
        ImageView pictureInPictureView = new ImageView(this);
        pictureInPictureView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_picture_in_picture_24dp));

        pictureInPictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    boolean supportsPIP = PictureInPictureActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                    if (supportsPIP)
                        PictureInPictureActivity.this.enterPictureInPictureMode();
                } else {
                    new AlertDialog.Builder(PictureInPictureActivity.this)
                            .setTitle("Can't enter picture in picture mode")
                            .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                            .show();
                }
            }
        });

        youTubePlayerView.getPlayerUIController().addView( pictureInPictureView );
    }

    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        if(getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if(isInPictureInPictureMode) {
            youTubePlayerView.enterFullScreen();
            youTubePlayerView.getPlayerUIController().showUI(false);
        } else {
            youTubePlayerView.exitFullScreen();
            youTubePlayerView.getPlayerUIController().showUI(true);
        }
    }
}
