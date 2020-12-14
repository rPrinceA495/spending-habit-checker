package com.example.do_i_need_it.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.do_i_need_it.R;
import com.example.do_i_need_it.model.Product;
import com.example.do_i_need_it.model.User;
import com.example.do_i_need_it.recyclerview.ProductAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ProfileFragment() {
        // Required empty public constructor
    }

    Dialog dialog;
    Button saveBtn;
    EditText editFullName, editPhoneNumber, editCountry;
    ImageButton profileImage;
    TextView closeBtn;
    ProgressBar progressBar;
    FirebaseStorage storage;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    User user;
    List<Product> productList = new ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final String TAG = "TAG";
        ImageView profile;
        TextView fullNameDisplay, emailDisplay, dobDisplay, phoneNumberDisplay, countryDisplay;
        TextView productsDisplay, boughtDisplay, discardedDisplay;
        TextView moneySpentDisplay, moneySavedDisplay;
        Button editProfileBtn;

        // Personal Info
        fullNameDisplay = Objects.requireNonNull(view.findViewById(R.id.fullNameDisplay));
        emailDisplay = Objects.requireNonNull(view.findViewById(R.id.emailDisplay));
        dobDisplay = Objects.requireNonNull(view.findViewById(R.id.dobDisplay));
        phoneNumberDisplay = Objects.requireNonNull(view.findViewById(R.id.phoneNumberDisplay));
        countryDisplay = Objects.requireNonNull(view.findViewById(R.id.countryDisplay));

        // Product Summary
        productsDisplay = Objects.requireNonNull(view.findViewById(R.id.productsDisplay));
        boughtDisplay = Objects.requireNonNull(view.findViewById(R.id.boughtDisplay));
        discardedDisplay = Objects.requireNonNull(view.findViewById(R.id.discardedDisplay));

        // Spent vs Saved
        moneySpentDisplay = Objects.requireNonNull(view.findViewById(R.id.moneySpentDisplay));
        moneySavedDisplay = Objects.requireNonNull(view.findViewById(R.id.moneySavedDisplay));

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        user = new User();

        assert fUser != null;
        emailDisplay.setText(fUser.getEmail());

        DocumentReference docRef = fStore.collection("users").document(Objects.requireNonNull(fUser.getEmail()));
        docRef.addSnapshotListener((value, error) -> {
            assert value != null;
            fullNameDisplay.setText(value.getString("full_name"));
            //editFullName.setText(value.getString("full_name"));
            user.setFullName(value.getString("full_name"));
            user.setPhoneNumber(value.getString("phone_number"));
            user.setCountry(value.getString("country"));
            phoneNumberDisplay.setText(value.getString("phone_number"));
            //editPhoneNumber.setText(value.getString("phone_number"));

            dobDisplay.setText(value.getString("dob"));
            countryDisplay.setText(value.getString("country"));
        });

        // Fetch Products for analytics:
        Query query = fStore.collection("products").whereEqualTo("prod_owner", fUser.getEmail());
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Product product = null;

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            //Log.d(TAG, document.getId() + " => " + document.getData());
                            String prodId = document.getId();
                            String prodTitle = document.getString("prod_title");
                            String prodWebsite = document.getString("prod_url");
                            String imageUrl = document.getString("image_path");
                            String dateAdded = document.getString("date_added");
                            double prodPrice = Double.valueOf(document.getString("prod_price"));
                            String prodStatus = document.getString("status");

                            product = new Product(prodId,prodTitle,prodWebsite,imageUrl,dateAdded,prodPrice);
                            product.setProdStatus(prodStatus);

                            productList.add(product);
                        }

                        // Display analytics:
                        productsDisplay.setText(String.format("%d", countProducts()));
                        boughtDisplay.setText(String.format("%d", countBoughtProducts()));
                        discardedDisplay.setText(String.format("%d", countDiscardedProducts()));

                        moneySpentDisplay.setText(String.format("%.2f", calculateMoneySpent()));
                        moneySavedDisplay.setText(String.format("%.2f", calculateMoneySaved()));

                        Log.d(TAG, "Array Items => " + productList.size());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });



        return view;
    }

    private double calculateMoneySaved() {
        double amount = 0;
        for(Product product: productList) {
            if(product.getProdStatus().equals("discarded")) {
                amount += product.getProdPrice();
            }
        }
        return amount;
    }

    private double calculateMoneySpent() {
        double amount = 0;
        for(Product product: productList) {
            if(product.getProdStatus().equals("purchased")) {
                amount += product.getProdPrice();
            }
        }
        return amount;
    }

    private int countBoughtProducts() {
        int count = 0;
        for(Product product: productList) {
            if(product.getProdStatus().equals("purchased")) {
                count += 1;
            }
        }
        return count;
    }

    private int countDiscardedProducts() {
        int count = 0;
        for(Product product: productList) {
            if(product.getProdStatus().equals("discarded")) {
                count += 1;
            }
        }
        return count;
    }

    private int countProducts() {
        return productList.size();
    }


}