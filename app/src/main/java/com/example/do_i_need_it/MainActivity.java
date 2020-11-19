package com.example.do_i_need_it;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.do_i_need_it.fragments.HelpFragment;
import com.example.do_i_need_it.fragments.MyProductsFragment;
import com.example.do_i_need_it.fragments.ProfileFragment;
import com.example.do_i_need_it.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        }
    };
}