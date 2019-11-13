package com.example.onlinelecturefairy.ui.tag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TagViewModel extends ViewModel {
    MutableLiveData<String> tagName;

    public void setName(String s) {
        if(tagName == null) {
            tagName = new MutableLiveData<>();
        }
        tagName.setValue(s);
    }

    public LiveData<String> getName() {
        return tagName;
    }
}
