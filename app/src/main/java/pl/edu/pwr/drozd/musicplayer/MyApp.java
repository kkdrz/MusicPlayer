package pl.edu.pwr.drozd.musicplayer;

import android.app.Application;

import pl.edu.pwr.drozd.musicplayer.dagger.AppComponent;
import pl.edu.pwr.drozd.musicplayer.dagger.AppModule;
import pl.edu.pwr.drozd.musicplayer.dagger.DaggerAppComponent;


public class MyApp extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
