package com.example.onlinelecturefairy.ui.tag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TagsViewModel extends ViewModel {
    MutableLiveData<List<String>> tags;

    public void setTags(List<String> tags) {
        if(this.tags == null) {
            this.tags = new MutableLiveData<>();
        }
        this.tags.setValue(tags);
    }

    public LiveData<List<String>> getTags() {
        return tags;
    }
}
