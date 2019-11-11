package com.example.onlinelecturefairy.grade;

public class Grade {
    private String lecture;
    private String score;
    private String scoreSub;
    private String description;

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
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getScoreSub() {
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
