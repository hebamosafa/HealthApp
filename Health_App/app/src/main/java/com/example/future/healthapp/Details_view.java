package com.example.future.healthapp;

import android.os.Bundle;
import android.util.Log;

import com.example.future.healthapp.Adaptors.DynamicPagerAdapter;
import com.example.future.healthapp.Fragments.DataSensors;
import com.example.future.healthapp.Fragments.FormResult;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Details_view extends AppCompatActivity {
    ViewPager mViewPager;
    static String done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_view);
        long id=getIntent().getLongExtra("HEBA",0);
        ArrayList<String> mlist= doctor_view.get();
        int p=(int)id;
        done=mlist.get(p);
        Log.d("HEBA", "onCreate: "+done);
        Log.d("HEBA", "onCreate: "+id);
        // Create the adapter that will return a fragment for each of the three
// primary sections of the activity.
        List<String> pageTitles = new ArrayList<String>() {{
            add("Form");
            add("Data");
        }};

// this information can come from a database or web service
        List<Class> fragmentTypes = new ArrayList<Class>() {{
            add(FormResult.class);
            add(DataSensors.class);
        }};

        FragmentPagerAdapter adapter =
                new DynamicPagerAdapter(getSupportFragmentManager(), pageTitles, fragmentTypes);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
    static public String d(){return done;}
}