package com.example.do_i_need_it;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.do_i_need_it.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailsActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ImageView productImage;
    TextView productTitle, productPrice, productUrl;
    ImageButton shareButton, editButton, mapButton, deleteButton;
    Button purchasedButton;
    Dialog dialog;
    Button addBtn;
    EditText editProdTitle, editProdWebsite, editProdPrice;
    ImageButton prodImage, prodLocation;
    TextView closeBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        dialog = new Dialog(this);

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

        /**
         *  BUTTON ACTION LISTENERS
         */
        purchasedButton.setOnClickListener(v -> showConfirmPurchasedDialog(product));

        shareButton.setOnClickListener(v -> {
            // Share Product
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I just added a new item to my wish list! Below are the details: \n" + product.getProdTitle() + "\n" + "Price: " + product.getProdPrice() + "\n" + "Find it at: "+ product.getProdWebsite());

            startActivity(sendIntent);
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(product);
            }
        });

        deleteButton.setOnClickListener(v -> {
            showConfirmDeleteDialog(product);
        });

    }

    private void showEditDialog(Product product) {
        dialog.setContentView(R.layout.edit_product_dialog);
        closeBtn = dialog.findViewById(R.id.closeBtn);
        editProdTitle = dialog.findViewById(R.id.editProdTitle);
        editProdWebsite = dialog.findViewById(R.id.editProdWebsite);
        editProdPrice = dialog.findViewById(R.id.editProdPrice);
        prodImage = dialog.findViewById(R.id.prodImage);
        prodLocation = dialog.findViewById(R.id.prodLocation);
        progressBar = dialog.findViewById(R.id.progressBar);
        addBtn = dialog.findViewById(R.id.addBtn);

        editProdTitle.setText(product.getProdTitle());
        editProdWebsite.setText(product.getProdWebsite());
        editProdPrice.setText(String.format("%s", product.getProdPrice()));

        prodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chooseImage();
            }
        });

        prodLocation = null;

        addBtn.setOnClickListener(v -> {

            String title = editProdTitle.getText().toString().trim();
            String url = editProdWebsite.getText().toString().trim();
            String price = editProdPrice.getText().toString().trim();

            // Form Validation
            if(TextUtils.isEmpty(title)) {
                editProdTitle.setError("Product title required.");
                return;
            }
            if(TextUtils.isEmpty(price)) {
                editProdPrice.setError("Please enter product price.");
                return;
            }


            //saveProduct(title, price, url);


        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showConfirmDeleteDialog(Product product) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Confirm Selection");
        builder.setMessage("Are you sure you would like to delete this item?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Change Product status to discarded in firestore
            DocumentReference ref = fStore.collection("products").document(product.getProdId());
            ref.update("status", "discarded" ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("action", "product status change");
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Product discarded.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.show();
    }


    private void showConfirmPurchasedDialog(Product product) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Confirm Selection");
        builder.setMessage("Mark product as purchased?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Change Product status to purchased in firestore:
            DocumentReference ref = fStore.collection("products").document(product.getProdId());
            ref.update("status", "purchased" ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Product marked as purchased.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        builder.show();
    }

}