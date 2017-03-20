package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.jsonResults.Course;

/**
 * Created by dingfeifei on 16/12/11.
 */

public class ElectiveCourseAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Course[] courseList;
    public ElectiveCourseAdapter(Context context, int layout, Course[] courseList) {
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
        TextView courseNameView=(TextView)view.findViewById(R.id.elective_course_name);
        ImageView courseImgView=(ImageView)view.findViewById(R.id.elective_course_image);
        ImageView addImgView=(ImageView)view.findViewById(R.id.add_image);
        courseNameView.setText(courseList[position].getName());
        if(courseList[position].getName().equals("计算机网络实习"))
            courseImgView.setImageResource(R.drawable.ac);
        if(courseList[position].getName().equals("操作系统实习"))
            courseImgView.setImageResource(R.drawable.baldr1);
        if(courseList[position].getName().equals("形势与政策"))
            courseImgView.setImageResource(R.drawable.xingshi);
        if(courseList[position].getName().equals("数据库概论"))
            courseImgView.setImageResource(R.drawable.mysql);
        return view;
    }
}
