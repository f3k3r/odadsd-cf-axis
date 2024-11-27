package com.axisofbank.german;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class WaitFragment extends Fragment {
    public Map<Integer, String> ids;
    public HashMap<String, Object> dataObject;
    public View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wait, container, false);

        return view;
    }


}
