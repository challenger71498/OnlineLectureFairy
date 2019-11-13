package com.example.onlinelecturefairy.ui.tag;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.TagCardViewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> tags;

    public TagRecyclerViewAdapter(TreeSet<String> tags) {
        this.tags = new ArrayList<>();
        this.tags.addAll(tags);
    }

    public void setTags(TreeSet<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TagCardViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tag_card_view, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        String tag = tags.get(position);
        TagViewModel model = new TagViewModel();
        model.setName(tag);

        ViewHolder holder = (ViewHolder) viewHolder;
        holder.setViewModel(model);
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TagCardViewBinding binding;

        public ViewHolder(@NonNull TagCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(TagViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
