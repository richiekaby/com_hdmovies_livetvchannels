package com.hdmovies.livetvchannels;

import android.app.Activity;
import android.app.ProgressDialog;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.classes.purchaselogic.JSONParser;
import com.hdmovies.livetvchannels.config.config;
import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;
import com.google.android.material.textfield.TextInputEditText;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rilixtech.CountryCodePicker;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//import instamojo.library.InstamojoPay;
//import instamojo.library.InstapayListener;

import static com.hdmovies.livetvchannels.util.Constant.PLANDAYS;
import static com.hdmovies.livetvchannels.util.Constant.PLANID;
import static com.hdmovies.livetvchannels.util.Constant.PLANNAME;
import static com.hdmovies.livetvchannels.util.Constant.PLANPRICE;

public class MyWalletActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback, PaymentResultListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private final JSONParser jsonParser = new JSONParser();
    private final JSONParserString jsonParserString = new JSONParserString();

    // url to get all products list
    private static final String url = config.mainurl + "payment.php";
    private static final String urlpaytmchecksum = config.paytmchecksum + "generateChecksum.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //instamojo
    private static final String TAG_INSTA_ORDERID = "instaorderid";
    private static final String TAG_INSTA_TXNID = "instatxnid";
    private static final String TAG_INSTA_PAYMENTID = "instapaymentid";
    private static final String TAG_INSTA_TOKEN = "instatoken";

    private String balance;
    private String email;
    private String number;
    private TextView walletBalance;

    //Prefrance
    private static PrefManager prf;

    //paytm
    private String paytmemail;
    private String paytmphone;
    private String paytmamount;
    private String paytmpurpose;
    private String paytmbuyername;
    private String paytmorder_id;
    private String paytmchecksumhash;

//    //instamojo
//    InstapayListener listener;
//    InstamojoPay instamojoPay;

    private String addamount;
    private String instaorderid;
    private String instatoken;
    private String instapaymentid;
    private String instatxnid;

    private int success;

    //Paypal
    final int REQUEST_CODE = 1;
    final String get_token = config.mainurl + "paypal/main.php";
    final String send_payment_details = config.mainurl + "paypal/checkout.php";
    String token, paypalamount;
    HashMap<String, String> paramHash;
    public String stringNonce;

    //GooglePay
    private String googleamount;
    public String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    public int GOOGLE_PAY_REQUEST_CODE = 123;

    private CountryCodePicker ccp;
    private TextInputEditText phoneed;

    private String planid;
    private String planname;
    private String planprice;
    private String plandays;

    Toolbar toolbar;

    public LinearLayout paytmln, googleln, paypalln, instamojoln, razorpayln, paykunln, traknpayln, flutterwaveln;
    public RadioButton paytm, google, paypal, instamojo, razorpay, paykun, traknpay, flutterwave;

    public void PaytmAddMoney(String email, String phone, String amount, String purpose, String buyername) {

        paytmemail = email;
        paytmphone = phone;
        paytmamount = amount;
        paytmpurpose = purpose;
        paytmbuyername = buyername;

        final int min = 1000;
        final int max = 10000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        paytmorder_id = prf.getString(Constant.USER_ID) +random;

        // Join Player in Match in Background Thread
        new GetChecksum().execute();

    }

    public void GooglePayAddMoney(String email, String number, String amount, String add_money_to_wallet, String name) {

        googleamount = amount;

        final int min = 100000;
        final int max = 1000000;
        final int random = new Random().nextInt((max - min) + 1) + min;

        Uri uri =
                Uri.parse("upi://pay").buildUpon()
                        .appendQueryParameter("pa", config.UPI)
                        .appendQueryParameter("pn", config.MERCHANTNAME)
                        .appendQueryParameter("mc", "") // "your-merchant-code"
                        .appendQueryParameter("tr", String.valueOf(random)) // "your-transaction-ref-id"
                        .appendQueryParameter("tn", add_money_to_wallet)
                        .appendQueryParameter("am", amount)
                        .appendQueryParameter("cu", "INR")
//                        .appendQueryParameter("url", "your-transaction-url")
                        .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

    }

