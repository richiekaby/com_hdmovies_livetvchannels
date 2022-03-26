package com.hdmovies.livetvchannels.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdmovies.livetvchannels.util.Constant;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.PlayerActivity;
import com.hdmovies.livetvchannels.R;

import java.io.File;

import static com.hdmovies.livetvchannels.MainActivity.TAG_INTERSTITIAL;
import static com.hdmovies.livetvchannels.MainActivity.interstitialAd;
import static com.hdmovies.livetvchannels.MainActivity.mInterstitialAdr;
import static com.hdmovies.livetvchannels.MainActivity.prf;
import static com.hdmovies.livetvchannels.MainActivity.requestNewInterstitial;


public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ItemRowHolder> {

    // Declare variables
    private Activity activity;
    private String[] filepath;
    private String[] filename;

    private Context mContext;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;

    public DownloadsAdapter(Context context, String[] fpath, String[] fname) {
        this.filepath = fpath;
        this.filename = fname;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_downloads_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {

//        holder.text.setText(filename[position]);

        Glide.with(mContext).load(Uri.fromFile(new File(filepath[position]))).placeholder(R.mipmap.app_icon).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {
                    if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
                        try {

                            if (MainActivity.checkfbAd()) {
                                if (mInterstitialAdr.isLoaded()) {
                                    mInterstitialAdr.show();

                                    mInterstitialAdr.setAdListener(new com.google.android.gms.ads.AdListener() {
                                        @Override
                                        public void onAdLoaded() {
                                            // Code to be executed when an ad finishes loading.
                                        }

                                        @Override
                                        public void onAdFailedToLoad(int errorCode) {
                                            // Code to be executed when an ad request fails.
                                        }

                                        @Override
                                        public void onAdOpened() {
                                            // Code to be executed when the ad is displayed.
                                        }

                                        @Override
                                        public void onAdLeftApplication() {
                                            // Code to be executed when the user has left the app.
                                        }

                                        @Override
                                        public void onAdClosed() {
                                            requestNewInterstitial();

                                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                // Do something for lollipop and above versions
                                                Intent i = new Intent(mContext, PlayerActivity.class);
                                                i.putExtra("id","1");
                                                i.putExtra("type","mp4");
                                                i.putExtra("image","image");
                                                i.putExtra("title",filename[position]);

                                                try {

                                                    final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                                    if (file.exists()) {
                                                        i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                                    } else {
                                                        i.putExtra("url", filepath[position]);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    i.putExtra("url", filepath[position]);
                                                }
                                                mContext.startActivity(i);
                                            } else {
                                                // do something for phones running an SDK before lollipop
                                                Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(mContext, PlayerActivity.class);
                                                i.putExtra("id","1");
                                                i.putExtra("type","mp4");
                                                i.putExtra("image","image");
                                                i.putExtra("url", filepath[position]);
                                                i.putExtra("title", filename[position]);
                                                mContext.startActivity(i);
                                            }
                                        }
                                    });
                                } else {
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        // Do something for lollipop and above versions
                                        Intent i = new Intent(mContext, PlayerActivity.class);
                                        i.putExtra("id","1");
                                        i.putExtra("type","mp4");
                                        i.putExtra("image","image");
                                        try {

                                            final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                            if (file.exists()) {
                                                i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                            } else {
                                                i.putExtra("url", filepath[position]);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            i.putExtra("url", filepath[position]);
                                        }
                                        i.putExtra("title", filename[position]);
                                        mContext.startActivity(i);
                                    } else {
                                        // do something for phones running an SDK before lollipop
                                        Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(mContext, PlayerActivity.class);
                                        i.putExtra("id","1");
                                        i.putExtra("type","mp4");
                                        i.putExtra("image","image");

                                        i.putExtra("url", filepath[position]);
                                        i.putExtra("title", filename[position]);
                                        mContext.startActivity(i);
                                    }
                                }
                            } else {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Do something for lollipop and above versions
                                    Intent i = new Intent(mContext, PlayerActivity.class);
                                    i.putExtra("id","1");
                                    i.putExtra("type","mp4");
                                    i.putExtra("image","image");
                                    try {

                                        final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                        if (file.exists()) {
                                            i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                        } else {
                                            i.putExtra("url", filepath[position]);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        i.putExtra("url", filepath[position]);
                                    }
                                    i.putExtra("title", filename[position]);
                                    mContext.startActivity(i);
                                } else {
                                    // do something for phones running an SDK before lollipop
                                    Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(mContext, PlayerActivity.class);
                                    i.putExtra("id","1");
                                    i.putExtra("type","mp4");
                                    i.putExtra("image","image");

                                    i.putExtra("url", filepath[position]);
                                    i.putExtra("title", filename[position]);
                                    mContext.startActivity(i);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {

                        if (interstitialAd.isAdLoaded() && !interstitialAd.isAdInvalidated()) {
                            if (MainActivity.checkfbAd()) {
                                interstitialAd.show();

                                // Set listeners for the Interstitial Ad
                                interstitialAd.setAdListener(new InterstitialAdListener() {
                                    @Override
                                    public void onInterstitialDisplayed(Ad ad) {
                                        // Interstitial ad displayed callback
                                    }

                                    @Override
                                    public void onInterstitialDismissed(Ad ad) {
                                        // Interstitial dismissed callback

                                        // Load a new interstitial.
                                        interstitialAd.loadAd();

                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            // Do something for lollipop and above versions
                                            Intent i = new Intent(mContext, PlayerActivity.class);
                                            i.putExtra("id","1");
                                            i.putExtra("type","mp4");
                                            i.putExtra("image","image");
                                            try {

                                                final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                                if (file.exists()) {
                                                    i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                                } else {
                                                    i.putExtra("url", filepath[position]);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                i.putExtra("url", filepath[position]);
                                            }
                                            i.putExtra("title", filename[position]);
                                            mContext.startActivity(i);
                                        } else {
                                            // do something for phones running an SDK before lollipop
                                            Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(mContext, PlayerActivity.class);
                                            i.putExtra("id","1");
                                            i.putExtra("type","mp4");
                                            i.putExtra("image","image");
                                            i.putExtra("url", filepath[position]);
                                            i.putExtra("title", filename[position]);
                                            mContext.startActivity(i);
                                        }
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        // Ad error callback
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        // Interstitial ad is loaded and ready to be displayed
                                        if (ad == interstitialAd) {
                                            System.out.println("Rajan_interstrial" + "Ad loaded. Click show to present!");
                                        }
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {
                                        // Ad clicked callback
                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {
                                        // Ad impression logged callback
                                    }
                                });
                            } else {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    // Do something for lollipop and above versions
                                    Intent i = new Intent(mContext, PlayerActivity.class);
                                    i.putExtra("id","1");
                                    i.putExtra("type","mp4");
                                    i.putExtra("image","image");
                                    try {

                                        final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                        if (file.exists()) {
                                            i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                        } else {
                                            i.putExtra("url", filepath[position]);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        i.putExtra("url", filepath[position]);
                                    }
                                    i.putExtra("title", filename[position]);
                                    mContext.startActivity(i);
                                } else {
                                    // do something for phones running an SDK before lollipop
                                    Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(mContext, PlayerActivity.class);
                                    i.putExtra("id","1");
                                    i.putExtra("type","mp4");
                                    i.putExtra("image","image");
                                    i.putExtra("url", filepath[position]);
                                    i.putExtra("title", filename[position]);
                                    mContext.startActivity(i);
                                }
                            }
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // Do something for lollipop and above versions
                                Intent i = new Intent(mContext, PlayerActivity.class);
                                i.putExtra("id","1");
                                i.putExtra("type","mp4");
                                i.putExtra("image","image");
                                try {

                                    final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                    if (file.exists()) {
                                        i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                    } else {
                                        i.putExtra("url", filepath[position]);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    i.putExtra("url", filepath[position]);
                                }
                                i.putExtra("title", filename[position]);
                                mContext.startActivity(i);
                            } else {
                                // do something for phones running an SDK before lollipop
                                Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(mContext, PlayerActivity.class);
                                i.putExtra("id","1");
                                i.putExtra("type","mp4");
                                i.putExtra("image","image");
                                i.putExtra("url", filepath[position]);
                                i.putExtra("title", filename[position]);
                                mContext.startActivity(i);
                            }
                        }
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            // Do something for lollipop and above versions
                            Intent i = new Intent(mContext, PlayerActivity.class);
                            i.putExtra("id","1");
                            i.putExtra("type","mp4");
                            i.putExtra("image","image");
                            try {

                                final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                                if (file.exists()) {
                                    i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                                } else {
                                    i.putExtra("url", filepath[position]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                i.putExtra("url", filepath[position]);
                            }
                            i.putExtra("title", filename[position]);
                            mContext.startActivity(i);
                        } else {
                            // do something for phones running an SDK before lollipop
                            Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(mContext, PlayerActivity.class);
                            i.putExtra("id","1");
                            i.putExtra("type","mp4");
                            i.putExtra("image","image");
                            i.putExtra("url", filepath[position]);
                            i.putExtra("title", filename[position]);
                            mContext.startActivity(i);
                        }
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Do something for lollipop and above versions
                        Intent i = new Intent(mContext, PlayerActivity.class);
                        i.putExtra("id","1");
                        i.putExtra("type","mp4");
                        i.putExtra("image","image");
                        try {

                            final File file = new File(mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);

                            if (file.exists()) {
                                i.putExtra("url", mContext.getFilesDir() + File.separator + mContext.getResources().getString(R.string.downloadfolder) + File.separator + filename);
                            } else {
                                i.putExtra("url", filepath[position]);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            i.putExtra("url", filepath[position]);
                        }
                        i.putExtra("title", filename[position]);
                        mContext.startActivity(i);
                    } else {
                        // do something for phones running an SDK before lollipop
                        Toast.makeText(mContext, mContext.getString(R.string.pip_not_support), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mContext, PlayerActivity.class);
                        i.putExtra("id","1");
                        i.putExtra("type","mp4");
                        i.putExtra("image","image");
                        i.putExtra("url", filepath[position]);
                        i.putExtra("title", filename[position]);
                        mContext.startActivity(i);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != filepath ? filepath.length : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image, image_pop_up;
        private TextView text, txt_cat_name, txt_time, text_view;
        private LinearLayout lyt_parent, premium;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            premium = itemView.findViewById(R.id.premium);
            txt_cat_name = itemView.findViewById(R.id.text_category);
            txt_time = itemView.findViewById(R.id.text_time);
            text_view = itemView.findViewById(R.id.text_view);
            image_pop_up = itemView.findViewById(R.id.image_pop_up);

        }
    }
}
