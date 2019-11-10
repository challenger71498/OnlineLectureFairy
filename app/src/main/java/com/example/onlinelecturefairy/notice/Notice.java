package com.example.onlinelecturefairy.notice;

public class Notice {
    String lecture;
    String title;
    String calendar;
    String description;

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Notice(String lecture, String title, String calendar, String description) {
        this.lecture = lecture;
        this.title = title;
        this.calendar = calendar;
        this.description = description;
    }
}
