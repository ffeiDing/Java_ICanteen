package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Course;
import com.netlab.vc.coursehelper.util.jsonResults.CourseResult;

import java.util.ArrayList;

/**
 * Created by Vc on 2016/11/4.
 */

public class MainCourseFragment extends Fragment {
    public MainCourseFragment(){}
    Course [] courseList=new Course[]{};
    private View view;
    private ListView coursePanel;
    private LinearLayout courseAddDelete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.content_main, container, false);
        coursePanel=(ListView)view.findViewById(R.id.course_list) ;
        courseAddDelete=(LinearLayout) view.findViewById(R.id.elective_course) ;
        new GetMyCourseTask().execute();


        Log.e("HEHE", "1");
        return view;
    }
    @Override
    public void onResume(){
        new GetMyCourseTask().execute();
        super.onResume();
    }
    public class GetMyCourseTask extends AsyncTask<Void,Void,Boolean>{
        /*
        private View view;
        public GetMyCourseTask(View _view){
            view=_view;
        }
        */
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id",Constants._id));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("COURSE_LIST"),
                        arrayList,WebConnection.CONNECT_GET);
                CourseResult courseResult = new Gson().fromJson(parameters.value,CourseResult.class);
                if(courseResult.getSuccess()) {
                    Log.e(courseResult.getSuccess().toString(),"1");
                    courseList=courseResult.getCourses();
                    return true;
                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            courseAddDelete.setOnClickListener(new LinearLayout.OnClickListener(){
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ElectiveActivity.class);
                    startActivity(intent);
                }
            });
            coursePanel.setAdapter(new CourseAdapter(getContext(),R.layout.course_item,courseList));
            coursePanel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(getActivity(),CourseActivity.class);
                    intent.putExtra("course_id",courseList[position].getCourse_id());
                    startActivity(intent);
                }
            });
        }

    }
}
