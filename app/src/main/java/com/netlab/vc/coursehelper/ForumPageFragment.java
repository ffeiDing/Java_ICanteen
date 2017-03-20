package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Forum;
import com.netlab.vc.coursehelper.util.jsonResults.ForumResult;

import java.util.ArrayList;
import java.util.Arrays;


public class ForumPageFragment extends Fragment
        implements AbsListView.OnScrollListener,SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout footer;
    private ListView myLayout;

    private ForumAdapter adapter;
    private int page = 1;
    private int lastVisibleIndex;
    private int newIndex;
    Forum[] forumList;
    String courseId,type;
    public ForumPageFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        courseId=getActivity().getIntent().getStringExtra("course_id");
        forumList=new Forum[]{};
        View view= inflater.inflate(R.layout.fragment_forum_page, container, false);
        refreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        footer=(LinearLayout)view.findViewById(R.id.footer_layout);
        myLayout = (ListView) view.findViewById(R.id.forum_listview);
        myLayout.setOnScrollListener(this);

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                &&lastVisibleIndex==adapter.getCount()){

            page++;
            new GetForumTask().execute();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
            totalItemCount) {
        lastVisibleIndex=firstVisibleItem+visibleItemCount;
        newIndex=firstVisibleItem;
        boolean enable = false;
        if(myLayout != null && myLayout.getChildCount() > 0){
            // check if the first item of the list is visible
            boolean firstItemVisible = myLayout.getFirstVisiblePosition() == 0;
            // check if the top of the first item is visible
            boolean topOfFirstItemVisible = myLayout.getChildAt(0).getTop() == 0;
            // enabling or disabling the refresh layout
            enable = firstItemVisible && topOfFirstItemVisible;
        }
        refreshLayout.setEnabled(enable);
    }
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @Override
    public void onRefresh() {
        if(refreshLayout.isRefreshing()){
            forumList=new Forum[]{};
            page=1;
            new GetForumTask().execute();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        forumList=new Forum[]{};
        page=1;
        new GetForumTask().execute();
    }
    private void initData() {
        page = 1;
        loadData();
    }
    public void setType(String type){
        this.type=type;
    }
    private void loadData() {
        // 这里模拟从服务器获取数据
        new GetForumTask().execute();
    }
    public class GetForumTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        public void onPreExecute(){
            footer.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", courseId));
                arrayList.add(new Parameters("type", type));
                arrayList.add(new Parameters("page", String.valueOf(page)));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("FORUM_INFO"),
                        arrayList,WebConnection.CONNECT_GET);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期的格式，遇到这个格式的数据转为Date对象
                Gson gson = gsonBuilder.create();
                ForumResult forumResult = gson.fromJson(parameters.value, ForumResult.class);
                if(forumResult.getSuccess()) {
                    forumList= concat(forumList,forumResult.getForums());
                    if (forumResult.getForums().length > 0)
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
            adapter = new ForumAdapter(getContext(), R.layout.forum_item, forumList);
            myLayout.setAdapter(adapter);
            myLayout.setSelection(newIndex);
            myLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {


                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra("course_id", courseId);
                        intent.putExtra("posting_id", forumList[position].getPosting_id());
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }
}
