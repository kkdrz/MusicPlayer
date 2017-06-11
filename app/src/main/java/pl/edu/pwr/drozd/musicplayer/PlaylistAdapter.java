package pl.edu.pwr.drozd.musicplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;


class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    private ArrayList<Song> playlist;

    PlaylistAdapter(ArrayList<Song> playlist) {
        this.playlist = playlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song current = playlist.get(position);
        holder.mSongAuthor.setText(current.getAuthor());
        holder.mSongTitle.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.song_title)  TextView mSongTitle;
        @BindView(R.id.song_author) TextView mSongAuthor;
        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
