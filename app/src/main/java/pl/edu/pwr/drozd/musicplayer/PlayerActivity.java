package pl.edu.pwr.drozd.musicplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    @BindView(R.id.backward_song_btn)   ImageButton mBackwardSongBtn;
    @BindView(R.id.forward_song_btn)    ImageButton mForwardSongBtn;
    @BindView(R.id.play_song_btn)       ImageButton mPlaySongBtn;
    @BindView(R.id.next_song_btn)       ImageButton mNextSongBtn;
    @BindView(R.id.prev_song_btn)       ImageButton mPrevSongBtn;
    @BindView(R.id.poster_img)          ImageView mPosterImage;
    @BindView(R.id.player_layout)       ViewGroup mPlayerLayout;
    @BindView(R.id.seek_bar)            SeekBar mSeekBar;
    @BindView(R.id.song_title)          TextView mSongTitle;
    @BindView(R.id.song_author)         TextView mSongAuthor;

    @Inject PlaylistManager playlistManager;
    @Inject MediaPlayer mMediaPlayer;
    @Inject Handler mHandler;
    ArrayList<Song> mPlaylist;
    int currentSongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        mPlaylist = playlistManager.getPlaylist();

        mSeekBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        playSong(0);
        OnPlayButtonPressed();
    }

    private void setBlurryBackground(String URL) {
        Utils.BackgroundImageSetterTask task = new Utils.BackgroundImageSetterTask(mPlayerLayout, getApplicationContext());
        task.execute(URL);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mMediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    private void playSong(int songIndex) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), mPlaylist.get(songIndex).getURI());
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            mSongTitle.setText(mPlaylist.get(songIndex).getTitle());
            mSongAuthor.setText(mPlaylist.get(songIndex).getAuthor());

            mPlaySongBtn.setImageResource(R.drawable.ic_pause_white_24dp);

            mSeekBar.setProgress(0);
            mSeekBar.setMax(100);

            updateProgressBar();
            displayRandomAlbumCover();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.play_song_btn)
    public void OnPlayButtonPressed() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mPlaySongBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
        else {
            mMediaPlayer.start();
            mPlaySongBtn.setImageResource(R.drawable.ic_pause_white_24dp);
        }
    }

    @OnClick(R.id.next_song_btn)
    public void OnNextButtonPressed() {
        if (currentSongIndex < (mPlaylist.size() - 1)) {
            playSong(++currentSongIndex);
        } else {
            currentSongIndex = 0;
            playSong(currentSongIndex);
        }
    }

    @OnClick(R.id.prev_song_btn)
    public void OnPrevButtonPressed() {
        if (currentSongIndex > 0) {
            playSong(--currentSongIndex);
        } else {
            currentSongIndex = mPlaylist.size()-1;
            playSong(currentSongIndex);
        }
    }

    @OnClick(R.id.forward_song_btn)
    public void OnForwardButtonPressed() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 5000);
    }

    @OnClick(R.id.backward_song_btn)
    public void OnBackwardButtonPressed() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 5000);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            int progress = (Utils.getProgressPercentage(currentDuration, totalDuration));
            mSeekBar.setProgress(progress);

            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        OnNextButtonPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }

    public void displayRandomAlbumCover() {
        int random = ThreadLocalRandom.current().nextInt(0, Utils.albumCovers.size()-1);
        Glide.with(this)
                .load(Utils.albumCovers.get(random))
                .into(mPosterImage);

        setBlurryBackground(Utils.albumCovers.get(random));
    }
}