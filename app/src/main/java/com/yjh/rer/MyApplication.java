package com.yjh.rer;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.yjh.rer.util.CrashLibrary;

import timber.log.Timber;

public class MyApplication extends Application {

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        mRefWatcher = LeakCanary.install(this);
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            CrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    CrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    CrashLibrary.logWarning(t);
                }
            }
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
}
