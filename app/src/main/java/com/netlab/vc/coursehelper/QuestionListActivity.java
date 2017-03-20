package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Answer;
import com.netlab.vc.coursehelper.util.jsonResults.Question;
import com.netlab.vc.coursehelper.util.jsonResults.QuestionResult;
import com.netlab.vc.coursehelper.util.jsonResults.UserAnswerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dingfeifei on 16/12/2.
 */

public class QuestionListActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private String quizId;
    private String courseId;
    private String quizName;
    private Question[] questions;
    private Answer[] originAnswers;
    private Boolean isAnswered;
    private ProgressBar progressBar;
    private QuestionAdapter qa;
    List<Map<String,Object>>mapList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        quizId=intent.getStringExtra("quiz_id");
        courseId=intent.getStringExtra("course_id");
        quizName=intent.getStringExtra("quiz_name");
        setTitle(quizName);
        isAnswered=false;
        if(intent.getIntExtra("answered",0)==1)
            isAnswered=true;
        findViews();
        new getQuestionTask().execute();
    }
    void findViews(){
        viewPager=(ViewPager)findViewById(R.id.question_pager);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isAnswered)
          getMenuInflater().inflate(R.menu.question_list, menu);
        return true;
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
        if(item.getItemId() == R.id.submit_test)
        {
            attemptSubmit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    void attemptSubmit(){
        Answer[] submitAnswers=qa.getAnswers();
        if(submitAnswers.length!=questions.length){
            Toast.makeText(QuestionListActivity.this, "尚未完成所有题目，请完成后提交。左滑进入下一题。", Toast.LENGTH_LONG).show();
            return;
        }
        new SubmitTestTask(submitAnswers).execute();
    }
    class getQuestionTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("quiz_id", quizId));
                Parameters questionParam = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("QUIZ_CONTENT"),
                        arrayList, WebConnection.CONNECT_GET);
                Gson gson=new Gson();
                questions = gson.fromJson(questionParam.value, QuestionResult.class).getQuestions();
                arrayList.add(new Parameters("course_id", courseId));
                Parameters originAnswerParam=null;
                if(isAnswered){
                    originAnswerParam = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("ANSWER_QUIZ_INFO"),
                            arrayList, WebConnection.CONNECT_GET);
                    originAnswers=gson.fromJson(originAnswerParam.value,UserAnswerInfo.class).getStatus();

                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean success){
            if(!success){
                return;
            }
            qa=new QuestionAdapter(QuestionListActivity.this,questions,originAnswers,isAnswered,false);
            viewPager.setAdapter(qa);
            progressBar.setVisibility(View.GONE);
        }
    }
    class SubmitTestTask extends AsyncTask<Void,Void,Boolean>{
        Answer[] submitAnswers;
        public SubmitTestTask(Answer[] answers){
            submitAnswers=answers;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
            arrayList.add(new Parameters("_id", Constants._id));
            arrayList.add(new Parameters("quiz_id", quizId));
            arrayList.add(new Parameters("course_id", courseId));
            arrayList.add(new Parameters("status",new Gson().toJson(submitAnswers)));
            try{
                Parameters Param = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("QUIZ_SUBMIT"),
                        arrayList, WebConnection.CONNECT_POST);
                Log.e("Param",Param.value);
                return true;
            }
            catch(Exception e){
                Log.e("Param","false");
                return false;
            }

        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(getApplicationContext(), "提交失败！", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "提交成功！", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
