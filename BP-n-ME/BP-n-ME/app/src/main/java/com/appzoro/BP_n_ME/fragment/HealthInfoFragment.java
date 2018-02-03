package com.appzoro.BP_n_ME.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appzoro.BP_n_ME.activity.MainActivity;
import com.appzoro.BP_n_ME.R;

/**
 * Created by Appzoro_ 5 on 7/29/2017.
 */

public class HealthInfoFragment extends Fragment implements View.OnClickListener {
    View view;
    TextView next;
    ImageView img_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_healthinfo, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        Listener();
        return view;
    }

    private void init() {
        next = view.findViewById(R.id.tv_next);
        img_back = view.findViewById(R.id.iv_back);
    }

    private void Listener() {
        next.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                getFragmentManager().beginTransaction().replace
                        (R.id.Fragment_frame_main_activity, new HealthSurveyFragment()).addToBackStack("HealthSurveyFragment").commit();
                break;
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity) getActivity()).getSupportActionBar().show();
    }
}
