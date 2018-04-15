package com.appzoro.BP_n_ME.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.mListAdapter;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Appzoro_ 5 on 9/15/2017.
 */

public class MedicationFragment extends Fragment {
    View view;
    ListView medsList;
    ArrayList<String> medArray = new ArrayList<>();
    ArrayList<String> medFrequency = new ArrayList<>();
    String frequency = "";
    String UID;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_medication, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Medication");
        init();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        medsList = view.findViewById(R.id.medsList);
        UID = prefs.getUID();
        getMeds();
    }

    public void getMeds() {
        progress_d.showProgress(getActivity());
        medArray.clear();
        mDatabase.child("app").child("users").child(UID).child("medicine").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            medArray.add(medicine.getKey());
                        }
                        prefs.setMeds(medArray);
                        getMedFrequency(medArray);
                        //Log.e("medArray size",""+ medArray.size());
                        if (medArray.size() == 0){
                            Log.e("medFrequency/////", medFrequency.toString());
                            //prefs.setFrequency(medFrequency);
                            setMedsAlert(medArray);
                            progress_d.hideProgress();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

    public void getMedFrequency(final ArrayList<String> medArray) {
        for (int i = 0; i < medArray.size(); i++) {
            medFrequency.clear();
            final int finalI = i;
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medArray.get(i)).child("frequency").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.e("currentMed", medArray.get(finalI));
                            //Log.e("reading", dataSnapshot.getValue().toString());
                            medFrequency.add(dataSnapshot.getValue().toString().trim());
                            //Log.e("medFrequency", medFrequency.toString());
                             if (finalI == medArray.size()-1){
                                //Log.e("medFrequency/////", medFrequency.toString());
                                prefs.setFrequency(medFrequency);
                                setMedsAlert(medArray);
                                progress_d.hideProgress();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }

    public void setMedsAlert(final ArrayList<String> patientmeds) {
        String[] mArray = new String[patientmeds.size()];
        mArray = patientmeds.toArray(mArray);

        mListAdapter medsAdapter = new mListAdapter(getActivity(), mArray);
        medsList.setAdapter(medsAdapter);
        final String[] finalMArray = mArray;

        medsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                long viewId = view.getId();
                if (viewId == R.id.iv_freq) {
                    setFrequencyClick(view, position, patientmeds);
                }
                if (viewId == R.id.iv_delete) {
                    DeleteMedsClick(view, position, finalMArray);
                }
            }
        });
    }

    private void DeleteMedsClick(View view, final int position, final String[] finalMArray) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Delete Medication");
        builder
                .setMessage("Would you like to delete '" + finalMArray[position] + "'?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("app").child("users").child(UID).child("medicine").child(finalMArray[position]).child("frequency").removeValue();
                        mDatabase.child("app").child("users").child(UID).child("medicine").child(finalMArray[position]).child("name").removeValue();
                        mDatabase.child("app").child("users").child(UID).child("medicine").child(finalMArray[position]).removeValue();

                        getMeds();
                        dialog.cancel();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setFrequencyClick(View view, final int position, final ArrayList<String> patientmeds/*, ArrayList<String> medsfreqs*/) {
        LinearLayout layout = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(param);
        layout.setOrientation(LinearLayout.VERTICAL);
        Spinner spinner = new Spinner(view.getContext());
        String[] string_list = getResources().getStringArray(R.array.frequency);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, string_list);
        spinner.setAdapter(adapter);
        if (medFrequency.get(position).equals("Daily")) {
            spinner.setSelection(0);
        } else if (medFrequency.get(position).equals("Twice daily")) {
            spinner.setSelection(1);
        }  else if (medFrequency.get(position).equals("Three times daily")) {
            spinner.setSelection(2);
        } else {
            spinner.setSelection(3);
        }
        spinner.setGravity(Gravity.CENTER);
        spinner.setPadding(20, 8, 12, 8);
        spinner.setBackgroundResource(R.drawable.spinner_border);
        spinner.setPopupBackgroundResource(R.color.white);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(75, 20, 50, 0); //pass int values for left,top,right,bottom
        spinner.setLayoutParams(params);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                frequency = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Change Frequency");
        builder.setView(spinner);
        layout.addView(spinner);
        builder.setView(layout);
        builder
                .setMessage("Would you like to change frequency : '" + patientmeds.get(position) + "'?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mDatabase.child("app").child("users").child(UID).child("medicine").child(patientmeds.get(position)).child("frequency").removeValue();
                        mDatabase.child("app").child("users").child(UID).child("medicine").child(patientmeds.get(position)).child("frequency").setValue(frequency);

                        getMeds();
                        dialog.cancel();
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
}
