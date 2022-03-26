//package com.hdmovies.livetvchannels;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.exoplayer2.ExoPlaybackException;
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.ExoPlayerFactory;
//import com.google.android.exoplayer2.PlaybackParameters;
//import com.google.android.exoplayer2.Player;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//import com.google.android.exoplayer2.Timeline;
//import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
//import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
//import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
//import com.google.android.exoplayer2.source.ExtractorMediaSource;
//import com.google.android.exoplayer2.source.MediaSource;
//import com.google.android.exoplayer2.source.TrackGroupArray;
//import com.google.android.exoplayer2.source.hls.HlsMediaSource;
//import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
//import com.google.android.exoplayer2.trackselection.TrackSelection;
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
//import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
//import com.google.android.exoplayer2.upstream.BandwidthMeter;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
//import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
//import com.google.android.exoplayer2.upstream.TransferListener;
//import com.google.android.exoplayer2.util.Util;
//import com.leo.simplearcloader.SimpleArcLoader;
//
//public class SimpleExoPlayerActivity extends AppCompatActivity {
//
//    private SimpleExoPlayerView simpleExoPlayerView;
//    private SimpleExoPlayer player;
//
//    private Timeline.Window window;
//    private DataSource.Factory mediaDataSourceFactory;
//    private DefaultTrackSelector trackSelector;
//    private boolean shouldAutoPlay;
//    private BandwidthMeter bandwidthMeter;
//
//    private ImageView ivHideControllerButton;
//    private String URL;
//    private String duration;
//    private SimpleArcLoader simple_arc_loader_exo;
//    private String local;
//    private ImageView exo_pause;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = this.getWindow();
//            Drawable background = this.getResources().getDrawable(R.drawable.statusbar_gradient);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//            //screen capture off
//            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
//
////            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
////                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
////                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
////                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
////                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
////                            WindowManager.LayoutParams.FLAG_SECURE,
////                    WindowManager.LayoutParams.FLAG_FULLSCREEN |
////                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
////                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
////                            WindowManager.LayoutParams.FLAG_SECURE |
////                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
////                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//
//
//            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
//            window.setBackgroundDrawable(background);
//        }
//
//        setContentView(R.layout.activity_simple_exo_player);
//        Bundle bundle = getIntent().getExtras();
//        this.URL =  bundle.getString("id");
//        this.local =  bundle.getString("local");
//        this.duration =  bundle.getString("duration");
//
//        shouldAutoPlay = true;
//        bandwidthMeter = new DefaultBandwidthMeter();
//        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
//        window = new Timeline.Window();
//        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller);
//        ivHideControllerButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_fullscreen_exit));
//        simple_arc_loader_exo = (SimpleArcLoader) findViewById(R.id.simple_arc_loader_exo);
//
//        exo_pause = (ImageView) findViewById(R.id.exo_pause);
//    }
//
//    private void initializePlayer() {
//
//        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
//        simpleExoPlayerView.requestFocus();
//
//        TrackSelection.Factory videoTrackSelectionFactory =
//                new AdaptiveTrackSelection.Factory(bandwidthMeter);
//
//        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//
//        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
//        simpleExoPlayerView.setControllerShowTimeoutMs(25000);
//        simpleExoPlayerView.setPlayer(player);
//        player.setPlayWhenReady(shouldAutoPlay);
//
//        // Log.v("MY ONE",URL);
//        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        MediaSource mediaSource;
//
//        if(URL.contains("m3u8") || URL.contains("M3U8")) {
//            Handler mainHandler = new Handler();
//            mediaSource = new HlsMediaSource(Uri.parse(URL),
//                    mediaDataSourceFactory, mainHandler, null);
//        } else {
//            mediaSource = new ExtractorMediaSource(Uri.parse(URL),
//                    mediaDataSourceFactory, extractorsFactory, null, null);
//        }
//
////        if (local!=null){
////            Log.v("this is path",local);
////            Uri imageUri = FileProvider.getUriForFile(SimpleExoPlayerActivity.this, SimpleExoPlayerActivity.this.getApplicationContext().getPackageName() + ".provider", new File(local));
////            mediaSource = new ExtractorMediaSource(imageUri,
////                    mediaDataSourceFactory, extractorsFactory, null, null);
////        }
//
//
//        player.seekTo(Integer.parseInt(duration));
//
//        player.prepare(mediaSource);
//        player.seekTo(Integer.parseInt(duration));
//
//        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        player.addListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(final Timeline timeline, final Object manifest)
//            {
//                System.out.println("Rajan_ExoPlayer: Timeline Changed." + timeline.toString());
//            }
//
//            @Override
//            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//            }
//
//            @Override
//            public void onLoadingChanged(final boolean isLoading)
//            {
//                System.out.println("Rajan_player_ExoPlayer: Loading changed.");
//            }
//
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                if (playbackState == ExoPlayer.STATE_READY){
//                    simple_arc_loader_exo.setVisibility(View.GONE);
//                }
//                if (playbackState == ExoPlayer.STATE_BUFFERING){
//                    simple_arc_loader_exo.setVisibility(View.VISIBLE);
//                    exo_pause.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onRepeatModeChanged(final int repeatMode)
//            {
//                System.out.println("Rajan_player_ExoPlayer: Repeat Mode Changed");
//            }
//
//            @Override
//            public void onPlayerError(final ExoPlaybackException error)
//            {
//                System.out.println("Rajan_player_ExoPlayer: Player Error."+ error);
//
//                String errorString = null;
//                if (error.type == ExoPlaybackException.TYPE_RENDERER)
//                {
//                    final Exception cause = error.getRendererException();
//                    if (cause instanceof MediaCodecRenderer.DecoderInitializationException)
//                    {
//                        // Special case for decoder initialization failures.
//                        final MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
//                                (MediaCodecRenderer.DecoderInitializationException) cause;
//                        if (decoderInitializationException.decoderName == null)
//                        {
//                            if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException)
//                            {
//                                errorString = "error_querying_decoders";
//                            }
//                            else if (decoderInitializationException.secureDecoderRequired)
//                            {
//                                errorString = "error_no_secure_decoder";
//                            }
//                            else
//                            {
//                                errorString = "error_no_decoder";
//                            }
//                        }
//                        else
//                        {
//                            errorString = "error_instantiating_decoder";
//                        }
//                    }
//                }
//                else if (error.getSourceException().getMessage().contains("403"))
//                {
//                    errorString = String.format("exo_403_Error Code: " + error.getSourceException().getMessage());
//                }
//                else
//                {
//                    errorString = String.format("exo_error_playback_Error Code: " + error.getSourceException().getMessage());
//                }
//
//                final AlertDialog dialog = new AlertDialog.Builder(SimpleExoPlayerActivity.this)
//                        .setTitle("exo_error_title")
//                        .setMessage(errorString)
//                        .setPositiveButton("ok", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(final DialogInterface dialog, final int id)
//                            {
////                                addBookmark();
//
//                                player.stop();
//                                releasePlayer();
//
//                                final Intent data = new Intent();
//                                setResult(RESULT_CANCELED,data);
//
//                                finish();
//                            }
//                        })
//                        .create();
//                dialog.show();
//            }
//
//            @Override
//            public void onPositionDiscontinuity()
//            {
//                System.out.println("Rajan_player_ExoPlayer: Position Discontinuity.");
//            }
//
//            @Override
//            public void onPlaybackParametersChanged(final PlaybackParameters playbackParameters)
//            {
//                System.out.println("Rajan_player_ExoPlayer: Playback Parameters Changed.");
//            }
//        });
//    }
//
//    private void releasePlayer() {
//        if (player != null) {
//            player.seekTo(Integer.parseInt(duration));
//            shouldAutoPlay = player.getPlayWhenReady();
//            player.release();
//            player = null;
//            trackSelector = null;
//
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (Util.SDK_INT <= 23) {
//            releasePlayer();
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (Util.SDK_INT > 23) {
//            releasePlayer();
//        }
//    }
//}
