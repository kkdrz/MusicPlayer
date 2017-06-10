package pl.edu.pwr.drozd.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.enrique.stackblur.StackBlurManager;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.backward_song_btn)   ImageButton mBackwardSongBtn;
    @BindView(R.id.forward_song_btn)    ImageButton mForwardSongBtn;
    @BindView(R.id.play_song_btn)       ImageButton mPlaySongBtn;
    @BindView(R.id.next_song_btn)       ImageButton mNextSongBtn;
    @BindView(R.id.prev_song_btn)       ImageButton mPrevSongBtn;
    @BindView(R.id.player_layout)       ViewGroup mPlayerLayout;

    @Inject PlaylistManager playlistManager;
    ArrayList<Song> mPlaylist;

    public final static int BACKGROUND_IMAGE = R.drawable.background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        mPlaylist = playlistManager.getPlaylist();

        setBlurryBackground();
    }

    private void setBlurryBackground() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), BACKGROUND_IMAGE);
        StackBlurManager blurManager = new StackBlurManager(bm);
        blurManager.process(20);
        mPlayerLayout.setBackground(new BitmapDrawable(getResources(), blurManager.returnBlurredImage()));
    }
}