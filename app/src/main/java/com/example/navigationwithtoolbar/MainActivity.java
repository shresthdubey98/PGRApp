package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;




import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    TextView signupButton,warning;
    TextView btnForgotPassword;
    EditText etusername,etpassword;
    Boolean currentTimeCheck,currentDayCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants constants = new Constants(MainActivity.this);

        Date currentTimestrap = Calendar.getInstance().getTime();
        String nowtime = new SimpleDateFormat("H:mm:ss").format(currentTimestrap);
        String nowDay = new SimpleDateFormat("E").format(currentTimestrap);
//        nowDay = "mon";
//        nowtime ="9:15:01";

        String fromTime = "09:15:00";
        String toTime = "15:30:00";
        //timeCheck  
        currentTimeCheck = timeCheck(nowtime,fromTime,toTime);
        currentDayCheck = checkDay(nowDay);

        System.out.println("a:"+currentTimeCheck+"\n"+"b:"+currentDayCheck);
        if (true || (currentDayCheck && currentTimeCheck)){
            if (constants.getEmail()!=""){
                Intent in = new Intent(MainActivity.this,HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.overridePendingTransition(0,0);
                startActivity(in);
            }else {
                setContentView(R.layout.activity_main);
                signupButton = findViewById(R.id.btnsignup);
                etusername = findViewById(R.id.etemail);
                etpassword = findViewById(R.id.etpassword);
                loginButton = findViewById(R.id.btnlogin);
                btnForgotPassword = findViewById(R.id.btnforgetpassword);
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(in);
                    }
                });
                signupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(MainActivity.this, SignUp.class);
                        startActivity(in);
                    }
                });
                btnForgotPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO:apply forgot password properties here
                        Intent i = new Intent(MainActivity.this,ForgetPassword.class);
                        startActivity(i);
                    }
                });
                loginButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logIn();
                    }
                });
            }

        }else{
            setContentView(R.layout.activity_main);
            signupButton = findViewById(R.id.btnsignup);
            etusername = findViewById(R.id.etemail);
            etpassword = findViewById(R.id.etpassword);
            warning = findViewById(R.id.warning_text);
            loginButton = findViewById(R.id.btnlogin);
            btnForgotPassword = findViewById(R.id.btnforgetpassword);
            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MainActivity.this, SignUp.class);
                    startActivity(in);
                }
            });
            btnForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:apply forgot password properties here
                    Intent i = new Intent(MainActivity.this,ForgetPassword.class);
                    startActivity(i);
                }
            });
            loginButton.setVisibility(View.GONE);
            warning.setVisibility(View.VISIBLE);
            etusername.setEnabled(false);
            etpassword.setEnabled(false);
        }

    }
    public void logIn(){
        String us = etusername.getText().toString();
        String ps = etpassword.getText().toString();
        String type = "login";
        BackgroundWorker backgroundWorker =new BackgroundWorker(this);
        try {
            backgroundWorker.execute(type, us, ps);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public class BackgroundWorker extends AsyncTask<String, String, String> {
        Context context;
        AlertDialog alertDialog;
        String user = "nouser";
        private Constants constants;
        private String ip;



        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String login_url = "http://"+ip+"/pgr/login.php";

            if (type.equals("login")){
                try {
                    String email = strings[1];
                    String password = strings[2];
                    user = email;
                    Log.i("status","inside the login try catch");
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                    Log.i("status","string post_data concatenation successful");

                    bufferedWriter.write(post_data);
                    Log.i("status","bufferedWriter.write(post_data) executed successfully");

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    //reading response for feedback
                    Log.i("status","now reading feedback");


                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String result = "";
                    String line  = "";
                    while ((line = bufferedReader.readLine())!=null){
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("LoginStatus");
        }

        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("ssss",s);
            try {
                boolean loginSuccessful = s.contains("loginsuccess");
                boolean incorrectPassword = s.contains("incorrectpassword");
                boolean userNotFound = s.contains("userdoesnotexist");
                boolean adminverificationreq = s.contains("needadminverification");
                boolean vneed = s.contains("vneed");
                Log.i("SessionId",s);

                if (loginSuccessful) {
                    //alertDialog.setMessage("Login Successful!");
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        constants.setEmail(jsonObj.getString("email"));
                        constants.setPhone(jsonObj.getString("phone"));
                        constants.setName(jsonObj.getString("name"));
                        constants.setGender(jsonObj.getString("gender"));
                        constants.setDob(jsonObj.getString("dob"));
                        String p = "";
                        for(int k=0;k<Integer.parseInt(jsonObj.getString("password_len"));k++){
                            p+="•";
                        }
                        constants.setPassword_coded(p);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Intent i = new Intent(context,HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Generating SESSION_ID
                    //s = s.replace("loginsuccess","");

                    context.startActivity(i);
                    return;
                } else if (incorrectPassword) {
                    alertDialog.setMessage("Incorrect Password!");
                    Log.i("info",s);
                } else if (userNotFound) {
                    alertDialog.setMessage("User does not exist!");
                } else if(vneed){
                    Intent i = new Intent(getApplication(),VerifyEmail.class);
                    i.putExtra("email",user);
                    startActivity(i);
                }else if(adminverificationreq){
                    alertDialog.setMessage("Administration verification is required!");
                } else {
                    alertDialog.setMessage("Unknown error!");
                    Log.i("error", s);
                }
                alertDialog.show();
            }catch (Exception e){
                e.printStackTrace();
                Log.i("Exception","e");
            }
        }

        public BackgroundWorker(Context ctx) {
            context = ctx;
        }
    }
    public boolean timeCheck(String now,String from,String to){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date time_from = formatter.parse(from);
            Date time_to = formatter.parse(to);
            Date timeNow = formatter.parse(now);
            if (time_from.before(timeNow) && time_to.after(timeNow)){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDay(String day){
        if (day.toLowerCase().equals("sat") || day.toLowerCase().equals("sun")){
            return false;
        }else {
            return true;
        }
    }
}
