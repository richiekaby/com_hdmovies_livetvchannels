package com.hdmovies.livetvchannels;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.fragment.AllVideoFragment;
import com.hdmovies.livetvchannels.fragment.CategoryFragment;
import com.hdmovies.livetvchannels.fragment.DownloadsFragment;
import com.hdmovies.livetvchannels.fragment.EarnFragment;
import com.hdmovies.livetvchannels.fragment.FavoriteFragment;
import com.hdmovies.livetvchannels.fragment.GiftVoucherFragment;
import com.hdmovies.livetvchannels.fragment.HomeFragment;
import com.hdmovies.livetvchannels.fragment.LatestVideoFragment;
import com.hdmovies.livetvchannels.fragment.ProfileNewFragment;
import com.hdmovies.livetvchannels.fragment.SettingFragment;
import com.hdmovies.livetvchannels.helper.BottomNavigationBehavior;
import com.hdmovies.livetvchannels.item.ItemAbout;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    NavigationView navigationView;
    Toolbar toolbar;
//    BottomNavigationView bottomNavigationView;
    private BubbleNavigationConstraintView bubbleNavigationLinearView;
    MyApplication App;
    ArrayList<ItemAbout> mListItem;
    JsonUtils jsonUtils;
    LinearLayout adLayout;
    private ConsentForm form;
    String strMessage;

    //Prefrance
    public static PrefManager prf;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RAJANR = "rajanr";

    public static final String TAG_APP_ID_AD_UNIT_ID = "app_id_ad_unit_id";
    public static final String TAG_BANNER = "banner";
    public static final String TAG_BANNERMAINRS = "bannermainrs";

    public static final String TAG_INTERSTITIAL = "interstitial";
    public static final String TAG_INTERSTITIALMAINRS = "interstitialmainrs";

    public static final String TAG_ADMOB_INTERSTITIAL_FREQUENCY = "ADMOB_INTERSTITIAL_FREQUENCY";

    //rajanads
    // ad will be shown after each x url loadings or clicks on navigation drawer menu
    public static final int ADMOB_INTERSTITIAL_FREQUENCY = 2;
    private static int sInterstitialCounter = 1;

    // ads
//    private com.google.android.gms.ads.AdView mAdView;
    private static AdRequest adRequest;
    public static com.google.android.gms.ads.InterstitialAd mInterstitialAdr;

    //facebookads
//    private LinearLayout bannerAdContainer;
//    private com.facebook.ads.AdView bannerAdView;

    //facebookads
//    private static final String TAG = DisplayUserActivity.class.getSimpleName();

    public static com.facebook.ads.InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
            window.setBackgroundDrawable(background);
        }
        setContentView(R.layout.activity_main);

        // If you call AudienceNetworkAds.buildInitSettings(Context).initialize()
        // in Application.onCreate() this call is not really necessary.
        // Otherwise call initialize() onCreate() of all Activities that contain ads or
        // from onCreate() of your Splash Activity.
        AudienceNetworkAds.initialize(this);

        prf = new PrefManager(MainActivity.this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        App = MyApplication.getInstance();

        mListItem = new ArrayList<>();
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        fragmentManager = getSupportFragmentManager();
        navigationView = findViewById(R.id.navigation_view);

        drawerLayout = findViewById(R.id.drawer_layout);
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setVisibility(View.VISIBLE);
        bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);

        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bubbleNavigationLinearView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
