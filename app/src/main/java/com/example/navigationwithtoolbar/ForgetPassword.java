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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {

    Toolbar toolbar;
    View logo_back;
    Button sendEmail;
    EditText etEmail;
    TextView tvAfterSent;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        toolbar = findViewById(R.id.forget_password_toolbar);
        logo_back = toolbar.getChildAt(1);
        etEmail = findViewById(R.id.forget_password_editText);
        tvAfterSent = findViewById(R.id.forget_password_afterSendText);
        progressBar = findViewById(R.id.forget_password_progressbar);
        sendEmail =findViewById(R.id.send_email);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:apply forget password settings.
                if (etEmail.getText().toString().trim().equalsIgnoreCase("")) {
                    etEmail.setError("Email Required");
                    etEmail.requestFocus();
                    return;
                }else if (!isEmailValid(etEmail.getText().toString())) {
                    etEmail.setError("Valid email is required");
                    etEmail.requestFocus();
                    return;
                }
                EmailSender emailSender = new EmailSender(ForgetPassword.this);
                emailSender.execute("send",etEmail.getText().toString());
            }
        });
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public class EmailSender extends AsyncTask<String, String, String> {
        Context context;
        AlertDialog alertDialog;
        private Constants constants;
        private String ip;
        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String login_url = "http://"+ip+"/pgr/forgetpassword.php";

            if (type.equals("send")){
                try {
                    String email = strings[1];
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
            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Status");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setAlpha(1);
                }
            });

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setAlpha(0);
                }
            });
            if(s == null){
                Toast.makeText(context, "Network Error!\nPlease check your network connection.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                boolean userExist = s.contains("exist");
                boolean userDoNotExist = s.contains("notexist");
                if (userDoNotExist) {
                    alertDialog.setMessage("This email address is not registered with us.");
                    alertDialog.show();
                    return;
                } else if (userExist) {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          etEmail.setEnabled(false);
                          tvAfterSent.setAlpha(1);
                          sendEmail.setAlpha(0);
                          sendEmail.setEnabled(false);
                      }
                  });
                } else {
                    Toast.makeText(context, "Unknown Error", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.i("Exception","e");
                Toast.makeText(context, "Unknown Error!", Toast.LENGTH_SHORT).show();
            }
        }

        public EmailSender(Context ctx) {
            context = ctx;
        }
    }
}
