package com.example.navigationwithtoolbar.productModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.navigationwithtoolbar.R;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable{

    private Context mCtx;
    private List<Product> productList;
    private List<Product> productListFull;
    private OnCardListner onCardListner;


    public ProductAdapter(Context mCtx, List<Product> productList, OnCardListner onCardListner) {
        this.mCtx = mCtx;
        this.productList = productList;
        productListFull = new ArrayList<>(productList);
        this.onCardListner = onCardListner;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        // View view = inflater.inflate(R.layout.list_layout,null);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_layout, viewGroup, false);
        ProductViewHolder holder = new ProductViewHolder(view, onCardListner);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        Product product = productList.get(i);
        productViewHolder.tCompanyName.setText(product.getCompanyName());
        productViewHolder.tPrice.setText(product.getPrice());
        productViewHolder.tStatus.setText(product.getStatus());
        productViewHolder.tCode.setText(product.getCode());
        if (product.getStatus().equals("Strong Buy")) {
            productViewHolder.tStatus.setBackgroundResource(R.color.strongBuy);
        } else if (product.getStatus().equals("Buy")) {
            productViewHolder.tStatus.setBackgroundResource(R.color.buy);
        } else if (product.getStatus().equals("Strong Sell")) {
            productViewHolder.tStatus.setBackgroundResource(R.color.strongSell);
        } else if (product.getStatus().equals("Sell")) {
            productViewHolder.tStatus.setBackgroundResource(R.color.sell);
        } else {
            productViewHolder.tStatus.setBackgroundResource(R.color.neutral);
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tCompanyName, tPrice, tStatus, tCode;
        OnCardListner onCardListner;

        public ProductViewHolder(@NonNull View itemView, OnCardListner onCardListner) {
            super(itemView);
            tCompanyName = itemView.findViewById(R.id.companyName);
            tPrice = itemView.findViewById(R.id.price);
            tStatus = itemView.findViewById(R.id.status);
            tCode = itemView.findViewById(R.id.code);
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

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(productListFull);
            }else{
                String filterPatern = constraint.toString().toLowerCase().trim();
                for(Product product:productListFull){
                    if(product.getCompanyName().toLowerCase().contains(filterPatern) || product.getCode().toLowerCase().contains(filterPatern) ){
                        filteredList.add(product);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productList.clear();
            productList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
    public List<Product> getlist(){
        return productList;
    }
}
