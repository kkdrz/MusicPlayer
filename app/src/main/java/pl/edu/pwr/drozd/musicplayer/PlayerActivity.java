package pl.edu.pwr.drozd.musicplayer;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.enrique.stackblur.StackBlurManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

public class PlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private static final String CURRENT_SONG_INDEX = "CURRENT_SONG";
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
    @BindView(R.id.currentTime)         TextView mCurrentTime;

    @Inject PlaylistManager playlistManager;
    @Inject MediaPlayer mMediaPlayer;
    @Inject Handler mHandler;
    @Inject SharedPreferences mSharedPrefs;
    ArrayList<Song> mPlaylist;
    int currentSongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("STATE: ", "OnCreate");
        setContentView(R.layout.activity_main);
        hideActionBarTitle();

        ((MyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        mPlaylist = playlistManager.getPlaylist();

        mSeekBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    private void hideActionBarTitle() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onStart() {
        Log.d("STATE: ", "OnStart");
        super.onStart();
        currentSongIndex = mSharedPrefs.getInt(CURRENT_SONG_INDEX, 0);
        loadSong(currentSongIndex);
    }

    @Override
    protected void onStop() {
        Log.d("STATE: ", "OnStop");
        super.onStop();
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(CURRENT_SONG_INDEX, currentSongIndex);
        editor.apply();
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

    private void loadSong(int songIndex) {
        try {
            mSongTitle.setText(mPlaylist.get(songIndex).getTitle());
            mSongAuthor.setText(mPlaylist.get(songIndex).getAuthor());

            mPlaySongBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);

            mSeekBar.setProgress(0);
            mSeekBar.setMax(100);

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), mPlaylist.get(songIndex).getURI());
            mMediaPlayer.prepare();

            updateProgressBar();
            displayRandomAlbumCover();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.play_song_btn)
    public void OnPlayButtonPressed() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mPlaySongBtn.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            mMediaPlayer.pause();
            mPlaySongBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    @OnClick(R.id.next_song_btn)
    public void OnNextButtonPressed() {
        boolean isPlaying = mMediaPlayer.isPlaying();
        if (currentSongIndex < (mPlaylist.size() - 1)) {
            loadSong(++currentSongIndex);
        } else {
            currentSongIndex = 0;
            loadSong(currentSongIndex);
        }
        OnPlayButtonPressed();
    }

    @OnClick(R.id.prev_song_btn)
    public void OnPrevButtonPressed() {
        boolean isPlaying = mMediaPlayer.isPlaying();
        if (currentSongIndex > 0) {
            loadSong(--currentSongIndex);
        } else {
            currentSongIndex = mPlaylist.size()-1;
            loadSong(currentSongIndex);
        }
        OnPlayButtonPressed();
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
        mHandler.postDelayed(mUpdateTimeTask, 50);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            int progress = Utils.getProgressPercentage(currentDuration, totalDuration);
            mSeekBar.setProgress(progress);

            String time = Utils.milliSecondsToTimer(mMediaPlayer.getCurrentPosition());
            mCurrentTime.setText(time);
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("STATE: ", "OnCompletion");
        OnNextButtonPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("STATE: ", "OnDestroy");
        mMediaPlayer.stop();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    public void displayRandomAlbumCover() {
        int random = ThreadLocalRandom.current().nextInt(0, Utils.albumCovers.size()-1);
        Glide.with(this)
                .load(Utils.albumCovers.get(random))
                .into(mPosterImage);

        setBlurryBackground(Utils.albumCovers.get(random));
    }

    private void setBlurryBackground(int drawable) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawable);
        StackBlurManager blurManager = new StackBlurManager(bm);
        blurManager.process(20);
        mPlayerLayout.setBackground(new BitmapDrawable(getResources(), blurManager.returnBlurredImage()));
    }
}