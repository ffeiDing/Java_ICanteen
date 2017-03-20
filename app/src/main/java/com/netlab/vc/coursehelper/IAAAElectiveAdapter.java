package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Vc on 2017/2/25.
 */

public class IAAAElectiveAdapter extends BaseAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private IAAAElective.Course[] courseList;
    public IAAAElectiveAdapter(Context context, int layout, IAAAElective.Course[] courseList) {
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.courseList=courseList;
    }
    @Override
    public int getCount() {
        return courseList.length;
    }

    @Override
    public Object getItem(int position) {
        return courseList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view=viewInflater.inflate(layout,parent,false);
        }
        TextView courseName=(TextView)view.findViewById(R.id.course_name);
        TextView selectPerson=(TextView)view.findViewById(R.id.selected_person);
        courseName.setText(courseList[position].name);
        selectPerson.setText(courseList[position].full==1?"full":"select");
        return view;
    }
}
