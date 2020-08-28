package com.example.future.healthapp.Band_Data;



import android.os.AsyncTask;
import android.util.Log;
import com.example.future.healthapp.Utils.FriendlyMessage;
import com.example.future.healthapp.Utils.preferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Req_customer extends AsyncTask<String, Void, String>{
    @Override

    protected String doInBackground(String... params) {

        try {


            // CALL GetText method to make post method call
            if(params.length>1){
            GetText(params[0],params[1],params[2],params[3]);}
            else{GetText(params[0],null,null,null);}
        }

        catch (Exception e) {


            Log.e("error", e.getMessage(), e);


            return "HTTP Not Sent";


        }


        return "HTTP Sent";


    }





    @Override


    protected void onPostExecute(String result) {





        Log.e("LongOperation",result+"");


    }





    @Override


    protected void onPreExecute() {


    }





    @Override


    protected void onProgressUpdate(Void... values) {


    }
    public  void  GetText(String murl,String name,String email,String uid) {
        preferences mpref=new preferences(getApplicationContext());
        String token="Bearer "+mpref.read_pref_s("token");
        OkHttpClient client = new OkHttpClient();
        JSONObject parameter=null;
        String url = murl;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        if (murl.equals("http://64.225.47.65:8080/api/customer")) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("name", name);
            params.put("title", name+"lazouem");
            params.put("email", email);
            parameter = new JSONObject(params);
        }
        try {
            Request request;
            if(parameter!=null) {
                RequestBody body = RequestBody.create(JSON, parameter.toString());

                //Log.d("TAG", "GetText: "+formBody);
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("X-Authorization", token)
                        .build();
                Response response = client.newCall(request).execute();
                final String myResponse = response.body().string();

                Log.d("TAG", "run:Request_Customer " + myResponse);
                JSONObject Jobject = new JSONObject(myResponse);
                String h = Jobject.getJSONObject("id").getString("id");
    //            Log.d("TAG", "run: " + h);
                mpref.write_pref_s(h,"cusID");

                String mDoctor=mpref.read_pref_s("mDoctor");
      //          Log.d("TAG", "Devices: "+mDoctor);
                DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference().child("Relation").child(mDoctor);
                final FriendlyMessage friendlyMessage = new FriendlyMessage(uid, name,h);
                databaseref.push().setValue(friendlyMessage);
                Devices mdev=new Devices();
                mdev.getid(h);

            }
            else{
                RequestBody formBody = new FormBody.Builder()
                        .add("password", "01111474910Aa")
                        .build();
                request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .addHeader("X-Authorization", token)
                        .build();
                Response response = client.newCall(request).execute();
                final String myResponse = response.body().string();
//                Log.d("TAG", "run: " + myResponse);


            }

        } catch (IOException | JSONException e) {
  //          Log.d("TAG", "GetText: Error: " + e.getMessage());
        }


    }
}