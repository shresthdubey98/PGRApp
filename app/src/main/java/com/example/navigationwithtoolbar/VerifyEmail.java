package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class VerifyEmail extends AppCompatActivity {
    Button btn;
    Toolbar toolbar;
    View logo_back;
    TextView sendAgain;
    ProgressBar sendAgainProgressBar,verifyProgressBar;
    EditText otpEdittext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        final String email = i.getStringExtra("email");
        setContentView(R.layout.activity_verify_email);
        otpEdittext = findViewById(R.id.otpEdittext);
        verifyProgressBar = findViewById(R.id.verify_email_progressbar);
        sendAgainProgressBar = findViewById(R.id.verify_email_sendagain_progressbar);
        btn = findViewById(R.id.btnVGoBackToLogin);
        sendAgain = findViewById(R.id.send_code_again);
        //------------------------toolbar stuff--------------------
        toolbar = findViewById(R.id.verify_email_toolbar);
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //---------------------------------------------------------
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpEdittext.getText().toString().equals("")){
                    otpEdittext.setError("Enter Code");
                    otpEdittext.requestFocus();
                }else{
                    Verify verify = new Verify(getApplication());
                    verify.execute("verify",email,otpEdittext.getText().toString());
                }
            }
        });
        sendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SendCodeAgain sendCodeAgain = new SendCodeAgain(getApplication());
                    sendCodeAgain.execute("send",email);
            }
        });
    }
    public class SendCodeAgain extends AsyncTask<String, String, String> {
        Context context;
        private Constants constants;
        private String ip;
        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String sendCodeAgain_url = "http://"+ip+"/pgr/sendcodeagain.php";
            if (type.equals("send")){
                try {
                    String email = strings[1];
                    Log.i("status","inside the login try catch");
                    URL url = new URL(sendCodeAgain_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
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
            return "error";
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendAgainProgressBar.setAlpha(1);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendAgainProgressBar.setAlpha(1);
                }
            });
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("ssss",s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sendAgainProgressBar.setAlpha(0);
                }
            });
            try {
                final boolean sendsuccess = s.contains("sendsuccess");
                boolean sendunsuccess = s.contains("sendunsuccess");
                boolean neterror = s.contains("error");

                Log.i("SessionId",s);

                if (sendsuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendAgain.setEnabled(false);
                            sendAgain.setText("Code Sent Successfully!");
                            sendAgain.setAlpha(Float.parseFloat("0.7"));
                        }
                    });
                } else if (sendunsuccess) {
                    Toast.makeText(context, "There is something wrong with your email! Please try again.", Toast.LENGTH_SHORT).show();
                } else if(neterror) {
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
                Log.i("Exception","e");
            }
        }

        public SendCodeAgain (Context ctx) {
            context = ctx;
        }
    }

    public class Verify extends AsyncTask<String, String, String> {
        Context context;
        private Constants constants;
        private String ip;
        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String sendCodeAgain_url = "http://"+ip+"/pgr/verifyemail.php";
            if (type.equals("verify")){
                try {
                    String email = strings[1];
                    String code = strings[2];
                    Log.i("status","inside the login try catch");
                    URL url = new URL(sendCodeAgain_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+URLEncoder.encode("code","UTF-8")+"="+URLEncoder.encode(code,"UTF-8");
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
            return "error";
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    verifyProgressBar.setAlpha(1);
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... values) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    verifyProgressBar.setAlpha(1);
                }
            });
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("ssss",s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    verifyProgressBar.setAlpha(0);
                }
            });
            try {
                final boolean vsuccess = s.contains("vsuccess");
                boolean vunsuccess = s.contains("wrongcode");
                boolean neterror = s.contains("error");

                Log.i("SessionId",s);

                if (vsuccess) {
                    Intent i = new Intent(getApplication(),MainActivity.class);
                    Toast.makeText(context, "Verification Successful! Please Login.", Toast.LENGTH_SHORT).show();
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else if (vunsuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otpEdittext.setError("Wrong OTP");
                            otpEdittext.requestFocus();
                        }
                    });
                } else if(neterror) {
                    Toast.makeText(context, "Network Error!", Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e){
                e.printStackTrace();
                Log.i("Exception","e");
            }
        }

        public Verify (Context ctx) {
            context = ctx;
        }
    }
}
