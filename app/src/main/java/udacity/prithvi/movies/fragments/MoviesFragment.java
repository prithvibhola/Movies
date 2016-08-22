package udacity.prithvi.movies.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import udacity.prithvi.movies.R;
import udacity.prithvi.movies.activities.MainActivity;
import udacity.prithvi.movies.adapters.MoviesAdapter;
//import udacity.prithvi.movies.adapters.PopularMoviesAdapter;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.CheckNetwork;
import udacity.prithvi.movies.utils.VolleySingleton;
import static udacity.prithvi.movies.model.Endpoints.*;
import static udacity.prithvi.movies.model.ResponseKeys.*;

public class MoviesFragment extends Fragment{

    private Context context;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue = null;
    private ArrayList<MoviesData> moviesList = new ArrayList();
    private MoviesAdapter moviesAdapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private String url;
    private int sortValue;

    private int visibleItemCount, totalItemCount, firstVisibleItem;
    private int previousTotal = 0, pageCount = 1, visibleThreshold = 4;
    private boolean loading = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        if (savedInstanceState != null) {
            moviesList = savedInstanceState.getParcelableArrayList("moviesList");
            pageCount = savedInstanceState.getInt("pageCount");
            previousTotal = savedInstanceState.getInt("previousTotal");
            firstVisibleItem = savedInstanceState.getInt("firstVisibleItem");
            visibleItemCount = savedInstanceState.getInt("visibleItemCount");
            totalItemCount = savedInstanceState.getInt("totalItemCount");
            loading = savedInstanceState.getBoolean("loading");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("moviesList", moviesList);
        outState.putInt("pageCount", pageCount);
        outState.putInt("previousTotal", previousTotal);
        outState.putInt("firstVisibleItem", firstVisibleItem);
        outState.putInt("visibleItemCount", visibleItemCount);
        outState.putInt("totalItemCount", totalItemCount);
        outState.putBoolean("loading", loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Fresco.initialize(context);

        View view;
        if(CheckNetwork.isConnectionAvailable(context)) {
            view = inflater.inflate(R.layout.fragment_movies, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.rvPopularMovies);

            moviesAdapter = new MoviesAdapter(moviesList, context, new MoviesAdapter.MovieAdapterOnClickHandler() {
                @Override
                public void onClick(String movieId, RecyclerView.ViewHolder vh) {
                    Bundle bundle = new Bundle();
                    Fragment movieDetail = new MovieDetailFragment();
                    bundle.putString("movieID", movieId);
                    movieDetail.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.movie_detail_container, movieDetail)
                            .commit();
                }
            });

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                gridLayoutManager = new GridLayoutManager(context, 3);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridLayoutManager = new GridLayoutManager(context, 2);
            }

            sortValue = getArguments().getInt("sort_value");
            if(sortValue == 0) {
                url = MOVIE_BASE_URL + SORT_POPULARITY + API_KEY;
            }else{
                url = MOVIE_BASE_URL + SORT_HIGHEST_RATED + API_KEY;
            }

            sendJSONRequest(url);
            setUpRecyclerView(recyclerView);
        }else{
            view = inflater.inflate(R.layout.fragment_no_connection, container, false);
        }
        return view;
    }

    public void sendJSONRequest(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        moviesList = parseJSONResponse(response);
                        moviesAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(request);
    }

    private ArrayList<MoviesData> parseJSONResponse(JSONObject response){

        if(response != null && response.length() > 0){

            try{
                JSONArray arrayMovies = response.getJSONArray(RESULTS);
                for(int i = 0; i < arrayMovies.length(); i++){
                    JSONObject currentMovie = arrayMovies.getJSONObject(i);
                    int id = currentMovie.getInt(ID);
                    String title = currentMovie.getString(TITLE);
                    String image = POSTER_PATH + currentMovie.getString(IMAGE);
                    String releaseDate = currentMovie.getString(RELEASE_DATE);
                    String overview = currentMovie.getString(OVERVIEW);

                    MoviesData moviesData = new MoviesData();
                    moviesData.setId(id);
                    moviesData.setTitle(title);
                    moviesData.setImage(image);
                    moviesData.setReleasedate(releaseDate);
                    moviesData.setOverview(overview);
                    moviesList.add(moviesData);
                }
            }catch (JSONException e){

            }
        }
        return moviesList;
    }

    private void setUpRecyclerView(RecyclerView rv){
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setLayoutManager(gridLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                String url;

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                //Hide or show FAB on scroll
                if (dy > 0) {
                    ((MainActivity) context).hideFloatingActionButton();
                } else if (dy < 0) {
                    ((MainActivity) context).showFloatingActionButton();
                }

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        pageCount++;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)){
                    if(sortValue == 0) {
                        url = MOVIE_BASE_URL + SORT_POPULARITY + API_KEY + "&page=" + String.valueOf(pageCount);
                    }else{
                        url = MOVIE_BASE_URL + SORT_HIGHEST_RATED + API_KEY + "&page=" + String.valueOf(pageCount);
                    }
                    sendJSONRequest(url);
                    loading = true;
                }
            }
        });
        rv.setAdapter(moviesAdapter);
    }
}
