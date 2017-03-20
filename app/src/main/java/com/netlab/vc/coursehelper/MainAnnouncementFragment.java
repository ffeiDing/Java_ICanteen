package com.netlab.vc.coursehelper;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vc on 2016/11/4.
 */

public class MainAnnouncementFragment extends Fragment {
    public MainAnnouncementFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.announcement_main, container, false);
        Log.e("HEHE", "2=");
        return view;
    }
}
