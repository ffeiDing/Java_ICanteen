package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Member;
import com.netlab.vc.coursehelper.util.jsonResults.ShowMyGroupResult;

import java.util.ArrayList;

import static com.netlab.vc.coursehelper.util.Constants._id;

/**
 * Created by dingfeifei on 16/11/20.
 */

public class ShowMyGroupActivity extends AppCompatActivity implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
    Member[] memberList=new Member[]{};
    private int lastVisibleIndex;
    private int newIndex;
    Boolean isLeader;
    private ShowMyGroupAdapter adapter;
    String courseId;
    TextView leaderNameView;
    TextView groupNameView;
    String leaderName;
    String leaderId;
    String groupName;
    ShowMyGroupResult groupResult;
    String groupId;
    Button deleteGroupButton;
    private ListView memberListView;
    private SwipeRefreshLayout refreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        groupId=intent.getStringExtra("group_id");
        courseId=intent.getStringExtra("course_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        memberListView = (ListView)findViewById(R.id.group_member_list);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.show_group_refreshLayout);
        leaderNameView = (TextView)findViewById(R.id.group_leader_name);
        groupNameView = (TextView)findViewById(R.id.group_name);
        deleteGroupButton=(Button)findViewById(R.id.group_delete);
        deleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteGroupTask().execute();
            }
        });
        refreshLayout.setOnRefreshListener(this);
        memberListView.setOnScrollListener(this);
        new ShowMyGroupTask().execute();
        Log.e("HEHE", "1");
        //return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        new ShowMyGroupTask().execute();
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
        if(memberListView != null && memberListView.getChildCount() > 0){
            // check if the first item of the list is visible
            boolean firstItemVisible = memberListView.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = memberListView.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        refreshLayout.setEnabled(enable);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_list, menu);
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
        if(item.getItemId() == R.id.submit_group)
        {
            if(isLeader == false){
                Toast.makeText(getApplicationContext(),"仅有组长有权限添加组员！",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(ShowMyGroupActivity.this,GroupWaitingActivity.class);
                intent.putExtra("course_id",courseId);
                intent.putExtra("group_id", groupId);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (refreshLayout.isRefreshing()) {
            new ShowMyGroupTask().execute();
        }
    }

    public class ShowMyGroupTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", _id));
                arrayList.add(new Parameters("group_id", groupId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("GROUP_SHOW"),
                        arrayList,WebConnection.CONNECT_GET);
                Log.e(parameters.name,parameters.value);
                ShowMyGroupResult showMyGroupResult = new Gson().fromJson(parameters.value,ShowMyGroupResult.class);
                memberList=showMyGroupResult.getMember();
                groupName=showMyGroupResult.getGroup_name();
                leaderName=showMyGroupResult.getLeader_name();
                leaderId=showMyGroupResult.getLeader_id();
                if (leaderId.equals(Constants._id))
                    isLeader = true;
                else
                    isLeader = false;
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
            groupNameView.setText(groupName);
            leaderNameView.setText(leaderName);
            adapter=new ShowMyGroupAdapter(ShowMyGroupActivity.this,R.layout.group_member_item,memberList);
            memberListView.setAdapter(adapter);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            if(isLeader&&memberList.length==0)
                deleteGroupButton.setVisibility(View.VISIBLE);
        }
    }
    public class DeleteGroupTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", _id));
                arrayList.add(new Parameters("group_id", groupId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+"/group/delete",
                        arrayList,WebConnection.CONNECT_POST);
                return parameters.name.equals("200");
            }
            catch (Exception e){
                return false;
            }
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(getApplicationContext(),"删除小组失败",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(),"删除小组成功",Toast.LENGTH_SHORT).show();
        }
    }
}