package com.appzoro.BP_n_ME.database;

import android.content.Context;

import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ownwer on 9/4/2017.
 */

public class GetFireBaseData {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    MedasolPrefs prefs;
    Context context;
    String UID;

    public GetFireBaseData(Context context) {

        this.context = context;
        prefs = new MedasolPrefs(context);

    }


    public void getRegisterDate() {
        UID = prefs.getUID();
        mDatabase.child("storage").child("users").child(UID).child("registrationdate").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String regdate = dataSnapshot.getValue().toString();
                        //Log.e("Register date", regdate);
                        prefs.setRegistrationDate(regdate);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


}
