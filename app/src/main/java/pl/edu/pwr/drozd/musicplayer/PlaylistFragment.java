package pl.edu.pwr.drozd.musicplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;

import static pl.edu.pwr.drozd.musicplayer.ViewPagerActivity.PLAYLIST;

public class PlaylistFragment extends Fragment {

    @BindView(R.id.playlist_recycler)       RecyclerView mRecyclerView;

    ArrayList<Song> playlist;

    public static PlaylistFragment newInstance(ArrayList<Song> playlist) {
        PlaylistFragment f = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PLAYLIST, playlist);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            playlist = bundle.getParcelableArrayList(PLAYLIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(new PlaylistAdapter(playlist));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        return view;
    }
}
