package com.example.onlinelecturefairy.ui.onlinelecture;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.ColorPicker;
import com.example.onlinelecturefairy.grade.Grade;
import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineLectureFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;
    OnlineLectureFragmentViewModel model;
    private ArrayList<OnlineLecture> lectures;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_lecture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(this).get(OnlineLectureFragmentViewModel.class);

        lectures = new ArrayList<>();
        OnlineLecture lec1 = new OnlineLecture("인성학개론", "11주차", "2019년 11월 12일", "F", OnlineLectureAdapter.HEADER);
        ArrayList<OnlineLecture> child1 = new ArrayList<>();
        child1.add(new OnlineLecture("인성학개론", "10주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        child1.add(new OnlineLecture("인성학개론", "9주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        child1.add(new OnlineLecture("인성학개론", "8주차", "2019년 11월 12일", "F", OnlineLectureAdapter.CHILD));
        child1.add(new OnlineLecture("인성학개론", "7주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        lec1.invisibleChildren = child1;
        lectures.add(lec1);
        lectures.add(new OnlineLecture("신나고 재밌는 안드로이드 스튜디오", "11주차", "2019년 11월 12일", "F", OnlineLectureAdapter.HEADER));
        lectures.add(new OnlineLecture("신나고 재밌는 안드로이드 스튜디오", "10주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        lectures.add(new OnlineLecture("신나고 재밌는 안드로이드 스튜디오", "9주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        lectures.add(new OnlineLecture("신나고 재밌는 안드로이드 스튜디오", "8주차", "2019년 11월 12일", "F", OnlineLectureAdapter.CHILD));
        lectures.add(new OnlineLecture("신나고 재밌는 안드로이드 스튜디오", "7주차", "2019년 11월 12일", "P", OnlineLectureAdapter.CHILD));
        for(OnlineLecture o : lectures) {
            ColorPicker.addLectureId(o.getLecture());
        }

        model.setLectures(lectures);
        model.getLectures().observe(getActivity(), lectures -> {
            //UI updates.
            RecyclerView recyclerView = getView().findViewById(R.id.onlineLectureRecycleView);
            OnlineLectureAdapter adapter = (OnlineLectureAdapter) recyclerView.getAdapter();
            if(adapter != null) {
                adapter.setLectures(lectures);
            } else {
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                adapter = new OnlineLectureAdapter(lectures);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(manager);
            }
        });

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.onlineLectureSwipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    @Override
    public void onRefresh() {

    }
}
