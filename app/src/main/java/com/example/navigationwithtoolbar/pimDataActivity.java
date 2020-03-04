package com.example.navigationwithtoolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.navigationwithtoolbar.productModel.PDFModel;
import com.example.navigationwithtoolbar.productModel.PDFModelAdapter;
import com.example.navigationwithtoolbar.productModel.PIMCardModel;
import com.example.navigationwithtoolbar.productModel.PIMCardModelAdapter;
import com.example.navigationwithtoolbar.productModel.VideoModel;
import com.example.navigationwithtoolbar.productModel.VideoModelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class pimDataActivity extends AppCompatActivity {

    int sno;
    Toolbar toolbar;
    LinearLayout progressBarLayout;
    LinearLayout networkErrorLayout;
    RecyclerView videoRecyclerView,pdfRecyclerView;
    Context context;
    View logo_back;
    private List<VideoModel> videoModelList;
    private List<PDFModel> pdfModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_data);
        Intent currentIntent = getIntent();
        sno = Integer.valueOf(currentIntent.getStringExtra("sno"));
        System.out.println(sno);
        context = this;
        toolbar = findViewById(R.id.pim_data_activity_toolbar);
        videoModelList = new ArrayList<>();
        pdfModelList = new ArrayList<>();
        progressBarLayout = findViewById(R.id.pim_data_progressbar);
        networkErrorLayout = findViewById(R.id.pim_network_error);

        videoRecyclerView = findViewById(R.id.pim_data_video_recycler_view);
        pdfRecyclerView = findViewById(R.id.pim_data_pdf_recycler_view);

        videoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pdfRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logo_back = toolbar.getChildAt(1);
        logo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Constants constants = new Constants(context);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+Constants.ip+"/pgr/getpiminnerdata.php?email="+constants.getEmail()+"&sno="+sno;//getPimInnerData

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("auth_error_code_001")){
                            progressBarLayout.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, "Auth Error", Toast.LENGTH_SHORT).show();
                        }else if(response.contains("title")){
                            try {
                                Log.i("response",response);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray videoJsonArray = new JSONArray(jsonObject.getString("video"));
                                JSONArray pdfJsonArray = new JSONArray(jsonObject.getString("pdf"));

                                for (int i = 0; i < videoJsonArray.length();i++){
                                    JSONObject videoJsonObj = videoJsonArray.getJSONObject(i);
                                    String title = videoJsonObj.getString("title");
                                    String url = videoJsonObj.getString("url");
                                    videoModelList.add(new VideoModel(title,url));
                                }
                                for (int i = 0; i < pdfJsonArray.length();i++){
                                    JSONObject pdfJsonObj = pdfJsonArray.getJSONObject(i);
                                    String title = pdfJsonObj.getString("title");
                                    String url = pdfJsonObj.getString("url");
                                    pdfModelList.add(new PDFModel(title,url));
                                }


                                VideoModelAdapter.OnCardListner videoOnCardListner =
                                        new VideoModelAdapter.OnCardListner() {
                                            @Override
                                            public void onCardClick(int position) {
                                                Log.i("Card Selected",videoModelList.get(position).getTitle());
//                                                Intent i = new Intent(context,pimDataActivity.class);
//                                                i.putExtra("sno",pimCardModelList.get(position).getSno());
//                                                startActivity(i);
//                                                Intent i = new Intent(context,ExoPlayerActivity.class);
//                                                i.putExtra("url",videoModelList.get(position).getUrl());
//                                                startActivity(i);
                                                Uri uri = Uri.parse(videoModelList.get(position).getUrl());
                                                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                                                startActivity(it);
                                            }
                                        };
                                PDFModelAdapter.OnCardListner pdfOnCardListner =
                                        new PDFModelAdapter.OnCardListner() {
                                            @Override
                                            public void onCardClick(int position) {
                                                Log.i("Card Selected",pdfModelList.get(position).getTitle());
//                                                Intent i = new Intent(context,pimDataActivity.class);
//                                                i.putExtra("sno",pimCardModelList.get(position).getSno());
//                                                startActivity(i);
                                                Uri uri = Uri.parse(pdfModelList.get(position).getUrl());
                                                Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                                                startActivity(it);
                                            }
                                        };

//                                System.out.println(pimCardModelList);
                                //PIMCardModelAdapter pimCardModelAdapter = new PIMCardModelAdapter(context,pimCardModelList,onCardListner);
                                VideoModelAdapter videoModelAdapter = new VideoModelAdapter(context,videoModelList,videoOnCardListner);
                                PDFModelAdapter pdfModelAdapter = new PDFModelAdapter(context,pdfModelList,pdfOnCardListner);

                                //authErrorLayout.setVisibility(View.INVISIBLE);
                                progressBarLayout.setVisibility(View.INVISIBLE);
                                videoRecyclerView.setVisibility(View.VISIBLE);
                                pdfRecyclerView.setVisibility(View.VISIBLE);
                                videoRecyclerView.setAdapter(videoModelAdapter);
                                pdfRecyclerView.setAdapter(pdfModelAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error");
            }
        });
        queue.add(stringRequest);
    }
}
