package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.DateFormatter;
import com.netlab.vc.coursehelper.util.jsonResults.Forum;

/**
 * Created by dingfeifei on 16/12/23.
 */

public class ForumAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Forum[] forumList;
    public ForumAdapter(Context context, int layout, Forum[] forumList) {
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.forumList=forumList;
    }

    @Override
    public int getCount() {
        return forumList.length;
    }

    @Override
    public Object getItem(int position) {
        return forumList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view=viewInflater.inflate(layout,parent,false);
        }
        TextView forumNameView=(TextView)view.findViewById(R.id.forum_listitem_title);
        //TextView forumContentView=(TextView)view.findViewById(R.id.forum_listitem_text);
        TextView announcementTimeView=(TextView)view.findViewById(R.id.forum_listitem_time);
        forumNameView.setText(forumList[position].getTitle());
        TextView forumAuthorView=(TextView)view.findViewById(R.id.forum_listitem_author);
        forumAuthorView.setText(forumList[position].getPostUser_name());
        //forumContentView.setText(forumList[position].getContent());
        announcementTimeView.setText(DateFormatter.format(forumList[position].getPostDate()));
        return view;
    }
}

