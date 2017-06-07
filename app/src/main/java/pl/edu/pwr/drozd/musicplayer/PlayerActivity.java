package pl.edu.pwr.drozd.musicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;

import javax.inject.Inject;

import butterknife.BindView;
import pl.edu.pwr.drozd.musicplayer.dagger.AppComponent;

public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.backward_song_btn)   ImageButton mBackwardSongBtn;
    @BindView(R.id.forward_song_btn)    ImageButton mForwardSongBtn;
    @BindView(R.id.play_song_btn)       ImageButton mPlaySongBtn;
    @BindView(R.id.next_song_btn)       ImageButton mNextSongBtn;
    @BindView(R.id.prev_song_btn)       ImageButton mPrevSongBtn;
    @BindView(R.id.player_layout)       ViewGroup mPlayerLayout;

    @Inject PlaylistManager playlistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MyApp) getApplication()).getAppComponent().inject(this);
    }
}