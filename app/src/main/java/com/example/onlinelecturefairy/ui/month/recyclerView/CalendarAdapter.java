//package com.example.onlinelecturefairy.ui.month.recyclerView;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.databinding.DataBindingUtil;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.onlinelecturefairy.R;
//import com.example.onlinelecturefairy.ui.calendar_day.DayCalendarViewModel;
//import com.example.onlinelecturefairy.databinding.DayItemBinding;
//
//import java.util.Calendar;
//import java.util.List;
//
//public class CalendarAdapter extends RecyclerView.Adapter {
//    private final int HEADER_TYPE = 0;
//    private final int EMPTY_TYPE = 1;
//    private final int DAY_TYPE = 2;
//
//    private List<Object> mCalendarList;
//
//    public CalendarAdapter(List<Object> calendarList) {
//        mCalendarList = calendarList;
//    }
//
//    public void setCalendarList(List<Object> calendarList) {
//        mCalendarList = calendarList;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemViewType(int position) { //뷰타입 나누기
//        Object item = mCalendarList.get(position);
//        if (item instanceof Long) {
//            return HEADER_TYPE; //날짜 타입
//        } else if (item instanceof String) {
//            return EMPTY_TYPE; // 비어있는 일자 타입
//        } else {
//            return DAY_TYPE; // 일자 타입
//
//        }
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        DayItemBinding binding =
//                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.calendar_day, parent, false);// 일자 타입
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
//        DayViewHolder holder = (DayViewHolder) viewHolder;
//        Object item = mCalendarList.get(position);
//        DayCalendarViewModel model = new DayCalendarViewModel();
//        if (item instanceof Calendar) {
//            model.initGoal(holder.itemView.getContext());
//            model.setCalendar((Calendar) item);
//        }
//        holder.setViewModel(model);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (mCalendarList != null) {
//            return mCalendarList.size();
//        }
//        return 0;
//    }
//
//    private class DayViewHolder extends RecyclerView.ViewHolder {// 요일 타입 ViewHolder
//        private DayItemBinding binding;
//
//        private DayViewHolder(@NonNull DayItemBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//
//        private void setViewModel(DayCalendarViewModel model) {
//            binding.setModel(model);
//            binding.executePendingBindings();
//        }
//    }
//}
