package com.hdmovies.livetvchannels.fragment;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.classes.purchaselogic.JSONParser;
import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.Validator;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.MyApplication;
import com.hdmovies.livetvchannels.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GiftVoucherFragment extends Fragment {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    // url to get all products list
    private static final String url = config.mainurl + "redeem_giftvoucher.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private PrefManager prf;

    //new
    private Context context;

    TextInputEditText vouchercode;

    Button btnSignUp;
    private Validator validator;
    MyApplication myApp;
    ProgressBar progressBar;
    ScrollView scrollView;

    private int success;
    private String message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        prf = new PrefManager(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_giftvoucher, container, false);

        myApp = MyApplication.getInstance();

        vouchercode = rootView.findViewById(R.id.vouchercode);
        btnSignUp = rootView.findViewById(R.id.submit);
        progressBar = rootView.findViewById(R.id.progressBar);
        scrollView = rootView.findViewById(R.id.lay_scroll);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (JsonUtils.isNetworkAvailable(requireActivity())) {

                    if (vouchercode.getText().length()>3) {
                        // Loading jsonarray in Background Thread
                        new OneLoadAllProducts().execute();
                    } else {
                        Toast.makeText(context, "Enter Valid Gift Voucher Code", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put(Constant.USER_ID, prf.getString(Constant.USER_ID));
            params.put(Constant.TAG_VOUCHERCODE, vouchercode.getText().toString());
            params.put("status", "Add Money Success");

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    try {
                        prf.setString(Constant.TAG_PLANACTIVE, json.getString(Constant.TAG_PLANACTIVE));
                        prf.setString(Constant.TAG_PLANDAYS, json.getString(Constant.TAG_PLANDAYS));
                        prf.setString(Constant.TAG_PLANSTART, json.getString(Constant.TAG_PLANSTART));
                        prf.setString(Constant.TAG_PLANEND, json.getString(Constant.TAG_PLANEND));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    message = json.getString("message");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                        Toast.makeText(context,"Gift Voucher Redeemed Succsessfully...Enjoy !",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
