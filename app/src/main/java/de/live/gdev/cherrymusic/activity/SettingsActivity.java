package de.live.gdev.cherrymusic.activity;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.live.gdev.cherrymusic.R;
import de.live.gdev.cherrymusic.util.AppSettings;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.settings_appbar)
    protected AppBarLayout appBarLayout;
    @BindView(R.id.settings_toolbar)
    protected Toolbar toolbar;


    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.settings__activity);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });
        showFragment(SettingsFragmentMaster.TAG, false);
    }

    protected void showFragment(String tag, boolean addToBackStack) {
        PreferenceFragment fragment = (PreferenceFragment) getFragmentManager().findFragmentByTag(tag);
        Integer profileFragmentNrToShow = null;
        if (fragment == null) {
            switch (tag) {
                case SettingsFragmentProfile.TAG0:
                    profileFragmentNrToShow = 0;
                    break;
                case SettingsFragmentProfile.TAG1:
                    profileFragmentNrToShow = 1;
                    break;
                case SettingsFragmentProfile.TAG2:
                    profileFragmentNrToShow = 2;
                    break;
                case SettingsFragmentMaster.TAG:
                default:
                    fragment = new SettingsFragmentMaster();
                    toolbar.setTitle(R.string.title_activity_settings);
                    break;
            }
        }
        if (profileFragmentNrToShow != null) {
            fragment = new SettingsFragmentProfile();
            ((SettingsFragmentProfile) fragment).setProfileNr(profileFragmentNrToShow);
            toolbar.setTitle(getResources().getStringArray(R.array.entries__profiles)[profileFragmentNrToShow]);
        }

        FragmentTransaction t = getFragmentManager().beginTransaction();
        if (addToBackStack) {
            t.addToBackStack(tag);
        }
        t.replace(R.id.settings_fragment_container, fragment, tag).commit();
    }

    public static class SettingsFragmentMaster extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String TAG = "settings.SettingsFragmentMaster";
        private AppSettings appSettings;

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("app");
            addPreferencesFromResource(R.xml.preferences_master);
            appSettings = AppSettings.get();
        }

        @Override
        public void onPause() {
            appSettings.unregisterPreferenceChangedListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            appSettings.setReloadRequired(true);
            loadSummaries();
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                AppSettings settings = AppSettings.get();
                String key = preference.getKey();
                if (settings.isKeyEqual(key, R.string.profile_1)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG0, true);
                    return true;
                } else if (settings.isKeyEqual(key, R.string.profile_2)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG1, true);
                    return true;
                }
                if (settings.isKeyEqual(key, R.string.profile_3)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG2, true);
                    return true;
                }
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @Override
        @SuppressLint("DefaultLocale")
        public void onResume() {
            super.onResume();
            ((SettingsActivity) getActivity()).toolbar.setTitle(R.string.title_activity_settings);
            appSettings.registerPreferenceChangedListener(this);
            loadSummaries();
        }

        public void loadSummaries() {
            AppSettings appSettings = AppSettings.get();
            String selectedProfile = getResources().getStringArray(R.array.entries__profiles)[appSettings.getSelectedProfileNr()];

            findPreference(getString(R.string.pref_key__app_selected_profile)).setSummary(selectedProfile);

            appSettings.loadProfile(0);
            findPreference(getString(R.string.profile_1)).setSummary(appSettings.getProfileSummary());
            appSettings.loadProfile(1);
            findPreference(getString(R.string.profile_2)).setSummary(appSettings.getProfileSummary());
            appSettings.loadProfile(2);
            findPreference(getString(R.string.profile_3)).setSummary(appSettings.getProfileSummary());
        }
    }


    public static class SettingsFragmentProfile extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String TAG0 = "settings.SettingsFragmentProfile0";
        public static final String TAG1 = "settings.SettingsFragmentProfile1";
        public static final String TAG2 = "settings.SettingsFragmentProfile2";

        private AppSettings appSettings;
        private int profileNr;

        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            getPreferenceManager().setSharedPreferencesName("Profile" + profileNr);
            addPreferencesFromResource(R.xml.preferences_profile);
            appSettings = AppSettings.get();
            appSettings.loadProfile(profileNr);
        }

        public void setProfileNr(int profileNr) {
            this.profileNr = profileNr;
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
            if (isAdded() && preference.hasKey()) {
                String key = preference.getKey();
            }
            return super.onPreferenceTreeClick(screen, preference);
        }

        @Override
        public void onResume() {
            super.onResume();
            appSettings.registerPreferenceChangedListener(appSettings.getSharedPreferenceCurrentProfile(), this);
            loadSummaries();
        }

        @Override
        public void onPause() {
            appSettings.unregisterPreferenceChangedListener(appSettings.getSharedPreferenceCurrentProfile(), this);
            super.onPause();
        }

        public void loadSummaries() {
            findPreference(getString(R.string.pref_key__profile_path_domain_and_directory)).setSummary(appSettings.isProfileEmpty() ? "" : appSettings.getProfilePathDomainAndDirectory());
            //findPreference(getString(R.string.pref_key__profile_path_filename)).setSummary(appSettings.getProfilePathFilename());
            findPreference(getString(R.string.pref_key__profile_login_username)).setSummary(appSettings.getProfileLoginUsername());
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            appSettings.setReloadRequired(true);
            loadSummaries();
        }
    }
}