package com.example.future.healthapp.Band_Data;

import android.os.AsyncTask;
import android.util.Log;
import com.example.future.healthapp.Utils.preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class generate_token extends AsyncTask<Void, Void, String> {


    @Override


    protected String doInBackground(Void... params) {
      //  Log.d("TAG", "doInBackground: "+params[0]);

        try {
            get_token();

        } catch (Exception e) {


            Log.e("error", e.getMessage(), e);


            return "Token Not generated";


        }


        return "Token generated";


    }



    void get_token(){
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("username" , "heba.mostafa8297@gmail.com");
            post_dict.put("password", "01111474910Aa");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String JsonResponse = null;
        String JsonDATA = String.valueOf(post_dict);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL("http://64.225.47.65:8080/api/auth/login");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            // json data
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.

            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.

            }
            JsonResponse = buffer.toString();


            JSONObject Jobject = new JSONObject(JsonResponse);
            String h = Jobject.getString("token");
            Log.d("TAG","run :Generate token"+h);
            preferences mpref=new preferences(getApplicationContext());
            mpref.write_pref_s(h,"token");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                   // Log.e("TAG", "Error closing stream", e);
                }
            }
        }
    }
}
