package co.sihe.hongmi;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class HMApplication extends Application {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG_CANARY) {
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
