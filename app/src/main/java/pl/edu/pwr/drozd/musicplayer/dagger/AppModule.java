package pl.edu.pwr.drozd.musicplayer.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import pl.edu.pwr.drozd.musicplayer.PlaylistManager;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return mApplication;
    }

    @Provides
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    PlaylistManager providesPlaylistManager(Context context) {
        return new PlaylistManager(context);
    }

    @Provides
    @Singleton
    MediaPlayer providesMediaPlayer() {
        return new MediaPlayer();
    }

    @Provides
    @Singleton
    Handler providesHandler() {
        return new Handler();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
