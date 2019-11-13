package com.example.onlinelecturefairy.ui.onlinelecture;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.OnlineLectureBinding;
import com.example.onlinelecturefairy.databinding.OnlineLectureSmallBinding;
import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineLectureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<OnlineLecture> lectures;

    public OnlineLectureAdapter(List<OnlineLecture> lectures) {
        this.lectures = lectures;
    }

    public void setLectures(List<OnlineLecture> lectures) {
        this.lectures = lectures;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType) {
            case HEADER:
            {
                OnlineLectureBinding binding
                        = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.online_lecture_card_view, parent, false);
                return new ViewHolder(binding);
            }
            case CHILD:
            {
                OnlineLectureSmallBinding binding
                        = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.online_lecture_card_view_small, parent, false);
                return new SmallViewHolder(binding);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OnlineLecture lecture = lectures.get(position);
        OnlineLectureViewModel model = new OnlineLectureViewModel();
        model.setLecture(lecture);
        switch(lecture.type) {
            case HEADER:
            {
                ViewHolder holder = (ViewHolder) viewHolder;
                holder.setViewModel(model);
                holder.lecture = lecture;
                holder.binding.onlineLectureContent.setOnClickListener(view -> {
                    if (lecture.invisibleChildren == null) {    //리스트에 children 추가
                        lecture.invisibleChildren = new ArrayList<OnlineLecture>();
                        int count = 0;
                        int pos = lectures.indexOf(holder.lecture);
                        while (lectures.size() > pos + 1 && lectures.get(pos + 1).type == CHILD) {
                            lecture.invisibleChildren.add(lectures.remove(pos + 1));
                            count++;
                        }
                        notifyItemRangeRemoved(pos + 1, count);
                        lecture.isClicked = false;
                        notifyItemChanged(pos); // 데이터 수정 명시
                    } else {    //리스트에 children 제거
                        int pos = lectures.indexOf(holder.lecture);
                        int index = pos + 1;
                        for (OnlineLecture i : lecture.invisibleChildren) {
                            lectures.add(index, i);
                            index++;
                        }
                        notifyItemRangeInserted(pos + 1, index - pos - 1);
                        lecture.isClicked = true;
                        notifyItemChanged(pos); // 데이터 수정 명시
                        lecture.invisibleChildren = null;
                    }
                });
            }
            break;
            case CHILD:
            {
                SmallViewHolder holder = (SmallViewHolder) viewHolder;
                holder.setViewModel(model);
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return lectures.get(position).type;
    }

    @Override
    public int getItemCount() {
        return lectures == null ? 0 : lectures.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        OnlineLectureBinding binding;
        OnlineLecture lecture;

        public ViewHolder(@NonNull OnlineLectureBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(OnlineLectureViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }

    public class SmallViewHolder extends RecyclerView.ViewHolder {
        OnlineLectureSmallBinding binding;

        public SmallViewHolder(@NonNull OnlineLectureSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setViewModel(OnlineLectureViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();;
        }
    }
}
