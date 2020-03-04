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


public class VideoModelAdapter  extends  RecyclerView.Adapter<VideoModelAdapter.VideoModelViewHolder>{

    private Context mCtx;
    private List<VideoModel> videoModelList;
    private OnCardListner onCardListner;


    public VideoModelAdapter(Context mCtx, List<VideoModel> videoModelList, OnCardListner onCardListner) {
        this.mCtx = mCtx;
        this.videoModelList = videoModelList;
        this.onCardListner = onCardListner;
    }

    @NonNull
    @Override
    public VideoModelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int id) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_card_layout, viewGroup, false);
        return new VideoModelViewHolder(view,onCardListner);

    }

    @Override
    public void onBindViewHolder(@NonNull VideoModelViewHolder videoModelViewHolder, int i) {
        VideoModel videoModel = videoModelList.get(i);
        videoModelViewHolder.tTitle.setText(videoModel.getTitle());

    }

    @Override
    public int getItemCount() {
        return videoModelList.size();
    }

    class VideoModelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tTitle;
        OnCardListner onCardListner;
        public VideoModelViewHolder(@NonNull View itemView, OnCardListner onCardListner) {
            super(itemView);
            tTitle = itemView.findViewById(R.id.video_title);
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
    public List<VideoModel> getlist(){
        return videoModelList;
    }
}
