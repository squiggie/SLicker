package slicker.com.slicker;

/**
 * Created by squiggie on 2/27/16.
 */
import android.app.Application;
import android.content.Context;

public class SlickerApplication extends Application {
    private static SlickerApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static SlickerApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}
