package com.netlab.vc.coursehelper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * Created by Vc on 2016/11/5.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {
    private final int PAGES=3;
    private MainCourseFragment mainCourseFragment=null;
    private MainAnnouncementFragment mainAnnouncementFragment=null;
    private MainGroupFragment mainGroupFragment=null;
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
        mainCourseFragment=new MainCourseFragment();
        mainAnnouncementFragment=new MainAnnouncementFragment();
        mainGroupFragment=new MainGroupFragment();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch(position){
            case 0:
                fragment=mainCourseFragment;
                break;
            case 1:
                fragment=mainAnnouncementFragment;
                break;
            case 2:
                fragment=mainGroupFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGES;
    }
    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }
}
