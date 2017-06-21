package pl.edu.pwr.drozd.musicplayer;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.github.nisrulz.sensey.OrientationDetector;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
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
    private Camera mCamera;
    private Camera.Parameters params;
    private boolean isFlashAvailable;
    boolean isFlashOn = false;
    boolean isCamera = false;

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
        instantiateFragments();
        mHandler.postDelayed(mGestureTask, 50);
        cameraStuff();
    }

    private void cameraStuff() {
        isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        try {
            mCamera = Camera.open();
            params = mCamera.getParameters();
            isCamera = true;
        } catch (RuntimeException e) {
            isCamera = false;
            Toast.makeText(this, "Aparat niedostępny.", Toast.LENGTH_SHORT);
            Log.e("Aparat", "niedostepny" );
            e.printStackTrace();
        }
    }

    private void instantiateFragments() {
        mPlaylistFragment = PlaylistFragment.newInstance(mPlaylist);
        mPlayerFragment = PlayerFragment.newInstance(mPlaylist);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stop();
        mHandler.removeCallbacks(mFlashTask);
        mHandler.removeCallbacks(mGestureTask);
        Sensey.getInstance().stopOrientationDetection(orientationListener);
        Sensey.getInstance().stopShakeDetection(shakeListener);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_diskoteka);
        View view = MenuItemCompat.getActionView(menuItem);
        SwitchCompat mSwitch = (SwitchCompat) view.findViewById(R.id.diskoteka_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isFlashAvailable) return;
                if (isChecked) {
                    Sensey.getInstance().startShakeDetection(shakeListener);
                    Sensey.getInstance().stopOrientationDetection(orientationListener);
                    mHandler.removeCallbacks(mGestureTask);
                }
                else {
                    Sensey.getInstance().stopShakeDetection(shakeListener);
                    turnOffFlashLight();
                    mHandler.removeCallbacks(mFlashTask);
                    mHandler.postDelayed(mGestureTask, 10);
                }
            }
        });
        return true;
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

    ShakeDetector.ShakeListener shakeListener = new ShakeDetector.ShakeListener() {
        @Override public void onShakeDetected() {
            if(isCamera) mHandler.postDelayed(mFlashTask, 10);
            else Toast.makeText(getApplicationContext(), "Camera niedostępna", Toast.LENGTH_SHORT).show();
        }

        @Override public void onShakeStopped() {
            mHandler.removeCallbacks(mFlashTask);
            turnOffFlashLight();
        }

    };

    private Runnable mGestureTask = new Runnable() {
        public void run() {
            Sensey.getInstance().startOrientationDetection(orientationListener);
        }
    };

    private Runnable mFlashTask = new Runnable() {
        public void run() {
            if(isFlashOn){
                turnOffFlashLight();
            } else turnOnFlashLight();
            mHandler.postDelayed(mFlashTask, 10);
        }
    };

    public void turnOnFlashLight() {
        if(isCamera) {
            params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(params);
            mCamera.startPreview();
            isFlashOn = true;
        }
    }

    public void turnOffFlashLight() {
        if (isCamera) {
            params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
            mCamera.stopPreview();
            isFlashOn = false;
        }
    }

}