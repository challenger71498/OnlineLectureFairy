package com.example.onlinelecturefairy.notice;

import java.util.List;

import kr.co.shineware.nlp.komoran.model.Token;

import static com.example.onlinelecturefairy.common.KomoranLoader.komoran;

public class Notice {
    private String lecture;
    private String title;
    private String calendar;
    private String description;
    private String subId;
    private List<Token> tokens; //for NLPx
    private List<String> tags;  //for tagging

    public Notice(String lecture, String title, String calendar, String description) {
        this.lecture = lecture;
        this.title = title;
        this.calendar = calendar;
        this.description = description;
        tokens = analyze(description);
    }

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
        tokens = analyze(description);
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Token> analyze(String str) {
        return komoran == null ? null : komoran.analyze(str).getTokenList();
    }

    public String toStringToken(List<Token> tokens) {
        return komoran == null ? "" : komoran.analyze(getDescription()).getPlainText();
    }

}
