package com.example.onlinelecturefairy.ui.notice;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.NoticeCardViewBinding;
import com.example.onlinelecturefairy.notice.Notice;
import com.example.onlinelecturefairy.ui.tag.TagRecyclerViewAdapter;
import com.example.onlinelecturefairy.ui.tag.TagsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder> implements Filterable {

    private List<Notice> notices;
    private List<Notice> filteredNotices;
    private Fragment fragment;

    public NoticeRecyclerAdapter(List<Notice> notices, Fragment fragment) {
        this.notices = notices;
        this.filteredNotices = notices;
        this.fragment = fragment;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
//        this.filteredNotices = notices;
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
        Notice notice = filteredNotices.get(position);

        NoticeViewModel model = new NoticeViewModel();
        model.setNotice(notice);

        viewHolder.setViewModel(model);

        TagsViewModel tagsModel = new TagsViewModel();

        tagsModel.setTags(notice.getTags());

        tagsModel.getTags().observe(fragment, tags -> {
            RecyclerView rv = viewHolder.binding.tagRecyclerVIew;
            rv.setNestedScrollingEnabled(false);
            TagRecyclerViewAdapter adapter = (TagRecyclerViewAdapter) rv.getAdapter();
            if (adapter != null) {
                adapter.setTags(tags);
//                notifyDataSetChanged();
            } else {
                adapter = new TagRecyclerViewAdapter(tags);
                rv.setAdapter(adapter);
                int spanCount = adapter.getItemCount() / 4;
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(spanCount == 0 ? 1 : spanCount, StaggeredGridLayoutManager.HORIZONTAL);
                rv.setLayoutManager(manager);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredNotices == null ? 0 : filteredNotices.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<String> str = new ArrayList<>(Arrays.asList(constraint.toString().split("[ ,]")));   //space 나 comma로 띄움
                //Log.e(TAG, "NOTICE_RECYCLER_ADAPTER_FILTER_STRING : " + TextUtils.join("+", str));
                if (constraint.toString().isEmpty()) {
                    filteredNotices = new ArrayList<>(notices);
                } else {
                    LinkedHashSet<Notice> filteringList = new LinkedHashSet<>();
                    for (Notice n : new ArrayList<>(notices)) {
                        HashSet<String> tags = new HashSet<>(n.getTags());
                        boolean foundAll = true;
                        for (String compare : str) {
                            boolean found = false;
                            for (String s : tags) {
                                if (s.contains(compare)) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                foundAll = false;
                                break;
                            }
                        }
                        if(foundAll) {
                            filteringList.add(n);
                        }
                    }
                    filteredNotices = new ArrayList<>(filteringList);
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredNotices;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredNotices = (ArrayList<Notice>) results.values;
                notifyDataSetChanged();
            }
        };
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
