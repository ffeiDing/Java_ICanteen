package com.netlab.vc.coursehelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.DateFormatter;
import com.netlab.vc.coursehelper.util.jsonResults.Reply;

/**
 * Created by Vc on 2017/1/3.
 */

public class ThreadListAdapter extends BaseAdapter implements ListAdapter {
    private LayoutInflater viewInflater;
    private final Context context;
    private final int layout;
    private Reply[] replies;
    private LikeCallBack mCallBack;
    public interface LikeCallBack {
        public void clickLike(View v,String a,String b,int c);
    }


    public ThreadListAdapter(Context context,int layout,Reply[] replies,LikeCallBack likeCallBack){
        this.viewInflater = LayoutInflater.from(context);
        this.context=context;
        this.layout=layout;
        this.replies=replies;
        this.mCallBack=likeCallBack;
    }
    @Override
    public int getCount() {
        return replies.length;
    }

    @Override
    public Object getItem(int position) {
        return replies[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null){
            view=viewInflater.inflate(layout,parent,false);
        }
        TextView userNameView= (TextView) view.findViewById(R.id.user_name);
        TextView threadContentView=(TextView)view.findViewById(R.id.thread_content);
        TextView postTimeView=(TextView)view.findViewById(R.id.post_time);
        TextView likeNumberView=(TextView)view.findViewById(R.id.like_number);
        //ImageView avatarView=(ImageView)view.findViewById(R.id.user_avatar);
        ImageView likeView=(ImageView)view.findViewById(R.id.like_button);
        likeView.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        //avatarView.setImageResource(R.drawable.common_google_signin_btn_icon_dark);
        userNameView.setText(replies[position].getReplyUser_name());
        threadContentView.setText(replies[position].getContent());
        postTimeView.setText(DateFormatter.format(replies[position].getPostDate()));
        likeNumberView.setText(String.valueOf(replies[position].getLike()));
        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.clickLike(v,replies[position].getReply_id(),"reply",position);
            }
        });
        return view;
    }
}
