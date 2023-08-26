package com.example.wallpaper;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;

public class MyPreferencesActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }
    protected void onDestroy()
    {
        super.onDestroy();
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, String rootKey)
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
            int wallpaper_number = prefs.getInt("Wallpaper number", 0);
            Preference mpreference;
            switch (wallpaper_number)
            {
                case 0:
                    setPreferencesFromResource(R.xml.prefs_tree_sketch, rootKey);
                    mpreference = findPreference("shiftFactor");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("-?\\d+(\\.\\d+)?")));
                    break;
                case 1:
                    setPreferencesFromResource(R.xml.prefs_snowflake_sketch, rootKey);
                    mpreference = findPreference("numberOfParts");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 50:
                    setPreferencesFromResource(R.xml.prefs_stars_sketch, rootKey);
                    break;
                case 2:
                    setPreferencesFromResource(R.xml.prefs_seasons_sketch, rootKey);
                    mpreference = findPreference("numFallObjects");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("speedFactor");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("-?\\d+(\\.\\d+)?")));
                    break;
                case 51:
                    setPreferencesFromResource(R.xml.prefs_ball_sketch, rootKey);
                    mpreference = findPreference("ballRadius");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("ballSpeed");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 3:
                    setPreferencesFromResource(R.xml.prefs_worm_sketch, rootKey);
                    mpreference = findPreference("crawlSpeed");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("fallSpeed");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("wormSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 4:
                    setPreferencesFromResource(R.xml.prefs_gecko_sketch, rootKey);
                    mpreference = findPreference("crawlSpeed");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("fallSpeed");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("geckoSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 5:
                    setPreferencesFromResource(R.xml.prefs_puff_sketch, rootKey);
                    mpreference = findPreference("puffSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("sizeIncrement");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("maxSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("steps");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 6:
                    setPreferencesFromResource(R.xml.prefs_bubbletea_sketch, rootKey);
                    mpreference = findPreference("drinkSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("charSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("handSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("maxSize");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 7:
                    setPreferencesFromResource(R.xml.prefs_tentacle_sketch, rootKey);
                    mpreference = findPreference("numSegments");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("segLength");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                case 8:
                    setPreferencesFromResource(R.xml.prefs_blue_growth_sketch, rootKey);
                    mpreference = findPreference("minWeight");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    mpreference = findPreference("maxWeight");
                    mpreference.setOnPreferenceChangeListener((preference, newValue) ->
                            (newValue != null && newValue.toString().length() > 0 && newValue.toString().matches("\\d*")));
                    break;
                default:
                    break;
            }
        }
    }
}