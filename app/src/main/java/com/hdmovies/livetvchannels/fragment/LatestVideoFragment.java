package com.hdmovies.livetvchannels.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hdmovies.livetvchannels.adapter.AllVideoAdapter;
import com.hdmovies.livetvchannels.item.ItemLatest;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.ItemOffsetDecoration;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class LatestVideoFragment extends Fragment {

    ArrayList<ItemLatest> mListItem;
    public RecyclerView recyclerView;
    AllVideoAdapter allVideoAdapter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_video, container, false);


        mListItem = new ArrayList<>();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_latest));
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.rv_video);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getLatestVideo().execute(Constant.LATEST_URL);
        }

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class getLatestVideo extends AsyncTask<String, Void, String> {

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
            allVideoAdapter = new AllVideoAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(allVideoAdapter);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.menu_latest));
    }
}
