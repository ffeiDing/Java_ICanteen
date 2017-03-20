package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.DateFormatter;
import com.netlab.vc.coursehelper.util.jsonResults.Announcement;

/**
 * Created by dingfeifei on 16/12/23.
 */

public class AnnouncementAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Announcement[] announcementList;
    public AnnouncementAdapter(Context context, int layout, Announcement[] announcementList) {
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.announcementList=announcementList;
    }

    @Override
    public int getCount() {
        return announcementList.length;
    }

    @Override
    public Object getItem(int position) {
        return announcementList[position];
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
        TextView announcementNameView=(TextView)view.findViewById(R.id.announcement_listitem_title);
        TextView announcementContentView=(TextView)view.findViewById(R.id.announcement_listitem_text);
        TextView announcementTimeView=(TextView)view.findViewById(R.id.announcement_listitem_time);
        announcementNameView.setText(announcementList[position].getTitle());
        announcementContentView.setText(announcementList[position].getContent());
        announcementTimeView.setText(DateFormatter.format(announcementList[position].getCreate_at()));
        return view;
    }
}

