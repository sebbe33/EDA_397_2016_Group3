package chalmers.eda397_2016_group3.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import chalmers.eda397_2016_group3.MainActivity;
import chalmers.eda397_2016_group3.R;

/**
 * Created by alexanderjaballah on 16-05-16.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.fragment_settings);
    }


    @Override
    public void onResume() {
        super.onResume();
        //register the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //some preference has changed, maybe we want to tell the user
        switch(key) {
            case "checkBoxTimer":
                if(sharedPreferences.getBoolean(key, true))
                    findPreference(key).setSummary("Turn off timer notification");
                else
                    findPreference(key).setSummary("Turn on timer notification");
                break;
            case "checkBoxTrello":
                if(sharedPreferences.getBoolean(key, true))
                    findPreference(key).setSummary("Turn off Trello notification");
                else
                    findPreference(key).setSummary("Turn on Trello notification");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}


