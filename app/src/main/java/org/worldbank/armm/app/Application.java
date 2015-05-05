package org.worldbank.armm.app;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;

import org.worldbank.armm.app.utils.Prefs;

import io.fabric.sdk.android.Fabric;

import ca.dalezak.androidbase.BaseApplication;
import ca.dalezak.androidbase.tasks.HttpQueue;
import ca.dalezak.androidbase.utils.Log;

public class Application extends BaseApplication implements Log.Callback {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.setCallback(this);
        Prefs.setServer(getServer());
        ActiveAndroid.initialize(this);
        Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        if (Prefs.hasName()) {
            Crashlytics.setUserName(Prefs.getName());
        }
        if (Prefs.hasUsername()) {
            Crashlytics.setUserEmail(Prefs.getUsername());
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    @Override
    protected void onUncaughtException(Throwable throwable) {
        Log.i(this, "onUncaughtException", throwable);
        Crashlytics.logException(throwable);
    }

    @Override
    public void deleteDatabase() {
        Log.i(this, "deleteDatabase");
        Prefs.clear();
        deleteDatabase(getManifest("AA_DB_NAME"));
        ActiveAndroid.clearCache();
        ActiveAndroid.dispose();
        ActiveAndroid.initialize(this);
        HttpQueue.getInstance().cancel();
        HttpQueue.getInstance().clear();
    }

    @Override
    public void onLogInfo(String tag, String message) {

    }

    @Override
    public void onLogDebug(String tag, String message) {

    }

    @Override
    public void onLogWarning(String tag, String message) {

    }

    @Override
    public void onLogException(String tag, Throwable throwable) {
        Crashlytics.logException(throwable);
    }
}