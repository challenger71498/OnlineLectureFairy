package com.example.onlinelecturefairy.ui.notice;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.NoticeCardViewBinding;
import com.example.onlinelecturefairy.notice.Notice;
import com.example.onlinelecturefairy.ui.tag.TagRecyclerViewAdapter;
import com.example.onlinelecturefairy.ui.tag.TagsViewModel;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder> implements LifecycleOwner, LifecycleObserver {
    private LifecycleRegistry registry = new LifecycleRegistry(this);

    List<Notice> notices;
    Fragment fragment;

    public NoticeRecyclerAdapter(List<Notice> notices, Fragment fragment) {
        this.notices = notices;
        this.fragment = fragment;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoticeCardViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.notice_card_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Notice notice = notices.get(position);

        ArrayList<String> str = new ArrayList<>();
        str.add("hello");
        str.add("hi");
        str.add("wassup");
        str.add("hello");
        str.add("hi");
        str.add("wassup");
        str.add("hello");
        str.add("hi");
        str.add("wassup");
        str.add("hi");
        str.add("wassup");
        notice.setTags(str);

        NoticeViewModel model = new NoticeViewModel();
        model.setNotice(notice);

        Log.e(TAG, "You got tags : " + notice.getTags().get(0));

        viewHolder.setViewModel(model);

        TagsViewModel tagsModel = new TagsViewModel();

        tagsModel.setTags(notice.getTags());

        tagsModel.getTags().observe(fragment, tags -> {
            RecyclerView rv = viewHolder.binding.tagRecyclerVIew;
            TagRecyclerViewAdapter adapter = (TagRecyclerViewAdapter) rv.getAdapter();
            if(adapter != null) {
                adapter.setTags(tags);
//                notifyDataSetChanged();
            } else {
                adapter = new TagRecyclerViewAdapter(tags);
                rv.setAdapter(adapter);
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(adapter.getItemCount() / 4, StaggeredGridLayoutManager.HORIZONTAL);
                rv.setLayoutManager(manager);
            }
            Log.e(TAG, "Adapter size: " + adapter.getItemCount() );
        });
    }

    @Override
    public int getItemCount() {
        return notices == null ? 0 : notices.size();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateEvent(LifecycleOwner owner, Lifecycle.Event event) {
        registry.handleLifecycleEvent(event);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NoticeCardViewBinding binding;

        public ViewHolder(@NonNull NoticeCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(NoticeViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
