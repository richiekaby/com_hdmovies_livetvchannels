package com.hdmovies.livetvchannels.player;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.hdmovies.livetvchannels.R;
import com.hdmovies.livetvchannels.databinding.FragmentPlayerBinding;


public class PlayerFragment extends Fragment {

    private PlayerViewModel mPlayerViewModel;
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ImageView ic_media_stop;
    private RelativeLayout payer_pause_play;
    private View view;
    private Boolean done =  false;
    private Boolean isLive =  false;
    private TextView text_view_exo_player_live;
    private TextView exo_duration;
    private TextView exo_live;
    private ImageView image_view_exo_player_rotation;

    private Boolean isLandscape =  true;
    private ImageView image_view_exo_player_subtitles;
    private RelativeLayout relative_layout_subtitles_dialog;
    private ImageView image_view_dialog_source_close;

    // lists
    private PrefManager pref;
    private static Integer videoId;
    private ImageView image_view_exo_player_replay_10;
    private ImageView image_view_exo_player_forward_10;
    private ImageView image_view_exo_player_back;

    //Rajan
    private static String videoUrlr;
    private static String videoResolutionr;
    private LinearLayout linear_layout_item_1080p, linear_layout_item_720p, linear_layout_item_480p, linear_layout_item_360p, linear_layout_item_240p;
    private ImageView image_view_1080p, image_view_720p, image_view_480p, image_view_360p, image_view_240p;

    @Override
    public void onResume() {
        super.onResume();
        mPlayerViewModel.play();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerViewModel.pause();
    }
    public static PlayerFragment newInstance(String videoUrl, Boolean isLive, String videoType, String videoTitle, String videoImage, Integer videoId_, String videoResolution) {
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString("videoUrl", videoUrl);
        args.putString("videoType", videoType);
        args.putString("videoTitle", videoTitle);
        args.putString("videoImage", videoImage);
        args.putBoolean("isLive", isLive);
        videoId = videoId_;
        videoUrlr = videoUrl;
        videoResolutionr = videoResolution;
        playerFragment.setArguments(args);
        return playerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPlayerBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        view = (View) binding.getRoot();
        this.pref =new PrefManager(getActivity());

        initView(binding);
        initAction();
        return view;
    }
    private void initView(FragmentPlayerBinding binding) {
        mPlayerViewModel = new PlayerViewModel(getActivity());

        image_view_exo_player_back = view.findViewById(R.id.image_view_exo_player_back);
        image_view_exo_player_replay_10 = view.findViewById(R.id.image_view_exo_player_replay_10);
        image_view_exo_player_forward_10 = view.findViewById(R.id.image_view_exo_player_forward_10);
        payer_pause_play = view.findViewById(R.id.payer_pause_play);
        relative_layout_subtitles_dialog = view.findViewById(R.id.relative_layout_subtitles_dialog);
        text_view_exo_player_live = view.findViewById(R.id.text_view_exo_player_live);
        image_view_exo_player_rotation = view.findViewById(R.id.image_view_exo_player_rotation);
        image_view_exo_player_subtitles = view.findViewById(R.id.image_view_exo_player_subtitles);
        image_view_dialog_source_close = view.findViewById(R.id.image_view_dialog_source_close);
        exo_duration = view.findViewById(R.id.exo_duration);
        exo_live = view.findViewById(R.id.exo_live);

        linear_layout_item_1080p = view.findViewById(R.id.linear_layout_item_1080p);
        linear_layout_item_720p = view.findViewById(R.id.linear_layout_item_720p);
        linear_layout_item_480p = view.findViewById(R.id.linear_layout_item_480p);
        linear_layout_item_360p = view.findViewById(R.id.linear_layout_item_360p);
        linear_layout_item_240p = view.findViewById(R.id.linear_layout_item_240p);

        image_view_1080p = view.findViewById(R.id.image_view_1080p);
        image_view_720p = view.findViewById(R.id.image_view_720p);
        image_view_480p = view.findViewById(R.id.image_view_480p);
        image_view_360p = view.findViewById(R.id.image_view_360p);
        image_view_240p = view.findViewById(R.id.image_view_240p);

        isLive  =  getUrlExtra().getBoolean("isLive");
        mPlayerViewModel.setPayerPausePlay(payer_pause_play);
        binding.setPlayerVm(mPlayerViewModel);
        mSimpleExoPlayerView = binding.videoView;
        mSimpleExoPlayerView.setShutterBackgroundColor(Color.TRANSPARENT);
        if (isLive) {
            text_view_exo_player_live.setVisibility(View.VISIBLE);
            exo_duration.setVisibility(View.GONE);
            exo_live.setVisibility(View.VISIBLE);
        }else{
            text_view_exo_player_live.setVisibility(View.GONE);
            exo_duration.setVisibility(View.VISIBLE);
            exo_live.setVisibility(View.GONE);
        }

        if (videoResolutionr.equalsIgnoreCase("Y")) {
            image_view_exo_player_subtitles.setVisibility(View.VISIBLE);
            image_view_1080p.setVisibility(View.VISIBLE);
        }
    }

