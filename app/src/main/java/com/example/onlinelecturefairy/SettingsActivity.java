package com.example.onlinelecturefairy;

import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // 로그아웃 버튼 기능 추가
            PreferenceScreen logoutPreference = getPreferenceManager().findPreference("logout");
            logoutPreference.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertLightDialogStyle);
                //set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인",
                                (dialog, id) -> {
                                    // 자동 로그인을 해제 (로그아웃했으므로)
                                    SharedPreferences pref = getPreferenceManager().getSharedPreferences();
                                    pref.edit()
                                            .putBoolean("autoLogin", false)
                                            .putString("everytimeAddress", "")
                                            .putString("PW", "")
                                            .apply();

                                    // Background Service 종료
                                    JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                                    scheduler.cancelAll();

                                    // LoginActivity로 이동
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                })
                        .setNegativeButton("취소",
                                (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
                return false;
            });
        }
    }
}