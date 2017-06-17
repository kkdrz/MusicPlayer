package pl.edu.pwr.drozd.musicplayer;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);
        hideActionBarTitle();
        ((MyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), this));
        mPlaylist = playlistManager.getPlaylist();
        mPlaylistFragment = PlaylistFragment.newInstance(mPlaylist);
        mPlayerFragment = PlayerFragment.newInstance(mPlaylist);

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
}