//    private void PaykunAddMoney(String email, String number, String amount, String add_money_to_wallet, String name) {
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("merchant_id",config.merchantIdLive);
//            object.put("access_token",config.accessTokenLive);
//            object.put("customer_name",name);
//            object.put("customer_email",email);
//            object.put("customer_phone",number);
//            object.put("product_name",planname);
//            object.put("order_no",System.currentTimeMillis()); // order no. should have 10 to 30 character in numeric format
//            object.put("amount",amount);  // minimum amount should be 10
//            object.put("isLive",false); // need to send false if you are in sandbox mode
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new PaykunApiCall.Builder(MyWalletActivity.this).sendJsonObject(object); // Paykun api to initialize your payment and send info.
//    }

//    private void TraknpayAddMoney(String email, String phone, String amount, String purpose, String buyername) {
//
//        addamount = amount;
//
//        Random rnd = new Random();
//        int n = 100000 + rnd.nextInt(900000);
//
//        PaymentParams pgPaymentParams = new PaymentParams();
//        pgPaymentParams.setAPiKey(config.PG_API_KEY);
//        pgPaymentParams.setAmount(amount);
//        pgPaymentParams.setEmail(email);
//        pgPaymentParams.setName(buyername);
//        pgPaymentParams.setPhone(phone);
//        pgPaymentParams.setOrderId(Integer.toString(n));
//        pgPaymentParams.setCurrency("INR");
//        pgPaymentParams.setDescription(purpose);
//        pgPaymentParams.setCity("RAJSTHAN");
//        pgPaymentParams.setState("RAJSTHAN");
//        pgPaymentParams.setAddressLine1("RAJSTHAN");
//        pgPaymentParams.setAddressLine2("RAJSTHAN");
//        pgPaymentParams.setZipCode("400001");
//        pgPaymentParams.setCountry("IND");
//        pgPaymentParams.setReturnUrl("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//        pgPaymentParams.setMode("LIVE");
//        pgPaymentParams.setUdf1("");
//        pgPaymentParams.setUdf2("");
//        pgPaymentParams.setUdf3("");
//        pgPaymentParams.setUdf4("");
//        pgPaymentParams.setUdf5("");
//        pgPaymentParams.setEnableAutoRefund("n");
//        pgPaymentParams.setOfferCode("testcoupon");
//        //pgPaymentParams.setSplitInfo("{\"vendors\":[{\"vendor_code\":\"24VEN985\",\"split_amount_percentage\":\"20\"}]}");
//
//        PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams,MyWalletActivity.this);
//        pgPaymentInitialzer.initiatePaymentProcess();
//    }

    private void FlutterWaveAddMoney(String string, String phn, String planprice, String purpose, String buyername) {

        addamount = planprice;

        final int min = 1000;
        final int max = 10000;
        final int random = new Random().nextInt((max - min) + 1) + min;
        paytmorder_id = prf.getString(Constant.USER_ID) +random;

        RavePayManager raveManager;

        raveManager = new RaveUiManager(this)
                .acceptMpesaPayments(false)
                .acceptAccountPayments(false)
                .acceptCardPayments(true)
                .allowSaveCardFeature(false, true)
                .acceptAchPayments(false)
                .acceptGHMobileMoneyPayments(false)
                .acceptUgMobileMoneyPayments(false)
                .acceptZmMobileMoneyPayments(false)
                .acceptRwfMobileMoneyPayments(false)
                .acceptUkPayments(true)
                .acceptSaBankPayments(false)
                .acceptFrancMobileMoneyPayments(false)
                .acceptBankTransferPayments(false)
                .acceptUssdPayments(false)
                .acceptBarterPayments(false)
                //                    .withTheme(R.style.TestNewTheme)
                .showStagingLabel(false)
                .setAmount(Double.parseDouble(planprice))
                .setCurrency(config.currency)
                .setEmail(string)
                .setfName(buyername)
                .setlName(buyername)
//                .setPhoneNumber(phoneNumber, false)
//                .setNarration(narration)
                .setPublicKey(config.publicKey)
                .setEncryptionKey(config.encryptionKey)
                .setTxRef(paytmorder_id)
                .onStagingEnv(false)
//                .setSubAccounts(subAccounts)
//                .isPreAuth(isPreAuthSwitch.isChecked())
//                .setMeta(meta)
                .shouldDisplayFee(true);

//        // Customize pay with bank transfer options (optional)
//        if (isPermanentAccountSwitch.isChecked())
//            ((RaveUiManager) raveManager).acceptBankTransferPayments(true, true);
//        else {
//            if (setExpirySwitch.isChecked()) {
//                int duration = 0, frequency = 0;
//                try {
//                    duration = Integer.parseInt(durationEt.getText().toString());
//                    frequency = Integer.parseInt(frequencyEt.getText().toString());
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//                ((RaveUiManager) raveManager).acceptBankTransferPayments(true, duration, frequency);
//            }
//        }

        raveManager.initialize();


    }

    class GetChecksum extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
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
            params.put( "MID" , config.MID);
            params.put( "ORDER_ID" , paytmorder_id);
            params.put( "CUST_ID" , prf.getString(Constant.USER_ID));
