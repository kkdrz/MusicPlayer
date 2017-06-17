package pl.edu.pwr.drozd.musicplayer;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import pl.edu.pwr.drozd.musicplayer.dagger.AppComponent;
import pl.edu.pwr.drozd.musicplayer.dagger.AppModule;
import pl.edu.pwr.drozd.musicplayer.dagger.DaggerAppComponent;


public class MyApp extends Application {

    private AppComponent mAppComponent;
    private RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        refWatcher =  LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApp application = (MyApp) context.getApplicationContext();
        return application.refWatcher;
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
