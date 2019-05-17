package com.hnweb.punnyfuzzleiap.punnyfuzzle.bo;


/* Created by Priyanka Harmale on 29/12/2018. */
public class Puzzle {
    private String id;
    private String pnumber;
    private String section;
    private String primary_word;
    private String image_caption;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    private String image_url,score;

    public String getPlayed_status() {
        return played_status;
    }

    public void setPlayed_status(String played_status) {
        this.played_status = played_status;
    }

    private String full_image_path;
    private String solution;
    private String added_date;
    private String status;
    private String options;
    private String spoonerism;
    private String played_status;


    private String option1;
    private String option2;
    private String option3;

    private Boolean isFree = false;
    private Boolean isAttempted = false;
    private Boolean isRight = false;

    public Puzzle() {
    }

    public Puzzle(String id, String pnumber, String section, String primary_word, String image_caption, String image_url,
                  String full_image_path, String solution, String added_date, String status, String options, String spoonerism) {
        this.id = id;
        this.pnumber = pnumber;
        this.section = section;
        this.primary_word = primary_word;
        this.image_caption = image_caption;
        this.image_url = image_url;
        this.full_image_path = full_image_path;
        this.solution = solution;
        this.added_date = added_date;
        this.status = status;
        this.options = options;
        this.spoonerism = spoonerism;

        setOptions();
    }

    public void setOptions() {
        String[] strOptions = options.split("\\,");
        option1 = strOptions[0];
        option2 = strOptions[1];
        option3 = strOptions[2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPrimary_word() {
        return primary_word;
    }

    public void setPrimary_word(String primary_word) {
        this.primary_word = primary_word;
    }

    public String getImage_caption() {
        return image_caption;
    }

    public void setImage_caption(String image_caption) {
        this.image_caption = image_caption;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getFull_image_path() {
        return full_image_path;
    }

    public void setFull_image_path(String full_image_path) {
        this.full_image_path = full_image_path;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
        setOptions();
    }

    public String getSpoonerism() {
        return spoonerism;
    }

    public void setSpoonerism(String spoonerism) {
        this.spoonerism = spoonerism;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public Boolean getAttempted() {
        return isAttempted;
    }

    public void setAttempted(Boolean attempted) {
        isAttempted = attempted;
    }

    public Boolean getRight() {
        return isRight;
    }

    public void setRight(Boolean right) {
        isRight = right;
    }

    public Boolean getFree() {
        return isFree;
    }

    public void setFree(Boolean free) {
        isFree = free;
    }
}
