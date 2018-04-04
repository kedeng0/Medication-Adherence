package com.example.ben.test_version_2;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


public class ItemScheduleFragment extends Fragment {
    String hour;
    String min;
    String medicine_type;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public ItemScheduleFragment() {
        // Required empty public constructor
    }
    public static ItemScheduleFragment newInstance() {
        ItemScheduleFragment fragment= new ItemScheduleFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    // UI interface first create
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_item_schedule, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.schedule_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_add_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivityForResult(new Intent(getActivity(), AddMedicine.class), 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                hour = extras.getString("EXTRA_HOUR");
                min = extras.getString("EXTRA_MINUTE");
                medicine_type = extras.getString("EXTRA_MEDICINE");
                Toast.makeText(getActivity(),hour+" "+min+" "+medicine_type,Toast.LENGTH_SHORT).show();
            }
        }
    }

}
