package com.example.onlinelecturefairy.grade;

import java.util.HashMap;

public class Grade {
    public static HashMap<String, Float> scoreBoard = new HashMap<>();

    public static final int GRADE_TYPE_NORMAL = 0;
    public static final int GRADE_TYPE_COMMON = 1;

    private String lecture;
    private String score;
    private String scoreSub;
    private String description;

    public int type = GRADE_TYPE_NORMAL;

    public Grade(String lecture, String score, String scoreSub, String description) {
        this.lecture = lecture;
        this.score = score;
        this.scoreSub = scoreSub;
        this.description = description;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getScore() {
        if(score.contains("-")) return "0";
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreSub() {
        if(scoreSub == "none") return "";
        return scoreSub;
    }

    public void setScoreSub(String scoreSub) {
        this.scoreSub = scoreSub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
