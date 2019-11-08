package com.example.onlinelecturefairy.ui.monthly;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class MonthlyFragment extends Fragment {
    private MonthlyViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(MonthlyViewModel.class);
        model.getCalendar().observe(this, days -> {
            // UI updates.
            RecyclerView v = getView().findViewById(R.id.recyclerView);
            MonthlyViewAdapter adapter = (MonthlyViewAdapter) v.getAdapter();
            if (adapter != null) {
                Log.e(TAG, "ADAPTER");
                adapter.setDataSet(days);
            } else {
                Log.e(TAG, "NULL");
                GridLayoutManager manager = new GridLayoutManager(getActivity(), 7) {
                    @Override
                    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                        // force size of viewHolder here, this will override layout_height and layout_width from xml
                        lp.height = getHeight() / 6;
                        lp.width = getWidth() / 7;
                        return true;
                    }
                };
                adapter = new MonthlyViewAdapter(getActivity(), days);
                v.setAdapter(adapter);
                v.setLayoutManager(manager);
            }
        });

        if(model != null) {
            model.initCalendar();
        }
    }
}
