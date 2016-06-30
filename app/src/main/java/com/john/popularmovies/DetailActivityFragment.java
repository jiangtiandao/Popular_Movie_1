package com.john.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }



    public class FetchMovie extends AsyncTask<String, Void, List<Video>> {

        final String AUTHORITY_TMDB = "api.themoviedb.org";
        final String SCHEME = "http";
        final String API_VERSION = "3";
        final String TYPE = "movie";
        final String VIDEO ="videos";
        final String API_KEY = "api_key";
        final String LOG_TAG = FetchMovie.class.getSimpleName();




        @Override
        protected List<Video> doInBackground(String... params) {
            Uri uri = new Uri.Builder()
                    .scheme(SCHEME)
                    .authority(AUTHORITY_TMDB)
                    .appendPath(API_VERSION)
                    .appendPath(TYPE)
                    .appendPath(params[0])
                    .appendPath(VIDEO)
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY).build();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<Video> result;
            // Will contain the raw JSON response as a string.
            String movieInfoJsonStr = null;
            try {
                // Construct the URL for the Movie query
                // Possible parameters are available at TMDB  API page, at
                // http://api.themoviedb.org/3/movie/{id}/video

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

                result = getGetVideoDataFromJson(movieInfoJsonStr);

            }

            return result;
        }


        @Override
        protected void onPostExecute(List<Video> results) {

        }

        public List<Video> getGetVideoDataFromJson(String movieDataJsonStr) {
            final String RESULTS = "results";
            com.alibaba.fastjson.JSONObject data = (com.alibaba.fastjson.JSONObject) JSON.parse(movieDataJsonStr);
            return JSON.parseArray(data.getJSONArray(RESULTS).toJSONString(), Video.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            Toast.makeText(getActivity(), intent.getStringExtra(Intent.EXTRA_TEXT), Toast.LENGTH_SHORT).show();
            FetchMovie fetchMovie= new FetchMovie();
            fetchMovie.execute(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        return inflater.inflate(R.layout.fragment_detail, container, false);

    }
}
