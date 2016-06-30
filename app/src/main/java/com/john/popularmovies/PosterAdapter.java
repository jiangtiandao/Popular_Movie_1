package com.john.popularmovies;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by John on 2016/6/17.
 * Custom adapter
 */
public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.MovieViewHolder> {
    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();
    private static final String AUTHORITY = "image.tmdb.org";

    private List<Movie> movies;
    private int posters_layout;


    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public int getMovieId(int position) {
        return movies.get(position).getId();
    }

    public PosterAdapter(List<Movie> movies, int posters_layout) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        this.movies = movies;
        this.posters_layout = posters_layout;
    }


    public void add(Movie item, int position) {
        movies.add(position, item);
        notifyItemInserted(position);
    }

    public void add(Movie item) {
        movies.add(item);
        notifyDataSetChanged();
    }

    public void setData(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void remove(Movie item) {
        int position = movies.indexOf(item);
        movies.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return movies.size();
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(posters_layout, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, final int position) {
        Movie poster = movies.get(position);
        Uri posterUri = new Uri.Builder()
                .scheme("http")
                .authority(AUTHORITY)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185")
                .appendEncodedPath(poster.getPoster_path()).build();
        holder.image.setImageURI(posterUri);

        if (mOnItemClickListener != null) {
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView image;

        public MovieViewHolder(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.grid_movie_poster);
        }
    }

}
