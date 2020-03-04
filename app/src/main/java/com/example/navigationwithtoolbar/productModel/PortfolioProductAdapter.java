package com.example.navigationwithtoolbar.productModel;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationwithtoolbar.R;
import com.example.navigationwithtoolbar.productModel.PortfolioProduct;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioProductAdapter extends RecyclerView.Adapter<PortfolioProductAdapter.PortfolioProductViewHolder>{

    private Context mCtx;
    private List<PortfolioProduct> portfolioProductList;
    private OnCardListner onCardListner;
    public float tinv = 0;
    public float tpl = 0;
    public float getTinv() {
        return tinv;
    }

    public float getTpl() {
        return tpl;
    }

    public PortfolioProductAdapter(Context mCtx, List<PortfolioProduct> portfolioProductList, OnCardListner onCardListner) {
        this.mCtx = mCtx;
        this.portfolioProductList = portfolioProductList;
        this.onCardListner = onCardListner;
    }

    @NonNull
    @Override
    public PortfolioProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
       // View view = inflater.inflate(R.layout.list_layout,null);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.portfolio_list_layout, viewGroup, false);
        PortfolioProductViewHolder holder = new PortfolioProductViewHolder(view,onCardListner);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull PortfolioProductViewHolder portfolioProductViewHolder, int i) {
        PortfolioProduct portfolioProduct = portfolioProductList.get(i);
        portfolioProductViewHolder.tCompanyName.setText(portfolioProduct.getCompanyName());
        portfolioProductViewHolder.tPrice.setText(portfolioProduct.getPrice());
        portfolioProductViewHolder.tStatus.setText(portfolioProduct.getStatus());
        portfolioProductViewHolder.tCode.setText(portfolioProduct.getCode());
        portfolioProductViewHolder.tavi.setText(portfolioProduct.getAvailableStocks());
        float I,A,W,P,PP,C;

        I = round(Float.parseFloat(portfolioProduct.getInvestment()),2);
        if (I<0) {
            I = 0;
        }
        tinv += I;
        System.out.println(tinv);
        A = round(Float.parseFloat(portfolioProduct.getAvailableStocks()),2);
        W = round(Float.parseFloat(portfolioProduct.getWorth()),2);
        C = round(Float.parseFloat(portfolioProduct.getPrice().replace("INR","")),2);

//        System.out.println("I:"+portfolioProduct.getInvestment()+"\n"+"A:"+A+"\n"+"W:"+W+"\n"+"C:"+C);

        if (W>I){
            //profit
            P = W-I;
            tpl+=P;
            PP = P/I*100;
            PP = round(PP,2);
            portfolioProductViewHolder.tposition.setText("+"+round(P,2));
            portfolioProductViewHolder.tposition.setTextColor(Color.parseColor("#00CC00"));
            portfolioProductViewHolder.tInvestment.setText(String.format("%.0f",round(I,2)));
            portfolioProductViewHolder.tworth.setText(String.format("%.0f",round(W,2))+"(+"+round(PP,2)+"%)");
            portfolioProductViewHolder.tworth.setTextColor(Color.parseColor("#00CC00"));
        }else if (I>W){
            //Loss
            P = I-W;
            tpl -= P;
            PP = P/I*100;
            PP = round(PP,2);
            portfolioProductViewHolder.tposition.setText("-"+round(P,2));
            portfolioProductViewHolder.tposition.setTextColor(Color.parseColor("#CC0000"));
            portfolioProductViewHolder.tInvestment.setText(String.format("%.0f",round(I,2)));
            portfolioProductViewHolder.tworth.setText(String.format("%.0f",round(W,2))+"(-"+round(PP,2)+"%)");
            portfolioProductViewHolder.tworth.setTextColor(Color.parseColor("#CC0000"));
        }else{
            //No profit no loss
            P = 0;
            portfolioProductViewHolder.tposition.setText(String.valueOf(round(P,2) ));
            portfolioProductViewHolder.tInvestment.setText(String.format("%.0f",round(I,2)));
            portfolioProductViewHolder.tworth.setText(String.format("%.0f",round(W,2))+"("+0+"%)");
        }

        if (portfolioProduct.getStatus().equals("Strong Buy")){
            portfolioProductViewHolder.tStatus.setBackgroundResource(R.color.strongBuy);
        }else if (portfolioProduct.getStatus().equals("Buy")){
            portfolioProductViewHolder.tStatus.setBackgroundResource(R.color.buy);
        }else if (portfolioProduct.getStatus().equals("Strong Sell")){
            portfolioProductViewHolder.tStatus.setBackgroundResource(R.color.strongSell);
        }else if (portfolioProduct.getStatus().equals("Sell")){
            portfolioProductViewHolder.tStatus.setBackgroundResource(R.color.sell);
        }else {
            portfolioProductViewHolder.tStatus.setBackgroundResource(R.color.neutral);
        }

    }

    @Override
    public int getItemCount() {
        return portfolioProductList.size();
    }

    class PortfolioProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tCompanyName,tPrice,tStatus,tCode,tavi,tworth,tInvestment,tposition;
        OnCardListner onCardListner;
        public PortfolioProductViewHolder(@NonNull View itemView,OnCardListner onCardListner) {
            super(itemView);
            tCompanyName = itemView.findViewById(R.id.companyName);
            tPrice = itemView.findViewById(R.id.price);
            tStatus = itemView.findViewById(R.id.status);
            tCode = itemView.findViewById(R.id.code);
            tavi = itemView.findViewById(R.id.portAvi);
            tworth = itemView.findViewById(R.id.portWorth);
            tInvestment = itemView.findViewById(R.id.investment);
            tposition = itemView.findViewById(R.id.position);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
            itemView.setOnClickListener(this);
            this.onCardListner = onCardListner;
        }

        @Override
        public void onClick(View v) {
            onCardListner.onCardClick(getAdapterPosition());
        }
    }
    public interface OnCardListner{
            void onCardClick(int position);
    }
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