    private void initAction(){
        this.image_view_exo_player_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        this.image_view_exo_player_forward_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000) > mPlayerViewModel.mExoPlayer.getDuration()) {
                    mPlayerViewModel.mExoPlayer.seekTo(mPlayerViewModel.mExoPlayer.getDuration());
                } else {
                    mPlayerViewModel.mExoPlayer.seekTo(mPlayerViewModel.mExoPlayer.getCurrentPosition() + 10000);
                }
            }
        });
        this.image_view_exo_player_replay_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerViewModel.mExoPlayer.getCurrentPosition() < 10000) {
                    mPlayerViewModel.mExoPlayer.seekTo(0);
                } else {
                    mPlayerViewModel.mExoPlayer.seekTo(mPlayerViewModel.mExoPlayer.getCurrentPosition() - 10000);
                }
            }
        });
        this.image_view_exo_player_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLandscape) {
                    PlayerFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isLandscape = false;
                } else {
                    PlayerFragment.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isLandscape = true;
                }
            }
        });
        this.image_view_exo_player_subtitles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerViewModel.pause();
                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                //mCustomPlayerViewModel.preparePlayer("https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/tracks/GoogleIO-2014-CastingToTheFuture2-en.vtt");
            }
        });
        this.image_view_dialog_source_close.setOnClickListener(v->{
            mPlayerViewModel.play();
            if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                relative_layout_subtitles_dialog.setVisibility(View.GONE);
            else
                relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);
        });
        this.linear_layout_item_1080p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_view_1080p.setVisibility(View.VISIBLE);
                image_view_720p.setVisibility(View.INVISIBLE);
                image_view_480p.setVisibility(View.INVISIBLE);
                image_view_360p.setVisibility(View.INVISIBLE);
                image_view_240p.setVisibility(View.INVISIBLE);

                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                mPlayerViewModel.mUrl = videoUrlr;
                mPlayerViewModel.preparePlayer(null,mPlayerViewModel.mExoPlayer.getCurrentPosition());
            }
        });
        this.linear_layout_item_720p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_view_1080p.setVisibility(View.INVISIBLE);
                image_view_720p.setVisibility(View.VISIBLE);
                image_view_480p.setVisibility(View.INVISIBLE);
                image_view_360p.setVisibility(View.INVISIBLE);
                image_view_240p.setVisibility(View.INVISIBLE);

                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                mPlayerViewModel.mUrl = videoUrlr.replace(videoUrlr.substring(videoUrlr.lastIndexOf(".")), "") + "_720.mp4";
                mPlayerViewModel.preparePlayer(null,mPlayerViewModel.mExoPlayer.getCurrentPosition());
            }
        });
        this.linear_layout_item_480p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_view_1080p.setVisibility(View.INVISIBLE);
                image_view_720p.setVisibility(View.INVISIBLE);
                image_view_480p.setVisibility(View.VISIBLE);
                image_view_360p.setVisibility(View.INVISIBLE);
                image_view_240p.setVisibility(View.INVISIBLE);

                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                mPlayerViewModel.mUrl = videoUrlr.replace(videoUrlr.substring(videoUrlr.lastIndexOf(".")), "") + "_480.mp4";
                mPlayerViewModel.preparePlayer(null,mPlayerViewModel.mExoPlayer.getCurrentPosition());
            }
        });
        this.linear_layout_item_360p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_view_1080p.setVisibility(View.INVISIBLE);
                image_view_720p.setVisibility(View.INVISIBLE);
                image_view_480p.setVisibility(View.INVISIBLE);
                image_view_360p.setVisibility(View.VISIBLE);
                image_view_240p.setVisibility(View.INVISIBLE);

                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                mPlayerViewModel.mUrl = videoUrlr.replace(videoUrlr.substring(videoUrlr.lastIndexOf(".")), "") + "_360.mp4";
                mPlayerViewModel.preparePlayer(null,mPlayerViewModel.mExoPlayer.getCurrentPosition());
            }
        });
        this.linear_layout_item_240p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_view_1080p.setVisibility(View.INVISIBLE);
                image_view_720p.setVisibility(View.INVISIBLE);
                image_view_480p.setVisibility(View.INVISIBLE);
                image_view_360p.setVisibility(View.INVISIBLE);
                image_view_240p.setVisibility(View.VISIBLE);

                if (relative_layout_subtitles_dialog.getVisibility() ==  View.VISIBLE)
                    relative_layout_subtitles_dialog.setVisibility(View.GONE);
                else
                    relative_layout_subtitles_dialog.setVisibility(View.VISIBLE);

                mPlayerViewModel.mUrl = videoUrlr.replace(videoUrlr.substring(videoUrlr.lastIndexOf(".")), "") + "_240.mp4";
                mPlayerViewModel.preparePlayer(null,mPlayerViewModel.mExoPlayer.getCurrentPosition());
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlayerViewModel.onStart(mSimpleExoPlayerView, getUrlExtra());
    }

    public void setFull(){
        mPlayerViewModel.setMediaFull();
    }
    public void setNormal(){
        mPlayerViewModel.setMediaNormal();

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Nullable
    private Bundle getUrlExtra() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle;
        }
        return null;
    }
}
