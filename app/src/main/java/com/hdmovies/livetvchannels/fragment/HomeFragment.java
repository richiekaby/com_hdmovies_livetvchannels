package com.hdmovies.livetvchannels.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hdmovies.livetvchannels.adapter.HashAdapter;
import com.hdmovies.livetvchannels.adapter.HomeAllAdapter;
import com.hdmovies.livetvchannels.adapter.HomeCatAdapter;
import com.hdmovies.livetvchannels.adapter.HomeLatestAdapter;
import com.hdmovies.livetvchannels.item.Hash;
import com.hdmovies.livetvchannels.item.ItemCategory;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.EnchantedViewPager;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.RecyclerTouchListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.hdmovies.livetvchannels.ActivityVideoDetails;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

import static com.hdmovies.livetvchannels.MainActivity.TAG_INTERSTITIAL;
import static com.hdmovies.livetvchannels.MainActivity.interstitialAd;
import static com.hdmovies.livetvchannels.MainActivity.mInterstitialAdr;
import static com.hdmovies.livetvchannels.MainActivity.prf;
import static com.hdmovies.livetvchannels.MainActivity.requestNewInterstitial;


public class HomeFragment extends Fragment {

    RecyclerView recyclerViewLatestVideo, recyclerViewAllVideo, recyclerViewCatVideo;
    EnchantedViewPager mViewPager;
    CustomViewPagerAdapter mAdapter;
    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ArrayList<ItemLatest> mSliderList;
    CircleIndicator circleIndicator;
    Button btnAll, btnLatest, btnCategory;
    int currentCount = 0;
    ArrayList<ItemLatest> mLatestList, mAllList, mAllListr;
    ArrayList<ItemCategory> mCatList;
    HomeCatAdapter homeCatAdapter;
    HomeLatestAdapter homeLatestAdapter;
    HomeAllAdapter homeAllAdapter;
    private FragmentManager fragmentManager;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;
    ItemCategory itemCategory;
    TextView txt_latest_video_no, txt_all_video_no, txt_cat_video_no;
    LinearLayout lay_1, lay_2, lay_3;
    private ProgressDialog pDialog;
    TextView textView;

    private RelativeLayout lay_latest;
    private RelativeLayout lay_all;
    private RelativeLayout lay_cat;

