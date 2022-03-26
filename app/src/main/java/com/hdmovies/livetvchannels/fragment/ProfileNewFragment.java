package com.hdmovies.livetvchannels.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hdmovies.livetvchannels.util.Constant;
import com.hdmovies.livetvchannels.util.JsonUtils;
import com.hdmovies.livetvchannels.util.PrefManager;
import com.google.android.material.textfield.TextInputEditText;
import com.hdmovies.livetvchannels.MainActivity;
import com.hdmovies.livetvchannels.MyApplication;
import com.hdmovies.livetvchannels.R;
import com.rilixtech.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;


public class ProfileNewFragment extends Fragment {

    private PrefManager prf;

    ProgressDialog pDialog;
    MyApplication myApp;
    ProgressBar progressBar;
    ScrollView scrollView;
    private String strMessage;
    private RelativeLayout profilemain;

    private TextInputEditText name_text_userprofile;
    private TextInputEditText input_email_text;
    private TextInputEditText input_phone_text;
    private TextInputEditText password_text_userprofile;
    private TextInputEditText dob_userprofile;
    private RadioButton editprofile_radio_male;
    private RadioButton editprofile_radio_female;
    private CountryCodePicker ccp;
    private TextInputEditText edt_premium;
    private Button save_changes_userprofile;

    private Calendar myCalendar;
    private Boolean dobchange = false;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_profile_new, container, false);

        prf= new PrefManager(requireActivity());

        pDialog = new ProgressDialog(requireActivity());
        myApp = MyApplication.getInstance();

        profilemain = (RelativeLayout) rootView.findViewById(R.id.profilemain);
        name_text_userprofile = (TextInputEditText) rootView.findViewById(R.id.name_text_userprofile);
        input_email_text = (TextInputEditText) rootView.findViewById(R.id.input_email_text);
        input_phone_text = (TextInputEditText) rootView.findViewById(R.id.input_phone_text);
        password_text_userprofile = (TextInputEditText) rootView.findViewById(R.id.password_text_userprofile);
        dob_userprofile = (TextInputEditText) rootView.findViewById(R.id.dob_userprofile);
        editprofile_radio_male = (RadioButton) rootView.findViewById(R.id.editprofile_radio_male);
        editprofile_radio_female = (RadioButton) rootView.findViewById(R.id.editprofile_radio_female);
        ccp = (CountryCodePicker) rootView.findViewById(R.id.ccp);
        edt_premium = (TextInputEditText) rootView.findViewById(R.id.edt_premium);
        save_changes_userprofile = (Button) rootView.findViewById(R.id.save_changes_userprofile);

        progressBar = rootView.findViewById(R.id.progressBar);
        scrollView = rootView.findViewById(R.id.lay_scroll);

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob_userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(requireActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editprofile_radio_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editprofile_radio_male.setChecked(true);
                editprofile_radio_female.setChecked(false);
            }
        });

        editprofile_radio_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editprofile_radio_male.setChecked(false);
                editprofile_radio_female.setChecked(true);
            }
        });
//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validator.validate();
//            }
//        });

        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getProfile().execute(Constant.PROFILE_URL + myApp.getUserId());
        }

        save_changes_userprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkvalues()) {
                    return;
                }
                String strName = name_text_userprofile.getText().toString().trim();
                String strEmail = input_email_text.getText().toString().trim();
                String strPassword = password_text_userprofile.getText().toString();
                String strMobile = ccp.getSelectedCountryCodeWithPlus().replaceAll(Pattern.quote("+"),"") + input_phone_text.getText().toString().trim();

                String strDob;
                if (dobchange) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    strDob = sdf.format(myCalendar.getTime());
                } else {
                    strDob = prf.getString(Constant.USER_DOB);
                }

                String strGender;
                if (editprofile_radio_male.isChecked()) {
                    strGender = "male";
                } else {
                    strGender = "female";
                }

                new MyTaskUpdate().execute(Constant.UPDATE_PROFILE_URL + myApp.getUserId()
                        + "&name=" + strName
                        + "&email=" + strEmail
                        + "&password=" + strPassword
                        + "&phone=" + strMobile
                        + "&dob=" + strDob
                        + "&gender=" + strGender
                );
            }
        });

