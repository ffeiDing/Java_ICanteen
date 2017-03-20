package com.netlab.vc.coursehelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.GroupWaitingResult;
import com.netlab.vc.coursehelper.util.jsonResults.Member;

import java.util.ArrayList;

import static com.netlab.vc.coursehelper.util.Constants._id;

/**
 * Created by dingfeifei on 16/11/20.
 */

public class GroupWaitingActivity extends AppCompatActivity implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
    Member[] memberList=new Member[]{};
    private int lastVisibleIndex;
    private int newIndex;
    private GroupWaitingAdapter adapter;
    String courseId;
    String groupId;
    GroupWaitingResult groupWaitingResult;
    private ListView waitMemberListview;
    private SwipeRefreshLayout refreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        courseId=intent.getStringExtra("course_id");
        groupId=intent.getStringExtra("group_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_waiting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        waitMemberListview = (ListView)findViewById(R.id.wait_member_listview);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        waitMemberListview.setOnScrollListener(this);
        new GroupWaitingTask().execute();
        Log.e("HEHE", "1");
        //return view;
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        lastVisibleIndex=firstVisibleItem+visibleItemCount;
        newIndex=firstVisibleItem;
        boolean enable = false;
        if(waitMemberListview != null && waitMemberListview.getChildCount() > 0){
            // check if the first item of the list is visible
            boolean firstItemVisible = waitMemberListview.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = waitMemberListview.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        refreshLayout.setEnabled(enable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (refreshLayout.isRefreshing()) {
            new GroupWaitingTask().execute();
        }
    }

    public class GroupWaitingTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", _id));
                arrayList.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("GROUP_WAITING"),
                        arrayList,WebConnection.CONNECT_GET);
                Log.e(parameters.name,parameters.value);
                groupWaitingResult = new Gson().fromJson(parameters.value,GroupWaitingResult.class);
                memberList=groupWaitingResult.getWaiting_member();
                return true;
            } catch (Exception e) {
                Log.e("error",e.toString());
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result){
            if(!result)
                return;
            adapter=new GroupWaitingAdapter(GroupWaitingActivity.this,R.layout.waiting_member_item,memberList);
            waitMemberListview.setAdapter(adapter);
            waitMemberListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(GroupWaitingActivity.this);
                    alertdialogbuilder.setMessage("同意添加该同学至本组？");
                    AlertDialog alertdialog1=alertdialogbuilder.create();
                    alertdialogbuilder.setPositiveButton("拒绝", new DialogInterface.OnClickListener(){
                        //RelativeLayout listitem = (RelativeLayout) parent.getItemAtPosition(position);
                        public void onClick(DialogInterface arg0,int arg1) {
                            new RejectMemberTask(memberList[position].getMember_id(),memberList[position].getMember_name()).execute();
                            Toast.makeText(getApplicationContext(),"已拒绝该申请！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertdialogbuilder.setNegativeButton("同意", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface arg0,int arg1) {
                            // 按确定按钮表示选该课
                            new AgreeMemberTask(memberList[position].getMember_id(),memberList[position].getMember_name()).execute();
                            Toast.makeText(getApplicationContext(),"添加成功！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertdialogbuilder.show();
                }
            });
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
    public class AgreeMemberTask extends AsyncTask<Void,Void,Boolean>{
        String member_name, member_id;
        public AgreeMemberTask(String _member_id, String _member_name){
            member_name = _member_name;
            member_id = _member_id;
        }
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("group_id", groupId));
                arrayList.add(new Parameters("course_id", courseId));
                arrayList.add(new Parameters("type", "confirm"));
                arrayList.add(new Parameters("member_id",member_id));
                arrayList.add(new Parameters("member_name", member_name));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("GROUP_DEAL"),
                        arrayList, WebConnection.CONNECT_POST);
                Log.e(parameters.name,parameters.value);
                //CourseResult courseResult = new Gson().fromJson(parameters.value,CourseResult.class);
                //Log.e("length:",String.valueOf(courseResult.getCourses().length));
                //if(courseResult.getSuccess()) {
                    //Log.e(courseResult.getSuccess().toString(),"1");
                    //courseList=courseResult.getCourses();
                    return true;
                //}
                //else
                    //return false;
            } catch (Exception e) {
                return false;
            }
        }
    }
    public class RejectMemberTask extends AsyncTask<Void,Void,Boolean>{
        String member_name, member_id;
        public RejectMemberTask(String _member_id, String _member_name){
            member_name = _member_name;
            member_id = _member_id;
        }
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("group_id", groupId));
                arrayList.add(new Parameters("course_id", courseId));
                arrayList.add(new Parameters("type", "reject"));
                arrayList.add(new Parameters("member_id",member_id));
                arrayList.add(new Parameters("member_name", member_name));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("GROUP_DEAL"),
                        arrayList, WebConnection.CONNECT_POST);
                Log.e(parameters.name,parameters.value);
                //CourseResult courseResult = new Gson().fromJson(parameters.value,CourseResult.class);
                //Log.e("length:",String.valueOf(courseResult.getCourses().length));
                //if(courseResult.getSuccess()) {
                //Log.e(courseResult.getSuccess().toString(),"1");
                //courseList=courseResult.getCourses();
                return true;
                //}
                //else
                //return false;
            } catch (Exception e) {
                return false;
            }
        }
    }

}