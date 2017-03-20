package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.jsonResults.Group;

/**
 * Created by dingfeifei on 16/12/11.
 */

public class GroupAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Group[] groupList;
    private String temp;
    public GroupAdapter(Context context, int layout, Group[] groupList) {
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.groupList=groupList;
    }

    @Override
    public int getCount() {
        return groupList.length;
    }

    @Override
    public Object getItem(int position) {
        return groupList[position];
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
        TextView groupNameView=(TextView)view.findViewById(R.id.group_name);
        TextView groupLeaderView=(TextView)view.findViewById(R.id.group_leader);
        TextView groupMemberView=(TextView)view.findViewById(R.id.group_member);
        //ImageView addImgView=(ImageView)view.findViewById(R.id.add_image);
        groupNameView.setText(groupList[position].getGroup_name());
        groupLeaderView.setText(groupList[position].getLeader_name());
        int l = groupList[position].getMember().length;
        temp = " ";
        if (l >= 1) {
            for (int i = 0; i < l; i++) {
                temp += groupList[position].getMember()[i].getMember_name();
                temp += "  ";
            }
        }

        groupMemberView.setText(temp);
        return view;
    }
}
