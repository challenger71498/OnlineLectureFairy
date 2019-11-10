package com.example.onlinelecturefairy.ui.grade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.onlinelecturefairy.R;

public class GradeFragment extends Fragment {

    private GradeViewModel gradeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        gradeViewModel =
                ViewModelProviders.of(this).get(GradeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_grade, container, false);
        return root;
    }
}