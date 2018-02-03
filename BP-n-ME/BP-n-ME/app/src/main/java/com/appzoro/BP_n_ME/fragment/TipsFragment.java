package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.R;
import com.appzoro.BP_n_ME.prefs.MedasolPrefs;

import java.util.Calendar;

/**
 * Created by Appzoro_ 5 on 8/3/2017.
 */

public class TipsFragment extends Fragment{
    View view;
    ListView mList;
    TextView tips;
    ArrayAdapter<String> adapter;
    String sampleString;
    int[] surveyResponse;
    MedasolPrefs prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_tips ,container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle("Tips of The day");
        init();
        return view;
    }

    private void init() {
        prefs = new MedasolPrefs(getActivity().getApplicationContext());
        tips = view.findViewById(R.id.tv_tips);
        tips.setVisibility(View.GONE);
        mList = view.findViewById(R.id.messagesListView);

        try {
            sampleString = prefs.getLifestyleSurveyAnswersRW(); //"101,203,405";
            //Log.e("string","sample"+ sampleString);
            String[] stringArray = sampleString.split(",");
            //Log.e("string","sample"+ stringArray.length + Arrays.toString(stringArray));
            surveyResponse = new int[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                String numberAsString = stringArray[i];
                surveyResponse[i] = Integer.parseInt(numberAsString.replace("[","").replace("]","").replace(" ",""));
            }
                //Log.e("eee", "eee"+ Arrays.toString(surveyResponse));
                /*setLifestyleResponse(surveyResponse);*/
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if (surveyResponse[0] != -1){
            //Log.e("eee", "eee"+Arrays.toString(surveyResponse));
            setLifestyleResponse(surveyResponse);
        }
        else {
            //Log.e("length", "length"+String.valueOf(surveyResponse.length));
            tips.setVisibility(View.VISIBLE);
        }
    }

    private void initializeMessagesList(String [] surveyResponse) {
        String [] mArray = {""};
        int count=0;
        for(int i=0;i<surveyResponse.length;i++){
            if(surveyResponse[i]==null){
                count++;
            }
        }

        if(count!=8) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            //Log.e("Cal", "the Day");
            //Random generator = new Random();
            int num = day % surveyResponse.length;
            //Log.e("num",Integer.toString(num));

            //String [] mArrayBefore = new String[messages.size()];
            //mArrayBefore = messages.toArray(mArrayBefore);
            //Log.e("list",Arrays.toString(surveyResponse));
            if (num < surveyResponse.length) {
                while (surveyResponse[num] == null & num < surveyResponse.length - 1) {
                    if (surveyResponse[num] == null) {
                    }
                    num++;
                    if (num == surveyResponse.length - 1) {
                        num = 0;
                    }
                }
                mArray[0] = surveyResponse[num];
            }
        }
        else{
            mArray[0] = "Please take the Lifestyle Survey to get a tip of the day!";
        }

        //Log.e("Tip of","the Day");
        //Log.e("list",Arrays.toString(surveyResponse));
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.tip_of_the_day, mArray);
        mList.setAdapter(adapter);
    }

    public void setLifestyleResponse(int[] intArray1) {
            //Log.e("Survey Response","Survey"+ Arrays.toString(intArray1));
            String[] surveyResponse = new String[8];
            String[] posArray = getResources().getStringArray(R.array.LifestylePositiveMessagesArray);
            String[] negArray = getResources().getStringArray(R.array.LifestyleNegativeMessagesArray);
            final int[] correctChoice = {4,1,2,2,1,2,2,4};
            final int[] wrongChoice = {0,0,1,1,0,1,1,0};

            for (int ind = 0; ind < intArray1.length; ind++) {
                if (intArray1[ind] == correctChoice[ind]) {
                    //Log.e("Int Array1",Integer.toString(intArray1[ind]));
                    surveyResponse[ind] = posArray[ind];
                } else if ((wrongChoice[ind]==0&intArray1[ind]!=-1) | intArray1[ind] == wrongChoice[ind]) {
                    //Log.e("Int Array1",Integer.toString(intArray1[ind]));
                    surveyResponse[ind] = negArray[ind];
                }
            }
            //Log.e("To Init",Arrays.toString(surveyResponse));
            initializeMessagesList(surveyResponse);
    }
}
