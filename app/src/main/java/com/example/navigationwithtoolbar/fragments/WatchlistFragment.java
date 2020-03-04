package com.example.navigationwithtoolbar.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationwithtoolbar.BuySellStock;
import com.example.navigationwithtoolbar.Constants;
import com.example.navigationwithtoolbar.HomeActivity;
import com.example.navigationwithtoolbar.R;
import com.example.navigationwithtoolbar.productModel.Product;
import com.example.navigationwithtoolbar.productModel.ProductAdapter;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchlistFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View v;
    private Constants constants;
    private String ip;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    LinearLayout noResults,netError;




    public WatchlistFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_watchlist,null);
        constants = new Constants(getActivity());
        ip = constants.getIp();
        productList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.watchlistRecyclerView);
        recyclerView.setHasFixedSize(false);
        noResults = v.findViewById(R.id.empty_watchlist);
        netError = v.findViewById(R.id.network_error);
        swipeRefreshLayout = v.findViewById(R.id.watchlistrefreshLayout);
        toolbar = v.findViewById(R.id.watchlist_toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetcIt();
            }
        });


        //-----------------toolbar stuff--------------------------------------
        toolbar.setTitle("Watchlist");
        final MenuItem spin = toolbar.getMenu().findItem(R.id.filter);
        final MenuItem search = toolbar.getMenu().findItem(R.id.search);
        search.setVisible(false);
        spin.setVisible(false);
        //-------------------------X---------X----------------X--------X-----------------------

        //---------------------Drawer stuff----------------------------------------------
        drawer = getActivity().findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(),drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //----------------------X---------X------------X-----------X------------------------



        fetcIt();
        return v;
        //return inflater.inflate(R.layout.fragment_watchlist, container, false);
    }
    public ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Product p = productAdapter.getlist().get(viewHolder.getAdapterPosition());
            String rCompany_code = p.getCode();
            //todo:here
            DeleteFromWatchlist delete = new DeleteFromWatchlist(getContext());
            delete.execute("delete",constants.getEmail(),rCompany_code);
            productAdapter.getlist().remove(viewHolder.getAdapterPosition());
            productAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    };
    public void fetcIt(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants constants = new Constants(getContext());
                WatchlistFragment.WatchlistFetcher dataFetcher = new WatchlistFragment.WatchlistFetcher(getActivity());
                dataFetcher.execute("fetchdata",constants.getEmail());
            }
        });
    }

    public class WatchlistFetcher extends AsyncTask<String,String,String> {
        Context context;
        String c,p,s,cd;
        public List<Product> productList;



        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            String getWatchlist_url = "http://"+ip+"/pgr/getwatchlist.php";
            productList = new ArrayList<>();
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }catch(Exception ex){
                ex.printStackTrace();
            }
            if (type.equals("fetchdata")){
                try {
                    String email = strings[1];
                    Log.i("status","inside the buy try catch");
                    URL url = new URL(getWatchlist_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    Log.i("status","Http url connection established properly");

                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    Log.i("status","buffer writer working");

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");

                    Log.i("postData",post_data);

                    bufferedWriter.write(post_data);
                    Log.i("status","bufferedWriter.write(post_data) executed successfully");

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    //-----------------------------------------------------

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
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }catch(Exception ex){
                ex.printStackTrace();
            }
            System.out.println("epr");
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
        protected void onPostExecute(String str) {
            if (str.contains("0 results")){
                noResults.setAlpha(1);
                return;
            }else if(str.equals("error")){
                netError.setAlpha(1);
                productList.clear();
                productAdapter =new ProductAdapter(getActivity(),productList,null);
                recyclerView.setAdapter(productAdapter);
                return;
            }else{
                noResults.setAlpha(0);
                netError.setAlpha(0);
            }


            Log.i("str",str);
            Log.i("jason String",str);
            try {
                JSONArray jsonArr = new JSONArray(str);
                for (int i = 0; i < jsonArr.length(); i++)
                {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    c = jsonObj.getString("companyName");
                    p = jsonObj.getString("price");
                    s = jsonObj.getString("status");
                    cd = jsonObj.getString("code");
                    productList.add(new Product(c,"INR "+p,s,cd));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProductAdapter.OnCardListner onCardListner = new ProductAdapter.OnCardListner() {
                            @Override
                            public void onCardClick(int position) {
                                Log.i("Card Selected",productList.get(position).getCompanyName());
                                Intent in = new Intent(getActivity(), BuySellStock.class);
                                in.putExtra("companyName",productList.get(position).getCompanyName());
                                in.putExtra("price",productList.get(position).getPrice());
                                in.putExtra("status",productList.get(position).getStatus());
                                in.putExtra("code",productList.get(position).getCode());
                                startActivity(in);
                            }
                        };
                        productAdapter =new ProductAdapter(getActivity(),productList,onCardListner);
                        recyclerView.setAdapter(productAdapter);
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public WatchlistFetcher(Context ctx) {
            context = ctx;
        }

    }
    public class DeleteFromWatchlist extends AsyncTask<String, String, String> {
        Context context;
        private Constants constants;
        private String ip;



        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            constants = new Constants(context);
            ip = constants.getIp();
            String delete_url = "http://"+ip+"/pgr/deletefromwatchlist.php";

            if (type.equals("delete")){
                try {
                    String email = strings[1];
                    String code = strings[2];
                    URL url = new URL(delete_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));


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
            return null;
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
            if (s == null){
                s ="networkerror";
            }
            try {
                boolean success = s.contains("done");
                boolean internalError = s.contains("error");
                boolean networkError = s.contains("networkerror");

                if (success) {
                    Log.i("Delete status","success");
                    return;
                } else if (internalError) {
                    Toast.makeText(context, "There was an internal error!\nPlease refresh and try again.", Toast.LENGTH_SHORT).show();
                } else if (networkError) {
                    Toast.makeText(context, "Network error!\nPlease refresh and try again.", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public DeleteFromWatchlist(Context ctx) {
            context = ctx;
        }
    }

}
