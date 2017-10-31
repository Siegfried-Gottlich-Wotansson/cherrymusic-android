package net.gsantner.webappwithlogin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import net.gsantner.opoc.util.AppSettingsBase;
import net.gsantner.webappwithlogin.App;

import de.live.gdev.cherrymusic.BuildConfig;
import de.live.gdev.cherrymusic.R;

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
        boolean value = getBool(R.string.pref_key__app_first_start, true);
        if (doSet) {
            setBool(R.string.pref_key__app_first_start, false);
        }
        return value;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isAppCurrentVersionFirstStart() {
        int value = getInt(R.string.pref_key__app_first_start_current_version, -1);
        setInt(R.string.pref_key__app_first_start_current_version, BuildConfig.VERSION_CODE);
        return value != BuildConfig.VERSION_CODE && !BuildConfig.IS_TEST_BUILD;
    }

    public boolean isShowMainFab() {
        return getBool(R.string.pref_key__show_main_fab, true);
    }

    public boolean isReloadRequired() {
        return getBool(R.string.pref_key__app_reload_required, false);
    }

    public void setReloadRequired(boolean value) {
        setBool(R.string.pref_key__app_reload_required, value);
    }

    public SharedPreferences getSharedPreferenceCurrentProfile() {
        return prefCurrentProfile;
    }


    //#############
    //# Profile
    //#############
    public void loadSelectedProfile() {
        prefCurrentProfile = _context.getSharedPreferences("Profile" + getSelectedProfileNr(), Context.MODE_PRIVATE);
    }

    public void loadProfile(int nr) {
        nr = nr >= 0 && nr < 3 ? nr : 0;
        prefCurrentProfile = _context.getSharedPreferences("Profile" + nr, Context.MODE_PRIVATE);
    }

    public int getSelectedProfileNr() {
        return getIntOfStringPref(R.string.pref_key__app_selected_profile, 0);
    }

    public boolean isProfileEmpty() {
        return TextUtils.isEmpty(getProfilePathDomainAndDirectory())
                || TextUtils.isEmpty(getProfileLoginPassword());
    }

    public String getProfileSummary() {
        return isProfileEmpty() ? rstr(R.string.no_data)
                : String.format("%s @%s", getProfileLoginUsername(), getProfilePathDomainAndDirectory());
    }

    public String getProfilePathDomainAndDirectory() {
        String ret = getString(R.string.pref_key__profile_path_domain_and_directory, "", prefCurrentProfile);
        if (!TextUtils.isEmpty(ret) && ret.endsWith("/")) {
            ret = ret.substring(0, ret.length() - 1);
            setProfilePathDomainAndDirectory(ret);
        }
        if (!TextUtils.isEmpty(ret) && !ret.startsWith("http")) {
            ret = "https://" + ret;
        }
        return ret;
    }

    public String getProfilePathFull() {
        String ret = getProfilePathDomainAndDirectory();
        ret = TextUtils.isEmpty(getProfilePathFilename()) ? ret : ret + "/" + getProfilePathFilename();
        return ret;
    }

    public void setProfilePathDomainAndDirectory(String value) {
        setString(R.string.pref_key__profile_path_domain_and_directory, value, prefCurrentProfile);
    }

    public String getProfilePathFilename() {
        return getString(R.string.pref_key__profile_path_filename, rstr(R.string.default_path_filename), prefCurrentProfile);
    }

    public String getProfileLoginUsername() {
        return getString(R.string.pref_key__profile_login_username, rstr(R.string.default_username), prefCurrentProfile);
    }

    public String getProfileLoginPassword() {
        return getString(R.string.pref_key__profile_login_password, "", prefCurrentProfile);
    }

    public boolean isProfileAutoLogin() {
        return getBool(R.string.pref_key__profile_autologin, true, prefCurrentProfile);
    }

    public boolean isProfileAcceptAllSsl() {
        return getBool(R.string.pref_key__profile_accept_all_ssl_certs, false, prefCurrentProfile);
    }

    public boolean isProfileLoadInDesktopMode() {
        return getBool(R.string.pref_key__profile_load_in_desktop_mode, true, prefCurrentProfile);
    }

    public void setProfileLoadInDesktopMode(boolean value) {
        setBool(R.string.pref_key__profile_load_in_desktop_mode, value, prefCurrentProfile);
    }

    public boolean isProfileHttpBasicEnabled() {
        return getBool(R.string.pref_key__profile_enable_http_basic_auth, false, prefCurrentProfile);
    }

    public String getProfileHttpBasicAuthUsername() {
        return getString(R.string.pref_key__profile_login_username_basic, rstr(R.string.default_username), prefCurrentProfile);
    }

    public String getProfileHttpBasicAuthPassword() {
        return getString(R.string.pref_key__profile_login_password_basic, "", prefCurrentProfile);
    }

    public void setProfileLoginUsername(String value) {
        setString(R.string.pref_key__profile_login_username, value, prefCurrentProfile);
    }

    public void getProfileLoginPassword(String value) {
        setString(R.string.pref_key__profile_login_password, value, prefCurrentProfile);
    }

    public void selectProfile(int index) {
        setInt(R.string.pref_key__app_selected_profile, index);
        loadSelectedProfile();
    }

    public String getLanguage() {
        return getString(R.string.pref_key__language, "");
    }

    public void setShowMainFab(boolean value) {
        setBool(R.string.pref_key__show_main_fab, value);
    }
}