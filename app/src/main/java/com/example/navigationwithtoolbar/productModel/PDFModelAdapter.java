package com.example.navigationwithtoolbar.productModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navigationwithtoolbar.R;
import java.util.List;

public class PDFModelAdapter  extends  RecyclerView.Adapter<PDFModelAdapter.PDFModelViewHolder>{

    private Context mCtx;
    private List<PDFModel> pdfModelList;
    private OnCardListner onCardListner;


    public PDFModelAdapter(Context mCtx, List<PDFModel> pdfModelList, OnCardListner onCardListner) {
        this.mCtx = mCtx;
        this.pdfModelList = pdfModelList;
        this.onCardListner = onCardListner;
    }

    @NonNull
    @Override
    public PDFModelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int id) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pdf_card_layout, viewGroup, false);
        return new PDFModelViewHolder(view,onCardListner);

    }

    @Override
    public void onBindViewHolder(@NonNull PDFModelViewHolder pdfModelViewHolder, int i) {
        PDFModel pdfModel = pdfModelList.get(i);
        pdfModelViewHolder.tTitle.setText(pdfModel.getTitle());

    }

    @Override
    public int getItemCount() {
        return pdfModelList.size();
    }

    class PDFModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tTitle;
        OnCardListner onCardListner;
        public PDFModelViewHolder(@NonNull View itemView, OnCardListner onCardListner) {
            super(itemView);
            tTitle = itemView.findViewById(R.id.pdf_title);
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
    public List<PDFModel> getlist(){
        return pdfModelList;
    }
}

