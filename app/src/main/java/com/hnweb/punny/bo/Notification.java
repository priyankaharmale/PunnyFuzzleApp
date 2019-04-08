package com.hnweb.punny.bo;

public class Notification {


    String nid,from_user_id,to_user_id,type,msg,updated_dt,view_by_to,username,email;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUpdated_dt() {
        return updated_dt;
    }

    public void setUpdated_dt(String updated_dt) {
        this.updated_dt = updated_dt;
    }

    public String getView_by_to() {
        return view_by_to;
    }

    public void setView_by_to(String view_by_to) {
        this.view_by_to = view_by_to;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
