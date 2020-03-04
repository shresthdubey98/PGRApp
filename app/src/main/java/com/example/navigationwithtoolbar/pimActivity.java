package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navigationwithtoolbar.productModel.PIMCardModel;
import com.example.navigationwithtoolbar.productModel.PIMCardModelAdapter;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class pimActivity extends AppCompatActivity {
    Toolbar toolbar;
    LinearLayout progressBarLayout;
    LinearLayout authErrorLayout;
    LinearLayout networkErrorLayout;
    TextView listofModeulesTextview;
    RecyclerView recyclerView;
    Context context;
    View logo_back;
    private List<PIMCardModel> pimCardModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_pim);
        toolbar = findViewById(R.id.pim_activity_toolbar);
        pimCardModelList = new ArrayList<>();
        listofModeulesTextview = findViewById(R.id.list_of_modules_textView);
        listofModeulesTextview.setVisibility(View.GONE);
        progressBarLayout = findViewById(R.id.pim_progressbar);
        progressBarLayout.setVisibility(View.VISIBLE);
        authErrorLayout = findViewById(R.id.pim_auth_failed);
        networkErrorLayout = findViewById(R.id.pim_network_error);
        recyclerView = findViewById(R.id.pimRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Constants constants = new Constants(context);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+Constants.ip+"/pgr/getpimdata.php?email="+constants.getEmail();
        System.out.println("URLIS:"+url);
//        String url ="http://"+Constants.ip+"/pgr/getpimdata.php?email="+constants.getEmail();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("auth_error_code_001")){
                            progressBarLayout.setVisibility(View.INVISIBLE);
                            authErrorLayout.setVisibility(View.VISIBLE);
                        }else if(response.contains("title")){
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for(int i =0 ;i< jsonArray.length();i++){
                                    System.out.println(jsonArray);
                                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                                    String title = jsonObj.getString("title");
                                    String sno = jsonObj.getString("sno");
                                    pimCardModelList.add(new PIMCardModel(sno,title));
                                }
                                PIMCardModelAdapter.OnCardListner onCardListner =
                                        new PIMCardModelAdapter.OnCardListner() {
                                            @Override
                                            public void onCardClick(int position) {
                                                Log.i("Card Selected",pimCardModelList.get(position).getTitle());
                                                Intent i = new Intent(context,pimDataActivity.class);
                                                i.putExtra("sno",pimCardModelList.get(position).getSno());
                                                startActivity(i);
                                            }
                                        };
//                                System.out.println(pimCardModelList);
                                PIMCardModelAdapter pimCardModelAdapter = new PIMCardModelAdapter(context,pimCardModelList,onCardListner);

                                authErrorLayout.setVisibility(View.INVISIBLE);
                                progressBarLayout.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                listofModeulesTextview.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(pimCardModelAdapter);

                            } catch (JSONException e) {
                                progressBarLayout.setVisibility(View.INVISIBLE);
                                networkErrorLayout.setVisibility(View.VISIBLE);
                                e.printStackTrace();

                            }
                        }else{
                            progressBarLayout.setVisibility(View.INVISIBLE);
                            networkErrorLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBarLayout.setVisibility(View.INVISIBLE);
                networkErrorLayout.setVisibility(View.VISIBLE);
                System.out.println("error");
            }
        });
        queue.add(stringRequest);
    }
}
