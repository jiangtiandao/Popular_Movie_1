package com.john.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {



    private PosterAdapter mAdapter;


    public class FetchPopularMovies extends AsyncTask<Void, Void, List<Movie>> {

        final String AUTHORITY_TMDB = "api.themoviedb.org";
        final String SCHEME = "http";
        final String API_VERSION = "3";
        final String TYPE = "movie";
        final String SORT_TYPE = "popular";
        final String API_KEY = "api_key";
        final String LOG_TAG = FetchPopularMovies.class.getSimpleName();


        String[] getMovieDataFromJson;

        @Override
        protected List<Movie> doInBackground(Void... params) {
            Uri uri = new Uri.Builder()
                    .scheme(SCHEME)
                    .authority(AUTHORITY_TMDB)
                    .appendPath(API_VERSION)
                    .appendPath(TYPE)
                    .appendPath(SORT_TYPE)
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY).build();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<Movie> result;
            // Will contain the raw JSON response as a string.
            String movieInfoJsonStr = null;
            try {
                // Construct the URL for the Movie query
                // Possible parameters are avaiable at TMDB  API page, at
                // http://api.themoviedb.org/3/movie/popular

                URL url = new URL(uri.toString());

                // Create the request  and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                AtomicReference<StringBuffer> buffer = new AtomicReference<>(new StringBuffer());
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    StringBuffer append = buffer.get().append(line).append("\n");
                }

                if (buffer.get().length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //noinspection UnusedAssignment
                movieInfoJsonStr = buffer.get().toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

                result = getGetMovieDataFromJson(movieInfoJsonStr);

            }

            return result;
        }


        @Override
        protected void onPostExecute(List<Movie> results) {
            if (results != null) {
                mAdapter.clear();
                mAdapter.setData(results);
            }
        }

        public List<Movie> getGetMovieDataFromJson(String movieDataJsonStr) {
            final String RESULTS = "results";
            com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) JSON.parse(movieDataJsonStr);
            return JSON.parseArray(data.getJSONArray(RESULTS).toJSONString(), Movie.class);
        }
    }

    public MainActivityFragment() {
    }


    public void updateMovie() {
        FetchPopularMovies fetchPopularMovieTask = new FetchPopularMovies();
        fetchPopularMovieTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.grid_view_popular_movie_poster);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mAdapter = new PosterAdapter(new ArrayList<Movie>(), R.layout.grid__movie_poster));
        GridLayoutManager LayoutManager = new GridLayoutManager(this.getActivity(), 2);
        recyclerView.setLayoutManager(LayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setOnItemClickListener(new PosterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = Integer.toString(mAdapter.getMovieId(position));
                Intent intent = new Intent(getContext(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, id);
                startActivity(intent);
            }
        });
        updateMovie();
        return rootView;
    }
}
