package com.liviugheorghe.pcc_client.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference themePref = findPreference("ui_mode");
            ListPreference touchpadAccuracyPref = findPreference("touchpad_sensitivity");
            if(themePref != null)
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String value = (String) newValue;
                App.modifyUITheme(value);
                return true;
            });
            if(touchpadAccuracyPref != null)
                touchpadAccuracyPref.setOnPreferenceChangeListener((preference, newValue) -> {
                    String value = (String) newValue;
                    App.getTouchpadParams().setSensitivity(Integer.parseInt(value));
                    return true;
                });
        }
    }
}