package com.example.onlinelecturefairy.ui.grade;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.ColorPicker;
import com.example.onlinelecturefairy.grade.Grade;
import com.example.onlinelecturefairy.notice.Notice;
import com.example.onlinelecturefairy.ui.notice.NoticeRecyclerAdapter;

import java.util.ArrayList;

public class GradeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;
    GradeFragmentViewModel model;
    private ArrayList<Grade> grades;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grade, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //성적 샘플 데이터
        grades = new ArrayList<>();
        grades.add(new Grade("(2019-02)동화의이해-001(이러닝)", "12.3", "/30", "중간고사 성적"));
        grades.add(new Grade("인성학개론", "55.5", "/60", "기말고사 성적"));
        grades.add(new Grade("(2019-02)동화의이해-001(이러닝)", "12.3", "/30", "중간고사 성적"));
        grades.add(new Grade("신나고 재밌는 안드로이드 스튜디오", "12.3", "/30", "중간고사 성적"));
        grades.add(new Grade("(2019-02)동화의이해-001(이러닝)", "12.3", "/30", "중간고사 성적"));
        for(Grade g : grades) {
            ColorPicker.addLectureId(g.getLecture());
        }

        model = ViewModelProviders.of(this).get(GradeFragmentViewModel.class);

        model.setGrades(grades);
        model.getGrades().observe(getActivity(), notices -> {
            //UI updates.
            RecyclerView recyclerView = getView().findViewById(R.id.gradeRecyclerView);
            GradeRecyclerAdapter adapter = (GradeRecyclerAdapter) recyclerView.getAdapter();
            if(adapter != null) {
                adapter.setGrades(grades);
            } else {
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                adapter = new GradeRecyclerAdapter(grades);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(manager);
            }
        });

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.gradeSwipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        //Crawling
    }

    @Override
    public void onRefresh() {
        //Crawling Method.
    }
}