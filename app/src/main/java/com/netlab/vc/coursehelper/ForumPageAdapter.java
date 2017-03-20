package com.netlab.vc.coursehelper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Vc on 2017/1/4.
 */

public class ForumPageAdapter extends FragmentPagerAdapter {

    public final int COUNT=2;
    private ForumPageFragment page_ex,page_qa;
    public ForumPageAdapter(FragmentManager fm) {
        super(fm);
        page_ex=new ForumPageFragment();
        page_ex.setType("EX");
        page_qa=new ForumPageFragment();
        page_qa.setType("QA");
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return page_qa;
        else
            return page_ex;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
