package com.example.onlinelecturefairy.ui.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlinelecturefairy.MainActivity;
import com.example.onlinelecturefairy.R;

public class TutorialStart extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button b = getView().findViewById(R.id.tutorialStartButton);
        b.setOnClickListener(v -> {
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
        });
    }
}