//        //off shift mode
//        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        adLayout = findViewById(R.id.ad_view);

        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new getAdsManage().execute(Constant.ABOUT_US_URL);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        visible_bottomNavigation();
                        HomeFragment homeFragment = new HomeFragment();
                        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                        highLightNavigationBottom(0);
                        return true;
                    case R.id.menu_go_latest:
                        LatestVideoFragment latestVideoFragment = new LatestVideoFragment();
                        loadFrag(latestVideoFragment, getString(R.string.menu_latest), fragmentManager);
                        return true;
                    case R.id.menu_go_category:
                        CategoryFragment categoryFragment = new CategoryFragment();
                        loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        highLightNavigationBottom(1);
                        return true;
                    case R.id.menu_go_favourite:
                        FavoriteFragment favoriteFragment = new FavoriteFragment();
                        loadFrag(favoriteFragment, getString(R.string.menu_favorite), fragmentManager);
                        highLightNavigationBottom(3);
                        return true;
                    case R.id.menu_go_subcription:
                        if (App.getIsLogin()) {
                            Intent subcription = new Intent(getApplicationContext(), SubcriptionActivity.class);
                            startActivity(subcription);
                            return true;
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case R.id.menu_go_giftvoucher:
                        if (App.getIsLogin()) {
                            invisible_bottomNavigation();

                            GiftVoucherFragment giftVoucherFragment = new GiftVoucherFragment();
                            loadFrag(giftVoucherFragment, getString(R.string.menu_favorite), fragmentManager);
                            toolbar.setTitle(getString(R.string.menu_giftvoucher));
                            return true;
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case R.id.menu_go_earn:
                        if (App.getIsLogin()) {
                            invisible_bottomNavigation();

                            EarnFragment earnFragment = new EarnFragment();
                            loadFrag(earnFragment, getString(R.string.menu_favorite), fragmentManager);
                            toolbar.setTitle(getString(R.string.menu_earn));
                            return true;
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case R.id.menu_go_profile:
                        if (App.getIsLogin()) {
                            invisible_bottomNavigation();

                            ProfileNewFragment profileFragment = new ProfileNewFragment();
                            loadFrag(profileFragment, getString(R.string.menu_favorite), fragmentManager);
                            toolbar.setTitle(getString(R.string.menu_profile));
                            return true;
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    case R.id.menu_go_setting:
                        SettingFragment settingFragment = new SettingFragment();
                        loadFrag(settingFragment, getString(R.string.menu_favorite), fragmentManager);
                        toolbar.setTitle(getString(R.string.menu_setting));
                        return true;
                    case R.id.menu_go_privacy:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(config.privacypolicy)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            anfe.printStackTrace();
                        }
                        return true;
                    case R.id.menu_go_rate:
                        final String appName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id="
                                            + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + appName)));
                        }
                        return true;
                    case R.id.menu_go_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;
                    case R.id.menu_go_login:
                        Intent intent = new Intent(getApplicationContext(), ActivitySignInNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.menu_go_logout:
                        Logout();
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (App.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.menu_go_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.menu_go_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(false);
        }

//        if (bottomNavigationView != null) {
//
//            // Select first menu item by default and show Fragment accordingly.
//            Menu menu = bottomNavigationView.getMenu();
//            selectFragment(menu.getItem(0));
//
//            // Set action to perform when any menu-item is selected.
//            bottomNavigationView.setOnNavigationItemSelectedListener(
//                    new BottomNavigationView.OnNavigationItemSelectedListener() {
//                        @Override
//                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                            selectFragment(item);
//                            return false;
//                        }
//                    });
//        }

        if (bubbleNavigationLinearView != null) {
            selectFragmentStyle(0);

            bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
                @Override
                public void onNavigationChanged(View view, int position) {
//                viewPager.setCurrentItem(position, true);
                    selectFragmentStyle(position);
                }
            });
        }
    }

    public void highLightNavigation(int position, String name) {

        navigationView.getMenu().getItem(position).setChecked(true);
        toolbar.setTitle(name);
    }

    public void highLightNavigationBottom(int position) {

//        bottomNavigationView.getMenu().getItem(position).setChecked(true);
        bubbleNavigationLinearView.setCurrentActiveItem(position);
    }

    public void visible_bottomNavigation() {
        bubbleNavigationLinearView.setVisibility(View.VISIBLE);
    }

    public void invisible_bottomNavigation() {
        bubbleNavigationLinearView.setVisibility(View.GONE);
    }

    private void Logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        App.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), ActivitySignInNew.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.exit_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.mipmap.app_icon)
                .show();
    }


    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        //  ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    protected void selectFragmentStyle(int item) {

        bubbleNavigationLinearView.setCurrentActiveItem(item);

        switch (item) {
            case 0:
                visible_bottomNavigation();
                // Action to perform when Home Menu item is selected.
                HomeFragment homeFragment = new HomeFragment();
                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                highLightNavigation(0, getString(R.string.menu_home));

                break;
            case 1:
                // Action to perform when Bag Menu item is selected.
                CategoryFragment categoryFragment = new CategoryFragment();
                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                highLightNavigation(2, getString(R.string.menu_category));
                break;
            case 2:
                // Action to perform when Account Menu item is selected.
                AllVideoFragment allVideoFragment = new AllVideoFragment();
                loadFrag(allVideoFragment, getString(R.string.menu_video), fragmentManager);
                highLightNavigation(0, getString(R.string.menu_video));
                break;

            case 3:
                // Action to perform when Account Menu item is selected.
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                loadFrag(favoriteFragment, getString(R.string.menu_favorite), fragmentManager);
                highLightNavigation(3, getString(R.string.menu_favorite));
                break;

            case 4:
                // Action to perform when Account Menu item is selected.
                DownloadsFragment downloadsFragment = new DownloadsFragment();
                loadFrag(downloadsFragment, getString(R.string.menu_download), fragmentManager);
                highLightNavigation(4, getString(R.string.menu_download));
                break;
        }
    }

