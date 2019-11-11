package com.example.onlinelecturefairy.legacy.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.onlinelecturefairy.R;

public class TimetableFragment extends Fragment {

    private TimetableViewModel timetableViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timetable, container, false);
    }
}