package com.example.prakharagarwal.fingerlickingawesome;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapter mviewPagerAdapter;
    ViewPager mviewPager;
    TabLayout mtabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mviewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mviewPagerAdapter.addFragment(new DineoutFragment(), "Dineout");
        mviewPager = (ViewPager) findViewById(R.id.viewpager);
        mtabLayout = (TabLayout) findViewById(R.id.tablayout);

        mviewPager.setAdapter(mviewPagerAdapter);
        mtabLayout.setupWithViewPager(mviewPager);

    }
}
