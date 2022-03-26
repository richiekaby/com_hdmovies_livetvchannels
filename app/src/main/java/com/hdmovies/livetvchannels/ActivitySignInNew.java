package com.hdmovies.livetvchannels;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ActivitySignInNew extends AppCompatActivity implements Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener {

    RelativeLayout lay_sign, lay_forgot;
    Button button_sign_up;
    @NotEmpty
    @Email
    EditText edtEmail;
    @NotEmpty
    @Password
    EditText edtPassword;
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

    private Dialog dialog;
    private  Boolean DialogOpened = false;
    private TextView text_view_go_pro,text_view_go_pro_skip;
    private TextView entergameusercaption;

    //Google Login
    private ImageView google;

    //Facebook Login
    private ImageView facebook;
    private LoginButton sign_in_button_facebook;
    private CallbackManager callbackManager;

    private RelativeLayout login_register_mobile;
    private RelativeLayout login_register_email;
    private TextView login_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_new);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prf= new PrefManager(getApplicationContext());

        MyApp = MyApplication.getInstance();
        pDialog = new ProgressDialog(this);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        lay_sign = findViewById(R.id.lay_sign);
        skiplogin = (TextView) findViewById(R.id.skip);

        Intent intent=getIntent();
        iswhichscreen=intent.getBooleanExtra("isfromdetail",false);
        videoiddetail=intent.getStringExtra("isvideoid");

        this.google = (ImageView) findViewById(R.id.google_sign_in);
        this.facebook = (ImageView) findViewById(R.id.facebook);
        this.sign_in_button_facebook =      (LoginButton)   findViewById(R.id.sign_in_button_facebook);
        this.sign_in_button_facebook.setReadPermissions(Arrays.asList("email"));

        this.sign_in_button_google   =      (SignInButton)  findViewById(R.id.sign_in_button);
        sign_in_button_google.setSize(SignInButton.SIZE_STANDARD);
//        TextView textView = (TextView) sign_in_button_google.getChildAt(0);
//        textView.setText(getResources().getString(R.string.login_gg_text));

        login_register_mobile = (RelativeLayout) findViewById(R.id.login_register_mobile);
        login_register_email = (RelativeLayout) findViewById(R.id.login_register_email);
        login_text = (TextView) findViewById(R.id.login_text);

        try {
            FaceookSignIn();
            GoogleSignIn();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Something went wrong. Please try again!");
        }

        this.facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_button_facebook.performClick();
            }
        });

        this.google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    strPromocode = "";

                    signIn();
                }
                else {
                    connected = false;
                    Toast.makeText(ActivitySignInNew.this, "No internet. Check Your Internet is connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.sign_in_button_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    strPromocode = "";

                    signIn();
                }
                else {
                    connected = false;
                    Toast.makeText(ActivitySignInNew.this, "No internet. Check Your Internet is connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        login_register_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignInNew.this, ActivityMobileNumberRegister.class);
                intent.putExtra("login",true);
                startActivity(intent);
            }
        });

        login_register_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignInNew.this, ActivityEmailRegister.class);
                intent.putExtra("login",true);
                startActivity(intent);
            }
        });

        login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignInNew.this, ActivitySignUpNew.class);
                startActivity(intent);
            }
        });

        skiplogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignInNew.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void showDialog(){
        this.dialog = new Dialog(this,
                R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_subscribe);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
//        this.entergameusercaption = (TextView) dialog.findViewById(R.id.entergameusercaption);
//        this.entergameusercaption.setText("Enter Your " + prf.getString(TAG_GAMENAME) +" Username : ");

//        ((TextInputEditText) dialog.findViewById(R.id.username)).setHint(prf.getString(TAG_GAMENAME) + " Username");

        this.text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);
        this.text_view_go_pro_skip=(TextView) dialog.findViewById(R.id.text_view_go_pro_skip);

        RelativeLayout relativeLayout_close_rate_gialog=(RelativeLayout) dialog.findViewById(R.id.relativeLayout_close_rate_gialog);
        relativeLayout_close_rate_gialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPromocode = ((TextInputEditText) dialog.findViewById(R.id.username)).getText().toString();

                if (!strPromocode.isEmpty()) {
                    dialog.dismiss();

                    // Join Player in Match in Background Thread
                    signIn();
                } else {
                    Toast.makeText(ActivitySignInNew.this, "Please Enter Your Referral Promo Code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        text_view_go_pro_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strPromocode = "";

                dialog.dismiss();

                // Join Player in Match in Background Thread
                signIn();
            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened=true;

    }

    public void FaceookSignIn(){

        // Other app specific specializationsign_in_button_facebook.setReadPermissions(Arrays.asList("public_profile"));
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        sign_in_button_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getResultFacebook(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MyApp, "Operation has been cancelled ! ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MyApp, "Operation has been cancelled ! ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getResultFacebook(JSONObject object){
        System.out.println("Rajan_facebook"+ object.toString());
        try {
//            signUp(object.getString("id").toString(),object.getString("id").toString(),object.getString("name").toString(),"facebook",object.getJSONObject("picture").getJSONObject("data").getString("url"));

            if(object.getString("name")!=null) {
                signUp(object.getString("id"), object.getString("id"), object.getString("name").replaceAll("[^A-Za-z0-9]", ""), strPromocode, "facebook", object.getJSONObject("picture").getJSONObject("data").getString("url"));
            } else {
                signUp(object.getString("id"),object.getString("id"), object.getString("id").toString().replaceAll("[^A-Za-z0-9]",""), strPromocode, "facebook", object.getJSONObject("picture").getJSONObject("data").getString("url"));
            }

            LoginManager.getInstance().logOut();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

//            strPromocode = promocode.getText().toString();

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
            if (JsonUtils.isNetworkAvailable(ActivitySignInNew.this)) {
                final int min = 1000;
                final int max = 10000;
                final int random = new Random().nextInt((max - min) + 1) + min;
                strPassword = String.valueOf(random);

                new MyTaskRegister().execute(Constant.REGISTER_URL_GOOGLE + strName + "&email=" + strEmail+"&password="+strPassword+"&promocode="+strPromocode+"&pkg="+getPackageName());
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
            if (JsonUtils.isNetworkAvailable(ActivitySignInNew.this)) {
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
                Intent i = new Intent(ActivitySignInNew.this, ActivityVideoDetails.class);
                i.putExtra("isvideoid",videoiddetail);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else {
                ActivityCompat.finishAffinity(ActivitySignInNew.this);
                Intent i = new Intent(ActivitySignInNew.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ActivitySignInNew.this, msg, Toast.LENGTH_SHORT).show();
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
