package wawl;

import android.content.Context;
import android.net.Uri;
import android.webkit.WebView;

import org.apache.http.util.EncodingUtils;

import de.live.gdev.cherrymusic.R;
import io.github.gsantner.webappwithlogin.App;
import io.github.gsantner.webappwithlogin.util.AppSettings;

public class WawlOverrides {
    public static void loadWebapp(WebView webView, AppSettings appSettings, boolean doLogin) {
        Context context = webView.getContext();
        Uri url;
        try {
            url = Uri.parse(appSettings.getProfilePathFull());
        } catch (Exception e) {
            webView.loadData(context.getString(R.string.no_valid_path), "text/html", "UTF-16");
            return;
        }

        String url_s = url.toString();
        if (appSettings.isProfileEmpty()) {
            webView.loadData(context.getString(R.string.no_valid_path), "text/html", "UTF-16");
        } else {
            webView.loadUrl(url_s);
            if (doLogin) {
                url_s += "?a=checklogin";
                String postData = "name=" + appSettings.getProfileLoginUsername() + "&password=" + appSettings.getProfileLoginPassword();
                webView.postUrl(url_s, EncodingUtils.getBytes(postData, "base64"));
            }
        }
    }

    public static void onAppFirstStart(App app, AppSettings appSettings){
        appSettings.loadProfile(2);
        appSettings.setProfileLoginUsername("user_");
        appSettings.setProfilePathDomainAndDirectory(app.getString(R.string.default_path_domain_and_folder));
        appSettings.loadProfile(0);
    }
}
