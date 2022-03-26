package com.hdmovies.livetvchannels;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.OtpEditText;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class OtpVerifyActivity extends AppCompatActivity
{


    private static final String TAG = "OtpVerifyActivity";
    private static final int RC_SIGN_IN = 9001;

    MyApplication MyApp;

    public JSONObject loginobject;
    ProgressDialog pDialog;

    String strEmail, strMessage, strName, strUserId;

    String VerificationCode = "";
    private OtpEditText otp_edit_text_login_activity;
    private RelativeLayout relative_layout_confirm_top_login_activity;
    private LinearLayout linear_layout_otp_confirm_login_activity;
    private String phoneNum ="";
    private String password ="";
    private String promocode ="";
    private PrefManager prf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        MyApp = MyApplication.getInstance();

        prf= new PrefManager(getApplicationContext());

        pDialog = new ProgressDialog(this);

        initView();
        initAction();

        //Send Otp
        Bundle bundle = getIntent().getExtras();
        try {
            phoneNum = bundle.getString("phone","123");
            password = bundle.getString("password","12345");
            promocode = bundle.getString("promocode","");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loginWithPhone();
    }


    public void initView(){

        this.otp_edit_text_login_activity   =      (OtpEditText)  findViewById(R.id.otp_edit_text_login_activity);
        this.relative_layout_confirm_top_login_activity   =      (RelativeLayout)  findViewById(R.id.relative_layout_confirm_top_login_activity);
        this.linear_layout_otp_confirm_login_activity =      (LinearLayout)   findViewById(R.id.linear_layout_otp_confirm_login_activity);

    }
    public void initAction(){
        relative_layout_confirm_top_login_activity.setOnClickListener(v->{

            if (otp_edit_text_login_activity.getText().toString().trim().length()==0){
                Toast.makeText(this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
            }else {
                if (otp_edit_text_login_activity.getText().toString().trim().equals(VerificationCode.toString().trim())) {
                    String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
//                    System.out.println("Rajan_phoneNum"+phoneNum);

                    signUp(phoneNum.replaceAll(Pattern.quote("+"),""), "null", "Name".replaceAll("[^A-Za-z0-9]", ""), promocode, "mobile", photo);
                } else {
                    Toast.makeText(this, "The verification code you have been entered incorrect !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginWithPhone() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum, 30L /*timeout*/, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        VerificationCode = phoneAuthCredential.getSmsCode().toString();
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(OtpVerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signUp(String toString, String strEmail, String strName, String strPromocode, String google, String photo) {
        try {
            if (JsonUtils.isNetworkAvailable(OtpVerifyActivity.this)) {
                String strPassword = password;
                new MyTaskRegister().execute(Constant.REGISTER_URL_MOBILE + strName + "&email=" + strEmail+"&password="+strPassword+"&phone="+toString+"&promocode="+strPromocode+"&pkg="+getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            set(OtpVerifyActivity.this,"Something went wrong. Please try again!");
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
                set(OtpVerifyActivity.this,"Something went wrong. Try Again!");
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
            set(OtpVerifyActivity.this,getString(R.string.error_title) + "\n" + strMessage);
        } else {
            strEmail = prf.getString(Constant.USER_EMAIL);

            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strUserId, strName, strEmail);

            ActivityCompat.finishAffinity(OtpVerifyActivity.this);
            Intent i = new Intent(OtpVerifyActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public static void set(Activity activity, String s){
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }
}