    private List<Hash> hashList = new ArrayList<>();
    private RecyclerView recyclerView;
    private HashAdapter mAdapters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Hashmap for ListView
        hashList = new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_cat_video_lis);

        mAdapters = new HashAdapter(getActivity(), hashList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        // vertical RecyclerView
        // keep hash_list_row.xmlwidth to `match_parent`
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        // horizontal RecyclerView
        // keep hash_list_row.xmlwidth to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        // adding inbuilt divider line
//        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapters);

//        // row click listenerMyDividerItemDecoration
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
////                Hash hash = hashList.get(position);
////                Toast.makeText(context, hash.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        mSliderList = new ArrayList<>();
        mAllList = new ArrayList<>();
        mAllListr = new ArrayList<>();
        mLatestList = new ArrayList<>();
        mCatList = new ArrayList<>();

        fragmentManager = requireActivity().getSupportFragmentManager();
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mScrollView = rootView.findViewById(R.id.scrollView);
        mViewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);

        recyclerViewLatestVideo = rootView.findViewById(R.id.rv_latest_video);
        recyclerViewAllVideo = rootView.findViewById(R.id.rv_all_video);
        recyclerViewCatVideo = rootView.findViewById(R.id.rv_cat_video);

        lay_1 = rootView.findViewById(R.id.lay_1);
        lay_2 = rootView.findViewById(R.id.lay_2);
        lay_3 = rootView.findViewById(R.id.lay_3);

        btnLatest = rootView.findViewById(R.id.btn_latest_video);
        btnAll = rootView.findViewById(R.id.btn_all_video);
        btnCategory = rootView.findViewById(R.id.btn_cat_video);

        txt_latest_video_no = rootView.findViewById(R.id.txt_latest_video_no);
        txt_all_video_no = rootView.findViewById(R.id.txt_all_video_no);
        txt_cat_video_no = rootView.findViewById(R.id.txt_cat_video_no);

        textView = rootView.findViewById(R.id.txt_no);

        lay_latest = rootView.findViewById(R.id.lay_latest);
        lay_all = rootView.findViewById(R.id.lay_all);
        lay_cat = rootView.findViewById(R.id.lay_cat);

        recyclerViewLatestVideo.setHasFixedSize(false);
        recyclerViewLatestVideo.setNestedScrollingEnabled(false);
        recyclerViewLatestVideo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerViewLatestVideo.addItemDecoration(itemDecoration);

        recyclerViewAllVideo.setHasFixedSize(false);
        recyclerViewAllVideo.setNestedScrollingEnabled(false);
        recyclerViewAllVideo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAllVideo.addItemDecoration(itemDecoration);

        recyclerViewCatVideo.setHasFixedSize(false);
        recyclerViewCatVideo.setNestedScrollingEnabled(false);
        recyclerViewCatVideo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCatVideo.addItemDecoration(itemDecoration);

        if (getResources().getString(R.string.isRTL).equals("true")) {
            lay_1.setBackgroundResource(R.drawable.home_title_gradient_right);
            lay_2.setBackgroundResource(R.drawable.home_title_gradient_right);
            lay_3.setBackgroundResource(R.drawable.home_title_gradient_right);
        } else {
            lay_1.setBackgroundResource(R.drawable.home_title_gradient);
            lay_2.setBackgroundResource(R.drawable.home_title_gradient);
            lay_3.setBackgroundResource(R.drawable.home_title_gradient);
        }

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) requireActivity()).highLightNavigation(1, getString(R.string.menu_latest));
                LatestVideoFragment latestVideoFragment = new LatestVideoFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(HomeFragment.this);
                fragmentTransaction.add(R.id.Container, latestVideoFragment, getString(R.string.menu_latest));
                fragmentTransaction.addToBackStack(getString(R.string.menu_latest));
                fragmentTransaction.commit();
                ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_latest));

            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) requireActivity()).highLightNavigationBottom(2);
                AllVideoFragment allVideoFragment = new AllVideoFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(HomeFragment.this);
                fragmentTransaction.add(R.id.Container, allVideoFragment, getString(R.string.menu_video));
                fragmentTransaction.addToBackStack(getString(R.string.menu_video));
                fragmentTransaction.commit();
                ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_video));

            }
        });


        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) requireActivity()).highLightNavigationBottom(1);
                CategoryFragment categoryFragment = new CategoryFragment();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.hide(HomeFragment.this);
                fragmentTransaction.add(R.id.Container, categoryFragment, getString(R.string.menu_category));
                fragmentTransaction.addToBackStack(getString(R.string.menu_category));
                fragmentTransaction.commit();
                ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_category));

            }
        });

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new HomeVideo().execute(Constant.HOME_URL);
        }
        mViewPager.useScale();
        mViewPager.removeAlpha();
        return rootView;
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        private CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = requireActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mSliderList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_slider_item, container, false);
            assert imageLayout != null;

            ImageView image = imageLayout.findViewById(R.id.image);
            TextView text = imageLayout.findViewById(R.id.text);
            LinearLayout lyt_parent = imageLayout.findViewById(R.id.rootLayout);

            text.setText(mSliderList.get(position).getLatestVideoName());

            switch (mSliderList.get(position).getLatestVideoType()) {
                case "local":
                    Glide.with((MainActivity) requireActivity()).load(mSliderList.get(position).getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
                case "server_url":
                    Glide.with((MainActivity) requireActivity()).load(mSliderList.get(position).getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
                case "youtube":
                    Glide.with((MainActivity) requireActivity()).load(Constant.YOUTUBE_IMAGE_FRONT + mSliderList.get(position).getLatestVideoPlayId() + Constant.YOUTUBE_SMALL_IMAGE_BACK).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
                case "dailymotion":
                    Glide.with((MainActivity) requireActivity()).load(Constant.DAILYMOTION_IMAGE_PATH + mSliderList.get(position).getLatestVideoPlayId()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
                case "vimeo":
                    Glide.with((MainActivity) requireActivity()).load(mSliderList.get(position).getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
                case "embed":
                    Glide.with((MainActivity) requireActivity()).load(mSliderList.get(position).getLatestVideoImgBig()).placeholder(R.drawable.loading).centerCrop().crossFade().diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
                    break;
            }

            imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.LATEST_IDD = mSliderList.get(position).getLatestId();

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

                                                Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                                startActivity(intent_single);
                                            }
                                        });
                                    } else {
                                        Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                        startActivity(intent_single);
                                    }
                                } else {
                                    Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                    startActivity(intent_single);
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

                                            Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                            startActivity(intent_single);
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
                                    Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                    startActivity(intent_single);
                                }
                            } else {
                                Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                                startActivity(intent_single);
                            }
                        } else {
                            Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                            startActivity(intent_single);
                        }
                    } else {
                        Intent intent_single = new Intent(requireActivity(), ActivityVideoDetails.class);
                        startActivity(intent_single);
                    }

                }
            });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mAdapter != null && viewPager.getAdapter().getCount() > 0) {
                        int position = currentCount % mAdapter.getCount();
                        currentCount++;
                        viewPager.setCurrentItem(position);
                        autoPlay(viewPager);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "auto scroll pager error.", e);
                }
            }
        }, 2500);
    }

    @SuppressLint("StaticFieldLeak")
    private class HomeVideo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //error solved
            if(getActivity() == null)
                return;

            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject mainJsonob = mainJson.getJSONObject(Constant.LATEST_ARRAY_NAME);
