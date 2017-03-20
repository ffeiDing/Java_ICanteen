package com.netlab.vc.coursehelper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.FileMeta;
import com.netlab.vc.coursehelper.util.jsonResults.FileMetaResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String courseId;
    private int lessonId;
    private ListView lessonListView;
    private ProgressBar progressBar;
    private TextView noContent;
    private SwipeRefreshLayout refreshLayout;
    private List<Map<String,Object>> fileList;
    private FileMeta[] fileMetas;
    private DownloadManager downloadManager;
    private BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();
        courseId=intent.getStringExtra("course_id");
        lessonId=intent.getIntExtra("lesson_id",0);
        lessonListView=(ListView)findViewById(R.id.file_list);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        noContent=(TextView)findViewById(R.id.no_contents);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.file_container);
        refreshLayout.setOnRefreshListener(this);
        downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        new GetFilesTask().execute();
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
            new GetFilesTask().execute();
        }
    }
    public class GetFilesTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {

            String queryUrl= Constants.baseUrl+Constants.AddUrls.get("FILE_INFO")+courseId+"/"+lessonId;
            ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
            arrayList.add(new Parameters("_id",Constants._id));
            try{
                Parameters parameters = WebConnection.connect(queryUrl,
                        arrayList, WebConnection.CONNECT_GET);
                fileMetas=new Gson().fromJson(parameters.value,FileMetaResult.class).getFiles();
                fileList=new ArrayList<>();
                for(FileMeta file:fileMetas){
                    Map<String,Object> fileItem=new HashMap<>();
                    fileItem.put("name",file.getName());
                    fileItem.put("user_name",file.getUser_name());
                    //fileItem.put("url",file.getUrl());
                    fileList.add(fileItem);
                }
                return true;
            }catch (Exception e){
                Log.e("error",e.toString());
                return false;
            }

        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                return;
            }
            lessonListView.setAdapter(new DownLoadFileAdapter(
                    LessonActivity.this,
                    fileList,
                    R.layout.file_item,
                    new String[]{"name","user_name"},
                    new int[]{R.id.file_name,R.id.user_name}));
            lessonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url="http://"+fileMetas[position].getUrl()+"?_id="+Constants._id;
                    Uri uri=Uri.parse(url.trim());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.addRequestHeader("x-access-token", Constants.token);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    long reference = downloadManager.enqueue(request);
                    listener(reference);

                }
            });
            progressBar.setVisibility(View.GONE);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
    private void listener(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(getApplicationContext(), "下载完成!", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }
        public class DownLoadFileAdapter extends SimpleAdapter{

        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the
         *                 list. The
         *                 Maps contain the data for each row, and should include all the entries
         *                 specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views
         *                 defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These
         *                 should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        List<? extends Map<String,?>> data;
        public DownLoadFileAdapter(Context context, List<? extends Map<String, ?>> _data, int resource, String[] from, int[] to) {
            super(context, _data, resource, from, to);
            //data=_data;

        }

    }
}
