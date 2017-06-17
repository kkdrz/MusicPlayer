package pl.edu.pwr.drozd.musicplayer;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.nisrulz.sensey.OrientationDetector;
import com.github.nisrulz.sensey.Sensey;
import com.squareup.leakcanary.RefWatcher;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

public class ViewPagerActivity extends AppCompatActivity implements PlaylistAdapter.ViewHolderListener, PlayerFragment.PlayerListener {

    @BindView(R.id.view_pager)      ViewPager mViewPager;
    @Inject PlaylistManager playlistManager;
    @Inject SharedPreferences mSharedPrefs;
    private ArrayList<Song> mPlaylist;
    private static final int NUM_PAGES = 2;
    public static String PLAYLIST = "playlist";
    private PlaylistFragment mPlaylistFragment;
    private PlayerFragment mPlayerFragment;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);
        hideActionBarTitle();
        ((MyApp) getApplication()).getAppComponent().inject(this);
        Sensey.getInstance().init(getApplicationContext());

        ButterKnife.bind(this);
        mHandler = new Handler();
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this));
        mPlaylist = playlistManager.getPlaylist();
        mPlaylistFragment = PlaylistFragment.newInstance(mPlaylist);
        mPlayerFragment = PlayerFragment.newInstance(mPlaylist);
        mHandler.postDelayed(mGestureTask, 50);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stop();
        RefWatcher refWatcher = MyApp.getRefWatcher(this);
        refWatcher.watch(this);
    }

    private void hideActionBarTitle() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onPlaylistItemClicked(int layoutPosition) {
        mPlayerFragment.loadSong(layoutPosition);
        mPlayerFragment.OnPlayButtonPressed();
    }

    @Override
    public void onSongChanged(int songIndex) {
        mPlaylistFragment.changeCurrentSong(songIndex);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        Context context;
        MyPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return mPlayerFragment;
                default:
                    return mPlaylistFragment;
            }
        }

    }
    OrientationDetector.OrientationListener orientationListener = new OrientationDetector.OrientationListener() {
        @Override public void onTopSideUp() {
            Log.d("GEST", "TOP");
        }

        @Override public void onBottomSideUp() {
            Log.d("GEST", "Bottom");
        }

        @Override public void onRightSideUp() {
            Log.d("GEST", "Right");
            mPlayerFragment.OnPrevButtonPressed();
            Sensey.getInstance().stopOrientationDetection(orientationListener);
            mHandler.postDelayed(mGestureTask, 100);
        }

        @Override public void onLeftSideUp() {
            Log.d("GEST", "Left");
            mPlayerFragment.OnNextButtonPressed();
            Sensey.getInstance().stopOrientationDetection(orientationListener);
            mHandler.postDelayed(mGestureTask, 100);
        }
    };
    private Runnable mGestureTask = new Runnable() {
        public void run() {
            Sensey.getInstance().startOrientationDetection(orientationListener);
        }
    };


}