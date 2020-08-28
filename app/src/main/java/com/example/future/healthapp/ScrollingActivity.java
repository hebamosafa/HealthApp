package com.example.future.healthapp;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.future.healthapp.Utils.IntenetConn;
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
    Bundle geted=null;
    LinearLayout form;
    public ScrollingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        uid=getIntent().getStringExtra("HEBA");
        Log.d(TAG, "onCreate: "+uid);
        database=FirebaseDatabase.getInstance();
        form = (LinearLayout)findViewById(R.id.mform);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = new EditText(ScrollingActivity.this);
                et.setHint("Type your question");
                et.setMinLines(1);
                et.setMaxLines(3);
                et.setId(id+100);

                form.addView(et);
                questions.add(et);
                id++;
                /////////////////////////////////
            }
        });
    }
    private void land_scape(Bundle geted) {
        for(int j=0;j<geted.size();j++){
            String h=geted.getString(String.valueOf(j));
            EditText et = new EditText(ScrollingActivity.this);
            et.setHint("Type your question");
            et.setMinLines(1);
            et.setMaxLines(3);
            et.setId(id+100);
            et.setText(h);
            form.addView(et);
            questions.add(et);
            id++;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu=menu;
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_upload) {
            test_push();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

  /*  private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }*/
    void test_push(){
        if(!(IntenetConn.check_internet(this))){
            Toast.makeText(this,"Sorry,There's No Internet Connection",Toast.LENGTH_LONG).show();
        }
        else{
        for(int u=0;u<id;u++){
            EditText et=(EditText) findViewById(u+100);
            if(!(et.getText().toString().equals(""))){
            Myform=Myform.concat(et.getText().toString()+'/');}
        }
        docRef=database.getReference().child("Forms").child(uid);
        if (!(Myform.equals(""))) {
            docRef.push().setValue(Myform);
            Myform = "";
            InputMethodManager mgr = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null){
                mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);}
            Toast.makeText(this,"The Form's been uploaded Successfully",Toast.LENGTH_SHORT).show();
            finish();

        }
        else{
            Toast.makeText(this,"Nothing to be uploaded !",Toast.LENGTH_SHORT).show();
        }
    }}
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Bundle m_array=new Bundle();
            for(int i=0;i<id;i++){
                EditText editText=(EditText) findViewById(i+100);
                if(editText.getText()!=null)
                {String m=editText.getText().toString();
                m_array.putString(String.valueOf(i),m);
            }
            savedInstanceState.putBundle("Added",m_array);}}
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        geted =savedInstanceState.getBundle("Added");
        if(geted!=null){
       //     Log.d(TAG, "onCreate: "+"ENTERRRRRRRRRRRRRRRR");
            land_scape(geted);
        }
    }
}