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
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Group;
import com.netlab.vc.coursehelper.util.jsonResults.GroupResult;
import com.netlab.vc.coursehelper.util.jsonResults.Member;
import com.netlab.vc.coursehelper.util.jsonResults.NoGroupResult;
import com.netlab.vc.coursehelper.util.jsonResults.QueryResult;

import java.util.ArrayList;
import java.util.Objects;

import static com.netlab.vc.coursehelper.util.Constants._id;

/**
 * Created by dingfeifei on 16/11/20.
 */

public class GroupSecondActivity extends AppCompatActivity implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener{
    public final static int NONE=0,APPLYING=1,INGROUP=2,UNKNOWN=3;
    private int lastVisibleIndex;
    private int newIndex;
    private int chooseSum;
    Member[] noGroupList=new Member[]{};
    ArrayList<Member> chooseList = new ArrayList<Member>();
    Group[] groupList=new Group[]{};
    int l;
    private GroupAdapter adapter;
    NoGroupResult noGroupResult;
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
            if(In == INGROUP){
                Toast.makeText(getApplicationContext(),"您已加入小组！",Toast.LENGTH_SHORT).show();
            }
            else{
                new NoGroupTask().execute();
            }
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyGroupClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (In !=INGROUP){
                Toast.makeText(getApplicationContext(),"您还没有加入任何小组！",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(GroupSecondActivity.this,ShowMyGroupActivity.class);
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
                Parameters parameters = WebConnection.connect(Constants.baseUrl+ Constants.AddUrls.get("GIVE_GROUP"),
                        arrayList, WebConnection.CONNECT_GET);
                //Log.e(parameters.name,parameters.value);
                GroupResult groupResult = new Gson().fromJson(parameters.value,GroupResult.class);
                groupList=groupResult.getGroups();
                parameters = WebConnection.connect(Constants.baseUrl+ Constants.AddUrls.get("GROUP_QUERY"),
                        arrayList, WebConnection.CONNECT_GET);
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
            adapter=new GroupAdapter(GroupSecondActivity.this, R.layout.group_item,groupList);
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
                }
            });
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    public class NoGroupTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList2 = new ArrayList<Parameters>();
                arrayList2.add(new Parameters("_id", _id));
                arrayList2.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+ Constants.AddUrls.get("NO_GROUP"),
                        arrayList2, WebConnection.CONNECT_GET);
                Log.e(parameters.name,parameters.value);
                noGroupResult = new Gson().fromJson(parameters.value,NoGroupResult.class);
                l = noGroupResult.getMembers().length;
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
            noGroupList = noGroupResult.getMembers();
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupSecondActivity.this);
            builder.setTitle("选择你最希望与之组队的队友，请尽量选择少于四个队友");
            final String[] hobbies = new String[l];
            chooseSum = 0;
            for (int i = 0; i < l; i++){
                hobbies[i] = noGroupList[i].getMember_name();
            }
            //    设置一个单项选择下拉框
            /**
             * 第一个参数指定我们要显示的一组下拉多选框的数据集合
             * 第二个参数代表哪几个选项被选择，如果是null，则表示一个都不选择，如果希望指定哪一个多选选项框被选择，
             * 需要传递一个boolean[]数组进去，其长度要和第一个参数的长度相同，例如 {true, false, false, true};
             * 第三个参数给每一个多选项绑定一个监听器
             */
            builder.setMultiChoiceItems(hobbies, null, new DialogInterface.OnMultiChoiceClickListener()
            {
                //StringBuffer sb = new StringBuffer(100);
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked)
                {
                    if(isChecked)
                    {
                        chooseList.add(noGroupList[which]);
                        chooseSum++;
                    }
                    //Toast.makeText(GroupSecondActivity.this, "已选择：" + sb.toString(), Toast.LENGTH_SHORT).show();
            }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    new SendWishTask().execute();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            builder.show();
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    public class SendWishTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList3 = new ArrayList<Parameters>();
                arrayList3.add(new Parameters("_id", _id));
                arrayList3.add(new Parameters("course_id", courseId));
                arrayList3.add(new Parameters("require_id", _id));
                arrayList3.add(new Parameters("require_name", Constants.realname));
                String jsonResult = new Gson().toJson(chooseList);
                arrayList3.add(new Parameters("target", jsonResult));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+ Constants.AddUrls.get("SEND_WISH"),
                        arrayList3, WebConnection.CONNECT_POST);
                Log.e(parameters.name,parameters.value);
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
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
}