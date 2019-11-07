package com.example.onlinelecturefairy.ui.event;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {
    private FragmentEventBinding binding;

    private final Activity activity;

    public EventFragment(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        binding = DataBindingUtil.setContentView(activity, R.layout.fragment_event);

        EventViewModel model = ViewModelProviders.of(this).get(EventViewModel.class);
        model.getEvent().observe(this, event -> {
            //update UI.
            binding.setEvent(event);
        });

        return view;
    }
}
