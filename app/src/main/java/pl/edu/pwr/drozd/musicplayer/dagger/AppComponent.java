package pl.edu.pwr.drozd.musicplayer.dagger;


import javax.inject.Singleton;

import dagger.Component;
import pl.edu.pwr.drozd.musicplayer.PlayerActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(PlayerActivity playerActivity);
}
