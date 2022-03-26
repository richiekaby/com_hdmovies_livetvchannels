package com.hdmovies.livetvchannels;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ActivitySignIn extends AppCompatActivity implements Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener {

    RelativeLayout lay_sign, lay_forgot;
    Button button_sign_up;
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password
    EditText edtPassword;
    TextInputEditText promocode;
    String strEmail, strPassword, strMessage, strName, strUserId, strPromocode;
    Button btnLogin;
    TextView skiplogin;
    private Validator validator;
    MyApplication MyApp;
    ProgressDialog pDialog;
    CheckBox checkBox;
    boolean iswhichscreen;
    String videoiddetail;
    JsonUtils jsonUtils;
    TextView signintext;

    //rajan
    private PrefManager prf;

    public JSONObject loginobject;

    private static final int RC_SIGN_IN = 9001;

    private SignInButton sign_in_button_google;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prf= new PrefManager(getApplicationContext());

        MyApp = MyApplication.getInstance();
        pDialog = new ProgressDialog(this);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        lay_sign = findViewById(R.id.lay_sign);
//        button_sign_up = findViewById(R.id.button_sign_up);
//        lay_forgot = findViewById(R.id.lay_forgot);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        checkBox = findViewById(R.id.checkBox);
        promocode = findViewById(R.id.promocode);
//        btnLogin = findViewById(R.id.button_login);
        skiplogin = (TextView) findViewById(R.id.skip);
        signintext= findViewById(R.id.signintext);
        signintext.setText("Welcome  to  " + getResources().getString(R.string.app_name));

        Intent intent=getIntent();
        iswhichscreen=intent.getBooleanExtra("isfromdetail",false);
        videoiddetail=intent.getStringExtra("isvideoid");

        this.sign_in_button_google   =      (SignInButton)  findViewById(R.id.sign_in_button);
        sign_in_button_google = (SignInButton) findViewById(R.id.sign_in_button);
        sign_in_button_google.setSize(SignInButton.SIZE_STANDARD);
        TextView textView = (TextView) sign_in_button_google.getChildAt(0);
        textView.setText(getResources().getString(R.string.login_gg_text));

        if(config.demo) {
            ((TextView) findViewById(R.id.txt)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtcodecanyon)).setVisibility(View.VISIBLE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    promocode.setVisibility(View.VISIBLE);
                } else {
                    promocode.setVisibility(View.GONE);
                }

            }
        });

        try {
            GoogleSignIn();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Something went wrong. Please try again!");
        }

        this.sign_in_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    signIn();
                }
                else {
                    connected = false;
                    Toast.makeText(ActivitySignIn.this, "No internet. Check Your Internet is connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        lay_sign.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent_up = new Intent(ActivitySignIn.this, ActivitySignUp.class);
//                startActivity(intent_up);
//            }
//        });
//
//        button_sign_up.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent_up = new Intent(ActivitySignIn.this, ActivitySignUp.class);
//                startActivity(intent_up);
//            }
//        });

        skiplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignIn.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

//        lay_forgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ActivitySignIn.this, ActivityForgot.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validator.validate();
//            }
//        });


//        if (MyApp.getIsRemember()) {
//            checkBox.setChecked(true);
//            edtEmail.setText(MyApp.getRememberEmail());
//            edtPassword.setText(MyApp.getRememberPassword());
//        }
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            getResultGoogle(result);
        }
    }

    public void GoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Rajan_google_login_onConnectionFailed:" + connectionResult);

    }

    private void getResultGoogle(GoogleSignInResult result) {
//        System.out.println("Rajan_google_login_handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            String photo = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg" ;
            if (acct.getPhotoUrl()!=null){
                photo =  acct.getPhotoUrl().toString();
            }

//            System.out.println("Rajan_google_login_detail"+acct.getId().toString()+acct.getId()+ acct.getDisplayName().toString()+"google"+photo);
            String gid;
            if(acct.getId() != null) {
                gid = acct.getId();
            } else {
                gid = "1234567890";
            }

            strPromocode = promocode.getText().toString();

            if(acct.getDisplayName()!=null) {
                signUp(gid, acct.getEmail(), acct.getDisplayName().replaceAll("[^A-Za-z0-9]", ""), strPromocode, "google", photo);
            } else {
                signUp(gid,acct.getEmail(), acct.getEmail().replaceAll("[^A-Za-z0-9]",""), strPromocode, "google", photo);
            }
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        } else {
//            System.out.println("Rajan_google_login_handleSignInResult:" + result.getStatus());

        }
    }

    private void signUp(String toString, String strEmail, String strName, String strPromocode, String google, String photo) {
        try {
            if (JsonUtils.isNetworkAvailable(ActivitySignIn.this)) {
                strPassword = "12345";
                new MyTaskRegister().execute(Constant.REGISTER_URL_GOOGLE + strName + "&email=" + strEmail+"&password="+strPassword+"&phone="+toString+"&promocode="+strPromocode+"&pkg="+getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Something went wrong. Please try again!");
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
                if (result.length() == 0) {
                    showToast("Something went wrong. Try Again!");
                }

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
    public void onValidationSucceeded() {
        // TODO Auto-generated method stub
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();

        if (checkBox.isChecked()) {
            MyApp.saveIsRemember(true);
            MyApp.saveRemember(strEmail, strPassword);
        } else {
            MyApp.saveIsRemember(false);
        }

        try {
            if (JsonUtils.isNetworkAvailable(ActivitySignIn.this)) {
                prf.setString(Constant.USER_EMAIL, strEmail);
                new MyTaskLogin().execute(Constant.LOGIN_URL + strEmail + "&password=" + strPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Something went wrong. Please try again!");
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
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

            if (result == null) {
                if (result.length() == 0) {
                    showToast("Something went wrong. Try Again!");
                }

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

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(getString(R.string.error_title) + "\n" + strMessage);
        } else {
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strUserId, strName, strEmail);
            if(iswhichscreen)
            {
                Intent i = new Intent(ActivitySignIn.this, ActivityVideoDetails.class);
                i.putExtra("isvideoid",videoiddetail);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else {
                ActivityCompat.finishAffinity(ActivitySignIn.this);
                Intent i = new Intent(ActivitySignIn.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ActivitySignIn.this, msg, Toast.LENGTH_SHORT).show();
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
