package com.example.onlinelecturefairy;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

//            EditTextPreference everytimePreference = getPreferenceManager().findPreference("everytimeAddress");
//            everytimePreference.setOnPreferenceClickListener(preference -> {
//                View promptsView = LayoutInflater.from(getActivity()).inflate(R.layout.prompts ,null);
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
//
//                alertDialogBuilder.setView(promptsView);
//                final EditText userInput = promptsView.findViewById(R.id.editTextDialogUserInput);
//                //set dialog message
//                alertDialogBuilder
//                        .setCancelable(false)
//                        .setPositiveButton("OK",
//                                (dialog, id) -> {
//                                    //get user input and set everytime URL.
//                                    String[] temp = userInput.getText().toString().split("@");
//                                    getPreferenceManager().getSharedPreferences().edit()
//                                            .putString("everytimeId", temp[1])
//                                            .apply();
//                                })
//                        .setNegativeButton("Cancel",
//                                (dialog, which) -> dialog.cancel());
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                alertDialog.show();
//                return false;
//            });
        }
    }
}