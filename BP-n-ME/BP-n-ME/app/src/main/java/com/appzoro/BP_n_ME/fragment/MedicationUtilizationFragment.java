package com.appzoro.BP_n_ME.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.adapter.MedsAdapter;
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
 * Created by Appzoro_ 5 on 6/17/2017.
 */

public class MedicationUtilizationFragment extends Fragment implements OnDateSelectedListener {
    View view;
    MaterialCalendarView calendarMed;
    LinearLayout nfcLayout;
    ListView medsName;
    Calendar calendar;
    CalendarDay registerDate;
    List<String> medication, frequency;
    ArrayList<String> highMedsList, lowMedsList;
    String year, month, day, UID;
    int curYear, curMonth, curDay, goalFreq;
    Date dat;
    MedasolPrefs prefs;
    private Progress_dialog progress_d;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_medicationutilization, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Medication Records");
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("");
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
        prefs.setMedsFirstTimeOpen(true);

        highMedsList = new ArrayList<>();
        lowMedsList = new ArrayList<>();

        calendarMed = view.findViewById(R.id.mcalendar_mul);
        String RegistrationDate = prefs.getEnrollmentDate();
        Log.e("RegistrationDate", RegistrationDate);
        try {
            String[] dateParts = RegistrationDate.split("-");
            year = dateParts[0];
            month = dateParts[1];
            day = dateParts[2];
            registerDate = (CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day)));

            calendarMed.state().edit()
                    .setMinimumDate(CalendarDay.from(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day)))
                    .commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        nfcLayout = view.findViewById(R.id.nfcLayout);
        calendarMed.setDateSelected(Calendar.getInstance().getTime(), true);
        calendarMed.addDecorator(new EventDecorator(getActivity(), registerDate));
        calendarMed.addDecorator(new CurrentDateDecorator(getActivity()));
        calendarMed.addDecorator(new DisabledColorDecorator(getActivity()));
        medsName = view.findViewById(R.id.medsname);

        medication = new ArrayList<String>(Arrays.asList(prefs.getMeds().replace("[", "").replace("]", "").replace(" ", "").split(",")));
        Log.e("meds", String.valueOf(medication));
        frequency = new ArrayList<String>(Arrays.asList(prefs.getFreqList().trim().replace("[", "").replace("]", "").split(", ")));
        Log.e("freq", String.valueOf(frequency));
        // final String medName = Arrays.asList(prefs.getMeds().replace("[","").replace("]","").replace(" ","").split(",")).get(i);

        //Log.e("msize", String.valueOf(medication.size()));
        //Log.e("fsize", String.valueOf(frequency.size()));

        goalFreq = 0;
        for (int i = 0; i < frequency.size(); i++) {
            if (frequency.get(i).equals("Daily")) {
                goalFreq += 1;
            }
            if (frequency.get(i).equals("Twice daily")) {
                goalFreq += 2;
            }
            if (frequency.get(i).equals("Three times daily")) {
                goalFreq += 3;
            }
            if (frequency.get(i).equals("Four times per day")) {
                goalFreq += 4;
            }
        }
        //Log.e("totalFreq", ""+ goalFreq);

        medsName.setAdapter(new MedsAdapter(getActivity(), medication, frequency));

        /*NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (nfcAdapter == null) {
            Toast.makeText(getActivity(),
                    "NFC NOT supported on this devices!", Toast.LENGTH_SHORT).show();
        } else if (nfcAdapter.isEnabled()) {
            Toast.makeText(getActivity(),
                    "NFC supported!", Toast.LENGTH_SHORT).show();

            TextView btnTag = new TextView(getActivity());
            btnTag.setText("NFC Available");
            btnTag.setTextSize(18);
            btnTag.setTextColor(Color.parseColor("#ffffff"));
            btnTag.setPadding(8, 8, 8, 8);
            btnTag.setGravity(Gravity.CENTER);
            btnTag.setBackgroundResource(R.drawable.button_border);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 15);//pass int values for left,top,right,bottom
            btnTag.setLayoutParams(params);
            btnTag.setVisibility(View.VISIBLE);
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), NFCTag.class);
                    startActivity(i);
                }
            });
            //nfcLayout.addView(btnTag);
        } else if (!nfcAdapter.isEnabled()) { //Your device doesn't support NFC
            Toast.makeText(getActivity(),
                    "NFC NOT Enabled!", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void Listener() {
        calendarMed.setOnDateChangedListener(this);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        String currDate = data;
        curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
        curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
        curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));

        Date current = new Date(curYear, curMonth, curDay);// some Dat
        Date survey = new Date(date.getYear(), date.getMonth() + 1, date.getDay());// some Date
        CalendarDay x = calendarMed.getSelectedDate();
        Date x1 = new Date(x.getYear(), x.getMonth() + 1, x.getDay());
        Date oldDateTime = new Date(1, 1, 2010);
        //Log.e("surveyMonth",Integer.toString(month));
        int one = (int) getDifferenceDays(oldDateTime, current);
        int two = (int) getDifferenceDays(oldDateTime, x1);

        String mon;
        String d;
        if (date.getMonth() + 1 < 10) {
            mon = "0" + Integer.toString(date.getMonth() + 1);
            //Log.e("Less Than","10: "+mon);
        } else {
            mon = Integer.toString(date.getMonth() + 1);
            Log.e("Greater Than", "10: " + mon);
        }

        if (date.getDay() < 10) {
            d = "0" + Integer.toString(date.getDay());
        } else {
            d = Integer.toString(date.getDay());
        }
        int daysSince = one - two;

        if (daysSince >= 0) {
            MedicationLogsRecordFragment fragment = new MedicationLogsRecordFragment();
            Bundle bundle = new Bundle();
            bundle.putString("date", mon + "/" + d + "/" + date.getYear());
            //Log.e("date",mon +"/"+ d +"/"+ date.getYear());
            bundle.putInt("daysSince", daysSince);
            fragment.setArguments(bundle);

            prefs.setMedsFirstTimeOpen(false);
            getFragmentManager().beginTransaction().add
                    (R.id.Fragment_frame_main_activity, fragment).addToBackStack("MedicationLogsRecordFragment").commit();
        } else {
            Toast.makeText(getActivity(), "Please only edit the current day!", Toast.LENGTH_SHORT).show();
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void getMedicationlog() {
        progress_d.showProgress(getActivity());
        mDatabase.child("app").child("users").child(UID).child("MedicalCondition").addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (prefs.isMedsFirstTimeOpen()) {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    String medsDate = "";
                    while (it.hasNext()) {
                        DataSnapshot medicine = it.next();
                        medsDate = medicine.getKey();
                        String color = medicine.getValue().toString();
                        /*List<String> medsList = Arrays.asList(Medication.replace("{", "").replace("}", "").split(", "));

                        double doses = ((double) medsList.size() / goalFreq) * 100;
                        if (doses >= 80) {
                            highMedsList.add(medsDate);    // more than 80% doses
                        } else {
                            lowMedsList.add(medsDate);
                        }*/

                        if (color.equals("green")){
                            highMedsList.add(medsDate);
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
                //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
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
            calendarMed.addDecorators(new MedicationUtilizationFragment.Decorator(getActivity(), calendarDays, "high"));
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
            calendarMed.addDecorators(new MedicationUtilizationFragment.Decorator(getActivity(), calendarDays, "low"));
        }
    }

    private class EventDecorator implements DayViewDecorator {
        private Context context;
        private CalendarDay registerDate;
        private final Drawable drawable1;

        EventDecorator(Context context, CalendarDay registerDate) {
            this.context = context;
            this.registerDate = registerDate;
            drawable1 = ResourcesCompat.getDrawable(getResources(), R.drawable.date_selector_red, null);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            CalendarDay today = CalendarDay.today();
            return day.isInRange(registerDate, CalendarDay.from(today.getYear(), today.getMonth(), today.getDay() - 1));
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable1);
            view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
        }
    }

    private class Decorator implements DayViewDecorator {
        private Context context;
        private final HashSet<CalendarDay> dates;
        String Meds;
        private Drawable drawable1;
        private Drawable drawable2;

        Decorator(Context context, Collection<CalendarDay> dates, String Meds) {
            this.context = context;
            this.dates = new HashSet<>(dates);
            this.Meds = Meds;
            Activity activity = getActivity();
            if (activity != null) {
                drawable1 = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.date_selector_green, null);
                drawable2 = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.date_selector_red, null);
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (Meds.equals("high")) {
                Activity activity = getActivity();
                if (activity != null) {
                    view.setSelectionDrawable(drawable1);
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                }
                //view.addSpan(new ForegroundColorSpan(Color.parseColor("#5ec639")));
            } else {
                Activity activity = getActivity();
                if (activity != null) {
                    view.setSelectionDrawable(drawable2);
                    view.addSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")));
                }
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