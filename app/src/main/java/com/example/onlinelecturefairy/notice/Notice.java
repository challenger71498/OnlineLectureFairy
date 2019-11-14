package com.example.onlinelecturefairy.notice;

import com.example.onlinelecturefairy.common.StringParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import static com.example.onlinelecturefairy.common.KomoranLoader.komoran;

public class Notice {
    private String lecture;
    private String title;
    private String calendar;
    private String description;
    private String subId;
    private List<Token> tokens; //for NLPx
    private TreeSet<String> tags;  //for tagging

    public Notice(String lecture, String title, String calendar, String description) {
        this.lecture = lecture;
        this.title = title;
        this.calendar = calendar;
        this.description = description;
        setTagsByDescription();
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
        setTagsByDescription();
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

    public TreeSet<String> getTags() {
        return tags;
    }

    public void setTags(TreeSet<String> tags) {
        this.tags = tags;
    }

    public void setTagsByDescription() {
        if (description != null) {
            tags = new TreeSet<>();

            KomoranResult res;

            res = komoran.analyze(title);
            tags.addAll(StringParser.findTestAtString(res, 0));

            res = komoran.analyze(description);

            tags.addAll(StringParser.findTestAtString(res, 0));

            ArrayList<Date> dates = StringParser.findDateAtString(res, 0);
            ArrayList<String> lists = new ArrayList<>();
            GregorianCalendar cal = new GregorianCalendar();
            for (Date d : dates) {
                cal.setTime(d);
                lists.add((cal.get(Calendar.MONTH) + 1) + "월_" + cal.get(Calendar.DAY_OF_MONTH) + "일");
            }

            tags.addAll(lists);
        }
    }

    public List<Token> analyze(String str) {
        return komoran == null ? null : komoran.analyze(str).getTokenList();
    }

    public String toStringToken(List<Token> tokens) {
        return komoran == null ? "" : komoran.analyze(getDescription()).getPlainText();
    }

}
