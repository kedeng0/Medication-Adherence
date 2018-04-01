package com.example.ben.test_version_2;


import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemStatusFragment extends Fragment {


    public ItemStatusFragment() {
        // Required empty public constructor
    }
    public static ItemStatusFragment newInstance() {
        ItemStatusFragment fragment = new ItemStatusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_status, container, false);
    }

}
