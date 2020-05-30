package com.example.future.healthapp;

import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ScrollingActivity extends AppCompatActivity {
    private Menu menu;
    FirebaseDatabase database;
    private ChildEventListener mChildEventListener;
    DatabaseReference docRef ;
    String uid;
    private static final String TAG = "HEBA";
    ArrayList<EditText> questions=new ArrayList<EditText>();
    FloatingActionButton upload;
    String Myform="";
    int id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        uid=getIntent().getStringExtra("HEBA");
        Log.d(TAG, "onCreate: "+uid);
        database=FirebaseDatabase.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final LinearLayout form = (LinearLayout)findViewById(R.id.mform);
        setSupportActionBar(toolbar);
        upload=(FloatingActionButton) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               for(int u=0;u<id;u++){
                   EditText et=(EditText) findViewById(u+8);
                   Myform=Myform.concat(et.getText().toString()+'/');
               }
               test_push();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = new EditText(ScrollingActivity.this);
                et.setHint("Type your question");
                et.setMinLines(1);
                et.setMaxLines(3);
                et.setId(id+8);

                form.addView(et);
                questions.add(et);
                id++;
                /////////////////////////////////
                final RadioButton[] rb = new RadioButton[3];
                RadioGroup rg = new RadioGroup(ScrollingActivity.this); //create the RadioGroup
                rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
                for(int i=0; i<3; i++){
                    rb[i]  = new RadioButton(ScrollingActivity.this);
                    rb[i].setText(" option " + i);
                    //rb[i].setId(i);
                    rg.addView(rb[i]); //the RadioButtons are added to the radioGroup instead of the layout
                }
                rg.setPadding(6,0,0,5);
                form.addView(rg);//you add the whole RadioGroup to the layout
            }
        });

        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption(R.id.action_upload);
                } else if (isShow) {
                    isShow = false;
                    hideOption(R.id.action_upload);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        hideOption(R.id.action_upload);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_upload) {
            test_push();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
    void test_push(){

        docRef=database.getReference().child("Forms").child(uid);
        docRef.push().setValue(Myform);
        Myform="";

    }
}