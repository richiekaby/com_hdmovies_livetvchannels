package com.hdmovies.livetvchannels;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityForgotEmailPassword extends AppCompatActivity {

    MyApplication MyApp;

    ProgressDialog pDialog;

    String strEmail, strMessage;

    private PrefManager prf;

    private TextView login_heading;
    private Button bottom_action_button;

    private TextInputEditText input_email_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_email_password);

        MyApp = MyApplication.getInstance();

        prf= new PrefManager(getApplicationContext());

        pDialog = new ProgressDialog(this);

        login_heading = (TextView) findViewById(R.id.login_heading);
        bottom_action_button = (Button) findViewById(R.id.bottom_action_button);

        input_email_text = (TextInputEditText) this.findViewById(R.id.input_email_text);

        login_heading.setText("Forgot Password ?");

        bottom_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!input_email_text.getText().toString().trim().isEmpty() && input_email_text.getText().toString().trim().length() >= 3) {

                    strEmail = input_email_text.getText().toString().trim();

                    new MyTaskLogin().execute(Constant.FORGOT_URL + strEmail);
                } else {
                    Toast.makeText(ActivityForgotEmailPassword.this, "Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ImageView) findViewById(R.id.back_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityForgotEmailPassword.this, ActivityEmailRegister.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class MyTaskLogin extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismissProgressDialog();

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        strMessage = objJson.getString(Constant.MSG);
                        Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(getString(R.string.error_title) + "\n" + strMessage);
            input_email_text.setText("");
            input_email_text.requestFocus();
        } else {
            showToast(strMessage);
            Intent intentco = new Intent(ActivityForgotEmailPassword.this, ActivitySignInNew.class);
            intentco.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentco);
            finish();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ActivityForgotEmailPassword.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        pDialog.dismiss();
    }
}
