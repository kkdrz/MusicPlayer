package pl.edu.pwr.drozd.musicplayer;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

import static pl.edu.pwr.drozd.musicplayer.ViewPagerActivity.PLAYLIST;


public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

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
    private ArrayList<Song> mPlaylist;
    private PlayerListener mListener;
    int currentSongIndex;

    public static PlayerFragment newInstance(ArrayList<Song> mPlaylist) {
        PlayerFragment f = new PlayerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PLAYLIST, mPlaylist);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApp) getActivity().getApplication()).getAppComponent().inject(this);
        mListener = (ViewPagerActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mPlaylist = playlistManager.getPlaylist();
        mSeekBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        currentSongIndex = mSharedPrefs.getInt(CURRENT_SONG_INDEX, 0);
        loadSong(currentSongIndex);
    }

    @Override
    public void onStop() {
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

    public void loadSong(int songIndex) {
        try {
            mSongTitle.setText(mPlaylist.get(songIndex).getTitle());
            mSongAuthor.setText(mPlaylist.get(songIndex).getAuthor());

            mPlaySongBtn.setImageResource(R.drawable.ic_play_arrow_white_24dp);

            mSeekBar.setProgress(0);
            mSeekBar.setMax(100);

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getActivity(), mPlaylist.get(songIndex).getURI());
            mMediaPlayer.prepare();

            updateProgressBar();
            Utils.setBlurryBackground(Utils.displayRandomAlbumCover(getContext(), mPosterImage), mPlayerLayout, getContext());
            currentSongIndex = songIndex;
            mListener.onSongChanged(currentSongIndex);

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
            mHandler.removeCallbacks(this);
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            int progress = Utils.getProgressPercentage(currentDuration, totalDuration);
            mSeekBar.setProgress(progress);

            String time = Utils.milliSecondsToTimer(mMediaPlayer.getCurrentPosition());
            mCurrentTime.setText(time);
            mHandler.postDelayed(this, 50);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("STATE: ", "OnCompletion");
        OnNextButtonPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("STATE: ", "OnDestroy");
        mMediaPlayer.stop();
        mHandler.removeCallbacks(mUpdateTimeTask);
        RefWatcher refWatcher = MyApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }


    interface PlayerListener {
        void onSongChanged(int songIndex);
    }
}
