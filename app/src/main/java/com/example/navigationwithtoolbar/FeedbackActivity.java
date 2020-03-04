package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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


public class FeedbackActivity extends AppCompatActivity {

    Toolbar toolbar;
    View logo_back;
    Button sbmt;
    EditText review;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbar = findViewById(R.id.feedback_toolbar);
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sbmt = findViewById(R.id.feedback_submit);
        review = findViewById(R.id.review_edittext);
        ratingBar = findViewById(R.id.ratingBar);
        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (ratingBar.getRating()!=0){
                     String rating = String.valueOf(ratingBar.getRating());
                     String r = review.getText().toString();
                     RatingSubmission ratingSubmission = new RatingSubmission(getApplication());
                     ratingSubmission.execute("submit",rating,r);
                 }else{
                     Toast.makeText(FeedbackActivity.this, "Please give a rating", Toast.LENGTH_SHORT).show();
                 }
            }

        });

    }
    private class RatingSubmission extends AsyncTask<String, String, String> {
        Context context;
        private Constants constants;
        private String ip;



        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String submit_url = "http://"+ip+"/pgr/review.php";

            if (type.equals("submit")){
                try {
                    String email = constants.getEmail();
                    String rating = strings[1];
                    String review = strings[2];
                    Log.i("status","inside the login try catch");
                    URL url = new URL(submit_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+
                            "&"+URLEncoder.encode("rating","UTF-8")+"="+URLEncoder.encode(rating,"UTF-8")+
                            "&"+URLEncoder.encode("review","UTF-8")+"="+URLEncoder.encode(review,"UTF-8");
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

        }

        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("source",s);
            boolean submitSuccessful = s.contains("submitsuccess");
            boolean entryExist = s.contains("entryexist");
            if (submitSuccessful) {
                Toast.makeText(context, "Thank you for submitting the feedback! ", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }else if(entryExist){
                Toast.makeText(context, "Your entry already exists! ", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(context, "There was an unknown error, please try again!", Toast.LENGTH_SHORT).show();
            }

        }

        public RatingSubmission(Context ctx) {
            context = ctx;
        }
    }
}
