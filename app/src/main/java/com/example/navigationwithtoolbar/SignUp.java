package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//TODO:App is crashing on click on SignUp button with an error message Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
public class SignUp extends AppCompatActivity {

    private String TAG = "SignUpActivity";
    TextView dateTextView;
    Constants constants;
    Button signUp;
    String age, date, stringName, stringEmail, stringPhone,stringAccess, stringGender, stringDob, stringPassword;
    int d, m, y;
    Toolbar toolbar;
    View logo_back;
    EditText name, email, number, password, confirmPassword, otp;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    RadioGroup maleFemale;
    RadioGroup pimPvt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        dateTextView = findViewById(R.id.selectDate);

        name = findViewById(R.id.etrname);
        email = findViewById(R.id.etremail);
        number = findViewById(R.id.etrphone);
        signUp = findViewById(R.id.btnRSignup);
        maleFemale = findViewById(R.id.maleFemale);
        pimPvt = findViewById(R.id.pimpvt);
        password = findViewById(R.id.etrpassword);
        confirmPassword = findViewById(R.id.etrconfirmpassword);
        date = "Select Date of Birth";
        toolbar = findViewById(R.id.signup_toolbar);
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        constants = new Constants(this);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dialog = new DatePickerDialog(
                        SignUp.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.button_background);
                dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.button_background);
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
                dateTextView.setText(date);
                date = year + "-" + month + "-" + dayOfMonth;
                dateTextView.setTextSize(22);
                stringDob = date;

            }
        };
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("Name Required");
                    name.requestFocus();
                    return;
                }
                if (email.getText().toString().trim().equalsIgnoreCase("")) {
                    email.setError("Email Required");
                    email.requestFocus();
                    return;
                } else if (!isEmailValid(email.getText().toString())) {
                    email.setError("Valid email is required");
                    email.requestFocus();
                    return;
                }
                if (number.getText().length() < 10) {
                    number.setError("Valid phone number is required");
                    number.requestFocus();
                    return;
                }
                String valueMaleFemale = "No value";
                String valuePimPvt = "No value";
                try {
                    valueMaleFemale =
                            ((RadioButton) findViewById(pimPvt.getCheckedRadioButtonId()))
                                    .getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    valuePimPvt =
                            ((RadioButton) findViewById(pimPvt.getCheckedRadioButtonId()))
                                    .getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("MF", valueMaleFemale);
                if (valueMaleFemale.equals("No value")) {
                    Toast.makeText(SignUp.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (valuePimPvt.equals("No value")) {
                    Toast.makeText(SignUp.this, "Please Select PIM or PVT", Toast.LENGTH_SHORT).show();
                    return;
                }
                age = getAge(y, m, d);
                Log.i("Age", age);
                if (date.equals("Select Date of Birth")) {
                    Toast.makeText(SignUp.this, "Please select your DOB", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(age) < 18) {
                    Log.i("Age", age);
                    Toast.makeText(SignUp.this, "Sorry, you must be 18+", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() > 0) {
                    if (password.getText().toString().length() < 8) {
                        password.setError("Password must be of minimum 8 characters");
                        password.requestFocus();
                    } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        confirmPassword.setError("Password does not match");
                        confirmPassword.requestFocus();
                    }
                } else {
                    password.setError("This field can not be empty");
                    password.requestFocus();
                    return;
                }

                BackgroundWorker backgroundWorker = new BackgroundWorker(SignUp.this);

                stringName = name.getText().toString();
                stringEmail = email.getText().toString();
                stringPassword = password.getText().toString();
                stringGender = getMF();
                stringAccess = getAccess();
                stringPhone = number.getText().toString();
                Log.i("Variables", stringName + "   " + stringEmail + "   " + stringPhone + "   " + stringGender + "   " + date + "   " + stringPassword);
                if (date.equals("Select Date of Birth")) {
                    Toast.makeText(SignUp.this, "Please select your DOB", Toast.LENGTH_SHORT).show();
                    return;
                }
                backgroundWorker.execute("register", stringName,stringEmail,stringPhone,stringGender,stringAccess,stringDob,stringPassword);
            }
        });
    }

    public String getMF() {
        RadioButton checkedRadioButton = maleFemale.findViewById(maleFemale.getCheckedRadioButtonId());
        String s = checkedRadioButton.getText().toString();
        if (s.equals("Male")) {
            return "M";
        } else {
            return "F";
        }
    }
    public String getAccess() {
        RadioButton checkedRadioButton = pimPvt.findViewById(pimPvt.getCheckedRadioButtonId());
        return checkedRadioButton.getText().toString().toLowerCase();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
    public class BackgroundWorker extends AsyncTask<String, String, String> {
        Context context;
        AlertDialog alertDialog;

        Boolean userExist=false;
        String ip = constants.getIp();
        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            String register_url = "http://"+ip+"/pgr/register.php";
            String userexist_url = "http://"+ip+"/pgr/userexist.php";

            if (type.equals("register")){
                try {
                    String name = strings[1];
                    String email = strings[2];
                    String phone = strings[3];
                    String gender = strings[4];
                    String access = strings[5];
                    String dob = strings[6];
                    String password = strings[7];
                    System.out.println("name "+name+
                            "\nemail"+email+
                            "\nphone"+phone+
                            "\ngender"+gender+
                            "\ndob"+dob+
                            "\npassword"+password);
                    Log.i("status","inside the try catch");
                    URL url = new URL(register_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                            +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                            +URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"
                            +URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(gender,"UTF-8")+"&"
                            +URLEncoder.encode("access","UTF-8")+"="+URLEncoder.encode(access,"UTF-8")+"&"
                            +URLEncoder.encode("dob","UTF-8")+"="+URLEncoder.encode(dob,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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
                    System.out.println(result);

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
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("LoginStatus");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("error")){
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
               boolean insertSuccessful = s.contains("insertsuccessful");
                boolean insertUnsuccessful = s.contains("insertunsuccessful");
                boolean userExist = s.contains("userExist");

                if (insertSuccessful) {
                    Intent i = new Intent(getApplication(),VerifyEmail.class);
                    i.putExtra("email",email.getText().toString());
                    startActivity(i);
                } else if (insertUnsuccessful) {
                    alertDialog.setMessage("Sorry, Something went wrong!");
                } else if (userExist) {
                    alertDialog.setMessage("Email already registered!");
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
        public Boolean ifUserExist(){
            if(userExist==true){
                return true;
            }else {
                return false;
            }
        }
    }
}