//    protected void selectFragment(MenuItem item) {
//
//        item.setChecked(true);
//
//        switch (item.getItemId()) {
//            case R.id.action_home:
//                visible_bottomNavigation();
//                // Action to perform when Home Menu item is selected.
//                 HomeFragment homeFragment = new HomeFragment();
//                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
//                highLightNavigation(0, getString(R.string.menu_home));
//
//                break;
//            case R.id.action_category:
//                // Action to perform when Bag Menu item is selected.
//                CategoryFragment categoryFragment = new CategoryFragment();
//                loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
//                highLightNavigation(2, getString(R.string.menu_category));
//                break;
//            case R.id.action_video:
//                // Action to perform when Account Menu item is selected.
//                AllVideoFragment allVideoFragment = new AllVideoFragment();
//                loadFrag(allVideoFragment, getString(R.string.menu_video), fragmentManager);
//                highLightNavigation(0, getString(R.string.menu_video));
//                break;
//
//            case R.id.action_favorite:
//                // Action to perform when Account Menu item is selected.
//                FavoriteFragment favoriteFragment = new FavoriteFragment();
//                loadFrag(favoriteFragment, getString(R.string.menu_favorite), fragmentManager);
//                highLightNavigation(3, getString(R.string.menu_favorite));
//                break;
//        }
//    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(getString(R.string.app_name));
            alert.setIcon(R.mipmap.app_icon);
            alert.setMessage(getString(R.string.exit_msg));
            alert.setPositiveButton(getString(R.string.exit_yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    });
            alert.setNegativeButton(getString(R.string.exit_no), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alert.show();
        }

    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, ActivitySearch.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                Intent i = new Intent(this,NotificationActivity.class);
                this.startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getAdsManage extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemAbout itemAbout = new ItemAbout();

                        itemAbout.setappDevelpoby(objJson.getString(Constant.APP_DEVELOP));
                        itemAbout.setappBannerId(objJson.getString(Constant.ADS_BANNER_ID));
                        itemAbout.setappFullId(objJson.getString(Constant.ADS_FULL_ID));
                        itemAbout.setappBannerOn(objJson.getString(Constant.ADS_BANNER_ON_OFF));
                        itemAbout.setAppBannerType(objJson.getString(Constant.ADS_BANNER_TYPE));
                        itemAbout.setappFullOn(objJson.getString(Constant.ADS_FULL_ON_OFF));
                        itemAbout.setAppFullType(objJson.getString(Constant.ADS_FULL_AD_TYPE));
                        itemAbout.setappFullPub(objJson.getString(Constant.ADS_PUB_ID));
                        itemAbout.setappFullAdsClick(objJson.getString(Constant.ADS_CLICK));
                        mListItem.add(itemAbout);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        ItemAbout itemAbout = mListItem.get(0);
        Constant.SAVE_ADS_BANNER_ID = itemAbout.getappBannerId();
        Constant.SAVE_ADS_FULL_ID = itemAbout.getappFullId();
        Constant.SAVE_ADS_BANNER_ON_OFF = itemAbout.getappBannerOn();
        Constant.SAVE_ADS_BANNER_TYPE = itemAbout.getAppBannerType();
        Constant.SAVE_ADS_FULL_ON_OFF = itemAbout.getappFullOn();
        Constant.SAVE_ADS_FULL_TYPE = itemAbout.getAppFullType();
        Constant.SAVE_ADS_PUB_ID = itemAbout.getappFullPub();
        Constant.SAVE_ADS_CLICK = itemAbout.getappFullAdsClick();

        prf.setString(TAG_APP_ID_AD_UNIT_ID,Constant.SAVE_ADS_PUB_ID);
        prf.setString(TAG_BANNER,Constant.SAVE_ADS_BANNER_TYPE);
        prf.setString(TAG_BANNERMAINRS,Constant.SAVE_ADS_BANNER_ID);
        prf.setString(TAG_INTERSTITIAL,Constant.SAVE_ADS_FULL_TYPE);
        prf.setString(TAG_INTERSTITIALMAINRS,Constant.SAVE_ADS_FULL_ID);
        prf.setString(TAG_ADMOB_INTERSTITIAL_FREQUENCY,Constant.SAVE_ADS_CLICK);

        if (App.getIsLogin()) {
            if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
                System.out.println("Rajan_login_status_userid"+App.getUserId());
                new MyTaskLoginStatus().execute(Constant.LOGIN_STATUS_URL + App.getUserId());
            }
        }

