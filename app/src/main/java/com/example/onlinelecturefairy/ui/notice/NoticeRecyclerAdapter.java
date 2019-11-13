package com.example.onlinelecturefairy.ui.notice;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.NoticeCardViewBinding;
import com.example.onlinelecturefairy.notice.Notice;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class NoticeRecyclerAdapter extends RecyclerView.Adapter<NoticeRecyclerAdapter.ViewHolder> {
    List<Notice> notices;

    public NoticeRecyclerAdapter(List<Notice> notices) {
        this.notices = notices;
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
        NoticeViewModel model = new NoticeViewModel();
        model.setNotice(notice);
        viewHolder.setViewModel(model);
    }

    @Override
    public int getItemCount() {
        return notices == null ? 0 : notices.size();
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
