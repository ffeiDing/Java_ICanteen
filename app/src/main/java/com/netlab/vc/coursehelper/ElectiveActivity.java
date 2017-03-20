package com.netlab.vc.coursehelper;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Course;
import com.netlab.vc.coursehelper.util.jsonResults.CourseResult;

import java.util.ArrayList;

/**
 * Created by dingfeifei on 16/11/20.
 */

public class ElectiveActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    Course[] courseList=new Course[]{};
    private ListView electiveList;
    private SwipeRefreshLayout refreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elective);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        electiveList = (ListView)findViewById(R.id.elective_course_list);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_elective);
        refreshLayout.setOnRefreshListener(this);
        new GetElectiveCourseTask().execute();
        Log.e("HEHE", "1");
        //return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (refreshLayout.isRefreshing()) {
//            ArrayList<NameValuePair> params = new ArrayList<>();
//            BasicNameValuePair valuesPair = new BasicNameValuePair("course_id", courseId);
//            params.add(valuesPair);
            new GetElectiveCourseTask().execute();
        }
    }

    public class GetElectiveCourseTask extends AsyncTask<Void,Void,Boolean> {
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
                arrayList.add(new Parameters("_id", Constants._id));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("ELECTIVE_COURSE"),
                        arrayList,WebConnection.CONNECT_GET);
                Log.e(parameters.name,parameters.value);
                CourseResult courseResult = new Gson().fromJson(parameters.value,CourseResult.class);
                Log.e("length:",String.valueOf(courseResult.getCourses().length));
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
            electiveList.setAdapter(new ElectiveCourseAdapter(ElectiveActivity.this,R.layout.elective_course_item,courseList));
            electiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(ElectiveActivity.this);
                    alertdialogbuilder.setMessage("确认添加该课程？");
                    AlertDialog alertdialog1=alertdialogbuilder.create();
                    alertdialogbuilder.setPositiveButton("取消", new DialogInterface.OnClickListener(){
                        //RelativeLayout listitem = (RelativeLayout) parent.getItemAtPosition(position);
                        public void onClick(DialogInterface arg0,int arg1) {
                            //android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    alertdialogbuilder.setNegativeButton("确定", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface arg0,int arg1) {
                            // 按确定按钮表示选该课
                            new SelectCourseTask(courseList[position].getCourse_id()).execute();
                            Toast.makeText(getApplicationContext(),"选课成功！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertdialogbuilder.show();
                }
            });
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    public class SelectCourseTask extends AsyncTask<Void,Void,Boolean>{
        String course_id;
        public SelectCourseTask(String _course_id){
            course_id = _course_id;
        }
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                arrayList.add(new Parameters("user_name",Constants.username));
                Log.e("course_id",course_id);
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("COURSE_SELECT"),
                        arrayList, WebConnection.CONNECT_POST);
                CourseResult courseResult = new Gson().fromJson(parameters.value,CourseResult.class);
                //Log.e("length:",String.valueOf(courseResult.getCourses().length));
                if(courseResult.getSuccess()) {
                    Log.e(courseResult.getSuccess().toString(),"1");
                    //courseList=courseResult.getCourses();
                    return true;
                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
