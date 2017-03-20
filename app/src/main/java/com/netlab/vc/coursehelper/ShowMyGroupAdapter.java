package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.jsonResults.Member;

/**
 * Created by dingfeifei on 16/12/11.
 */

public class ShowMyGroupAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Member[] memberList;

    private String temp;
    public ShowMyGroupAdapter(Context context, int layout, Member[] memberList) {
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.memberList=memberList;
    }

    @Override
    public int getCount() {
        return memberList.length;
    }

    @Override
    public Object getItem(int position) {
        return memberList[position];
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
        TextView memberNameView=(TextView)view.findViewById(R.id.group_member_name);
        //TextView groupMemberView=(TextView)view.findViewById(R.id.group_member);
        //ImageView addImgView=(ImageView)view.findViewById(R.id.add_image);
        memberNameView.setText(memberList[position].getMember_name());

        /*
        int i = 0;
        if (groupList[position].getMember()!= null) {
            while (groupList[position].getMember()[i].getMember_name() != null) {
                temp += groupList[position].getMember()[i].getMember_name();
                temp += " ";
                i++;
            }
        }
        groupMemberView.setText(temp);
        */
        return view;
    }
}
