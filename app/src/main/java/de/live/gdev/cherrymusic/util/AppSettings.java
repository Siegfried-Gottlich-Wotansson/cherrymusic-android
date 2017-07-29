package de.live.gdev.cherrymusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import de.live.gdev.cherrymusic.App;
import de.live.gdev.cherrymusic.BuildConfig;
import de.live.gdev.cherrymusic.R;
import io.github.gsantner.opoc.util.AppSettingsBase;

public class AppSettings extends AppSettingsBase {
    private SharedPreferences prefCurrentProfile;

    //#####################
    //## Methods
    //#####################
    private AppSettings(Context context) {
        super(context);
        loadSelectedProfile();
    }

    public static AppSettings get() {
        return new AppSettings(App.get());
    }

    //###########################################
    //## Settings options
    //############################################

    public boolean isAppFirstStart(boolean doSet) {
        boolean value = getBool(prefApp, R.string.pref_key__app_first_start, true);
        if (doSet) {
            setBool(prefApp, R.string.pref_key__app_first_start, false);
        }
        return value;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isAppCurrentVersionFirstStart() {
        int value = getInt(prefApp, R.string.pref_key__app_first_start_current_version, -1);
        setInt(prefApp, R.string.pref_key__app_first_start_current_version, BuildConfig.VERSION_CODE);
        return value != BuildConfig.VERSION_CODE && !BuildConfig.IS_TEST_BUILD;
    }

    public boolean isShowMainFab() {
        return getBool(prefApp, R.string.pref_key__show_main_fab, true);
    }

    public boolean isReloadRequired() {
        return getBool(prefApp, R.string.pref_key__app_reload_required, false);
    }

    public void setReloadRequired(boolean value) {
        setBool(prefApp, R.string.pref_key__app_reload_required, value);
    }

    public SharedPreferences getSharedPreferenceCurrentProfile() {
        return prefCurrentProfile;
    }


    //#############
    //# Profile
    //#############
    public void loadSelectedProfile() {
        prefCurrentProfile = context.getSharedPreferences("Profile" + getSelectedProfileNr(), Context.MODE_PRIVATE);
    }

    public void loadProfile(int nr) {
        nr = nr >= 0 && nr < 3 ? nr : 0;
        prefCurrentProfile = context.getSharedPreferences("Profile" + nr, Context.MODE_PRIVATE);
    }

    public int getSelectedProfileNr() {
        return getIntOfStringPref(R.string.pref_key__app_selected_profile, 0);
    }

    public boolean isProfileEmpty() {
        return getProfilePathDomainAndDirectory().equals(rstr(R.string.default_path_domain_and_folder))
                || TextUtils.isEmpty(getProfileLoginPassword());
    }

    public String getProfileSummary() {
        return isProfileEmpty() ? rstr(R.string.no_data)
                : String.format("%s @%s", getProfileLoginUsername(), getProfilePathDomainAndDirectory());
    }

    public String getProfilePathDomainAndDirectory() {
        String ret = getString(prefCurrentProfile, R.string.pref_key__profile_path_domain_and_directory, rstr(R.string.default_path_domain_and_folder));
        if (!TextUtils.isEmpty(ret) && ret.endsWith("/")) {
            ret = ret.substring(0, ret.length() - 1);
            setProfilePathDomainAndDirectory(ret);
        }
        return ret;
    }

    public String getProfilePathFull() {
        String ret = getProfilePathDomainAndDirectory();
        ret = TextUtils.isEmpty(getProfilePathFilename()) ? ret : ret + "/" + getProfilePathFilename();
        return ret;
    }

    public void setProfilePathDomainAndDirectory(String value) {
        setString(prefCurrentProfile, R.string.pref_key__profile_path_domain_and_directory, value);
    }

    public String getProfilePathFilename() {
        return getString(prefCurrentProfile, R.string.pref_key__profile_path_filename, "");
    }

    public String getProfileLoginUsername() {
        return getString(prefCurrentProfile, R.string.pref_key__profile_login_username, rstr(R.string.default_username));
    }

    public String getProfileLoginPassword() {
        return getString(prefCurrentProfile, R.string.pref_key__profile_login_password, "");
    }

    public boolean isProfileAutoLogin() {
        return getBool(prefCurrentProfile, R.string.pref_key__profile_autologin, true);
    }

    public boolean isProfileAcceptAllSsl() {
        return getBool(prefCurrentProfile, R.string.pref_key__profile_accept_all_ssl_certs, false);
    }

    public boolean isProfileLoadInDesktopMode() {
        return getBool(prefCurrentProfile, R.string.pref_key__profile_load_in_desktop_mode, true);
    }

    public void setProfileLoadInDesktopMode(boolean value) {
        setBool(prefCurrentProfile, R.string.pref_key__profile_load_in_desktop_mode, value);
    }

    public String getProfileHttpAuthUsername() {
        return getString(prefCurrentProfile, R.string.pref_key__profile_httpauth_username, rstr(R.string.default_username));
    }

    public String getProfileHttpAuthPassword() {
        return getString(prefCurrentProfile, R.string.pref_key__profile_httpauth_password, "");
    }
}