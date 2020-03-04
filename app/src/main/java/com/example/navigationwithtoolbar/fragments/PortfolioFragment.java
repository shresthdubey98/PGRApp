package com.example.navigationwithtoolbar.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigationwithtoolbar.BuySellStock;
import com.example.navigationwithtoolbar.Constants;
import com.example.navigationwithtoolbar.R;
import com.example.navigationwithtoolbar.productModel.PortfolioProduct;
import com.example.navigationwithtoolbar.productModel.PortfolioProductAdapter;
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

import static com.example.navigationwithtoolbar.productModel.PortfolioProductAdapter.round;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment {
    private View v ;
    private RecyclerView recyclerView;
    private PortfolioProductAdapter portfolioProductAdapter;
    private List<PortfolioProduct> portfolioProductList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Constants constants;
    private String ip;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    LinearLayout noResults,netError;
    private TextView totalProfitLoss,totalInvestment;
    float tinvist=0,tprofitloss=0;
    public PortfolioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_portfolio,null);
        constants = new Constants(getActivity());
        ip = constants.getIp();
        portfolioProductList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.portfolioRecyclerView);
        recyclerView.setHasFixedSize(true);
        toolbar = v.findViewById(R.id.portfolio_toolbar);
        swipeRefreshLayout = v.findViewById(R.id.refreshLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetcIt();
                tinvist = 0;
                tprofitloss = 0;
            }
        });
        noResults = v.findViewById(R.id.empty_portfolio);
        netError = v.findViewById(R.id.portfolio_network_error);
        totalProfitLoss = v.findViewById(R.id.totalgainorloss);
        totalInvestment = v.findViewById(R.id.totalinv);

        //-----------------toolbar stuff--------------------------------------
        toolbar.setTitle("Portfolio");
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
        //return inflater.inflate(R.layout.fragment_home, container, false);
        return v;
        //return inflater.inflate(R.layout.fragment_portfolio, container, false);
    }
    public void fetcIt(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants constants = new Constants(getActivity());
                PortfolioFetcher portfolioFetcher = new PortfolioFetcher(getActivity());
                portfolioFetcher.execute("getportfolio", constants.getEmail());
            }
        });
    }
    public class PortfolioFetcher extends AsyncTask<String,String,String> {
        Context context;
        String c,p,s,cd,avi,worth,investment;
        public List<PortfolioProduct> portfolioProductList;




        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            String getPortfolio_url = "http://"+ip+"/pgr/portfolio.php";
            portfolioProductList = new ArrayList<>();
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
            if (type.equals("getportfolio")){
                try {
                    String email = strings[1];
                    Log.i("status","inside the buy try catch");
                    URL url = new URL(getPortfolio_url);
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
                    //----------------------------------
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
            if (str.contains("no data")){
                noResults.setAlpha(1);
                return;
            }else if(str.equals("error")){
                netError.setAlpha(1);
                portfolioProductList.clear();
                portfolioProductAdapter =new PortfolioProductAdapter(getActivity(),portfolioProductList,null);
                recyclerView.setAdapter(portfolioProductAdapter);
                totalInvestment.setText("Total Investment: --");
                totalProfitLoss.setText("Total Gain/Loss: --");

                return;
            }else{
                noResults.setAlpha(0);
                netError.setAlpha(0);
            }
            Log.i("jason String",str);
            try {
                final float tinv = 0,tpl = 0;

                System.out.println("source code"+str);
                JSONArray jsonArr = new JSONArray(str);
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    c = jsonObj.getString("companyName");
                    p = jsonObj.getString("currentPrice");
                    s = jsonObj.getString("status");
                    cd = jsonObj.getString("code");
                    avi = jsonObj.getString("stockAvailable");
                    worth = String.valueOf(Float.parseFloat(p.replace("INR ", "")) * Float.parseFloat(avi));
                    System.out.println(worth);
                    investment = jsonObj.getString("effectivePrice");
                    if (!avi.equals("0")) {
                        portfolioProductList.add(new PortfolioProduct(c, "INR " + p, s, cd, avi, worth, investment));
                    }
                }

                for(int i=0;i<portfolioProductList.toArray().length;i++){

                    Log.i("inv-"+i,portfolioProductList.get(i).getInvestment());
                    Log.i("avi-"+i,portfolioProductList.get(i).getAvailableStocks());
                    float W = Float.parseFloat(portfolioProductList.get(i).getWorth());
                    float I = Float.parseFloat(portfolioProductList.get(i).getInvestment());
                    if(Integer.parseInt(portfolioProductList.get(i).getAvailableStocks()) == 0){
                        I = 0;
                        W = 0;
                    }
                    tinvist += I;
                    if(I<W){
                        //profit
                        tprofitloss+=(W-I);
                    }else if(W<I){
                        //loss
                        tprofitloss-=(I-W);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PortfolioProductAdapter.OnCardListner onCardListner = new PortfolioProductAdapter.OnCardListner() {
                            @Override
                            public void onCardClick(int position) {
                                Log.i("Card Selected",portfolioProductList.get(position).getCompanyName());
                                Intent in = new Intent(getActivity(), BuySellStock.class);
                                in.putExtra("companyName",portfolioProductList.get(position).getCompanyName());
                                in.putExtra("price",portfolioProductList.get(position).getPrice());
                                in.putExtra("status",portfolioProductList.get(position).getStatus());
                                in.putExtra("code",portfolioProductList.get(position).getCode());
                                startActivity(in);
                            }
                        };
                        portfolioProductAdapter =new PortfolioProductAdapter(getActivity(),portfolioProductList,onCardListner);
                        recyclerView.setAdapter(portfolioProductAdapter);

                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        totalInvestment.setText("Total Investment:"+String.format("%.0f",round(tinvist,2)));
//                        Log.i("inv",String.valueOf(portfolioProductAdapter.getTinv()));
//                        Log.i("profit or loss",String.valueOf(portfolioProductAdapter.getTpl()));

                        if(tprofitloss>0.1){
                            totalProfitLoss.setText("Total Gain:"+tprofitloss);
                            totalProfitLoss.setTextColor(getResources().getColor(R.color.strongBuy));
                        }else if(tprofitloss<-0.1){
                            totalProfitLoss.setText("Total Loss:"+tprofitloss);
                            totalProfitLoss.setTextColor(getResources().getColor(R.color.strongSell));
                        }else{
                            totalProfitLoss.setText("Total Gain/Loss:"+portfolioProductAdapter.getTpl());
                        }
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public PortfolioFetcher(Context ctx) {
            context = ctx;
        }

    }


}
