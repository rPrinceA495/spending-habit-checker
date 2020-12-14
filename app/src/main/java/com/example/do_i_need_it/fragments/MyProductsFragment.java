package com.example.do_i_need_it.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.service.carrier.CarrierMessagingService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_i_need_it.MainActivity;
import com.example.do_i_need_it.ProductDetailsActivity;
import com.example.do_i_need_it.R;
import com.example.do_i_need_it.model.Product;
import com.example.do_i_need_it.recyclerview.ProductAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProductsFragment extends Fragment implements ProductAdapter.OnProductListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProductAdapter prodAdapter;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    List<Product> productList = new ArrayList<>();

    public MyProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProductsFragment newInstance(String param1, String param2) {
        MyProductsFragment fragment = new MyProductsFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);

        final String TAG = "TAG";
        RecyclerView recyclerView;
        ProgressBar productsLoader;
        TextView noProducts;

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        productsLoader = view.findViewById(R.id.products_loader);
        noProducts = view.findViewById(R.id.no_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        productsLoader.setVisibility(View.VISIBLE);

        assert fUser != null;
        Query query = fStore.collection("products").whereEqualTo("prod_owner", fUser.getEmail()).whereNotEqualTo("status", "discarded");
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
                        // Conditionally render recyclerview
                        if(productList.size() == 0) {
                            productsLoader.setVisibility(View.GONE);
                            noProducts.setVisibility(View.VISIBLE);
                        }else {
                            noProducts.setVisibility(View.INVISIBLE);
                            // Add Products to recyclerview
                            prodAdapter = new ProductAdapter(getActivity(), productList, this);
                            productsLoader.setVisibility(View.GONE);
                            recyclerView.setAdapter(prodAdapter);
                        }
                        Log.d(TAG, "Array Items => " + productList.size());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        productsLoader.setVisibility(View.GONE);
                    }
                });

        // Declare ItemTouchHelper and attach it to recyclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }



    // Swipe Gesture Functionality throughItemTouchHelper:
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            DocumentReference ref = fStore.collection("products").document(productList.get(position).getProdId());

            // Find direction of swipe:
            switch(direction) {
                case ItemTouchHelper.LEFT:
                    productList.remove(position);
                    prodAdapter.notifyItemRemoved(position);
                    // Change product status to discarded in firestore:
                    ref.update("status", "discarded" ).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Product discarded.", Toast.LENGTH_SHORT).show();
                    });
                    break;
                case ItemTouchHelper.RIGHT:
                    // Change Product status to purchased in firestore:
                    ref.update("status", "purchased" ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Back to My Products List
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.putExtra("action", "product status change");
                            startActivity(intent);
                            Toast.makeText(getActivity(), "Product marked as purchased.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }

        }
    };

    @Override
    public void onProductClick(int position) {
        Intent openProduct = new Intent(getActivity(), ProductDetailsActivity.class);
        openProduct.putExtra("product", productList.get(position));
        startActivity(openProduct);
    }
}