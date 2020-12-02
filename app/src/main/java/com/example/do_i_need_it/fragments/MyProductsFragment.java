package com.example.do_i_need_it.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.do_i_need_it.R;
import com.example.do_i_need_it.model.Product;
import com.example.do_i_need_it.recyclerview.ProductAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        List<Product> productList = new ArrayList<>();
        FirebaseAuth fAuth;
        FirebaseUser fUser;
        FirebaseFirestore fStore;

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        assert fUser != null;
        Query query = fStore.collection("products").whereEqualTo("prod_owner", fUser.getEmail());
        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Product product = null;

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            String prodId = document.getId();
                            String prodTitle = document.getString("prod_title");
                            String prodWebsite = document.getString("prod_url");
                            String imageUrl = document.getString("image_path");
                            String dateAdded = document.getString("date_added");
                            String prodPrice = document.getString("prod_price");

                            product = new Product(prodId,prodTitle,prodWebsite,imageUrl,dateAdded,prodPrice);

                            productList.add(product);

                            ProductAdapter prodAdapter = new ProductAdapter(getActivity(),productList);
                            recyclerView.setAdapter(prodAdapter);

                        }
                        Log.d(TAG, "Array Items => " + productList.size());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });





        return view;
    }
}