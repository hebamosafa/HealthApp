package com.example.future.healthapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.future.healthapp.Adaptors.DynamicPagerAdapter;
import com.example.future.healthapp.Fragments.DataSensors;
import com.example.future.healthapp.Fragments.FormResult;
import com.example.future.healthapp.Utils.IntenetConn;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class Details_view extends AppCompatActivity {
    ViewPager mViewPager;
    static String done;
    static String cus_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_view);
        if(!(IntenetConn.check_internet(this))){
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }
        long id=getIntent().getLongExtra("HEBA",0);
        ArrayList<String> mlist= doctor_view.get();
        int p=(int)id;
        done=mlist.get(p);
        mlist=doctor_view.get2();
        cus_ID=mlist.get(p);
//        Log.d("HEBA", "onCreate: "+done);
  //      Log.d("HEBA", "onCreate: "+cus_ID);

        List<String> pageTitles = new ArrayList<String>() {{
            add("Data");
            add("Form");
        }};

        List<Class> fragmentTypes = new ArrayList<Class>() {{
            add(DataSensors.class);
            add(FormResult.class);

        }};

        FragmentPagerAdapter adapter =
                new DynamicPagerAdapter(getSupportFragmentManager(), pageTitles, fragmentTypes);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    static public String d(){return done;}
    static public String CusID(){return cus_ID;}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.patient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_reload) {
            Intent i = new Intent(Details_view.this, Details_view.class);
            finish();
            overridePendingTransition(0, 0);
            startActivity(i);
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}