//        validator = new Validator(this);
//        validator.setValidationListener(this);

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

    private void updateLabel() {
        dobchange = true;

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob_userprofile.setText(sdf.format(myCalendar.getTime()));
    }

    @SuppressLint("StaticFieldLeak")
    private class getProfile extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            profilemain.setVisibility(View.GONE);
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
            profilemain.setVisibility(View.VISIBLE);

            if (null == result) {
                showToast(getString(R.string.no_data));
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        try {
                            prf.setString(Constant.USER_ID, objJson.getString(Constant.USER_ID));
                            prf.setString(Constant.USER_NAME, objJson.getString(Constant.USER_NAME));
                            prf.setString(Constant.USER_EMAIL, objJson.getString(Constant.USER_EMAIL));
                            prf.setString(Constant.USER_PHONE, objJson.getString(Constant.USER_PHONE));
                            prf.setString(Constant.USER_DOB, objJson.getString(Constant.USER_DOB));
                            prf.setString(Constant.USER_GENDER, objJson.getString(Constant.USER_GENDER));
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
        if (prf.getString(Constant.USER_NAME).length() > 3 && prf.getString(Constant.USER_NAME)!= "null") {
            name_text_userprofile.setText(prf.getString(Constant.USER_NAME));
        }

        if (prf.getString(Constant.USER_EMAIL).length() > 3 && prf.getString(Constant.USER_EMAIL)!= "null") {
            input_email_text.setText(prf.getString(Constant.USER_EMAIL));
            input_email_text.setEnabled(false);
            input_email_text.setActivated(false);
        }

        if (prf.getString(Constant.USER_PHONE).length() > 5 && prf.getString(Constant.USER_PHONE)!= "null") {
            ccp.setCountryForPhoneCode(Integer.parseInt(getContryCode("+"+prf.getString(Constant.USER_PHONE))));
            input_phone_text.setText(getPhoneNumber("+"+prf.getString(Constant.USER_PHONE)));
//            ccp.setEnabled(false);
//            input_phone_text.setEnabled(false);
        }

        //Set User Date Of Birth
        setUserDob();

        //Set User Gender
        if (prf.getString(Constant.USER_GENDER).length() >= 3 && prf.getString(Constant.USER_NAME)!= "null") {
            if (prf.getString(Constant.USER_GENDER).equalsIgnoreCase("male")) {
                editprofile_radio_male.setChecked(true);
            } else {
                editprofile_radio_female.setChecked(true);
            }
        }

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

    }

    private void setUserDob() {
        //User DOB
        //Input date in String format
        System.out.println("Rajan_planend"+prf.getString(Constant.USER_DOB));
        String input = prf.getString(Constant.USER_DOB);
        System.out.println("Rajan_detailvideo_enddate"+input);
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(input);

            //old date format to new date format
            output = outputformat.format(date);
            dob_userprofile.setText(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    private String getContryCode(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(requireActivity());
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, "");

//            System.out.println("Rajan_Country code: " + numberProto.getCountryCode());
//            System.out.println("Rajan_Country code: " + numberProto.getNationalNumber());
            return String.valueOf(numberProto.getCountryCode());
            //This prints "Country code: 91"
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return String.valueOf("+91");
        }
    }

    private String getPhoneNumber(String number) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(requireActivity());
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, "");

//            System.out.println("Rajan_Country code: " + numberProto.getCountryCode());
//            System.out.println("Rajan_Country code: " + numberProto.getNationalNumber());
            return String.valueOf(numberProto.getNationalNumber());
            //This prints "Country code: 91"
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return String.valueOf("1234567890");
        }
    }

    private Boolean checkvalues() {
        if (name_text_userprofile.getText().toString().trim().isEmpty()) {
            Toast.makeText(myApp, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name_text_userprofile.getText().toString().trim().length()<3) {
            Toast.makeText(myApp, "Please Enter Valid Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (input_email_text.getText().toString().trim().isEmpty()) {
            Toast.makeText(myApp, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password_text_userprofile.getText().toString().isEmpty()) {
            Toast.makeText(myApp, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!(editprofile_radio_male.isChecked() || editprofile_radio_female.isChecked())) {
            Toast.makeText(myApp, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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

                        try {
                            prf.setString(Constant.USER_ID, objJson.getString(Constant.USER_ID));
                            prf.setString(Constant.USER_NAME, objJson.getString(Constant.USER_NAME));
                            prf.setString(Constant.USER_EMAIL, objJson.getString(Constant.USER_EMAIL));
                            prf.setString(Constant.USER_PHONE, objJson.getString(Constant.USER_PHONE));
                            prf.setString(Constant.USER_DOB, objJson.getString(Constant.USER_DOB));
                            prf.setString(Constant.USER_GENDER, objJson.getString(Constant.USER_GENDER));
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
                setResult();
            }
        }
    }

    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            showToast(getString(R.string.error_title) + "\n" + strMessage);
        } else {
//            myApp.saveLogin(myApp.getUserId(), strName, strEmail);
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
