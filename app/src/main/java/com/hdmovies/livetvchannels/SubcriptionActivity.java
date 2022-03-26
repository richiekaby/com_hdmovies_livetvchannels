package com.hdmovies.livetvchannels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.classes.purchaselogic.JSONParser;
import com.hdmovies.livetvchannels.adapter.SubcriptionsAdapter;
import com.hdmovies.livetvchannels.item.Subcription;
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

import static com.hdmovies.livetvchannels.util.Constant.PLANDAYS;
import static com.hdmovies.livetvchannels.util.Constant.PLANID;
import static com.hdmovies.livetvchannels.util.Constant.PLANNAME;
import static com.hdmovies.livetvchannels.util.Constant.PLANPRICE;

public class SubcriptionActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();

    private ArrayList<HashMap<String, String>> offersList;

    // url to get all products list
    private static final String url = Constant.GET_ALL_PLAN_URL;

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

    private List<Subcription> subcriptionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubcriptionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription);

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
        getSupportActionBar().setTitle(getResources().getString(R.string.subcription));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        subcriptionList = new ArrayList();

        mAdapter = new SubcriptionsAdapter(subcriptionList);

        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep subcription_list_rowst_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep subcription_list_row.xmlow.xml width to `wrap_content`
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
                Subcription subcription = subcriptionList.get(position);
                Toast.makeText(getApplicationContext(), subcription.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();

                Intent in = new Intent(SubcriptionActivity.this, MyWalletActivity.class);
                in.putExtra(PLANID, subcription.getPlanid());
                in.putExtra(PLANNAME, subcription.getTitle());
                in.putExtra(PLANPRICE, subcription.getPrice());
                in.putExtra(PLANDAYS, subcription.getDays());
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
            pDialog = new ProgressDialog(SubcriptionActivity.this);
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
                        map.put(PLANID, c.getString(PLANID));
                        map.put(PLANNAME, c.getString(PLANNAME));
                        map.put(PLANPRICE, c.getString(PLANPRICE));
                        map.put(PLANDAYS, c.getString(PLANDAYS));

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

                            Subcription subcription = new Subcription();
                            subcription.setPlanid(offersList.get(i).get(PLANID));
                            subcription.setTitle(offersList.get(i).get(PLANNAME));
                            subcription.setPrice(offersList.get(i).get(PLANPRICE));
                            subcription.setDays(offersList.get(i).get(PLANDAYS));

                            subcriptionList.add(subcription);

                            // notify adapter about data set changes
                            // so that it will render the list with new data
                            mAdapter.notifyDataSetChanged();

                        }


                    } else {
                        Toast.makeText(SubcriptionActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }
}
