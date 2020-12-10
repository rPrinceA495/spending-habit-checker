package com.example.do_i_need_it;

import androidx.annotation.NonNull;
import android.app.AlertDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_i_need_it.fragments.HelpFragment;
import com.example.do_i_need_it.fragments.MyProductsFragment;
import com.example.do_i_need_it.fragments.ProfileFragment;
import com.example.do_i_need_it.fragments.SettingsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fab;
    ProgressBar progressBar;
    Button addBtn, cancelBtn;
    EditText prodTitle, prodWebsite, prodPrice;
    ImageButton prodImage, prodLocation;
    TextView closeBtn;
    Dialog dialog;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Hooks
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        String action = getIntent().getStringExtra("action");

        if(action != null && action.equals("product status change")) {
            // Transact to product fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProductsFragment()).commit();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        }

        dialog = new Dialog(this);

        fab.setOnClickListener(v -> showProductDialog());


    }

    private void showProductDialog() {

        dialog.setContentView(R.layout.add_product_dialog);
        closeBtn = dialog.findViewById(R.id.closeBtn);
        prodTitle = dialog.findViewById(R.id.prodTitle);
        prodWebsite = dialog.findViewById(R.id.prodWebsite);
        prodPrice = dialog.findViewById(R.id.prodPrice);
        prodImage = dialog.findViewById(R.id.prodImage);
        prodLocation = dialog.findViewById(R.id.prodLocation);
        progressBar = dialog.findViewById(R.id.progressBar);
        addBtn = dialog.findViewById(R.id.addBtn);

        prodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        prodLocation = null;

        addBtn.setOnClickListener(v -> {

            String title = prodTitle.getText().toString().trim();
            String url = prodWebsite.getText().toString().trim();
            String price = prodPrice.getText().toString().trim();

            // Form Validation
            if(TextUtils.isEmpty(title)) {
                prodTitle.setError("Product title required.");
                return;
            }
            if(TextUtils.isEmpty(price)) {
                prodPrice.setError("Please enter product price.");
                return;
            }


            saveProduct(title, price, url);


        });

        closeBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void saveProduct(String title, String price, String url) {

        progressBar.setVisibility(View.VISIBLE);

        if(imageUri != null) {
            final String randomKey = UUID.randomUUID().toString();
            StorageReference riversRef = storageReference.child("images/" + randomKey);

            riversRef.putFile(imageUri)
                    .addOnCompleteListener(task -> riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Uri imageDownloadUrl = uri;;

                            // Store user details to firestore:
                            DocumentReference prodDocRef = fStore.collection("products").document();

                            final Map<String, Object> prod = new HashMap<>();
                            prod.put("prod_title", title);
                            prod.put("prod_url", url);
                            prod.put("prod_price", price);
                            prod.put("image_path", imageDownloadUrl.toString());
                            prod.put("prod_owner", Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
                            prod.put("date_added", new Date().toString());
                            prod.put("status", "");

                            prodDocRef.set(prod).addOnSuccessListener(aVoid -> {

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "New product added.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Product details successfully stored.");
                                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                //finish();
                                imageUri = null;
                                dialog.dismiss();
                                // Transact to product fragment
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProductsFragment()).commit();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "onFailure: database ref not created.");
                            });;

                        }
                    }));

        }else {
            // Store product details to firestore:
            DocumentReference prodDocRef = fStore.collection("products").document();

            final Map<String, Object> prod = new HashMap<>();
            prod.put("prod_title", title);
            prod.put("prod_url", url);
            prod.put("prod_price", price);
            prod.put("image_path", "");
            prod.put("prod_owner", Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
            prod.put("date_added", new Date().toString());
            prod.put("status", "");

            prodDocRef.set(prod).addOnSuccessListener(aVoid -> {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "New product added.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Product details successfully stored.");
                //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //finish();
                dialog.dismiss();
                // Transact to product fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProductsFragment()).commit();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFailure: database ref not created.");
            });;
        }

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(getApplicationContext(), "Image attached.", Toast.LENGTH_SHORT).show();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment;

        switch(item.getItemId()) {
            case R.id.profile_link:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.my_products_link:
                selectedFragment = new MyProductsFragment();
                break;
            case R.id.help_link:
                selectedFragment = new HelpFragment();
                break;
            case R.id.settings_link:
                selectedFragment = new SettingsFragment();
                break;
            default:
                selectedFragment = null;
                break;
        }

        assert selectedFragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };


}