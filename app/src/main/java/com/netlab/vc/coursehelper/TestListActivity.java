package com.netlab.vc.coursehelper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.DateHelper;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.RateTextCircularProgressBar;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.TestList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dingfeifei on 16/12/2.
 */

/*
 * 展示测试列表的activity
 */
public class TestListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ListView testListView;
    private ProgressBar progressBar;
    private TextView noTest;
    private String courseId;
    private TestList testList;
    private TestListActivity instance;
    private SwipeRefreshLayout refreshLayout;
    List<Map<String,Object> >mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        instance=this;
        Intent intent=getIntent();
        courseId=intent.getStringExtra("course_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        getData();
    }
    void findViews(){
        testListView=(ListView) findViewById(R.id.test_list);
        progressBar=(ProgressBar) findViewById(R.id.load_progress);
        noTest=(TextView)findViewById(R.id.no_contents);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.test_list_container);
        refreshLayout.setOnRefreshListener(this);
    }
    void getData(){
        new getTestListTask().execute();

    }
    @Override
    public void onRefresh() {
        getData();
    }
    public class getTestListTask extends AsyncTask<Void,Void,Boolean>{
        int[] scoreList;
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("QUIZ_LIST"),
                        arrayList, WebConnection.CONNECT_GET);
                testList = new Gson().fromJson(parameters.value, TestList.class);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success)return;
            mapList=new ArrayList<>();
            scoreList=new int[testList.getQuizzes().length];
            for(int i=0;i<testList.getQuizzes().length;i++){

                TestList.Quiz q=testList.getQuizzes()[i];
                scoreList[i]=q.getCorrectAnswer()*100/q.getTotal();
                Map<String,Object> quizItem=new HashMap<>();
                quizItem.put("name",q.getName());
                quizItem.put("finish_time", DateHelper.getDateAsMinute(new Date(q.getTo())));
                mapList.add(quizItem);
            }
            testListView.setAdapter(new QuizListAdapter(TestListActivity.this,
                    mapList,
                    R.layout.test_item,new String[]{"name", "finish_time"},
                    new int[]{R.id.test_name, R.id.finish_time},scoreList));
            testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(TestListActivity.this,QuestionListActivity.class);
                    intent.putExtra("quiz_id",testList.getQuizzes()[position].getQuiz_id());
                    intent.putExtra("course_id",courseId);
                    intent.putExtra("answered",testList.getQuizzes()[position].getAnswered());
                    intent.putExtra("quiz_name",testList.getQuizzes()[position].getName());

                    startActivity(intent);
                }
            });
            progressBar.setVisibility(View.GONE);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
    public class QuizListAdapter extends SimpleAdapter{

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        int []scoreList;
        public QuizListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,int [] _scoreList) {
            super(context, data, resource, from, to);
            scoreList=_scoreList;
        }
        @Override
        public View getView(final int position, View view, ViewGroup parent){
            final View convertView = super.getView(position, null, parent);
            final RateTextCircularProgressBar progressBar;
            progressBar = (RateTextCircularProgressBar) convertView.findViewById(R.id.progress_bar);
            progressBar.setTextColor(getResources().getColor(R.color.cherry_red));
            progressBar.getCircularProgressBar().setPrimaryColor(getResources().getColor(R.color.high_red));
            progressBar.setTextSize(15);
            progressBar.setMax(100);
            progressBar.setProgress(scoreList[position]);
            return convertView;
        }
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
}