//            params.put( "MOBILE_NO" , paytmphone);
            params.put( "EMAIL" , prf.getString(Constant.USER_EMAIL));
            params.put( "CHANNEL_ID" , "WAP");
            params.put( "TXN_AMOUNT" , paytmamount);
            params.put( "WEBSITE" , config.WEBSITE);
            params.put( "INDUSTRY_TYPE_ID" , config.INDUSTRY_TYPE_ID);
            params.put( "CALLBACK_URL", config.CALLBACK_URL + paytmorder_id);

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(urlpaytmchecksum, "POST", params);

            // Check your log cat for JSON reponse

            if(json != null){
                try {

                    paytmchecksumhash=json.has("CHECKSUMHASH")?json.getString("CHECKSUMHASH"):"";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    try {

                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put( "MID" , config.MID);
                        // Key in your staging and production MID available in your dashboard

                        paramMap.put( "ORDER_ID" , paytmorder_id);
                        paramMap.put( "CUST_ID" , prf.getString(Constant.USER_ID));
//                        paramMap.put( "MOBILE_NO" , paytmphone);
                        paramMap.put( "EMAIL" , prf.getString(Constant.USER_EMAIL));
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , paytmamount);
                        paramMap.put( "WEBSITE" , config.WEBSITE);
                        paramMap.put( "INDUSTRY_TYPE_ID" , config.INDUSTRY_TYPE_ID);
                        paramMap.put( "CALLBACK_URL", config.CALLBACK_URL + paytmorder_id);
                        paramMap.put( "CHECKSUMHASH" , paytmchecksumhash);
                        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

                        // For Staging environment:
//                        PaytmPGService Service = PaytmPGService.getStagingService();

                        // For Production environment:
                        PaytmPGService Service = PaytmPGService.getProductionService();

                        Service.initialize(Order, null);

                        Service.startPaymentTransaction(MyWalletActivity.this, true, true, MyWalletActivity.this);

                    } catch (Exception e) {
                        System.out.println("Rjn_paytm"+e.toString());
                        e.printStackTrace();
                    }

                }
            });

        }

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {

        // getting JSON string from URL
        JSONObject json = null;
        try {
            String resstatus=inResponse.getString("STATUS");

            if(resstatus.equalsIgnoreCase("TXN_SUCCESS")) {

                instaorderid = inResponse.getString("ORDERID");
                instatxnid = inResponse.getString("TXNID");
                addamount = inResponse.getString("TXNAMOUNT");
                instapaymentid = inResponse.getString("CHECKSUMHASH");
                instatoken = inResponse.getString("MID");

                // Loading jsonarray in Background Thread
                new OneLoadAllProducts().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.payment));
        getSupportActionBar().setTitle(getResources().getString(R.string.payment));

        // Call the function callInstamojo to start payment here

        prf = new PrefManager(MyWalletActivity.this);

        planid = getIntent().getStringExtra(PLANID);
        planname = getIntent().getStringExtra(PLANNAME);
        planprice = getIntent().getStringExtra(PLANPRICE);
        plandays = getIntent().getStringExtra(PLANDAYS);

        paytmln = (LinearLayout) findViewById(R.id.paytmln);
        paypalln = (LinearLayout) findViewById(R.id.paypalln);
        instamojoln = (LinearLayout) findViewById(R.id.instamojoln);
        razorpayln = (LinearLayout) findViewById(R.id.razorpayln);
        googleln = (LinearLayout) findViewById(R.id.googleln);
        paykunln = (LinearLayout) findViewById(R.id.paykunln);
        traknpayln = (LinearLayout) findViewById(R.id.traknpayln);
        flutterwaveln = (LinearLayout) findViewById(R.id.flutterwaveln);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneed = (TextInputEditText) this.findViewById(R.id.numbered);
//        ccp.registerPhoneNumberTextView(phoneed);

//        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
//            @Override
//            public void onCountrySelected(Country selectedCountry) {
//                countrycode = selectedCountry.getPhoneCode();
//                Toast.makeText(MobileVerifyActivity.this, "Updated " + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT).show();
//            }
//        });

        if(!config.paytm) {
            paytmln.setVisibility(View.GONE);
        }
        if(!config.paypal) {
            paypalln.setVisibility(View.GONE);
        }
        if(!config.instamojo) {
            instamojoln.setVisibility(View.GONE);
        }
        if(!config.razorpay) {
            razorpayln.setVisibility(View.GONE);
        }
        if(!config.google) {
            googleln.setVisibility(View.GONE);
        }
        if(!config.paykun) {
            paykunln.setVisibility(View.GONE);
        }
        if(!config.traknpay) {
            traknpayln.setVisibility(View.GONE);
        }
        if(!config.flutterwave) {
            flutterwaveln.setVisibility(View.GONE);
        }

        paytm = (RadioButton) findViewById(R.id.radio0);
        paypal = (RadioButton) findViewById(R.id.radio01);
        instamojo = (RadioButton) findViewById(R.id.radio02);
        razorpay = (RadioButton) findViewById(R.id.radio03);
        google = (RadioButton) findViewById(R.id.radio5);
        paykun = (RadioButton) findViewById(R.id.radio7);
        traknpay = (RadioButton) findViewById(R.id.radio11);
        flutterwave = (RadioButton) findViewById(R.id.radio12);

        walletBalance = (TextView) findViewById(R.id.walletBalance);
        walletBalance.setText(config.currency + " "+planprice);

        Button pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!phoneed.getText().toString().trim().isEmpty() && phoneed.getText().toString().trim().length() >= 5) {
                    String phone = ccp.getSelectedCountryCode()+phoneed.getText().toString().trim();
                    String phn = phoneed.getText().toString().trim();

                    if (paytm.isChecked()) {
                        PaytmAddMoney(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                    } else if (paypal.isChecked()){
                        onBraintreeSubmit(email, phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                    } else if (instamojo.isChecked()){
                        callInstamojoPay(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                    } if (razorpay.isChecked()) {
                        startPayment(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                    } else if (google.isChecked()) {
                        if (isAppInstalled(MyWalletActivity.this,"com.google.android.apps.nbu.paisa.user")) {
                            GooglePayAddMoney(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                        } else {
                            Toast.makeText(MyWalletActivity.this, "Google Pay App is not installed in Your Phone, First install it", Toast.LENGTH_LONG).show();
                        }
                    }
//                    else if (paykun.isChecked()) {
//                        PaykunAddMoney(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
//                    } if (traknpay.isChecked()) {
//                        TraknpayAddMoney(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
//                    }
                    else if (flutterwave.isChecked()) {
                        FlutterWaveAddMoney(prf.getString(Constant.USER_EMAIL), phn, planprice, "Add Money to Wallet", prf.getString(Constant.USER_NAME));
                    }
                    else {
                        ((TextView) findViewById(R.id.errorMessage)).setText("Select Payment Mode");
                        ((TextView) findViewById(R.id.errorMessage)).setVisibility(View.VISIBLE);
//                        Toast.makeText(MyWalletActivity.this, "Select Payment Mode", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    phoneed.setError("Please enter valid mobile number");
                }
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(true);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(true);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        instamojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(true);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(true);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(true);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        paykun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(true);
                traknpay.setChecked(false);
                flutterwave.setChecked(false);
            }
        });

        traknpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(true);
                flutterwave.setChecked(false);
            }
        });

        flutterwave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paytm.setChecked(false);
                paypal.setChecked(false);
                instamojo.setChecked(false);
                razorpay.setChecked(false);
                google.setChecked(false);
                paykun.setChecked(false);
                traknpay.setChecked(false);
                flutterwave.setChecked(true);
            }
        });

        //Paypal
        if(config.paypal) {
            new HttpRequest().execute();
        }
        //Razorpay
        if(config.razorpay) {
            /*
             To ensure faster loading of the Checkout form,
              call this method as early as possible in your checkout flow.
            */
            Checkout.preload(getApplicationContext());
        }

    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
