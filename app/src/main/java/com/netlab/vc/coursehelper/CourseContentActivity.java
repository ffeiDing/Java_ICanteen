package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Lesson;
import com.netlab.vc.coursehelper.util.jsonResults.LessonInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseContentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String courseId;
    private ListView lessonPanel;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshLayout;
    private Lesson[] lessons;
    private TextView noContent;
    private List<Map<String,Object>> lessonList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        courseId=intent.getStringExtra("course_id");
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.lesson_container);
        refreshLayout.setOnRefreshListener(this);
        lessonPanel=(ListView) findViewById(R.id.lesson_list);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        noContent=(TextView)findViewById(R.id.no_contents);
        getData();
    }
    public void getData(){
        new GetCourseContentTask().execute();
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
            new GetCourseContentTask().execute();
        }
    }
    public class GetCourseContentTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id",Constants._id));
                arrayList.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("LESSON_INFO"),
                        arrayList, WebConnection.CONNECT_GET);
                lessonList=new ArrayList<>();
                lessons=new Gson().fromJson(parameters.value,LessonInfo.class).getLessons();
                for(int i=0;i<lessons.length;i++){
                    Map<String,Object> lessonItem=new HashMap<>();
                    lessonItem.put("lesson_name",lessons[i].getLesson_name());
                    lessonItem.put("lesson_id",lessons[i].getLesson_id());
                    lessonList.add(lessonItem);
                }
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                //TODO
                return;
            }
            lessonPanel.setAdapter(new SimpleAdapter(
                        CourseContentActivity.this,
                        lessonList,
                        R.layout.lesson_item,
                        new String[]{"lesson_id","lesson_name"},
                        new int[]{R.id.lesson_id,R.id.lesson_name}
                    ));
            progressBar.setVisibility(View.GONE);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            lessonPanel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(CourseContentActivity.this,LessonActivity.class);
                    intent.putExtra("course_id",courseId);
                    intent.putExtra("lesson_id",lessons[position].getLesson_id());
                    startActivity(intent);
                }
            });
        }
    }
}
