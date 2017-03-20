package com.netlab.vc.coursehelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.ApplyResult;
import com.netlab.vc.coursehelper.util.jsonResults.Group;
import com.netlab.vc.coursehelper.util.jsonResults.GroupResult;
import com.netlab.vc.coursehelper.util.jsonResults.Member;
import com.netlab.vc.coursehelper.util.jsonResults.QueryResult;

import java.util.ArrayList;
import java.util.Objects;

import static com.netlab.vc.coursehelper.util.Constants._id;

/**
 * Created by dingfeifei on 16/11/20.
 */

public class GroupActivity extends AppCompatActivity implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
    public final static int NONE=0,APPLYING=1,INGROUP=2,UNKNOWN=3;
    private int lastVisibleIndex;
    private int newIndex;
    Group[] groupList=new Group[]{};
    private GroupAdapter adapter;
    String groupId;
    private int In=UNKNOWN;
    String courseId;
    GroupResult groupResult;
    String input;
    private ListView groupListView;
    private FloatingActionButton myGroupButton;
    private SwipeRefreshLayout refreshLayout;
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        courseId=intent.getStringExtra("course_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        groupListView = (ListView)findViewById(R.id.group_list);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.group_refreshLayout);
        myGroupButton = (FloatingActionButton)findViewById(R.id.my_group);
        refreshLayout.setOnRefreshListener(this);
        myGroupButton.setOnClickListener(new MyGroupClickListener());
        groupListView.setOnScrollListener(this);
        new GetGroupTask().execute();
        Log.e("HEHE", "1");
        //return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        new GetGroupTask().execute();
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        lastVisibleIndex=firstVisibleItem+visibleItemCount;
        newIndex=firstVisibleItem;
        boolean enable = false;
        if(groupListView != null && groupListView.getChildCount() > 0){
            // check if the first item of the list is visible
            boolean firstItemVisible = groupListView.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = groupListView.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        refreshLayout.setEnabled(enable);
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
            if(In !=NONE){
                Toast.makeText(getApplicationContext(),"您已加入小组或申请小组！",Toast.LENGTH_SHORT).show();
            }
            else{
                final EditText et = new EditText(this);
                et.setHint("组名：");
                new AlertDialog.Builder(this).setTitle("新建小组")
                        .setIcon(R.drawable.ic_error_black_24dp)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                input = et.getText().toString();
                                if (input.equals("")) {
                                    Toast.makeText(getApplicationContext(), "组名不能为空！" + input, Toast.LENGTH_LONG).show();
                                }
                                else {
                                    new Thread(runnable).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            ArrayList<Parameters> arrayList1 = new ArrayList<Parameters>();
            arrayList1.add(new Parameters("_id", _id));
            arrayList1.add(new Parameters("course_id", courseId));
            arrayList1.add(new Parameters("leader_name", Constants.realname));
            arrayList1.add(new Parameters("group_name", input));
            Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("GROUP_CREATE"),
                    arrayList1,WebConnection.CONNECT_POST);
            Log.e(parameters.name,parameters.value);
        }
    };
    public class MyGroupClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (In !=INGROUP){
                Toast.makeText(getApplicationContext(),"您还没有加入任何小组！",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(GroupActivity.this,ShowMyGroupActivity.class);
                intent.putExtra("group_id",groupId);
                intent.putExtra("course_id", courseId);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_list, menu);
        return true;
    }

    @Override
    public void onRefresh() {
        if (refreshLayout.isRefreshing()) {
            new GetGroupTask().execute();
        }
    }

    public class GetGroupTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", _id));
                arrayList.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("GIVE_GROUP"),
                        arrayList,WebConnection.CONNECT_GET);
                //Log.e(parameters.name,parameters.value);
                GroupResult groupResult = new Gson().fromJson(parameters.value,GroupResult.class);
                groupList=groupResult.getGroups();
                parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("GROUP_QUERY"),
                        arrayList,WebConnection.CONNECT_GET);
                QueryResult queryResult = new Gson().fromJson(parameters.value,QueryResult.class);
                switch (queryResult.getStatus()) {
                    case "none":
                        In = NONE;
                        break;
                    case "applying group":
                        In = APPLYING;
                        break;
                    default:
                        In = INGROUP;
                        groupId=queryResult.getGroup_id();
                        break;
                }
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
            adapter=new GroupAdapter(GroupActivity.this,R.layout.group_item,groupList);
            groupListView.setAdapter(adapter);
            groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(Objects.equals(groupList[position].getLeader_id(), Constants._id)){
                        myGroupButton.callOnClick();
                        return;
                    }

                    for(Member member: groupList[position].getMember()){
                        if(member.getMember_id().equals(Constants._id)){
                            myGroupButton.callOnClick();
                            return;
                        }
                    }
                    if (In!=NONE)
                        return;
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(GroupActivity.this);
                    alertdialogbuilder.setMessage("申请加入该组？");
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
                            new ApplyTask(groupList[position].get_id(),Constants.realname, _id).execute();
                        }
                    });
                    alertdialogbuilder.show();
                }
            });
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    public class ApplyTask extends AsyncTask<Void,Void,Boolean>{
        String group_id, member_name, member_id;
        public ApplyTask(String _group_id, String _member_name, String _member_id){
            group_id = _group_id;
            member_name = _member_name;
            member_id = _member_id;
        }
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", _id));
                arrayList.add(new Parameters("group_id", group_id));
                arrayList.add(new Parameters("member_name", member_name));
                arrayList.add(new Parameters("member_id", member_id));
                Log.e("member_name",member_name);
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("GROUP_APPLY"),
                        arrayList, WebConnection.CONNECT_POST);
                ApplyResult applyResult = new Gson().fromJson(parameters.value,ApplyResult.class);
                if(applyResult.getSuccess()){
                    Log.e(applyResult.getSuccess().toString(),"1");
                    Toast.makeText(getApplicationContext(),"申请成功！",Toast.LENGTH_SHORT).show();
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