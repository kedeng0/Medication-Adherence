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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ItemScheduleFragment extends Fragment {

    ArrayList<Pill> pills;
    ListView listView;
    private static CustomAdapter adapter;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_item_schedule, container, false);
        // List view test
        listView=(ListView)view.findViewById(R.id.list);
        pills= new ArrayList<>();
        pills.add(new Pill("Aspirin",10, 0,1));
        pills.add(new Pill("Pain killer",15, 50,1));
        pills.add(new Pill("Hypnotic",22, 30,1));
        adapter= new CustomAdapter(pills,getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pill pill= pills.get(position);
                Utils.toast(getActivity(),Integer.toString(position));
            }
        });
        // ************
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
                String name = extras.getString("EXTRA_NAME");
                int hour = extras.getInt("EXTRA_HOUR");
                int minute = extras.getInt("EXTRA_MINUTE");
                int amount = extras.getInt("EXTRA_AMOUNT");
                pills.add(new Pill(name,hour, minute,amount));
                adapter.notifyDataSetChanged();
            }
        }
    }

}
