package com.hdmovies.livetvchannels.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.MyApplication;
import com.hdmovies.livetvchannels.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment implements Validator.ValidationListener {

    private PrefManager prf;

    @NotEmpty
    EditText edtFullName;
    @Email
    EditText edtEmail;
    EditText edt_premium;
    @Password
    EditText edtPassword;
    @Length(max = 14, min = 6, message = "Enter valid Phone Number")
    EditText edtMobile;
    Button btnSignUp;
    String strName, strEmail, strPassword, strMobile, strMessage;
    private Validator validator;
    ProgressDialog pDialog;
    MyApplication myApp;
    ProgressBar progressBar;
    ScrollView scrollView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        prf= new PrefManager(requireActivity());

        pDialog = new ProgressDialog(requireActivity());
        myApp = MyApplication.getInstance();
        edtFullName = rootView.findViewById(R.id.edt_name);
        edtEmail = rootView.findViewById(R.id.edt_email);
        edt_premium = rootView.findViewById(R.id.edt_premium);
        edtPassword = rootView.findViewById(R.id.edt_password);
        edtMobile = rootView.findViewById(R.id.edt_phone);
        btnSignUp = rootView.findViewById(R.id.button);
        progressBar = rootView.findViewById(R.id.progressBar);
        scrollView = rootView.findViewById(R.id.lay_scroll);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getProfile().execute(Constant.PROFILE_URL + myApp.getUserId());
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        //Premium Plan
        //Input date in String format
        System.out.println("Rajan_planend"+prf.getString(Constant.TAG_PLANEND));
        String input = prf.getString(Constant.TAG_PLANEND);
        System.out.println("Rajan_detailvideo_enddate"+input);
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        Date date;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(input);
            Date date1 = Calendar.getInstance().getTime();
            System.out.println("Rajan_Current time => " + date1);

            if (date.compareTo(date1) > 0) {
                System.out.println("Rajan_app_Date is after Date1");
//                playvideo(view);
                edt_premium.setText("Premium Plan is valid for next: "+datedifferanceindays(date, date1) + " days");
            } else if (date.compareTo(date1) < 0) {
                System.out.println("Rajan_app_Date is before Date1");
                edt_premium.setText("No Premium Plan is Active");
            } else if (date.compareTo(date1) == 0) {
                System.out.println("app_Date is equal to Date1");
                edt_premium.setText("No Premium Plan is Active");
            }
            //old date format to new date format
//                output = outputformat.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return rootView;
    }

    public long datedifferanceindays(Date pd, Date cd) {
        long days = 0;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String pdformattedDate = df.format(pd);
        String cdformattedDate = df.format(cd);
        System.out.println("Rajan_pdate"+pdformattedDate);
        System.out.println("Rajan_cdate"+cdformattedDate);

        //add days
        Calendar pdc = Calendar.getInstance();
        Calendar cdc = Calendar.getInstance();

        try {
            pdc.setTime(df.parse(pdformattedDate));
            cdc.setTime(df.parse(cdformattedDate));

            // Get the represented date in milliseconds
            long millis1 = pdc.getTimeInMillis();
            long millis2 = cdc.getTimeInMillis();

            // Calculate difference in milliseconds
            long diff = millis1 - millis2;

            // Calculate difference in seconds
            long diffSeconds = diff / 1000;

            // Calculate difference in minutes
            long diffMinutes = diff / (60 * 1000);

            // Calculate difference in hours
            long diffHours = diff / (60 * 60 * 1000);

            // Calculate difference in days
            days = diff / (24 * 60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    @Override
    public void onValidationSucceeded() {
        strName = edtFullName.getText().toString();
        strEmail = edtEmail.getText().toString();
        strPassword = edtPassword.getText().toString();
        strMobile = edtMobile.getText().toString();

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new MyTaskUpdate().execute(Constant.UPDATE_PROFILE_URL + myApp.getUserId() + "&name=" + strName + "&email=" + strEmail + "&password=" + strPassword + "&phone=" + strMobile);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getProfile extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //error solved
            if(getActivity() == null)
                return;

            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        edtFullName.setText(objJson.getString(Constant.USER_NAME));
                        edtEmail.setText(objJson.getString(Constant.USER_EMAIL));
                        edtMobile.setText(objJson.getString(Constant.USER_PHONE));

                        try {
                            prf.setString(Constant.USER_ID, objJson.getString(Constant.USER_ID));
                            prf.setString(Constant.USER_NAME, objJson.getString(Constant.USER_NAME));
                            prf.setString(Constant.USER_EMAIL, objJson.getString(Constant.USER_EMAIL));
                            prf.setString(Constant.TAG_PLANID, objJson.getString(Constant.TAG_PLANID));
                            prf.setString(Constant.TAG_PLANACTIVE, objJson.getString(Constant.TAG_PLANACTIVE));
                            prf.setString(Constant.TAG_PLANDAYS, objJson.getString(Constant.TAG_PLANDAYS));
                            prf.setString(Constant.TAG_PLANSTART, objJson.getString(Constant.TAG_PLANSTART));
                            prf.setString(Constant.TAG_PLANEND, objJson.getString(Constant.TAG_PLANEND));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    @SuppressLint("StaticFieldLeak")
    private class MyTaskUpdate extends AsyncTask<String, Void, String> {

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
        } else {
            myApp.saveLogin(myApp.getUserId(), strName, strEmail);
            showToast(getString(R.string.your_profile_update));
            Intent i = new Intent(requireActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            requireActivity().finish();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
