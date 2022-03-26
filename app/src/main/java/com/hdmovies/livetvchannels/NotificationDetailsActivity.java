package com.hdmovies.livetvchannels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.PrefManager;

import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_ID;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_IMAGE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_LOG_ENTDATE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_MSG;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_TITLE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_URL;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_VIDEOID;

public class NotificationDetailsActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    //Prefrance
    private static PrefManager prf;

    private int success;

    Toolbar toolbar;

    private TextView titlevalue;
    private TextView msgvalue;

    private Button joinButton;
    private Button playButton;

    private ImageView topImage;

    private String notificationid;
    private String title;
    private String msg;
    private String image;
    private String videoid;
    private String url;
    private String log_entdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        prf = new PrefManager(NotificationDetailsActivity.this);

        try {
            notificationid = getIntent().getStringExtra(TAG_NOTIFICATION_ID);
            title = getIntent().getStringExtra(TAG_NOTIFICATION_TITLE);
            msg = getIntent().getStringExtra(TAG_NOTIFICATION_MSG);
            image = getIntent().getStringExtra(TAG_NOTIFICATION_IMAGE);
            videoid = getIntent().getStringExtra(TAG_NOTIFICATION_VIDEOID);
            url = getIntent().getStringExtra(TAG_NOTIFICATION_URL);
            log_entdate = getIntent().getStringExtra(TAG_NOTIFICATION_LOG_ENTDATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        toolbar = findViewById(R.id.toolbar_videos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.notification) + " Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titlevalue = (TextView) findViewById(R.id.titlevalue);
        titlevalue.setText(title);

        msgvalue = (TextView) findViewById(R.id.msgvalue);
        msgvalue.setText(msg);

        joinButton = (Button) findViewById(R.id.JoinButton);
        if (url.length()>5) {
            joinButton.setVisibility(View.VISIBLE);
        }
        playButton = (Button) findViewById(R.id.playButton);
        if (!videoid.contains("0") && videoid != null) {
            playButton.setVisibility(View.VISIBLE);
        }

        topImage = (ImageView) findViewById(R.id.matchImage);
        if (image.length()>5) {
            Glide.with(NotificationDetailsActivity.this).load(Constant.IMAGE_PATH_URL + image).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(topImage);
        }

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url)));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(NotificationDetailsActivity.this, "Not Valid Url", Toast.LENGTH_SHORT).show();
                }
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!videoid.equals("0")) {
                        Constant.LATEST_IDD=videoid;
                        Intent intent = new Intent(NotificationDetailsActivity.this, ActivityVideoDetails.class);
                        intent.putExtra("Id", Constant.LATEST_IDD);
                        intent.putExtra("isNotification", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
