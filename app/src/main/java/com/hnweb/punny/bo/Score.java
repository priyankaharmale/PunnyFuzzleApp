package com.hnweb.punny.bo;

public class Score {

    String pid,title,score,total_puzzel,play_time,username,played_status;

    public String getPid() {
        return pid;
    }

    public String getPlayed_status() {
        return played_status;
    }

    public void setPlayed_status(String played_status) {
        this.played_status = played_status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTotal_puzzel() {
        return total_puzzel;
    }

    public void setTotal_puzzel(String total_puzzel) {
        this.total_puzzel = total_puzzel;
    }

    public String getPlay_time() {
        return play_time;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }
}
