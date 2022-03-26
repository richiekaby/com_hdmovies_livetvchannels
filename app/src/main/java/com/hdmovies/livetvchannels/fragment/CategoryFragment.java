package com.hdmovies.livetvchannels.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hdmovies.livetvchannels.adapter.CategoryAdapter;
import com.hdmovies.livetvchannels.item.ItemCategory;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.RecyclerTouchListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.hdmovies.livetvchannels.MainActivity.TAG_INTERSTITIAL;
import static com.hdmovies.livetvchannels.MainActivity.interstitialAd;
import static com.hdmovies.livetvchannels.MainActivity.mInterstitialAdr;
import static com.hdmovies.livetvchannels.MainActivity.prf;
import static com.hdmovies.livetvchannels.MainActivity.requestNewInterstitial;


public class CategoryFragment extends Fragment {

    ArrayList<ItemCategory> mListItem;
    public RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    TextView textView;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    ItemCategory itemCategory;
    private InterstitialAd mInterstitial;
    private int AD_COUNT = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);


        mListItem = new ArrayList<>();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_category));
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.rv_video);
        textView = rootView.findViewById(R.id.txt_no);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        fragmentManager = getFragmentManager();

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getSubCat().execute(Constant.CATEGORY_URL);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                itemCategory = mListItem.get(position);
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
                                            fragmentTransaction.hide(CategoryFragment.this);
                                            fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                            fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                            fragmentTransaction.commit();
                                            ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                                        }
                                    });
                                } else {
                                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.hide(CategoryFragment.this);
                                    fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                    fragmentTransaction.commit();
                                    ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                                }
                            } else {
                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.hide(CategoryFragment.this);
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
                                        fragmentTransaction.hide(CategoryFragment.this);
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
                                fragmentTransaction.hide(CategoryFragment.this);
                                fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                                fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                                fragmentTransaction.commit();
                                ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                            }
                        } else {
                            CategoryListFragment categoryListFragment = new CategoryListFragment();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.hide(CategoryFragment.this);
                            fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                            fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                            fragmentTransaction.commit();
                            ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                        }
                    } else {
                        CategoryListFragment categoryListFragment = new CategoryListFragment();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.hide(CategoryFragment.this);
                        fragmentTransaction.add(R.id.Container, categoryListFragment, Constant.CATEGORY_TITLEE);
                        fragmentTransaction.addToBackStack(Constant.CATEGORY_TITLEE);
                        fragmentTransaction.commit();
                        ((MainActivity) requireActivity()).setToolbarTitle(Constant.CATEGORY_TITLEE);
                    }
                } else {
                    CategoryListFragment categoryListFragment = new CategoryListFragment();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(CategoryFragment.this);
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

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getSubCat extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
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

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemCategory objItem = new ItemCategory();

                        objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getString(Constant.CATEGORY_CID));
                        objItem.setCategoryImageUrl(objJson.getString(Constant.CATEGORY_IMAGE));

                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {
        if (getActivity() != null) {
            categoryAdapter = new CategoryAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(categoryAdapter);

            if (categoryAdapter.getItemCount() == 0) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_category));
    }
}
