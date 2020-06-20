package com.example.future.healthapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.future.healthapp.R;

import androidx.fragment.app.Fragment;

public class DataSensors  extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.data_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.datatest);
        return rootView;
    }
}
