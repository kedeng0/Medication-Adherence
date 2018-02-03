package com.appzoro.BP_n_ME.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.activity.PatientMonitor;
import com.appzoro.BP_n_ME.adapter.MedicationRecordAdapter;
import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;
import com.appzoro.BP_n_ME.util.CurrentDateDecorator;
import com.appzoro.BP_n_ME.util.DisabledColorDecorator;
import com.appzoro.BP_n_ME.util.Progress_dialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Appzoro_ 5 on 7/20/2017.
 */

public class TakenMedicationRecordFragment extends Fragment implements OnDateSelectedListener {
    View view;
    MaterialCalendarView calendarMeds;
    ListView medsList;
    MedicationRecordAdapter adapter;
    Calendar calendar;
    CalendarDay registerDate;
    List<String> medication, frequency;
    ArrayList<String> highMedsList, lowMedsList;
    int curYear, curMonth, curDay, goalFreq, daysSince;
    String year, month, day, UID, Date, ParseDate;
    Date dat;
    Boolean past = false;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_takenmedicationrecord,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Patient Monitor");
        ((PatientMonitor) getActivity()).getSupportActionBar().setSubtitle("Medication Utilization Log");
        init();
        Listener();
        getMedicationlog();
        return view;
    }

    private void init() {
        progress_d = Progress_dialog.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        UID = prefs.getUID();
        prefs.setMedsFirstOpen(true);

        highMedsList = new ArrayList<>();
        lowMedsList = new ArrayList<>();

        calendarMeds = view.findViewById(R.id.mcalendar);
        medsList = view.findViewById(R.id.medicationListView);

        String RegistrationDate = prefs.getEnrollmentDate();
        try{
            String [] dateParts = RegistrationDate.split("-");
            year = dateParts[0];
            month = dateParts[1];
            day = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)));

            calendarMeds.state().edit()
                    .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day)))
                    .commit();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        calendarMeds.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarMeds.addDecorator(new EventDecorator(getActivity(),registerDate));
        calendarMeds.addDecorator(new CurrentDateDecorator(getActivity()));
        calendarMeds.addDecorator(new DisabledColorDecorator(getActivity()));

        medication =  new ArrayList<>(Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")));
        frequency =  new ArrayList<>(Arrays.asList(prefs.getFreqList().trim().replace("[","").replace("]","").split(", ")));
        //Log.e("meds", String.valueOf(medication));
        //Log.e("freq", String.valueOf(frequency));

        goalFreq = 0;
        for (int i= 0; i<frequency.size(); i++){
            if (frequency.get(i).equals("Daily")){
                goalFreq +=  1;
            }
            if (frequency.get(i).equals("Twice daily")){
                goalFreq +=  2;
            }
            if (frequency.get(i).equals("Three times daily")){
                goalFreq += 3;
            }
        }
        //Log.e("totalFreq", ""+ goalFreq);

        String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        Log.e("current date","date"+ currentDate);
        update(currentDate);
    }

    private void Listener() {
        calendarMeds.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected( MaterialCalendarView widget,  CalendarDay date, boolean selected) {
        String currDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
        curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
        curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));

        Date current = new Date(curYear,curMonth,curDay);// some Dat
        Date survey = new Date(date.getYear(),date.getMonth()+1,date.getDay());// some Date
        CalendarDay x=calendarMeds.getSelectedDate();
        Date x1 = new Date(x.getYear(),x.getMonth()+1,x.getDay());
        Date oldDateTime = new Date(1,1,2010);
        //Log.e("surveyMonth",Integer.toString(month));
        int one = (int)getDifferenceDays(oldDateTime,current);
        int two = (int)getDifferenceDays(oldDateTime,x1);

        String mon;
        String d;
        if (date.getMonth()+1 < 10) {
            mon = "0" + Integer.toString(date.getMonth()+1);
        }else {
            mon = Integer.toString(date.getMonth()+1);
        }

        if (date.getDay() < 10) {
            d = "0" + Integer.toString(date.getDay());
        }else {
            d = Integer.toString(date.getDay());
        }
        daysSince = one-two;
        if (daysSince>=0) {
            Date = mon +"/"+ d +"/"+ date.getYear();
            update(Date);
        }
        else{
            Toast.makeText(getActivity(), "There are no future logs!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void update(final String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        try {
            Date clicked = formatter.parse(date);
            Date today = new Date();
            if((int)getDifferenceDays(clicked,today)>0){
                past = true;
            }
            else{
                past = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date Date = inputFormat.parse(date);
            ParseDate = outputFormat.format(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("ParseDate",ParseDate);

        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ArrayList<String> records = new ArrayList<>();
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        final ArrayList<String> medTimes = new ArrayList<String>();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            String MedTime = medicine.getKey();
                            medTimes.add(MedTime);
                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);

                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                if (HR < 10 ){
                                    records.add("0" + HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                } else {
                                    records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM   " + medicine.getValue().toString());
                                }
                            }else{
                                records.add(medicine.getKey().substring(0, colonNdx + 3)+ "AM   " + medicine.getValue().toString());
                            }
                        }
                        String [] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);
                        //Log.e("mArray", ""+ mArray.length);
                        if (mArray.length == 0){
                            Toast.makeText(getActivity(), "Patient didn’t record medication intake!", Toast.LENGTH_SHORT).show();
                        }

                        try{
                            adapter = new MedicationRecordAdapter(getActivity(),mArray, date,daysSince);
                            medsList.setAdapter(adapter);
                        } catch (Exception ex){
                            ex.printStackTrace();
                        }

                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray =timeArray;

                        //if(past) {
                        if(daysSince == 0) {
                            medsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long l) {
                                    long viewId = view.getId();
                                    if (viewId == R.id.iv_delete) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Delete Medication Log");
                                        builder
                                                .setMessage("Would you like to delete " + finalMArray[item] + "?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Log.e("FinalmArray", finalMArray[item]);
                                                        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(ParseDate).child(finalTimeArray[item]).removeValue();
                                                        medTimes.remove(item);
                                                        records.remove(item);
                                                        update(date);
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
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void getMedicationlog() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("medicineLog").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (prefs.isMedsFirstOpen()) {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    while (it.hasNext()) {
                        DataSnapshot medicine = it.next();
                        String medsDate = medicine.getKey();
                        String Medication = medicine.getValue().toString();
                        //Log.e("Medication Date", medsDate);
                        //Log.e("Medication ", Medication);

                        List<String> medsList = Arrays.asList(Medication.replace("{", "").replace("}", "").split(", "));
                        double doses = ((double)medsList.size()/goalFreq) * 100;

                        if (doses >= 80) {
                            highMedsList.add(medsDate);    // more than 80% doses
                        } else {
                            lowMedsList.add(medsDate);
                        }
                    }
                    if (!it.hasNext()) {
                        setHighMedsDecorator();
                        setLowMedsDecorator();
                        progress_d.hideProgress();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setHighMedsDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < highMedsList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(highMedsList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            calendarMeds.addDecorators(new TakenMedicationRecordFragment.Decorator(calendarDays, "high"));
        }
    }

    private void setLowMedsDecorator() {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (int i = 0; i < lowMedsList.size(); i++) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    dat = sdf.parse(lowMedsList.get(i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CalendarDay calendarDay = CalendarDay.from(dat);
                calendarDayList.add(calendarDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            calendarDays = calendarDayList;
            calendarMeds.addDecorators(new TakenMedicationRecordFragment.Decorator(calendarDays, "low"));
        }
    }

    private class EventDecorator implements DayViewDecorator {
        private Context context;
        private CalendarDay registerDate;

        EventDecorator(Context context ,CalendarDay registerDate) {
            this.context = context;
            this.registerDate = registerDate;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            CalendarDay today = CalendarDay.today();
            return day.isInRange(registerDate, CalendarDay.from(today.getYear(), today.getMonth(), today.getDay()-1));
        }

        @Override
        public void decorate(DayViewFacade view) {
            try {
                view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_red));
                view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private class Decorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;
        String Meds;

        Decorator(Collection<CalendarDay> dates, String Meds) {
            this.dates = new HashSet<>(dates);
            this.Meds = Meds;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (Meds.equals("high")) {
                view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_green));
                view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                //view.addSpan(new ForegroundColorSpan(Color.parseColor("#5ec639")));
            } else {
                view.setSelectionDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.date_selector_red));
                view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                //view.addSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")));
            }
        }
    }

    private Collection<CalendarDay> calendarDays = new Collection<CalendarDay>() {
        @Override
        public boolean add(CalendarDay object) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends CalendarDay> collection) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean contains(Object object) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @NonNull
        @Override
        public Iterator<CalendarDay> iterator() {
            return null;
        }

        @Override
        public boolean remove(Object object) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public <T> T[] toArray(T[] array) {
            return null;
        }
    };
}
