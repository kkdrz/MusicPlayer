package pl.edu.pwr.drozd.musicplayer;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.pwr.drozd.musicplayer.dataModel.Song;


class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    private ArrayList<Song> playlist;
    private Context context;
    private ViewHolderListener mListener;
    private int currentSong = -1;

    PlaylistAdapter(Context context, ArrayList<Song> playlist, ViewHolderListener listener) {
        this.context = context;
        this.playlist = playlist;
        this.mListener = listener;
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
        Utils.displayRandomAlbumCover(context, holder.mPosterImage);

        if (currentSong != -1 && currentSong == position)
            holder.mItemLayout.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.playlist_item_background_selected, null));
        else
            holder.mItemLayout.setBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.playlist_item_background, null));
    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public void setCurrentSong(int currentSong) {
        int prevSong = this.currentSong;
        this.currentSong = currentSong;
        notifyItemsChanged(prevSong, currentSong);
    }

    public void notifyItemsChanged(int first, int second) {
        notifyItemChanged(first);
        notifyItemChanged(second);
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        @BindView(R.id.song_title)      TextView mSongTitle;
        @BindView(R.id.song_author)     TextView mSongAuthor;
        @BindView(R.id.img_poster)      ImageView mPosterImage;
        @BindView(R.id.playlist_item)   ViewGroup mItemLayout;
        MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            int prevSong = currentSong;
            currentSong = getLayoutPosition();
            mListener.onPlaylistItemClicked(currentSong);
            notifyItemsChanged(prevSong, currentSong);
        }
    }

    public interface ViewHolderListener {
        void onPlaylistItemClicked(int layoutPosition);
    }
}
