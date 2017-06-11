package pl.edu.pwr.drozd.musicplayer.dagger;


import javax.inject.Singleton;

import dagger.Component;
import pl.edu.pwr.drozd.musicplayer.PlayerFragment;
import pl.edu.pwr.drozd.musicplayer.ViewPagerActivity;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(PlayerFragment playerFragment);
    void inject(ViewPagerActivity pagerActivity);
}
