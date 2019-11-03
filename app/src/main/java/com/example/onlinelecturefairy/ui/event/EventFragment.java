package com.example.onlinelecturefairy.ui.event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onlinelecturefairy.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EventFragment extends Fragment {
    TextView date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event, container, false);

        date = view.findViewById(R.id.summaryText);

        set("Test", R.color.colorAccent);

        return view;
    }

    //Set function.
    public void set(String s, int c) {
        setSummary(s);
        setColor(c);
    }

    //Sets summary(title).
    private void setSummary(String s) {
        date.setText(s);
    }

    //Sets color.
    private void setColor(int color) {
        Objects.requireNonNull(getView()).setBackgroundColor(getResources().getColor(color));
    }
}
