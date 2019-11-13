package com.example.onlinelecturefairy.ui.tag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.TreeSet;

public class TagsViewModel extends ViewModel {
    MutableLiveData<TreeSet<String>> tags;

    public void setTags(TreeSet<String> tags) {
        if(this.tags == null) {
            this.tags = new MutableLiveData<>();
        }
        this.tags.setValue(tags);
    }

    public LiveData<TreeSet<String>> getTags() {
        return tags;
    }
}
