package com.example.future.healthapp.Gmail;

import android.os.AsyncTask;


import android.util.Log;

import com.example.future.healthapp.Gmail.GMailSender;


/**


 * Created by GsolC on 2/24/2017.


 */





public class LongOperation extends AsyncTask<Void, Void, String> {


    @Override


    protected String doInBackground(Void... params) {


        try {




            GMailSender sender = new GMailSender("Heba Mostafa", "01111474910");


            sender.sendMail("This is a testing mail",


                    "This is Body of testing mail","heba.microsoft@gmail.com",


                    "heba.mostafa20497@gmail.com")                   ;





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