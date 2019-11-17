package com.example.onlinelecturefairy;

import android.app.AlertDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.example.onlinelecturefairy.common.ScheduleJob;

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

            ListPreference notificationCheck = getPreferenceManager().findPreference("notificationCheck");
            notificationCheck.setOnPreferenceChangeListener((preference, newValue) -> {
                ScheduleJob s = new ScheduleJob();
                s.refreshBackground(SettingsFragment.this.getActivity().getApplicationContext());
                s.startBackground(SettingsFragment.this.getActivity().getApplicationContext());
                return true;
            });

            PreferenceScreen notificationSyncNow = getPreferenceManager().findPreference("syncBackground");
            notificationSyncNow.setOnPreferenceClickListener(preference -> {
                ScheduleJob s = new ScheduleJob();
                s.startBackground(SettingsFragment.this.getActivity().getApplicationContext());
                return true;
            });


            SwitchPreferenceCompat everytimeSync = getPreferenceManager().findPreference("everytimeSync");
            everytimeSync.setOnPreferenceChangeListener((preference, newValue) -> {
                if((boolean) newValue) {
                    ScheduleJob s = new ScheduleJob();
                    s.refreshGoogle(SettingsFragment.this.getActivity().getApplicationContext());
                    s.startGoogle(SettingsFragment.this.getActivity().getApplicationContext());
                }
                return true;
            });

            PreferenceScreen everytimeSyncNow = getPreferenceManager().findPreference("syncGoogle");
            everytimeSyncNow.setOnPreferenceClickListener(preference -> {
                ScheduleJob s = new ScheduleJob();
                s.startGoogle(SettingsFragment.this.getActivity().getApplicationContext());
                return true;
            });

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