package com.hdmovies.livetvchannels;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityEmailRegister extends AppCompatActivity {

    MyApplication MyApp;

    public JSONObject loginobject;
    ProgressDialog pDialog;

    String strEmail, strMessage, strName, strUserId;

    private PrefManager prf;

    private TextView login_heading;
    private Button mobile_action_button;

    private TextInputEditText phoneed;
    private TextInputEditText input_password_text;
    private TextInputEditText input_promo_code_text;

    private TextView forgot_password;

    private CheckBox checkbox2;
    private CheckBox checkbox3;

    private RelativeLayout promocode_ly;
    private RelativeLayout gdpr_layout;

    private Boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_register);

        MyApp = MyApplication.getInstance();

        prf= new PrefManager(getApplicationContext());

        pDialog = new ProgressDialog(this);

        //Send Otp
        Bundle bundle = getIntent().getExtras();
        try {
            login = bundle.getBoolean("login",true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        login_heading = (TextView) findViewById(R.id.login_heading);
        mobile_action_button = (Button) findViewById(R.id.mobile_action_button);

        phoneed = (TextInputEditText) this.findViewById(R.id.input_phone_text);
        input_password_text = (TextInputEditText) this.findViewById(R.id.input_password_text);
        input_promo_code_text = (TextInputEditText) this.findViewById(R.id.input_promo_code_text);
        forgot_password = (TextView) this.findViewById(R.id.forgot_password);
        checkbox2 = (CheckBox) this.findViewById(R.id.checkbox2);
        checkbox3 = (CheckBox) this.findViewById(R.id.checkbox3);
        promocode_ly = (RelativeLayout) this.findViewById(R.id.promocode_ly);
        gdpr_layout = (RelativeLayout) this.findViewById(R.id.gdpr_layout);

        if (login) {
            //login via email
            login_heading.setText("Login Via Email ID");
            mobile_action_button.setText("LOGIN");
            promocode_ly.setVisibility(View.GONE);
            forgot_password.setVisibility(View.VISIBLE);
            gdpr_layout.setVisibility(View.GONE);
        } else {
            //register via email
            login_heading.setText("Register Via Email ID");
            mobile_action_button.setText("REGISTER");
            promocode_ly.setVisibility(View.VISIBLE);
            gdpr_layout.setVisibility(View.VISIBLE);
        }

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityEmailRegister.this, ActivityForgotEmailPassword.class);
                startActivity(i);
            }
        });


        mobile_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() >= 8) {

                    if (!input_password_text.getText().toString().trim().isEmpty()) {

                        if (login) {
                            //login via email
                            String email = phoneed.getText().toString().trim();
                            String password = input_password_text.getText().toString().trim();
                            String promocode = input_promo_code_text.getText().toString().trim();

                            signin("", email, password, "Name".replaceAll("[^A-Za-z0-9]", ""), promocode, "email", "photo");
                        } else {
                            //register via email
                            if (checkbox2.isChecked() && checkbox3.isChecked()) {
                                String email = phoneed.getText().toString().trim();
                                String password = input_password_text.getText().toString().trim();
                                String promocode = input_promo_code_text.getText().toString().trim();

                                signup("", email, password, "Name".replaceAll("[^A-Za-z0-9]", ""), promocode, "email", "photo");
                            } else {
                                Toast.makeText(ActivityEmailRegister.this, "Please Select All Terms and Conditions", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(ActivityEmailRegister.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityEmailRegister.this, "Enter Valid Email ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ImageView) findViewById(R.id.back_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityEmailRegister.this, ActivitySignInNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void signin(String phone, String strEmail, String password, String strName, String strPromocode, String google, String photo) {
        try {
            if (JsonUtils.isNetworkAvailable(ActivityEmailRegister.this)) {
                String strPassword = password;
                new MyTaskRegister().execute(Constant.REGISTER_URL_EMAIL_LOGIN + strName + "&email=" + strEmail+"&password="+strPassword+"&phone="+phone+"&promocode="+strPromocode+"&pkg="+getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            set(ActivityEmailRegister.this,"Something went wrong. Please try again!");
        }
    }

    class MyTaskRegister extends AsyncTask<String, Void, String> {

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

            if (result == null) {
                set(ActivityEmailRegister.this,"Something went wrong. Try Again!");
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        loginobject = jsonArray.getJSONObject(i);

                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);

                            if(loginobject != null) {
                                try {
                                    System.out.println("Rajan_login_user_id" + loginobject.getString(Constant.USER_ID));
                                    prf.setString(Constant.USER_ID, loginobject.getString(Constant.USER_ID));
                                    prf.setString(Constant.USER_NAME, loginobject.getString(Constant.USER_NAME));
                                    prf.setString(Constant.USER_EMAIL, loginobject.getString(Constant.USER_EMAIL));
                                    prf.setString(Constant.USER_PHONE, loginobject.getString(Constant.USER_PHONE));
                                    prf.setString(Constant.TAG_PLANID, loginobject.getString(Constant.TAG_PLANID));
                                    prf.setString(Constant.TAG_PLANACTIVE, loginobject.getString(Constant.TAG_PLANACTIVE));
                                    prf.setString(Constant.TAG_PLANDAYS, loginobject.getString(Constant.TAG_PLANDAYS));
                                    prf.setString(Constant.TAG_PLANSTART, loginobject.getString(Constant.TAG_PLANSTART));
                                    prf.setString(Constant.TAG_PLANEND, loginobject.getString(Constant.TAG_PLANEND));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
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

    public void setResult() {
        if (Constant.GET_SUCCESS_MSG == 0) {
            set(ActivityEmailRegister.this,strMessage);
        } else {
            strEmail = prf.getString(Constant.USER_EMAIL);

            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strUserId, strName, strEmail);

            ActivityCompat.finishAffinity(ActivityEmailRegister.this);
            Intent i = new Intent(ActivityEmailRegister.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void signup(String phone, String strEmail, String password, String strName, String strPromocode, String google, String photo) {
        try {
            if (JsonUtils.isNetworkAvailable(ActivityEmailRegister.this)) {
                String strPassword = password;
                new MyTaskRegisterEmail().execute(Constant.REGISTER_URL_EMAIL_REGISTER + strName + "&email=" + strEmail+"&password="+strPassword+"&phone="+phone+"&promocode="+strPromocode+"&pkg="+getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            set(ActivityEmailRegister.this,"Something went wrong. Please try again!");
        }
    }

    class MyTaskRegisterEmail extends AsyncTask<String, Void, String> {

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

            if (result == null) {
                set(ActivityEmailRegister.this,"Something went wrong. Try Again!");
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        loginobject = jsonArray.getJSONObject(i);

                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strUserId = objJson.getString(Constant.USER_ID);

                            if(loginobject != null) {
                                try {
                                    System.out.println("Rajan_login_user_id" + loginobject.getString(Constant.USER_ID));
                                    prf.setString(Constant.USER_ID, loginobject.getString(Constant.USER_ID));
                                    prf.setString(Constant.USER_NAME, loginobject.getString(Constant.USER_NAME));
                                    prf.setString(Constant.USER_EMAIL, loginobject.getString(Constant.USER_EMAIL));
                                    prf.setString(Constant.USER_PHONE, loginobject.getString(Constant.USER_PHONE));
                                    prf.setString(Constant.TAG_PLANID, loginobject.getString(Constant.TAG_PLANID));
                                    prf.setString(Constant.TAG_PLANACTIVE, loginobject.getString(Constant.TAG_PLANACTIVE));
                                    prf.setString(Constant.TAG_PLANDAYS, loginobject.getString(Constant.TAG_PLANDAYS));
                                    prf.setString(Constant.TAG_PLANSTART, loginobject.getString(Constant.TAG_PLANSTART));
                                    prf.setString(Constant.TAG_PLANEND, loginobject.getString(Constant.TAG_PLANEND));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void set(Activity activity, String s){
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }
}
