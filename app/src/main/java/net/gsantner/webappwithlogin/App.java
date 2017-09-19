package net.gsantner.webappwithlogin;

import android.app.Application;

import net.gsantner.webappwithlogin.util.AppSettings;
import wawl.WawlOverrides;

public class App extends Application {
    private volatile static App app;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        AppSettings appSettings = AppSettings.get();
        if (appSettings.isAppFirstStart(false)) {
            WawlOverrides.onAppFirstStart(this, appSettings);
        }
    }
}
