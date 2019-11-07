package com.example.onlinelecturefairy.event;

public interface IEvent {
    String getSummary();
    void setSummary(String summary);

    String getDescription();
    void setDescription(String description);

    String getLocation();
    void setLocation(String location);

    int getColor();
    void setColor(int color);


}
