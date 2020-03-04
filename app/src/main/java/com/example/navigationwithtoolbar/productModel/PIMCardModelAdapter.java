package com.example.navigationwithtoolbar.productModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationwithtoolbar.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PIMCardModelAdapter  extends  RecyclerView.Adapter<PIMCardModelAdapter.PIMCardModelViewHolder>{

    private Context mCtx;
    private List<PIMCardModel> pimCardModelList;
    private List<PIMCardModel> pimCardModelListFull;
    private OnCardListner onCardListner;


    public PIMCardModelAdapter(Context mCtx, List<PIMCardModel> pimCardModelList, OnCardListner onCardListner) {
        this.mCtx = mCtx;
        this.pimCardModelList = pimCardModelList;
        this.onCardListner = onCardListner;
    }

    @NonNull
    @Override
    public PIMCardModelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int id) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pim_card_layout, viewGroup, false);
        PIMCardModelViewHolder holder = new PIMCardModelViewHolder(view,onCardListner);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PIMCardModelViewHolder pimCardModelViewHolder, int i) {
        PIMCardModel pimCardModel = pimCardModelList.get(i);
        pimCardModelViewHolder.tTitle.setText(pimCardModel.title);
        pimCardModelViewHolder.tsno.setText(pimCardModel.sno+".");

    }

    @Override
    public int getItemCount() {
        return pimCardModelList.size();
    }

    class PIMCardModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tTitle, tsno;
        OnCardListner onCardListner;
        public PIMCardModelViewHolder(@NonNull View itemView, OnCardListner onCardListner) {
            super(itemView);
            tTitle = itemView.findViewById(R.id.module_title);
            tsno = itemView.findViewById(R.id.sno);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            itemView.setOnClickListener(this);
            this.onCardListner = onCardListner;
        }
        @Override
        public void onClick(View v) {
            onCardListner.onCardClick(getAdapterPosition());
        }
    }
    public interface OnCardListner {
        void onCardClick(int position);
    }
    public List<PIMCardModel> getlist(){
        return pimCardModelList;
    }
}
