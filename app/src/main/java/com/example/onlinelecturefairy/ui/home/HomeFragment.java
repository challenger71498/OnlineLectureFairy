package com.example.onlinelecturefairy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.notice.Notice;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeViewModel model = ViewModelProviders.of(this).get(HomeViewModel.class);

        //테스트 초기값 설정
        ArrayList<Notice> ntc = new ArrayList<>();
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다. 이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "이 곳에 공지사항의 설명이 들어갈 예정입니다."));
        ntc.add(new Notice("test", "공지", "test", "항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다. 이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다."));
        ntc.add(new Notice("test", "공지", "test", "항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다. 이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));
        ntc.add(new Notice("test", "공지", "test", "이 곳에 공지사항의 설명이 들어갈 예정입니다."));
        ntc.add(new Notice("test", "공지", "test", "테스트 설명 항목입니다. 이 곳에 공지사항의 설명이 들어갈 예정입니다. 항목이 너무 길어질 경우 사이즈가 자동으로 늘어납니다."));

        model.setNotices(ntc);

        model.getNotices().observe(this, notices -> {
            //UI updates.
            RecyclerView recyclerView = getView().findViewById(R.id.homeRecyclerView);
            HomeRecyclerAdapter adapter = (HomeRecyclerAdapter) recyclerView.getAdapter();
            if(adapter != null) {
                adapter.setNotices(notices);
            } else {
                LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                adapter = new HomeRecyclerAdapter(notices);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(manager);
            }
        });

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.swipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    @Override
    public void onRefresh() {
        //TODO:새로 고치는 코드

        //Should stop refreshing.
        swipe.setRefreshing(false);
    }


}