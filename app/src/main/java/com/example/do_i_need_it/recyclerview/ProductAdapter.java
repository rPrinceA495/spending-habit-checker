package com.example.do_i_need_it.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.do_i_need_it.R;
import com.example.do_i_need_it.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<Product> productList;
    private OnProductListener opl;

    public ProductAdapter(Context context, List<Product> productList, OnProductListener opl) {
        this.context = context;
        this.productList = productList;
        this.opl = opl;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(view, opl);

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        // Binding of product item:
        holder.titleLabel.setText(productList.get(position).getProdTitle());
        holder.priceLabel.setText(String.format("$ %.2f", productList.get(position).getProdPrice()));
        holder.dateAdded.setText(String.format("Added: %s %s", productList.get(position).getDateAdded().substring(0,10), " 2020"));

        String imageUri = null;
        imageUri = productList.get(position).getImageUrl();

        if(imageUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.prod_img_default).into(holder.prodImg);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(imageUri).into(holder.prodImg);
        }

        if(imageUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.prod_img_default).into(holder.prodImg);
        }else {
            Glide.with(holder.itemView.getContext())
                    .load(imageUri).into(holder.prodImg);
        }

        if(productList.get(position).getProdStatus().equals("purchased")) {
            holder.statusLabel.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return this.productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView prodImg;
        TextView titleLabel, priceLabel, statusLabel, dateAdded;
        OnProductListener opl;

        public ProductViewHolder(@NonNull View itemView, OnProductListener opl) {
            super(itemView);

            prodImg = itemView.findViewById(R.id.prod_img);
            titleLabel = itemView.findViewById(R.id.title_label);
            priceLabel = itemView.findViewById(R.id.price_label);
            statusLabel = itemView.findViewById(R.id.status_label);
            dateAdded = itemView.findViewById(R.id.date_added_label);
            this.opl = opl;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            opl.onProductClick(getAdapterPosition());
        }
    }

    public interface OnProductListener {
        void onProductClick(int position);
    }
}