//         checkForConsent();

        //ads
        // Initialize the Mobile Ads SDK.
        if(prf.getString(TAG_APP_ID_AD_UNIT_ID) != "")
            MobileAds.initialize(this, prf.getString(TAG_APP_ID_AD_UNIT_ID));

//        bannerAdContainer = (LinearLayout) findViewById(R.id.banner_container);

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("admob") || prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            adRequest = new AdRequest.Builder().build();
        }

        //enable banner ads at home screen
//        JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);

//        if(prf.getString(TAG_BANNER).equalsIgnoreCase("admob")) {
////        mAdView = (AdView) findViewById(R.id.adView_view);
//            mAdView = new com.google.android.gms.ads.AdView(this);
//            mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
//            mAdView.setAdUnitId(prf.getString(TAG_BANNERMAINRS));
//            ((LinearLayout) bannerAdContainer).addView(mAdView);
//            mAdView.loadAd(adRequest);
//        }

        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            mInterstitialAdr = new com.google.android.gms.ads.InterstitialAd(this);
            mInterstitialAdr.setAdUnitId(prf.getString(TAG_INTERSTITIALMAINRS));

            mInterstitialAdr.setAdListener(new com.google.android.gms.ads.AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }

        try {
//            //facebookads
//            if(prf.getString(TAG_BANNER).equalsIgnoreCase("fb")) {
//                loadAdView();
//            }

            //facebookadsinterstrial
            if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
                loadinterstrialAdView();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
//        if (bannerAdView != null) {
//            bannerAdView.destroy();
//            bannerAdView = null;
//        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        super.onDestroy();
    }

