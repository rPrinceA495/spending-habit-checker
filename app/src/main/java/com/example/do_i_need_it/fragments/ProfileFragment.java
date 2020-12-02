package com.example.do_i_need_it.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.do_i_need_it.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

        FirebaseAuth fAuth;
        FirebaseUser fUser;
        FirebaseFirestore fStore;

        // Personal Info
        profile = Objects.requireNonNull(view.findViewById(R.id.profile));
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

        assert fUser != null;
        emailDisplay.setText(fUser.getEmail());

        DocumentReference docRef = fStore.collection("users").document(Objects.requireNonNull(fUser.getEmail()));
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                fullNameDisplay.setText(value.getString("full_name"));
                phoneNumberDisplay.setText(value.getString("phone_number"));
                dobDisplay.setText(value.getString("dob"));
                countryDisplay.setText(value.getString("country"));
            }
        });

        return view;
    }
}