//                    System.out.println("Rajan_mainJsonob"+mainJsonob);
                    JSONArray jsonArray = mainJsonob.getJSONArray("featured_video");
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

                        mSliderList.add(objItem);
                    }

                    JSONArray jsonArrayla = mainJsonob.getJSONArray("latest_video");
                    JSONObject objJsonla;
                    for (int i = 0; i < jsonArrayla.length(); i++) {
                        objJsonla = jsonArrayla.getJSONObject(i);

                        ItemLatest objItem = new ItemLatest();

                        objItem.setLatestId(objJsonla.getString(Constant.LATEST_ID));
                        objItem.setLatestCategoryName(objJsonla.getString(Constant.LATEST_CAT_NAME));
                        objItem.setLatestCategoryId(objJsonla.getString(Constant.LATEST_CATID));
                        objItem.setLatestVideoUrl(objJsonla.getString(Constant.LATEST_VIDEO_URL));
                        objItem.setLatestVideoPlayId(objJsonla.getString(Constant.LATEST_VIDEO_ID));
                        objItem.setLatestVideoName(objJsonla.getString(Constant.LATEST_VIDEO_NAME));
                        objItem.setLatestDuration(objJsonla.getString(Constant.LATEST_VIDEO_DURATION));
                        objItem.setLatestDescription(objJsonla.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                        objItem.setLatestVideoImgBig(objJsonla.getString(Constant.LATEST_IMAGE_URL));
                        objItem.setLatestVideoType(objJsonla.getString(Constant.LATEST_TYPE));
                        objItem.setLatestVideoRate(objJsonla.getString(Constant.LATEST_RATE));
                        objItem.setLatestVideoView(objJsonla.getString(Constant.LATEST_VIEW));
                        objItem.setLatestPremium(objJsonla.getString(Constant.TAG_PREMIUM));

                        mLatestList.add(objItem);
                    }

                    JSONArray jsonArraymost = mainJsonob.getJSONArray("all_video");
                    JSONObject objJsonmost;
                    for (int i = 0; i < jsonArraymost.length(); i++) {
                        objJsonmost = jsonArraymost.getJSONObject(i);

                        ItemLatest objItem = new ItemLatest();

                        objItem.setLatestId(objJsonmost.getString(Constant.LATEST_ID));
                        objItem.setLatestCategoryName(objJsonmost.getString(Constant.LATEST_CAT_NAME));
                        objItem.setLatestCategoryId(objJsonmost.getString(Constant.LATEST_CATID));
                        objItem.setLatestVideoUrl(objJsonmost.getString(Constant.LATEST_VIDEO_URL));
                        objItem.setLatestVideoPlayId(objJsonmost.getString(Constant.LATEST_VIDEO_ID));
                        objItem.setLatestVideoName(objJsonmost.getString(Constant.LATEST_VIDEO_NAME));
                        objItem.setLatestDuration(objJsonmost.getString(Constant.LATEST_VIDEO_DURATION));
                        objItem.setLatestDescription(objJsonmost.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                        objItem.setLatestVideoImgBig(objJsonmost.getString(Constant.LATEST_IMAGE_URL));
                        objItem.setLatestVideoType(objJsonmost.getString(Constant.LATEST_TYPE));
                        objItem.setLatestVideoRate(objJsonmost.getString(Constant.LATEST_RATE));
                        objItem.setLatestVideoView(objJsonmost.getString(Constant.LATEST_VIEW));
//                        System.out.println("Rajan_PREMIUM"+objJsonmost.getString(Constant.TAG_PREMIUM));
                        objItem.setLatestPremium(objJsonmost.getString(Constant.TAG_PREMIUM));

                        mAllList.add(objItem);
                    }

                    //Rajan
                    JSONArray jsonArraymostr = mainJsonob.getJSONArray("all_videor");
//                    System.out.println("Rajan_jsonArraymostr"+jsonArraymostr);
                    JSONObject objJsonmostr;
                    String id,title = "";
                    for (int i = 0; i < jsonArraymostr.length(); i++) {
                        mAllListr = new ArrayList<>();

                        do {
                            objJsonmostr = jsonArraymostr.getJSONObject(i);

                            ItemLatest objItem = new ItemLatest();

                            objItem.setLatestId(objJsonmostr.getString(Constant.LATEST_ID));
                            title = objJsonmostr.getString(Constant.LATEST_CAT_NAME);
                            objItem.setLatestCategoryName(objJsonmostr.getString(Constant.LATEST_CAT_NAME));
                            id = objJsonmostr.getString(Constant.LATEST_CATID);
                            objItem.setLatestCategoryId(objJsonmostr.getString(Constant.LATEST_CATID));
                            objItem.setLatestVideoUrl(objJsonmostr.getString(Constant.LATEST_VIDEO_URL));
                            objItem.setLatestVideoPlayId(objJsonmostr.getString(Constant.LATEST_VIDEO_ID));
                            objItem.setLatestVideoName(objJsonmostr.getString(Constant.LATEST_VIDEO_NAME));
                            objItem.setLatestDuration(objJsonmostr.getString(Constant.LATEST_VIDEO_DURATION));
                            objItem.setLatestDescription(objJsonmostr.getString(Constant.LATEST_VIDEO_DESCRIPTION));
                            objItem.setLatestVideoImgBig(objJsonmostr.getString(Constant.LATEST_IMAGE_URL));
                            objItem.setLatestVideoType(objJsonmostr.getString(Constant.LATEST_TYPE));
                            objItem.setLatestVideoRate(objJsonmostr.getString(Constant.LATEST_RATE));
                            objItem.setLatestVideoView(objJsonmostr.getString(Constant.LATEST_VIEW));
                            objItem.setLatestPremium(objJsonmostr.getString(Constant.TAG_PREMIUM));

                            mAllListr.add(objItem);
//                            System.out.println("Rajan_jsonArraymostr"+title+i);

                            if (i+1 < jsonArraymostr.length()) {
                                if (!title.equalsIgnoreCase(jsonArraymostr.getJSONObject(i + 1).getString(Constant.LATEST_CAT_NAME))) {
                                    break;
                                }
                            }
                            i++;
                        } while(i < jsonArraymostr.length());

                        //show no of videos in each category
//                        if(mAllListr.size() >= 4) {
//                            mAllListr.subList(3, mAllListr.size()).clear();
//                        }

                        Hash hash = new Hash(title, id, mAllListr.size()+1+"", mAllListr);
                        hashList.add(hash);
                    }
//                    System.out.println("Rajan_hashList_size"+hashList.size());

                    JSONArray jsonArray2 = mainJsonob.getJSONArray("category");
                    JSONObject objJson2 = null;
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        objJson2 = jsonArray2.getJSONObject(i);

                        ItemCategory objItem = new ItemCategory();

                        objItem.setCategoryId(objJson2.getString(Constant.CATEGORY_CID));
                        objItem.setCategoryImageUrl(objJson2.getString(Constant.CATEGORY_IMAGE));
                        objItem.setCategoryName(objJson2.getString(Constant.CATEGORY_NAME));

                        mCatList.add(objItem);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setHomeVideo();
            }
        }
    }

    private void setHomeVideo() {

        if (getActivity() != null) {
            if (!mSliderList.isEmpty()) {
                mAdapter = new CustomViewPagerAdapter();
                mViewPager.setAdapter(mAdapter);
                circleIndicator.setViewPager(mViewPager);
                autoPlay(mViewPager);
            }
            if (mSliderList.size() == 0) {
                mViewPager.setVisibility(View.GONE);
            } else {
                mViewPager.setVisibility(View.VISIBLE);
            }


            txt_latest_video_no.setText(String.valueOf(mLatestList.size()) + "\u0020" + getResources().getString(R.string.total_video));
            txt_all_video_no.setText(String.valueOf(mAllList.size()) + "\u0020" + getResources().getString(R.string.total_video));
            txt_cat_video_no.setText(String.valueOf(mCatList.size()) + "\u0020" + getResources().getString(R.string.total_category));

            if (getActivity() != null) {
                homeLatestAdapter = new HomeLatestAdapter(getActivity(), mLatestList);
                recyclerViewLatestVideo.setAdapter(homeLatestAdapter);
            }
            if (getActivity() != null) {
                homeAllAdapter = new HomeAllAdapter(getActivity(), mAllList);
                recyclerViewAllVideo.setAdapter(homeAllAdapter);
            }
            if (getActivity() != null) {
                homeCatAdapter = new HomeCatAdapter(getActivity(), mCatList);
                recyclerViewCatVideo.setAdapter(homeCatAdapter);
            }

            if ((mLatestList.size() == 0) || mAllList.size() == 0) {
                lay_latest.setVisibility(View.GONE);
                lay_all.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            } else {
                lay_latest.setVisibility(View.VISIBLE);
                lay_all.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }

            if (mCatList.size() == 0) {
                lay_cat.setVisibility(View.GONE);
            } else {
                lay_cat.setVisibility(View.VISIBLE);
            }

            recyclerViewCatVideo.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewCatVideo, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    itemCategory = mCatList.get(position);
                    Constant.CATEGORY_IDD = itemCategory.getCategoryId();
                    Constant.CATEGORY_TITLEE = itemCategory.getCategoryName();

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

                                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                fragmentTransaction.hide(HomeFragment.this);
                                                fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                                fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                                fragmentTransaction.commit();
                                                ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                                            }
                                        });
                                    } else {
                                        CategoryListFragment categoryListFragment = new CategoryListFragment();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.hide(HomeFragment.this);
                                        fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                        fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                        fragmentTransaction.commit();
                                        ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                                    }
                                } else {
                                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.hide(HomeFragment.this);
                                    fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.commit();
                                    ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
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

                                            CategoryListFragment categoryListFragment = new CategoryListFragment();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.hide(HomeFragment.this);
                                            fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                            fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                            fragmentTransaction.commit();
                                            ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
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
                                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.hide(HomeFragment.this);
                                    fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.commit();
                                    ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                                }
                            } else {
                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.hide(HomeFragment.this);
                                fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                fragmentTransaction.commit();
                                ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                            }
                        } else {
                            CategoryListFragment categoryListFragment = new CategoryListFragment();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.hide(HomeFragment.this);
                            fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                            fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                            fragmentTransaction.commit();
                            ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                        }
                    } else {
                        CategoryListFragment categoryListFragment = new CategoryListFragment();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.hide(HomeFragment.this);
                        fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                        fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                        fragmentTransaction.commit();
                        ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        }

    }
}