package com.example.ben.test_version_2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemMoreFragment extends Fragment {


    public ItemMoreFragment() {
        // Required empty public constructor
    }
    public static ItemMoreFragment newInstance() {
        ItemMoreFragment fragment = new ItemMoreFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_more, container, false);
    }

}
