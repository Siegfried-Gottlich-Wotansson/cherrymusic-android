package de.live.gdev.cherrymusic.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.live.gdev.cherrymusic.BuildConfig;
import de.live.gdev.cherrymusic.R;
import de.live.gdev.cherrymusic.util.AppSettings;
import io.github.gsantner.opoc.util.Helpers;
import io.github.gsantner.opoc.util.HelpersA;
import io.github.gsantner.opoc.util.SimpleMarkdownParser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final boolean LOAD_IN_DESKTOP_MODE = true;

    @BindView(R.id.web_view)
    WebView webView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    AppSettings appSettings;

    @Override
    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    protected void onCreate(Bundle savedInstanceState) {
        // Setup UI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main__activity);
        ButterKnife.bind(this);
        appSettings = AppSettings.get();

        // Setup bars
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.action_donate_bitcoin).setVisible(!BuildConfig.IS_GPLAY_BUILD);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_subtext))
                .setText("v" + Helpers.get().getAppVersionName());
        fab.setVisibility(appSettings.isShowMainFab() ? View.VISIBLE : View.GONE);
        appSettings.setReloadRequired(false);

        // Set web settings
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(appSettings.isProfileLoadInDesktopMode());
        webSettings.setLoadWithOverviewMode(appSettings.isProfileLoadInDesktopMode());
        webSettings.setUseWideViewPort(appSettings.isProfileLoadInDesktopMode());
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (appSettings.isProfileAcceptAllSsl()) {
                    handler.proceed();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.ssl_toast_error, Snackbar.LENGTH_SHORT).show();
                    webView.loadData(getString(R.string.ssl_webview_error_str), "text/html", "UTF-16");
                }
            }
        });

        // Show first start dialog / changelog
        try {
            SimpleMarkdownParser mdParser = SimpleMarkdownParser.get().setDefaultSmpFilter(SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW);
            if (appSettings.isAppFirstStart(true)) {
                String html = mdParser.parse(getString(R.string.copyright_license_text_official).replace("\n", "  \n"), "").getHtml();
                html += mdParser.parse(getResources().openRawResource(R.raw.licenses_3rd_party), "").getHtml();

                HelpersA.get(this).showDialogWithHtmlTextView(R.string.licenses, html);
            } else if (appSettings.isAppCurrentVersionFirstStart()) {
                mdParser.parse(
                        getResources().openRawResource(R.raw.changelog), "");
                HelpersA.get(this).showDialogWithHtmlTextView(R.string.changelog, mdParser.getHtml());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main__menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return handleBarClick(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        handleBarClick(item);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean handleBarClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                HelpersA.get(this).animateToActivity(SettingsActivity.class, false, null);
                return true;
            }
            case R.id.action_login: {
                loadWebapp(true);
                return true;
            }
            case R.id.action_info: {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
            case R.id.action_exit: {
                webView.clearCache(true);
                webView.clearFormData();
                webView.clearHistory();
                webView.clearMatches();
                webView.clearSslPreferences();
                finish();
                System.exit(0);
                return true;
            }
            case R.id.action_reload: {
                webView.reload();
                return true;
            }
            case R.id.action_donate_bitcoin: {
                Helpers.get().showDonateBitcoinRequest();
                return true;
            }
            case R.id.action_homepage_additional: {
                Helpers.get().openWebpageInExternalBrowser(getString(R.string.page_additional_homepage));
                return true;
            }
            case R.id.action_homepage_author: {
                Helpers.get().openWebpageInExternalBrowser(getString(R.string.page_author));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int key, KeyEvent e) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        if ((key == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(key, e);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appSettings.isReloadRequired()) {
            recreate();
            return;

        }
        loadWebapp(appSettings.isProfileAutoLogin());
    }

    @OnClick(R.id.fab)
    public void onFloatingActionButtonClicked(View v) {
        drawer.openDrawer(GravityCompat.START);
    }

    @OnLongClick(R.id.fab)
    public boolean onFloatingActionButtonLongClicked(View v) {
        loadWebapp(false);
        return true;
    }

    public void loadWebapp(boolean doLogin) {
        Uri url;
        try {
            url = Uri.parse(appSettings.getProfilePathFull());
        } catch (Exception e) {
            webView.loadData(getString(R.string.no_valid_path), "text/html", "UTF-16");
            return;
        }

        String url_s = url.toString();
        if (appSettings.isProfileEmpty()) {
            webView.loadData(getString(R.string.no_valid_path), "text/html", "UTF-16");
        } else {
            webView.loadUrl(url_s);
            if (doLogin) {
                String postData = "username=" + appSettings.getProfileLoginUsername() + "&password=" + appSettings.getProfileLoginPassword() + "&login=login";
                webView.postUrl(url_s, EncodingUtils.getBytes(postData, "base64"));
            }
        }
    }
}
