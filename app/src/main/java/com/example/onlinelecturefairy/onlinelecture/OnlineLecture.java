package com.example.onlinelecturefairy.onlinelecture;

import java.util.List;

public class OnlineLecture {
    private String lecture;
    private String week;
    private String date;
    private String pass;

    public List<OnlineLecture> getInvisibleChildren() {
        return invisibleChildren;
    }

    public void setInvisibleChildren(List<OnlineLecture> invisibleChildren) {
        this.invisibleChildren = invisibleChildren;
    }

    public int type;
    public List<OnlineLecture> invisibleChildren;
    public boolean isClicked;

    public OnlineLecture(String lecture, String week, String date, String pass, int type) {
        this.lecture = lecture;
        this.week = week;
        this.date = date;
        this.pass = pass;
        this.type = type;
    }

    public OnlineLecture(String lecture, String week, String date, String pass, int type, List<OnlineLecture> child) {
        this.lecture = lecture;
        this.week = week;
        this.date = date;
        this.pass = pass;
        this.type = type;
        this.invisibleChildren = child;
    }


    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isPassed() {
        return pass.equals("P");
    }

//    public boolean isThisWeek()
//    {
//        GregorianCalendar calendar = new GregorianCalendar();   //Today
//        GregorianCalendar cal = new GregorianCalendar();        //Lecture
//        cal.setTime(date);
//        return calendar.get(Calendar.WEEK_OF_MONTH) == cal.get(Calendar.WEEK_OF_MONTH);
//    }
}
