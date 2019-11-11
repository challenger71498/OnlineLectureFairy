package com.example.onlinelecturefairy.lecture;

public class Lecture {
    private String id;
    private int colorId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public Lecture(String id, int colorId) {
        this.id = id;
        this.colorId = colorId;
    }
}
