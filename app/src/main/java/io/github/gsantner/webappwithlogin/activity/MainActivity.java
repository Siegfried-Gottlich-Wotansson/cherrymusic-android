package io.github.gsantner.webappwithlogin.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.live.gdev.cherrymusic.BuildConfig;
import de.live.gdev.cherrymusic.R;

import net.gsantner.opoc.util.ActivityUtils;
import net.gsantner.opoc.util.SimpleMarkdownParser;
import io.github.gsantner.webappwithlogin.util.AppSettings;
import io.github.gsantner.webappwithlogin.util.ContextUtils;
import wawl.WawlOverrides;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final boolean LOAD_IN_DESKTOP_MODE = true;

    @BindView(R.id.web_view)
    public WebView webView;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @BindView(R.id.fab)
    public FloatingActionButton fab;

    protected AppSettings appSettings;

    @Override
    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    protected void onCreate(Bundle savedInstanceState) {
        // Setup UI
        super.onCreate(savedInstanceState);
        ContextUtils.get().setAppLanguage(AppSettings.get().getLanguage());
        setContentView(R.layout.main__activity);
        ButterKnife.bind(this);
        appSettings = AppSettings.get();



        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        // Setup bars
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.action_donate_bitcoin).setVisible(!BuildConfig.IS_GPLAY_BUILD);
        fab.setVisibility(appSettings.isShowMainFab() ? View.VISIBLE : View.GONE);
        appSettings.setReloadRequired(false);

        if (appSettings.isAppFirstStart(false)) {
            appSettings.setShowMainFab(false);
        }

        // Set web settings
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(appSettings.isProfileLoadInDesktopMode());
        webSettings.setLoadWithOverviewMode(appSettings.isProfileLoadInDesktopMode());
        webSettings.setUseWideViewPort(appSettings.isProfileLoadInDesktopMode());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (appSettings.isProfileAcceptAllSsl()) {
                    handler.proceed();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), R.string.ssl_toast_error, Snackbar.LENGTH_SHORT).show();
                    webView.loadData(getString(R.string.ssl_webview_error_str), "text/html", "UTF-16");
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                if (appSettings.isProfileHttpBasicEnabled()) {
                    handler.proceed(appSettings.getProfileHttpBasicAuthUsername(), appSettings.getProfileHttpBasicAuthPassword());
                } else {
                    handler.cancel();
                }
            }
        });

        // Show first start dialog / changelog
        try {
            SimpleMarkdownParser mdParser = SimpleMarkdownParser.get().setDefaultSmpFilter(SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW);
            if (appSettings.isAppFirstStart(true)) {
                String html = mdParser.parse(getString(R.string.copyright_license_text_official).replace("\n", "  \n"), "").getHtml();
                html += mdParser.parse(getResources().openRawResource(R.raw.licenses_3rd_party), "").getHtml();

                new ActivityUtils(this).showDialogWithHtmlTextView(R.string.licenses, html);
            } else if (appSettings.isAppCurrentVersionFirstStart()) {
                mdParser.parse(
                        getResources().openRawResource(R.raw.changelog), "",
                        SimpleMarkdownParser.FILTER_ANDROID_TEXTVIEW, SimpleMarkdownParser.FILTER_CHANGELOG);
                new ActivityUtils(this).showDialogWithHtmlTextView(R.string.changelog, mdParser.getHtml());
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
                new ActivityUtils(this).animateToActivity(SettingsActivity.class, false, null);
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
                if (getResources().getBoolean(R.bool.should_exit_with_system_too)) {
                    System.exit(0);
                }
                return true;
            }
            case R.id.action_reload: {

                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags ^= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(attrs);
                webView.reload();
                return true;
            }
            case R.id.action_donate_bitcoin: {
                ContextUtils.get().showDonateBitcoinRequest(R.string.donate__bitcoin_id, R.string.donate__bitcoin_amount, R.string.donate__bitcoin_amount, R.string.donate__bitcoin_url);
                return true;
            }
            case R.id.action_homepage_additional: {
                ContextUtils.get().openWebpageInExternalBrowser(getString(R.string.page_additional_homepage));
                return true;
            }
            case R.id.action_homepage_author: {
                ContextUtils.get().openWebpageInExternalBrowser(getString(R.string.page_author));
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
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_subtext))
                .setText(getResources().getStringArray(R.array.entries__profiles)[appSettings.getSelectedProfileNr()]);
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
        WawlOverrides.loadWebapp(webView, appSettings, doLogin);
    }
}
