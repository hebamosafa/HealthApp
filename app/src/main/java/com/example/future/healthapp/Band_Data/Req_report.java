package com.example.future.healthapp.Band_Data;

import android.os.AsyncTask;
import android.util.Log;

import com.example.future.healthapp.Fragments.DataSensors;
import com.example.future.healthapp.Utils.preferences;
import com.example.future.healthapp.data_patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.text.ParseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Req_report extends AsyncTask<String, Void, String> {

    @Override

    protected String doInBackground(String... params) {

        try {

            GetReport(params[0],params[1],params[2],params[3]);

        } catch (Exception e) {


            Log.e("error", e.getMessage(), e);


            return "HTTP Not Sent";


        }


        return "HTTP Sent";


    }

    public  void  GetReport(String dev ,String key,String MN,String pd) throws ParseException {
        preferences mpref=new preferences(getApplicationContext());
        String token="Bearer "+mpref.read_pref_s("token");
        OkHttpClient client = new OkHttpClient();
        ////////////
        Long tsLong = System.currentTimeMillis();
        String now = tsLong.toString();
        String date;
        ///////////////

        Log.d("TAG", "dev: "+dev);
        if(mpref.read_pref_s("Date").equals("0")){
            date="1597874400000";
            Log.d("TAG", "GetReport:NULL ");

        }
        else{
          date=mpref.read_pref_s("Date");
            Log.d("TAG", "GetReport:not ");
        }
        System.out.println("Today is " +now);
        long interval=tsLong-Long.parseLong(date);
        //Log.d("TAG", "GetReport: "+String.valueOf(interval));
        /////////////////////
        String url = "http://64.225.47.65:8080/api/plugins/telemetry/DEVICE/"+dev+"/values/timeseries?keys="+key+"&startTs="+date+"&endTs="+now+"&agg="+MN+"&interval="+String.valueOf(interval);
        try {
            Request request;
                request = new Request.Builder()
                        .url(url)
                        .method("GET",null)
                        .addHeader("X-Authorization", token)
                        .build();
                Response response = client.newCall(request).execute();
                final String myResponse = response.body().string();
            Log.d("TAG", "run:Report " + myResponse);
            JSONObject jsonObject = new JSONObject(myResponse);
            JSONArray results = jsonObject.getJSONArray(key);
            JSONObject jo = results.getJSONObject(0);

            String h = jo.getString("value");

            Log.d("TAG", "run: " + h);
            DataSensors ds=new DataSensors();


            if(MN.equals("MIN")) {
                //d for doctor
                //p for patient
                if(pd.equals("d"))
                    ds.send(h + "-");
                else
                    data_patient.send(h + "-");
            }
            else
            if(pd.equals("d"))
                ds.send(h + "/");
            else
                data_patient.send(h + "/");

            }  catch (IOException | JSONException e) {
            e.printStackTrace();
//            Log.d("TAG", "GetReport: "+e);
        }
    }
}