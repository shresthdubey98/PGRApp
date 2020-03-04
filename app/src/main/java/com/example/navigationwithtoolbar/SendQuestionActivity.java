package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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

public class SendQuestionActivity extends AppCompatActivity {
    Toolbar toolbar;
    View logo_back;
    Button sbmt;
    EditText etQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_question);
        toolbar = findViewById(R.id.sendQuestion_toolbar);
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sbmt = findViewById(R.id.question_submit);
        etQuestion = findViewById(R.id.question);
        sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendQuestion sendQuestion = new SendQuestion(getApplication());
                sendQuestion.execute("submit",etQuestion.getText().toString());
            }
        });
    }
    private class SendQuestion extends AsyncTask<String, String, String> {
        Context context;
        private Constants constants;
        private String ip;



        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String submit_url = "http://"+ip+"/pgr/question.php";

            if (type.equals("submit")){
                try {
                    String email = constants.getEmail();
                    String qus = strings[1];
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
                            "&"+URLEncoder.encode("question","UTF-8")+"="+URLEncoder.encode(qus,"UTF-8");
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
            boolean notSuccess = s.contains("submitNotSuccess");
            if (submitSuccessful) {
                Toast.makeText(context, "Your query has been successfully submitted! ", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }else if(notSuccess){
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(context, "There was an unknown error, please try again!", Toast.LENGTH_SHORT).show();
            }

        }

        public SendQuestion(Context ctx) {
            context = ctx;
        }
    }
}
