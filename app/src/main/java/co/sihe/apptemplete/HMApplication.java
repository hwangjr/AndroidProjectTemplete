package co.sihe.apptemplete;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class HMApplication extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            refWatcher = LeakCanary.install(this);
        } else {
            refWatcher = RefWatcher.DISABLED;
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        HMApplication application = (HMApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}
