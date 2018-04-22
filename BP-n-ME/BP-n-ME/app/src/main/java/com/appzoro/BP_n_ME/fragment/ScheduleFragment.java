package com.appzoro.BP_n_ME.fragment;


import android.content.Context;
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
import com.appzoro.BP_n_ME.model.PillComparator;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.BleManager;
import com.appzoro.BP_n_ME.util.util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;


public class ScheduleFragment extends Fragment {
    private final static String TAG = "Schedule Fragment";
    ArrayList<Pill> pills;
    ListView listView;
    private static CustomAdapter adapter;
    private DatabaseReference mDatabase;
    FirebaseUser user;
    MedasolPrefs prefs;
    ArrayList<String> medication, frequency;

    String UID;
//    private ArrayList<ArrayList<String>> aObject;



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

        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        medication = new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[", "").replace("]", "").replace(" ", "").split(",")));
        frequency =  new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));

        // List view test
        pills = new ArrayList<>();
        listView=(ListView)view.findViewById(R.id.list);
//        pills.add(new Pill("Aspirin",10, 0,1));
//        pills.add(new Pill("Pain killer",15, 50,1));
//        pills.add(new Pill("Hypnotic",22, 30,1));
        adapter= new CustomAdapter(pills,getActivity());
        listView.setAdapter(adapter);
        readFromFile(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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
                                    String deletedPillName = pills.get(position).getName();
                                    deleteLine(position, getActivity());
//                                    getActivity().invalidateOptionsMenu();
                                    adapter.notifyDataSetChanged();
                                    for (Pill p : pills) {
                                        if(p.getName().equals(deletedPillName)) {
                                            int ind = medication.indexOf(p.getName());
                                            String temp = frequency.get(ind);
                                            String res = "Daily";
                                            if(temp.equals("Twice daily")) {
                                                res = "Daily";
                                            } else if (temp.equals("Three times daily")) {
                                                res = "Twice daily";
                                            } else if (temp.equals("Four times per day")) {
                                                res = "Three times daily";
                                            }
                                            frequency.set(ind, res);
                                            prefs.setFrequency(frequency);
                                            return;
                                        }
                                    }
                                    medication.remove(deletedPillName);
                                    prefs.setMeds(medication);
                                    writeToDatabase();
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

//        getActivity().invalidateOptionsMenu();
        return view;
    }

    private void readFromFile(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
//        String filename = UID + ".txt";
        pills.clear();
        String filename = "b.txt";
        File file = new File(context.getFilesDir(), filename);
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {

                String[] stringArray = strLine.split(", ");
                Log.d("DEBUG",stringArray[0]+stringArray[1]+stringArray[2]+stringArray[3]);
//                String name = stringArray[0];
//                int hour = Integer.parseInt(stringArray[1]);
//                int minute = Integer.parseInt(stringArray[2]);
//                int amount = Integer.parseInt(stringArray[3]);
//                Log.d("DEBUG",name+hour+minute+amount);
                pills.add(new Pill(stringArray[0], Integer.parseInt(stringArray[1]),
                        Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3])));
//                adapter.notifyDataSetChanged();
            }
            Log.d("DEBUG",Integer.toString(pills.size()));
            in.close();
            fis.close();
        }
        catch (Exception e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
        Collections.sort(pills,new PillComparator());
        Log.d("DEBUG","Sorted write start");
        writePillsToFile(file);
    }

    private void deleteLine(int position, Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
//        String filename = UID + ".txt";
        pills.clear();
        String filename = "b.txt";
        File file = new File(context.getFilesDir(), filename);
        try {
            Log.d("DEBUG","Delete read start");
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int i = 0;
            while ((strLine = br.readLine()) != null) {
                String[] stringArray = strLine.split(", ");
                if (i != position) {
                    Log.d("DEBUG",Integer.toString(i));
                Log.d("DEBUG",stringArray[0]+stringArray[1]+stringArray[2]+stringArray[3]);
//                String name = stringArray[0];
//                int hour = Integer.parseInt(stringArray[1]);
//                int minute = Integer.parseInt(stringArray[2]);
//                int amount = Integer.parseInt(stringArray[3]);
//                Log.d("DEBUG",name+hour+minute+amount);
                    pills.add(new Pill(stringArray[0], Integer.parseInt(stringArray[1]),
                            Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[3])));
                }
//                adapter.notifyDataSetChanged();
                i++;
            }
            in.close();
            fis.close();
        }
        catch (Exception e)
        {
            Log.e("Exception", "File delete read failed: " + e.toString());
        }
        Log.d("DEBUG","Delete write start");
        writePillsToFile(file);
    }

    private void writePillsToFile(File file) {
        // write pills to text
        try {
            FileOutputStream fos = new FileOutputStream(file,false);
            for (int i = 0; i < pills.size(); i++) {
                StringBuilder text = new StringBuilder();
                String concatenate = ", ";
                text.append(pills.get(i).getName() + concatenate);
                text.append(pills.get(i).getHour());
                text.append(concatenate);
                text.append(pills.get(i).getMinute());
                text.append(concatenate);
                text.append(pills.get(i).getAmount());
                text.append(concatenate);
                text.append(System.getProperty("line.separator"));
                Log.d("DEBUG",text.toString());
                fos.write(text.toString().getBytes());
            }
            fos.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_add_item, menu);
        //menu.findItem(R.id.action_add).setVisible(!disableButtonFlag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add:
                //startActivityForResult(new Intent(getActivity(), AddMedicine.class), 1);
//                getFragmentManager().beginTransaction().add
//                        (R.id.Fragment_frame_main_activity, new AddMedicineFragment()).addToBackStack("AddMedicineFragment").commit();
                //disableButtonFlag = true;
                getFragmentManager().beginTransaction().replace
                    (R.id.Fragment_frame_main_activity, new AddMedicineFragment()).addToBackStack("AddMedicineFragment").commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeToDatabase() {
        for (int i = 0; i < medication.size(); i++) {
            String userMeds = medication.get(i);
            Log.e("meds",medication.get(i) + frequency.get(i).trim());
            mDatabase.child("app").child("users").child(user.getUid()).child("medicine").child(userMeds).child("name").setValue(medication.get(i).trim());
            mDatabase.child("app").child("users").child(user.getUid()).child("medicine").child(userMeds).child("frequency").setValue(frequency.get(i).trim());
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
//
//    @Override
//    public void onResume() {
//        Log.e("DEBUG", "onResume of Schedule Fragment");
//        //disableButtonFlag = false;
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            Log.e("DEBUG", "Bundle");
//            String name = bundle.getString("EXTRA_NAME");
//            int hour = bundle.getInt("EXTRA_HOUR");
//            int minute = bundle.getInt("EXTRA_MINUTE");
//            int amount = bundle.getInt("EXTRA_AMOUNT");
//            util.toast(getActivity(),name+hour+minute+amount);
//            pills.add(new Pill(name, hour, minute, amount));
//            adapter.notifyDataSetChanged();
//        }
//        super.onResume();
//    }

}
