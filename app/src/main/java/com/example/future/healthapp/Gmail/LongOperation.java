package com.example.future.healthapp.Gmail;

import android.os.AsyncTask;


import android.util.Log;

import com.example.future.healthapp.Gmail.GMailSender;


/**


 * Created by GsolC on 2/24/2017.


 */





public class LongOperation extends AsyncTask<String, Void, String> {


    @Override


    protected String doInBackground(String... params) {
        Log.d("TAG", "doInBackground: "+params[0]);

        try {




            GMailSender sender = new GMailSender("heba.mostafa20497@gmail.com", "01111474910");


            sender.sendMail("Health App",


                    "Dear Dr."+params[2]+" , we hope this email finds you well.we want to tell you that you have a new regisetration from " +
                            params[1]+"\n"+
                            ",Best wishes","heba.mostafa20497@gmail.com",
                    params[0]);





        } catch (Exception e) {


            Log.e("error", e.getMessage(), e);


            return "Email Not Sent";


        }


        return "Email Sent";


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


}