//    private void loadAdView() {
//        if (bannerAdView != null) {
//            bannerAdView.destroy();
//            bannerAdView = null;
//        }
//
////        bannerAdContainer = (LinearLayout) findViewById(R.id.banner_container);
//
//        // Update progress message
////        setLabel(getString(R.string.loading_status));
//
//        // Create a banner's ad view with a unique placement ID (generate your own on the Facebook
//        // app settings). Use different ID for each ad placement in your app.
////        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
//        bannerAdView = new AdView(this, prf.getString(TAG_BANNERMAINRS), AdSize.BANNER_HEIGHT_50);
//
//        // Reposition the ad and add it to the view hierarchy.
//        bannerAdContainer.addView(bannerAdView);
//
//        // Set a listener to get notified on changes or when the user interact with the ad.
////        bannerAdView.setAdListener(this);
//        bannerAdView.setAdListener(new AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Ad error callback
////                Toast.makeText(DisplayUserActivity.this, "Error: " + adError.getErrorMessage(),
////                        Toast.LENGTH_LONG).show();
//                if (ad == bannerAdView) {
////            setLabel("Ad failed to load: " + error.getErrorMessage());
//                    System.out.println("Rajan_bannermain_Ad failed to load: " + adError.getErrorMessage());
//                }
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Ad loaded callback
//                bannerAdContainer.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//            }
//        });
//
//        // Initiate a request to load an ad.
//        bannerAdView.loadAd();
//    }

    private void loadinterstrialAdView() {

        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
//        setLabel("Loading interstitial ad...");

        // Create the interstitial unit with a placement ID (generate your own on the Facebook app settings).
        // Use different ID for each ad placement in your app.
        interstitialAd = new InterstitialAd(this,prf.getString(TAG_INTERSTITIALMAINRS));

        // Set a listener to get notified on changes or when the user interact with the ad.
//        interstitialAd.setAdListener(this);
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
//                // Cleanup.
//                interstitialAd.destroy();
//                interstitialAd = null;

                // Load a new interstitial.
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                if (ad == interstitialAd) {
                    System.out.println("Rajan_interstrial"+"Interstitial ad failed to load: " + adError.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                if (ad == interstitialAd) {
                    System.out.println("Rajan_interstrial"+"Ad loaded. Click show to present!");
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                System.out.println("Rajan_interstrial_onLoggingImpression");
            }
        });

        // Load a new interstitial.
        interstitialAd.loadAd();

    }

    // ads
    public static void requestNewInterstitial() {
        mInterstitialAdr.loadAd(adRequest);
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTaskLoginStatus extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResultStatus();
            }
        }
    }

    public void setResultStatus() {


        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(getString(R.string.error_login_failed));
            App.saveIsLogin(false);
            Intent intent = new Intent(getApplicationContext(), ActivitySignInNew.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
    }

    public void checkForConsent() {

        // ConsentInformation.getInstance(MainActivity.this).addTestDevice("FA0F55855A8169A47EB9D713413B9FE9");
        // Geography appears as in EEA for test devices.
        // ConsentInformation.getInstance(MainActivity.this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        // Geography appears as not in EEA for debug devices.
        ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
        String[] publisherIds = {Constant.SAVE_ADS_PUB_ID};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d("consentStatus", consentStatus.toString());
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        JsonUtils.personalization_ad = true;
//                        JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                        break;
                    case NON_PERSONALIZED:
                        JsonUtils.personalization_ad = false;
//                        JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                        break;
                    case UNKNOWN:
                        if (ConsentInformation.getInstance(getBaseContext())
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            JsonUtils.personalization_ad = true;
//                            JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    public void requestConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(config.privacypolicy);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(MainActivity.this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        showForm();
                        // Consent form loaded successfully.
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d("consentStatus_form", consentStatus.toString());
                        switch (consentStatus) {
                            case PERSONALIZED:
                                JsonUtils.personalization_ad = true;
//                                JsonUtils.showPersonalizedAds(adLayout, MainActivity.this);
                                break;
                            case NON_PERSONALIZED:
                                JsonUtils.personalization_ad = false;
//                                JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                                break;
                            case UNKNOWN:
                                JsonUtils.personalization_ad = false;
//                                JsonUtils.showNonPersonalizedAds(adLayout, MainActivity.this);
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("errorDescription", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    private void showForm() {
        if (form != null) {
            form.show();
        }
    }

    public static boolean checkfbAd() {
//        System.out.println("Rajan_fb_counter"+sInterstitialCounter);
        if(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY) != "") {
            if (Integer.parseInt(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY)) > 0 && sInterstitialCounter % Integer.parseInt(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY)) == 0) {
                sInterstitialCounter++;
                return true;
            } else {
                sInterstitialCounter++;
                return false;
            }
        } else {
            if (MainActivity.ADMOB_INTERSTITIAL_FREQUENCY > 0 && sInterstitialCounter % MainActivity.ADMOB_INTERSTITIAL_FREQUENCY == 0) {
                sInterstitialCounter++;
                return true;
            } else {
                sInterstitialCounter++;
                return false;
            }
        }
    }
}

