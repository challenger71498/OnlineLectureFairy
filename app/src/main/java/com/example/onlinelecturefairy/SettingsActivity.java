package com.example.onlinelecturefairy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                //set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인",
                                (dialog, id) -> {
                                    // 자동 로그인을 해제 (로그아웃했으므로)
                                    SharedPreferences pref = getPreferenceManager().getSharedPreferences();
                                    pref.edit()
                                            .putBoolean("autoLogin", false)
                                            .apply();

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