package com.netlab.vc.coursehelper.util.jsonResults;

/**
 * Created by dingfeifei on 17/1/3.
 */

public class Reply{

    private String replyUser_id;//回复人的 id String
    //private String
    private String content;//回复内容 String
    private Number like;//回复的被点赞数 Number
    private Long postDate;//回复时间 Date
    private Boolean replyed;//回复是否被回复过 bool
    private String reply_id;//回复的 id String
    private String replyUser_name;

    public Boolean getReplyed() {
        return replyed;
    }

    public String getReplyUser_name() {
        return replyUser_name;
    }

    public void setReplyUser_name(String replyUser_name) {
        this.replyUser_name = replyUser_name;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getReplyUser_id() {
        return replyUser_id;
    }

    public void setReplyUser_id(String replyUser_id) {
        this.replyUser_id = replyUser_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Number getLike() {
        return like;
    }

    public void setLike(Number like) {
        this.like = like;
    }

    public Long getPostDate() {
        return postDate;
    }

    public void setPostDate(Long postDate) {
        this.postDate = postDate;
    }

    public Boolean isReplyed() {
        return replyed;
    }

    public void setReplyed(Boolean replyed) {
        this.replyed = replyed;
    }

    Reply(){}
}