//        final Activity activity = MyWalletActivity.this;
//        instamojoPay = new InstamojoPay();
//        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
//        registerReceiver(instamojoPay, filter);
//        JSONObject pay = new JSONObject();
//        try {
//            pay.put("email", email);
//            pay.put("phone", phone);
//            pay.put("purpose", purpose);
//            addamount=amount;
//            pay.put("amount", amount);
//            pay.put("name", buyername);
//            pay.put("send_sms", true);
//            pay.put("send_email", true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            System.out.println("Rjn_instamojo_error"+e.getMessage());
//        }
//        initListener();
//        instamojoPay.start(activity, pay, listener);
    }

    private void initListener() {
//        listener = new InstapayListener() {
//            @Override
//            public void onSuccess(String response) {
//                System.out.println("Rjn_payment"+response);
//
//                String[] str = response.split(":");
//                String[] split = str[1].split("=");
//                instaorderid = split[1];
//                split = str[2].split("=");
//                instatxnid = split[1];
//                split = str[3].split("=");
//                instapaymentid = split[1];
//                str = str[4].split("=");
//                instatoken = str[1];
//
//                // Loading jsonarray in Background Thread
//                new OneLoadAllProducts().execute();
//            }
//
//            @Override
//            public void onFailure(int code, String reason) {
//                System.out.println("Rjn_payment_error"+"code:"+code+"reason:"+reason);
//                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
//                        .show();
//            }
//        };
    }

    class OneLoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
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
            params.put(Constant.USER_NAME, prf.getString(Constant.USER_NAME));
            params.put(PLANID, planid);
            params.put("addamount", planprice);
            params.put(PLANDAYS, plandays);

            params.put(TAG_INSTA_ORDERID, instaorderid);
            params.put(TAG_INSTA_TXNID, instatxnid);
            params.put(TAG_INSTA_PAYMENTID, instapaymentid);
            params.put(TAG_INSTA_TOKEN, instatoken);
            params.put("status", "Add Money Success");

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

                try {
                    prf.setString(Constant.TAG_PLANID, json.getString(Constant.TAG_PLANID));
                    prf.setString(Constant.TAG_PLANACTIVE, json.getString(Constant.TAG_PLANACTIVE));
                    prf.setString(Constant.TAG_PLANDAYS, json.getString(Constant.TAG_PLANDAYS));
                    prf.setString(Constant.TAG_PLANSTART, json.getString(Constant.TAG_PLANSTART));
                    prf.setString(Constant.TAG_PLANEND, json.getString(Constant.TAG_PLANEND));
                } catch (Exception e) {
                    e.printStackTrace();
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
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {
//                        // jsonarray found
//                        // Getting Array of jsonarray
//
//                        prf.setString(Constant.TAG_PLANID, planid);
//                        prf.setString(Constant.TAG_PLANACTIVE, "Y");
//                        prf.setString(Constant.TAG_PLANDAYS, plandays);
//
//                        Date cur = Calendar.getInstance().getTime();
//
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        String formattedDate = df.format(cur);
//                        String output = null;
//
//                        if (prf.getString(Constant.TAG_PLANEND).compareTo(formattedDate) > 0) {
//                            System.out.println("Rajan_app_Date is after Date1_Plan is Active");
//
//                            //add days
//
//                            plandays = String.valueOf(Integer.parseInt(prf.getString(Constant.TAG_PLANDAYS)) + Integer.parseInt(plandays));
//                            String dt = prf.getString(Constant.TAG_PLANEND);  // Start date current date
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Calendar c = Calendar.getInstance();
//                            try {
//                                c.setTime(sdf.parse(dt));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            c.add(Calendar.DATE, Integer.parseInt(plandays));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            output = sdf1.format(c.getTime());
//
//                        } else {
//                            System.out.println("app_Date is equal to Date1_No Premium Plan is Active");
//
//                            //add days
//
//                            String dt = formattedDate;  // Start date current date
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Calendar c = Calendar.getInstance();
//                            try {
//                                c.setTime(sdf.parse(dt));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            c.add(Calendar.DATE, Integer.parseInt(plandays));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
//                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            output = sdf1.format(c.getTime());
//                        }
//
//                        System.out.println("Rajan_Current time => " + formattedDate);
//                        System.out.println("Rajan_Current time_planend => " + output);
//
//                        prf.setString(Constant.TAG_PLANSTART, formattedDate);
//                        prf.setString(Constant.TAG_PLANEND, output);

                        Intent intent = new Intent(MyWalletActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                        Toast.makeText(MyWalletActivity.this,"Payment done. Enjoy !",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MyWalletActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

    //PayPal

    class OneLoadAllProductsPayPal extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null && pDialog.isShowing()) {
                pDialog = new ProgressDialog(MyWalletActivity.this);
                pDialog.setMessage("Loading Please wait...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            Map<String, String> params = new HashMap<>();
            params.put(Constant.USER_ID, prf.getString(Constant.USER_ID));
            params.put(Constant.USER_NAME, prf.getString(Constant.USER_NAME));
            params.put(PLANID, planid);
            params.put("addamount", planprice);
            params.put(PLANDAYS, plandays);

            params.put("addamount", addamount);
            params.put(TAG_INSTA_ORDERID, stringNonce);
            params.put(TAG_INSTA_TXNID, "111");
            params.put(TAG_INSTA_PAYMENTID, stringNonce);
            params.put(TAG_INSTA_TOKEN, "PayPal");
            params.put("status", "Add Money Success");

            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(TAG_SUCCESS);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /*
                      Updating parsed JSON data into ListView
                     */
                    if (success == 1) {
                        // jsonarray found
                        // Getting Array of jsonarray

                        prf.setString(Constant.TAG_PLANID, planid);
                        prf.setString(Constant.TAG_PLANACTIVE, "Y");
                        prf.setString(Constant.TAG_PLANDAYS, plandays);

                        Date cur = Calendar.getInstance().getTime();

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(cur);

                        //add days

                        String dt = formattedDate;  // Start date current date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, Integer.parseInt(plandays));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String output = sdf1.format(c.getTime());

                        System.out.println("Rajan_Current time => " + formattedDate);
                        System.out.println("Rajan_Current time_planend => " + output);

                        prf.setString(Constant.TAG_PLANSTART, formattedDate);
                        prf.setString(Constant.TAG_PLANEND, output);

                        Intent intent = new Intent(MyWalletActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                        Toast.makeText(MyWalletActivity.this,"Payment done. Enjoy !",Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(MyWalletActivity.this,"Something went wrong. Try again!",Toast.LENGTH_LONG).show();

                    }

                }
            });

        }

    }

    //Paypal

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                stringNonce = nonce.getNonce();
                System.out.println("Rajan_mylog_Result: " + stringNonce);
                // Send payment price with the nonce
                // use the result to update your UI and send the payment method nonce to your server
                if (!paypalamount.toString().isEmpty()) {
//                    amount = paypalamount.toString();
                    paramHash = new HashMap<>();
                    paramHash.put("amount", paypalamount);
                    paramHash.put("nonce", stringNonce);
//                    sendPaymentDetails();
                    // Loading jsonarray in Background Thread
                    new OneLoadAllProductsPayPalSendNonceDetails().execute();
                } else
                    Toast.makeText(MyWalletActivity.this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user canceled");
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                System.out.println("Rajan_mylog_Error : " + error.toString());
            }
        }

        //GooglePay
        if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            // Process based on the data in response.
//            System.out.println("Rajan_googlepay_result"+ requestCode + "resultCode" + resultCode);
//            System.out.println("Rajan_googlepay_result"+ data.toString() + data.getStringExtra("Status"));
//            System.out.println("Rajan_googlepay_result"+ data.toString() + data.getStringExtra("response"));

            String status = data.getStringExtra("Status");
            if (status.equalsIgnoreCase("SUCCESS")) {

                try {

                    instaorderid = data.getStringExtra("txnRef");
                    instatxnid = data.getStringExtra("txnId");
                    addamount = googleamount;
                    instapaymentid = data.getStringExtra("txnRef");
                    instatoken = "12345";

                    // Loading jsonarray in Background Thread
                    new OneLoadAllProducts().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }

//        //Traknpay
//        if (requestCode == PGConstants.REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    String paymentResponse = data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
//                    System.out.println("paymentResponse: " + paymentResponse);
//                    if (paymentResponse.equals("null")) {
//                        System.out.println("Transaction Error!");
//                        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
//                    } else {
//                        JSONObject response = new JSONObject(paymentResponse);
//                        String status = response.getString("response_message");
//                        if (status.contains("successful")) {
//
//                            try {
//
//                                instaorderid = response.getString("transaction_id");
//                                instatxnid = response.getString("transaction_id");
//                                instapaymentid = response.getString("transaction_id");
//                                instatoken = "12345";
//
//                                // Loading jsonarray in Background Thread
//                                new OneLoadAllProducts().execute();
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
//            }
//
//        }

        //Flutterwave
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
//                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                try {

                    instaorderid = paytmorder_id;
                    instatxnid = paytmorder_id;
                    instapaymentid = paytmorder_id;
                    instatoken = "12345";

                    // Loading jsonarray in Background Thread
                    new OneLoadAllProducts().execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onBraintreeSubmit(String email, String phone, String amount, String purpose, String buyername) {

        addamount = amount;
        paypalamount = amount;

        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }

    class OneLoadAllProductsPayPalSendNonceDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyWalletActivity.this);
            pDialog.setMessage("Loading Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters

            // getting JSON string from URL
            String json = jsonParserString.makeHttpRequest(send_payment_details, "POST", paramHash);

            // Check your log cat for JSON reponse
//            Log.d("All jsonarray: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                if (json.contains("Successful")) {
                    Toast.makeText(MyWalletActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MyWalletActivity.this, "Transaction failed", Toast.LENGTH_LONG).show();
                Log.d("mylog", "Final Response: " + json.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                /*
                  Updating parsed JSON data into ListView
                 */
                    // Loading jsonarray in Background Thread
                    new OneLoadAllProductsPayPal().execute();


                }
            });

        }

    }

    private class HttpRequest extends AsyncTask {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(MyWalletActivity.this, android.R.style.Theme_DeviceDefault_Dialog);
            progress.setCancelable(false);
            progress.setMessage("We are contacting our servers for token, Please wait");
            progress.setTitle("Getting token");
            progress.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(MyWalletActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                        }
                    });
                    token = responseBody;
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Rajan_paypal_gettoken_failed" + ex.toString());
//                            Toast.makeText(MyWalletActivity.this, "Failed to get token: ", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progress.dismiss();
        }
    }

    //Razorpay
    public void startPayment(String email, String phone, String amount, String purpose, String buyername) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        co.setKeyID(config.ApiKey);
        try {
            JSONObject options = new JSONObject();
            options.put("name", buyername);
            options.put("description", purpose);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", config.Razorpay_currency);
            options.put("amount", Integer.parseInt(amount)*100);

            JSONObject preFill = new JSONObject();
            preFill.put("email", email);
            preFill.put("contact", phone);

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // getting JSON string from URL
        JSONObject json = null;

        try {
            System.out.println("Rajan_Payment Successful: " + razorpayPaymentID);

            instaorderid = razorpayPaymentID;
            instatxnid = razorpayPaymentID;
            addamount = planprice;
            instapaymentid = "CHECKSUMHASH";
            instatoken = "MID";

            // Loading jsonarray in Background Thread
            new OneLoadAllProducts().execute();

        } catch (Exception e) {
            System.out.println("Rajan_Exception in onPaymentSuccess"+ e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println("Rajan_Exception in onPaymentError"+ e.getMessage());
            e.printStackTrace();
        }
    }

//    //Paykun
//    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
//    public void getResults(Events.PaymentMessage message) {
//        if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SUCCESS)){
//            // do your stuff here
//            // message.getTransactionId() will return your failed or succeed transaction id
//            /* if you want to get your transaction detail call message.getTransactionDetail()
//             *  getTransactionDetail return all the field from server and you can use it here as per your need
//             *  For Example you want to get Order id from detail use message.getTransactionDetail().order.orderId */
//            if(!TextUtils.isEmpty(message.getTransactionId())) {
////                Toast.makeText(MyWalletActivity.this, "Your Transaction is succeed with transaction id : "+message.getTransactionId() , Toast.LENGTH_SHORT).show();
//                Log.v(" order id "," getting order id value : "+message.getTransactionDetail().order.orderId);
//
//                try {
//
//                    instaorderid = message.getTransactionDetail().order.orderId;
//                    instatxnid = message.getTransactionId();
//                    addamount = planprice;
//                    instapaymentid = message.getTransactionId();
//                    instatoken = "12345";
//
//                    // Loading jsonarray in Background Thread
//                    new OneLoadAllProducts().execute();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_FAILED)){
//            // do your stuff here
//            Toast.makeText(MyWalletActivity.this,"Your Transaction is failed",Toast.LENGTH_SHORT).show();
//        }
//        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SERVER_ISSUE)){
//            // do your stuff here
//            Toast.makeText(MyWalletActivity.this,PaykunHelper.MESSAGE_SERVER_ISSUE,Toast.LENGTH_SHORT).show();
//        }else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING)){
//            // do your stuff here
//            Toast.makeText(MyWalletActivity.this,"Access Token missing",Toast.LENGTH_SHORT).show();
//        }
//        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING)){
//            // do your stuff here
//            Toast.makeText(MyWalletActivity.this,"Merchant Id is missing",Toast.LENGTH_SHORT).show();
//        }
//        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_INVALID_REQUEST)){
//            Toast.makeText(MyWalletActivity.this,"Invalid Request",Toast.LENGTH_SHORT).show();
//        }
//        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE)){
//            Toast.makeText(MyWalletActivity.this,"Network is not available",Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        //Paykun
//        // Register this activity to listen to event.
//        GlobalBus.getBus().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        //Paykun
//        // Unregister from activity
//        GlobalBus.getBus().unregister(this);
    }

}
