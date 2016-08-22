package udacity.prithvi.movies.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.activities.MainActivity;
import udacity.prithvi.movies.adapters.MovieDetailsAdapter;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.MoviesProvider;
import udacity.prithvi.movies.utils.MoviesProviderMethods;
import udacity.prithvi.movies.utils.VolleySingleton;

import static udacity.prithvi.movies.model.ResponseKeys.*;
import static udacity.prithvi.movies.model.Endpoints.*;

public class MovieDetailFragment extends Fragment {

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue = null;
    private ImageView movieImage, back;
    private ImageLoader imageLoader;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MoviesData movie;
    private ArrayList<String> trailerInfo = new ArrayList<>();
    private ArrayList<String> reviewInfo = new ArrayList<>();

    private FloatingActionButton fab;
    private Activity activity;
    private int fragmentValue;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
        movie = new MoviesData();
        imageLoader = volleySingleton.getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_movie_detail);
        mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        movieImage = (ImageView) view.findViewById(R.id.ivMovieImage);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if(MainActivity.twoPane == false) {
            back = (ImageView) view.findViewById(R.id.ivBack);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }

        //GETTING VALUES FROM FRAGMENTS
        final String movieID = getArguments().getString("movieID");
        fragmentValue = getArguments().getInt("fragmentId");

        sendMovieDetailRequest(movieID);
        return view;
    }

    private void sendMovieDetailRequest(final String id){
        String url = MOVIE_BASE_URL + id + "?" + API_KEY;

        if(fragmentValue == 0) {
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    (String) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            movie = parseJSONResponse(response);
                            getTrailerInfo(id);
                            otherwork();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(request);
        }else if (fragmentValue == 2) {
            movie = MoviesProviderMethods.getMovieFromDatabase(activity, id);
            otherwork();
            mAdapter = new MovieDetailsAdapter(movie, activity);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void otherwork(){
        String coverImage = movie.getCoverImage();
        if (coverImage != null) {
            imageLoader.get(coverImage, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap map = response.getBitmap();
                    movieImage.setImageBitmap(map);
                    if (map != null) {
                        Palette.from(map).generate(new Palette.PaletteAsyncListener() {
                            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                                if (vibrantSwatch != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        activity.getWindow().setStatusBarColor(vibrantSwatch.getRgb());
                                    }
                                }
                            }
                        });
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }

        boolean isMovieInDb;
        isMovieInDb = MoviesProviderMethods.isMovieInDatabase(activity, movie.getId());

        if (isMovieInDb) {
            fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_white_24dp));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_border_white_24dp));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMovieInDb = MoviesProviderMethods.isMovieInDatabase(activity, movie.getId());

                if (isMovieInDb) {
                    Uri contentUri = MoviesProvider.BASE_CONTENT_URI;
                    activity.getContentResolver().delete(contentUri, "id=?", new String[]{String.valueOf(movie.getId())});
                    fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_border_white_24dp));
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.removed), Snackbar.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ID, movie.getId());
                    values.put(TITLE, movie.getTitle());
                    values.put(TAGLINE, movie.getTagLine());
                    values.put(STATUS, movie.getStatus());
                    values.put(RELEASE_DATE, movie.getReleasedate());
                    values.put(GENRE_NAME, movie.getGenre());
                    values.put(IMAGE, movie.getImage());
                    values.put(COVER_IMAGE, movie.getCoverImage());
                    values.put(OVERVIEW, movie.getOverview());
                    values.put(DURATION, movie.getDuration());
                    values.put(POPULARITY, movie.getPopularity());
                    values.put(OVERVIEW, movie.getOverview());
                    values.put(RATING, movie.getRating());
                    values.put(LANGUAGE, movie.getLanguage());
                    activity.getContentResolver().insert(MoviesProvider.BASE_CONTENT_URI, values);
                    fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_white_24dp));
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.liked) + " " + getResources().getString(R.string.heart), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private MoviesData parseJSONResponse(JSONObject response){

        try {

            int id = response.getInt(ID);
            String title = response.getString(TITLE);
            String tagLine = response.getString(TAGLINE);
            String releaseDate = response.getString(RELEASE_DATE);
            String status = response.getString(STATUS);
            String overview = response.getString(OVERVIEW);
            String image = POSTER_PATH + response.getString(IMAGE);
            String movieCoverImage = COVER_IMAGE_PATH + response.getString(COVER_IMAGE);
            int duration = Integer.parseInt(response.getString(DURATION));
            float popularity = Float.parseFloat(response.getString(POPULARITY));
            float rating = Float.parseFloat(response.getString(RATING));
            String language = response.getString(LANGUAGE);

            String genres = "";
            JSONArray genreArray = response.getJSONArray(GENRES);
            for (int i = 0; i < genreArray.length(); i++) {
                String genre = genreArray.getJSONObject(i).getString(GENRE_NAME);
                if (i != genreArray.length() - 1)
                    genres += genre + ", ";
                else
                    genres += genre + ".";
            }

            movie.setId(id);
            movie.setTitle(title);
            movie.setTagLine(tagLine);
            movie.setReleasedate(releaseDate);
            movie.setStatus(status);
            movie.setOverview(overview);
            movie.setCoverImage(movieCoverImage);
            movie.setImage(image);
            movie.setDuration(duration);
            movie.setPopularity(popularity);
            movie.setGenre(genres);
            movie.setRating(rating);
            movie.setLanguage(language);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private void getTrailerInfo(final String id) {
        String requestUrl = MOVIE_BASE_URL + id + "/videos?" + API_KEY;

        JsonObjectRequest mTrailerRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestUrl,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mTrailerArray = response.getJSONArray(RESULTS);
                            for (int i = 0; i < mTrailerArray.length(); i++) {
                                JSONObject mTrailerObject = mTrailerArray.getJSONObject(i);
                                trailerInfo.add(mTrailerObject.getString(TRAILER_KEY) + ",," + mTrailerObject.getString(TRAILER_NAME)
                                        + ",," + mTrailerObject.getString(TRAILER_SITE) + ",," + mTrailerObject.getString(TRAILER_SIZE)
                                        + ",," + mTrailerObject.getString(TRAILER_TYPE));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getMovieReviews(id);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(mTrailerRequest);
    }

    private void getMovieReviews(final String id){
        String requestURL = MOVIE_BASE_URL + id + "/reviews?" + API_KEY;

        JsonObjectRequest mReviewsRequest = new JsonObjectRequest(
                Request.Method.GET,
                requestURL,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mReviewArray = response.getJSONArray(RESULTS);
                            for(int i = 0; i < mReviewArray.length(); i++){
                                JSONObject mReview = mReviewArray.getJSONObject(i);
                                reviewInfo.add(mReview.getString(REVIEW_AUTHOR) + "-" + mReview.getString(REVIEW_CONTENT));
                            }
                            if(activity != null){
                                mAdapter = new MovieDetailsAdapter(movie, trailerInfo, reviewInfo, activity);
                                mRecyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(mReviewsRequest);
    }
}
