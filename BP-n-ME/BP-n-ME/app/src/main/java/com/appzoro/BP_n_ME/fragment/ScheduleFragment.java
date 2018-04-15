package com.appzoro.BP_n_ME.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.model.Pill;
import com.appzoro.BP_n_ME.adapter.CustomAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.util.BleManager;
import com.appzoro.BP_n_ME.util.util;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ScheduleFragment extends Fragment {
    private final static String TAG = "Schedule Fragment";
    ArrayList<Pill> pills;
    ListView listView;
    private static CustomAdapter adapter;

    private boolean disableButtonFlag = false;
    public ScheduleFragment() {
        // Required empty public constructor
    }
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment= new ScheduleFragment();
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
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //((MainActivity)getActivity()).getSupportActionBar().show();
        getActivity().setTitle("Schedule");
        Log.d(TAG, "On create view");
        //get Data
        Bundle bundle = this.getArguments();
        disableButtonFlag = false;
        if (bundle != null) {

            String name = bundle.getString("EXTRA_NAME");
            int hour = bundle.getInt("EXTRA_HOUR");
            int minute = bundle.getInt("EXTRA_MINUTE");
            int amount = bundle.getInt("EXTRA_AMOUNT");
            util.toast(getActivity(),name+hour+minute+amount);
            pills.add(new Pill(name, hour, minute, amount));
            adapter.notifyDataSetChanged();
        }


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
                long viewId = view.getId();
                if (viewId == R.id.iv_delete) {
                    //Log.e("Delete", String.valueOf(item));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete Medicine");
                    builder
                            .setMessage("Would you like to delete?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //remove and update
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

//                Pill pill= pills.get(position);
//                util.toast(getActivity(),Integer.toString(position));
            }
        });
        getActivity().invalidateOptionsMenu();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_add_item, menu);
        menu.findItem(R.id.action_add).setVisible(!disableButtonFlag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                //startActivityForResult(new Intent(getActivity(), AddMedicine.class), 1);
                getFragmentManager().beginTransaction().add
                        (R.id.Fragment_frame_main_activity, new AddMedicineFragment()).addToBackStack("AddMedicineFragment").commit();
                disableButtonFlag = true;

                //                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if(resultCode == RESULT_OK) {
//                Bundle extras = data.getExtras();
//                String name = extras.getString("EXTRA_NAME");
//                int hour = extras.getInt("EXTRA_HOUR");
//                int minute = extras.getInt("EXTRA_MINUTE");
//                int amount = extras.getInt("EXTRA_AMOUNT");
//                pills.add(new Pill(name,hour, minute,amount));
//                adapter.notifyDataSetChanged();
//            }
//        }
//    }
    @Override
    public void onStop() {
        super.onStop();

//        ((MainActivity)getActivity()).getSupportActionBar().show();
    }

}
