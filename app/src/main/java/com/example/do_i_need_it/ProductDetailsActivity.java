package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.do_i_need_it.model.Product;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView productImage;
    TextView productTitle, productPrice, productUrl;
    ImageButton shareButton, editButton, mapButton, deleteButton;
    Button purchasedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Product product = (Product) getIntent().getSerializableExtra("product");

        productImage = findViewById(R.id.product_image);
        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        productUrl = findViewById(R.id.product_url);
        shareButton = findViewById(R.id.share_btn);
        editButton = findViewById(R.id.edit_btn);
        mapButton = findViewById(R.id.map_btn);
        deleteButton = findViewById(R.id.delete_btn);
        purchasedButton = findViewById(R.id.purchased_btn);

        // Set product attributes to view:
        productTitle.setText(product.getProdTitle());
        productPrice.setText(String.format("$%s", product.getProdPrice()));
        productUrl.setText(product.getProdWebsite());

        String imageUri = product.getImageUrl();

        if(imageUri.isEmpty()) {
            // Default image
            Glide.with(this)
                    .load(R.drawable.prod_img_default).into(productImage);
        }else {
            // Firebase stored image
            Glide.with(this)
                    .load(imageUri).into(productImage);
        }

    }
}