package com.hdmovies.livetvchannels.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.hdmovies.livetvchannels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EarnFragment extends Fragment {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RAJANR = "rajanr";

    //user
    private static final String TAG_USERNAME = "username";

    //Prefrance
    private static PrefManager prf;

    //new
    private Context context;

    private ImageView howrefer;
    private TextView referCode;
    private TextView referDesc;
    private Button referNow;
    private LinearLayout referralOfferLL;

    ProgressBar progressBar;

    public EarnFragment() {
        // Required empty public constructor
    }

    public static EarnFragment newInstance(String param1, String param2) {
        EarnFragment fragment = new EarnFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        prf = new PrefManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootViewone = inflater.inflate(R.layout.fragment_earn, container, false);

        referNow = (Button) rootViewone.findViewById(R.id.referButton);
        referDesc = (TextView) rootViewone.findViewById(R.id.refMessage);
        referCode = (TextView) rootViewone.findViewById(R.id.referCode);
        howrefer = (ImageView) rootViewone.findViewById(R.id.howrefer);
        referralOfferLL = (LinearLayout) rootViewone.findViewById(R.id.referralLL);
        progressBar = rootViewone.findViewById(R.id.progressBar);

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getReferEarn().execute(Constant.EARN_URL + prf.getString(Constant.USER_ID));
        }

        referCode.setText(prf.getString(Constant.USER_ID));

        referNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent("android.intent.action.SEND");
                in.setType("text/plain");
                String string = "Download Apk From " + config.appurl + " Promo code is: " +prf.getString(Constant.USER_ID);
                in.putExtra("android.intent.extra.SUBJECT", getString(R.string.shareSub));
                in.putExtra("android.intent.extra.TEXT", string);
                startActivity(Intent.createChooser(in, "Share using"));
            }
        });

        return rootViewone;
    }

    @SuppressLint("StaticFieldLeak")
    private class getReferEarn extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            referralOfferLL.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            referralOfferLL.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        referDesc.setText(objJson.getString(Constant.TAG_REFERDESC));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {

    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
