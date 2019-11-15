package com.example.onlinelecturefairy.ui.tutorial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.onlinelecturefairy.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class TutorialBatteryManagement extends Fragment {
    PowerManager pm;
    String packageName;
    Button b;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_battery_management, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        packageName = getActivity().getPackageName();
        b = getView().findViewById(R.id.batterybutton);

        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            b.setEnabled(false);
            b.setText("배터리 관리 설정이 완료되었습니다.");
        }

        b.setOnClickListener(v -> {
            Log.e(TAG, "onViewCreated: PRESSED");

            Intent i = new Intent();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                i.setData(Uri.parse("package:" + packageName));
            }

            this.startActivityForResult(i, 0);
//
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//
//            ft.detach(this).attach(this).commitNow();

//            Intent i = new Intent();
//            i.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
//            i.setData(Uri.parse("package:"+getActivity().getPackageName()));
//            startActivity(i);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            b.setEnabled(false);
            b.setText("배터리 관리 설정이 완료되었습니다.");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
