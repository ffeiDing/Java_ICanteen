package com.netlab.vc.coursehelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Announcement;
import com.netlab.vc.coursehelper.util.jsonResults.AnnouncementResult;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.id.input;

/**
 * Created by dingfeifei on 16/12/16.
 */

public class AnnouncementActivity extends AppCompatActivity implements OnScrollListener,SwipeRefreshLayout.OnRefreshListener {
    Announcement[] announcementList = new Announcement[]{};
    String courseId;
    private int page = 1;
    private int lastVisibleIndex;
    private int newIndex;
    //private ListView announceListView;
    private AnnouncementAdapter adapter;
    ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
    private SwipeRefreshLayout refreshLayout;
    private ListView myLayout;
    private LinearLayout footer;
    private EditText editTitle,editContent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回箭头
        Intent intent = getIntent();
        courseId = intent.getStringExtra("course_id");
        myLayout = (ListView) findViewById(R.id.announcement_listview);
        footer=(LinearLayout)findViewById(R.id.footer_layout);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        adapter = new AnnouncementAdapter(AnnouncementActivity.this, R.layout.announcement_item, announcementList);
        myLayout.setOnScrollListener(this);
        initData();
        final EditText editText = new EditText(this);
        editText.setMinLines(3);
        editText.setGravity(Gravity.BOTTOM|Gravity.START);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Constants.admin)
            getMenuInflater().inflate(R.menu.group_list, menu);
        return true;
    }
    private void initData() {
        page = 1;
        loadData();
    }

    private void loadData() {
        // 这里模拟从服务器获取数据
        new GetAnnouncementTask().execute();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if(item.getItemId()==R.id.submit_group){
            View view=getLayoutInflater().inflate(R.layout.activity_new_post,null);

            editTitle=(EditText)view.findViewById(R.id.edit_title);
            editContent=(EditText)view.findViewById(R.id.edit_content);
            editContent.setMinLines(3);
            Button b=(Button)view.findViewById(R.id.submit_post);
            b.setVisibility(View.GONE);
            ImageButton ib=(ImageButton)view.findViewById(R.id.imageButton);
            ib.setVisibility(View.GONE);
            new AlertDialog.Builder(this).setTitle("发布通知")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(view)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String title = editTitle.getText().toString();
                            String content=editContent.getText().toString();
                            if (title.equals("")) {
                                Toast.makeText(getApplicationContext(), "标题不能为空！" + input, Toast.LENGTH_LONG).show();
                            }
                            else {
                                new NewAnnouncementTask().execute(title,content);
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE
                &&lastVisibleIndex==adapter.getCount()){

            page++;
            new GetAnnouncementTask().execute();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        lastVisibleIndex=firstVisibleItem+visibleItemCount;
        newIndex=firstVisibleItem;
    }
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Override
    public void onRefresh() {
        if(refreshLayout.isRefreshing()){
            announcementList=new Announcement[]{};
            page=1;
            new GetAnnouncementTask().execute();
        }
    }

    public class GetAnnouncementTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        public void onPreExecute(){
            footer.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("page", String.valueOf(page)));
                arrayList.add(new Parameters("course_id", courseId));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("ANNOUNCEMENT_INFO"),
                        arrayList,WebConnection.CONNECT_GET);
                AnnouncementResult announcementResult = new Gson().fromJson(parameters.value, AnnouncementResult.class);
                if(announcementResult.getSuccess()) {
                    announcementList= concat(announcementList,announcementResult.getAnnouncements());
                    if (announcementResult.getAnnouncements().length > 0)
                        return true;
                    else{
                        page--;
                        return false;
                    }

                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result){
            footer.setVisibility(View.GONE);
            if(!result){
                return;
            }
            //myLayout.setAdapter(new AnnouncementAdapter(AnnouncementActivity.this,R.layout.announcement_item,announcementList));
            Log.e("1","2");
            adapter=new AnnouncementAdapter(AnnouncementActivity.this,R.layout.announcement_item,announcementList);
            myLayout.setAdapter(adapter);
            myLayout.setSelection(newIndex);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
    public class NewAnnouncementTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
            arrayList.add(new Parameters("_id", Constants._id));
            arrayList.add(new Parameters("course_id",courseId));
            arrayList.add(new Parameters("title",params[0]));
            arrayList.add(new Parameters("content",params[1]));
            Parameters parameters = WebConnection.connect(Constants.privateBaseUrl+"/notification/post",
                    arrayList,WebConnection.CONNECT_POST);
            return parameters.name.equals("200");
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(getApplicationContext(),"发布失败！",Toast.LENGTH_LONG).show();
            }
            onRefresh();
        }
    }
}
