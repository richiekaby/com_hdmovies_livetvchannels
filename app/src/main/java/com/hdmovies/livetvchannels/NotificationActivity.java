package com.hdmovies.livetvchannels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classes.purchaselogic.JSONParser;
import com.hdmovies.livetvchannels.adapter.NotificationsAdapter;
import com.hdmovies.livetvchannels.item.Notification;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.hdmovies.livetvchannels.util.RecyclerTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_ID;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_IMAGE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_LOG_ENTDATE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_MSG;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_TITLE;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_URL;
import static com.hdmovies.livetvchannels.util.Constant.TAG_NOTIFICATION_VIDEOID;

public class NotificationActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = Constant.GET_ALL_NOTIFICATION_URL;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RAJANR = "rajanr";

    //user
    private static final String TAG_USERID = "userid";

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance
    private static PrefManager prf;

    private int success;

    Toolbar toolbar;

    private List<Notification> notificationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotificationsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            Drawable background = this.getResources().getDrawable(R.drawable.statusbar_gradient);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //screen capture off
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

            window.setStatusBarColor(this.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(this.getResources().getColor(android.R.color.black));
            window.setBackgroundDrawable(background);
        }

        toolbar = findViewById(R.id.toolbar_videos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.notification));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        notificationList = new ArrayList();

        mAdapter = new NotificationsAdapter(notificationList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep notification_list_rowst_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep notification_list_row.xmlow.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listenerMyDividerItemDecoration
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Notification notification = notificationList.get(position);

                Intent in = new Intent(NotificationActivity.this, NotificationDetailsActivity.class);
                in.putExtra(TAG_NOTIFICATION_ID, notification.getNotificationid());
                in.putExtra(TAG_NOTIFICATION_TITLE, notification.getTitle());
                in.putExtra(TAG_NOTIFICATION_MSG, notification.getMsg());
                in.putExtra(TAG_NOTIFICATION_IMAGE, notification.getImage());
                in.putExtra(TAG_NOTIFICATION_VIDEOID, notification.getVideoid());
                in.putExtra(TAG_NOTIFICATION_URL, notification.getUrl());
                in.putExtra(TAG_NOTIFICATION_LOG_ENTDATE, notification.getLog_entdate());
                startActivity(in);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prf = new PrefManager(getApplicationContext());

        // Hashmap for ListView
        offersList = new ArrayList<>();

        new OneLoadAllProducts().execute();
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

    /**
     * Prepares sample data to provide data set to adapter
     */

    class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NotificationActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put(TAG_USERID, prf.getString(TAG_USERID));

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                if(success==1) {
                    // jsonarray found
                    // Getting Array of jsonarray
                    jsonarray = json.getJSONArray(TAG_RAJANR);

                    // looping through All jsonarray
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<>();

                        // adding each child node to HashMap key => value
                        //match
                        map.put(TAG_NOTIFICATION_ID, c.getString(TAG_NOTIFICATION_ID));
                        map.put(TAG_NOTIFICATION_TITLE, c.getString(TAG_NOTIFICATION_TITLE));
                        map.put(TAG_NOTIFICATION_MSG, c.getString(TAG_NOTIFICATION_MSG));
                        map.put(TAG_NOTIFICATION_IMAGE, c.getString(TAG_NOTIFICATION_IMAGE));
                        map.put(TAG_NOTIFICATION_VIDEOID, c.getString(TAG_NOTIFICATION_VIDEOID));
                        map.put(TAG_NOTIFICATION_URL, c.getString(TAG_NOTIFICATION_URL));
                        map.put(TAG_NOTIFICATION_LOG_ENTDATE, c.getString(TAG_NOTIFICATION_LOG_ENTDATE));

                        // adding HashList to ArrayList
                        offersList.add(map);
                    }
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

            //error solved
//            if(getActivity() == null)
//                return;

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray

                        /*
                          Updating parsed JSON data into ListView
                         */
                        for (int i = 0; i < offersList.size(); i++) {

                            Notification notification = new Notification();
                            notification.setNotificationid(offersList.get(i).get(TAG_NOTIFICATION_ID));
                            notification.setTitle(offersList.get(i).get(TAG_NOTIFICATION_TITLE));
                            notification.setMsg(offersList.get(i).get(TAG_NOTIFICATION_MSG));
                            notification.setImage(offersList.get(i).get(TAG_NOTIFICATION_IMAGE));
                            notification.setVideoid(offersList.get(i).get(TAG_NOTIFICATION_VIDEOID));
                            notification.setUrl(offersList.get(i).get(TAG_NOTIFICATION_URL));
                            notification.setLog_entdate(offersList.get(i).get(TAG_NOTIFICATION_LOG_ENTDATE));

                            notificationList.add(notification);

                            // notify adapter about data set changes
                            // so that it will render the list with new data
                            mAdapter.notifyDataSetChanged();

                        }


                    } else {
                        Toast.makeText(NotificationActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }
}
