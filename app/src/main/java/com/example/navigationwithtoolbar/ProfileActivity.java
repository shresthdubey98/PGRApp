package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    EditText etEmail,etDob,etGender,etPhone;
    TextView txName,txPassword;
    Toolbar toolbar;
    ImageButton editButtonPhone,editButtonDob,editButtonPassword,editButtonGender;
    ImageButton confirmEditButtonPhone;
    Constants constants;
    RequestQueue queue;
    View changePasswordView;
    View logo_back;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    String date;
    int d,m,y;

    //change password dialogue declaration
    EditText etOldPwd,etNewPwd,etNewPwdConfirm;
    Button btnCancle,btnConfirm;
    AlertDialog changepasswordAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        constants =  new Constants(this);
        txName =findViewById(R.id.profile_name);
        etEmail = findViewById(R.id.profile_email);
        etDob = findViewById(R.id.profile_dob);
        txPassword = findViewById(R.id.profile_password);
        etGender = findViewById(R.id.profile_gender);
        etPhone = findViewById(R.id.profile_phone);
        changePasswordView = getLayoutInflater().inflate(R.layout.change_password,null);
        //volley
        queue = Volley.newRequestQueue(this);

        //change password dialogue
        etOldPwd = changePasswordView.findViewById(R.id.et_old_password);
        etNewPwd = changePasswordView.findViewById(R.id.et_new_password);
        etNewPwdConfirm = changePasswordView.findViewById(R.id.et_reenter_new_password);
        btnCancle = changePasswordView.findViewById(R.id.cp_cancele);
        btnConfirm = changePasswordView.findViewById(R.id.cp_confirm);
        changepasswordAlert = new AlertDialog.Builder(ProfileActivity.this).create();
        //edit buttons
        editButtonPhone = findViewById(R.id.edit_phone);
        editButtonDob = findViewById(R.id.edit_dob);
        editButtonPassword = findViewById(R.id.edit_password);
        editButtonGender = findViewById(R.id.edit_gender);

        //confirm buttons
        confirmEditButtonPhone = findViewById(R.id.confirm_edit_phone);
      //  phone_confirm = findViewById(R.id.profile_phone_edit_check);

        txName.setText(constants.getName());
        etEmail.setText(constants.getEmail());
        etDob.setText(constants.getDob());
        if(constants.getGender().equals("M")){
            etGender.setText("Male");
        }else{
            etGender.setText("Female");
        }
        txPassword.setText(constants.getPassword_coded());
        etPhone.setText(constants.getPhone());

        toolbar = findViewById(R.id.profile_toolbar);
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //edit phone process
        editButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonPhone.setVisibility(View.GONE);
                confirmEditButtonPhone.setVisibility(View.VISIBLE);
                etPhone.setEnabled(true);
                etPhone.requestFocus();
                etPhone.selectAll();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etPhone, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        confirmEditButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(etPhone.getText().length()<10){
                   etPhone.setError("Invalid Number");
               }else{
                   confirmEditButtonPhone.setVisibility(View.GONE);
                   etPhone.setEnabled(false);
                   editButtonPhone.setVisibility(View.VISIBLE);
                   //sending edit request
                   String url = "http://"+Constants.ip+"/pgr/editphone.php?" +
                           "email="+constants.getEmail()+
                           "&"+
                           "newPhone="+etPhone.getText().toString();
                   StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                           new Response.Listener<String>() {
                               @Override
                               public void onResponse(String response) {
                                   // Display the first 500 characters of the response string.
                                   if (response.equals("updateSuccess")){
                                       constants.setPhone(etPhone.getText().toString());
                                       Toast.makeText(ProfileActivity.this, "Phone number updated", Toast.LENGTH_SHORT).show();
                                   }else{
                                       etPhone.setText(constants.getPhone());
                                       Toast.makeText(ProfileActivity.this, "server error", Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }, new Response.ErrorListener() {
                       @Override
                       public void onErrorResponse(VolleyError error) {
                           Toast.makeText(ProfileActivity.this, "server timed out!", Toast.LENGTH_SHORT).show();
                           etPhone.setText(constants.getPhone());
                       }
                   });
                   queue.add(stringRequest);
               }
            }
        });
        //edit date of birth process
        editButtonDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        ProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_background);
                dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.button_background);
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setText("Update");
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.i("Date", year + "/" + month + "/" + dayOfMonth);
                month = month + 1;
                d = dayOfMonth;
                m = month;
                y = year;
                date = dayOfMonth + "/" + month + "/" + year;
                //do the changing here
                etDob.setText(date);

                date = year + "-" + month + "-" + dayOfMonth;
//                dateTextView.setTextSize(22);
//                stringDob = date;
                String url = "http://"+Constants.ip+"/pgr/editDob.php?" +
                        "email="+constants.getEmail()+
                        "&"+
                        "newDob="+date;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                if (response.equals("updateSuccess")){
                                    constants.setDob(date);
                                    Toast.makeText(ProfileActivity.this, "DOB updated", Toast.LENGTH_SHORT).show();
                                }else{
                                    etDob.setText(constants.getDob());
                                    Toast.makeText(ProfileActivity.this, "server error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, "server timed out!", Toast.LENGTH_SHORT).show();
                        etDob.setText(constants.getDob());
                    }
                });
                queue.add(stringRequest);
            }
        };
        editButtonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                builder.setTitle("Change Password");
                changepasswordAlert.setView(changePasswordView);
//                builder.setCancelable(false);
                changepasswordAlert.show();
            }
        });


        etNewPwdConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etNewPwd.getText().toString().equals(s.toString())){
                    btnConfirm.setEnabled(false);
                    etNewPwdConfirm.setTextColor(Color.BLACK);
                }else{
                    btnConfirm.setEnabled(true);
                    etNewPwdConfirm.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!etNewPwd.getText().toString().equals(s.toString())){
                    etNewPwdConfirm.setError("Password Mismatch");
                    btnConfirm.setEnabled(false);
                }else{
                    btnConfirm.setEnabled(true);
                }
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepasswordAlert.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etOldPwd.getText().toString().equals("")||
                        etNewPwd.getText().toString().equals("")||
                        etNewPwdConfirm.getText().toString().equals("")
                ){
                    Toast.makeText(ProfileActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }else if(etNewPwd.getText().length()<8){
                    etNewPwd.setError("length should be at least 8 characters");
                    etNewPwd.requestFocus();
                }
                else{
                    Toast.makeText(ProfileActivity.this, "processing...", Toast.LENGTH_SHORT).show();
                    String url = "http://"+Constants.ip+"/pgr/changepassword.php?" +
                            "email="+constants.getEmail()+
                            "&"+
                            "oldPassword="+etOldPwd.getText().toString()+
                            "&"+
                            "newPassword="+etNewPwd.getText().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    if (response.equals("updateSuccess")){
                                        Toast.makeText(ProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                        changepasswordAlert.dismiss();
                                    }else if(response.equals("wrongOldPassword")){
                                        etOldPwd.setError("Wrong Password");
                                        etOldPwd.requestFocus();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ProfileActivity.this, "server timed out!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });
    }

}
