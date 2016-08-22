package udacity.prithvi.movies.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.activities.MainActivity;
import udacity.prithvi.movies.adapters.FavouriteMoviesAdapter;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.MoviesProviderMethods;

public class FavouriteMoviesFragment extends Fragment {

    private Activity activity;
    private ArrayList<MoviesData> movieList = new ArrayList<>();
    private RecyclerView recyclerViewFavouriteMovies;
    private GridLayoutManager gridLayoutManager;
    private FavouriteMoviesAdapter favouriteMoviesAdapter;
    private TextView textEmpty;
    private ImageView imageEmpty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Fresco.initialize(activity);

        View view;
        view = inflater.inflate(R.layout.fragment_favourite_movies, container, false);
        recyclerViewFavouriteMovies = (RecyclerView) view.findViewById(R.id.rvFavouriteMovies);
        textEmpty = (TextView) view.findViewById(R.id.tvEmptyFav);
        imageEmpty = (ImageView) view.findViewById(R.id.ivEmptyFav);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(activity, 3);
        }
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(activity, 2);
        }

        getMovies();

        recyclerViewFavouriteMovies.setLayoutManager(new LinearLayoutManager(recyclerViewFavouriteMovies.getContext()));
        recyclerViewFavouriteMovies.setLayoutManager(gridLayoutManager);
        favouriteMoviesAdapter = new FavouriteMoviesAdapter(movieList, activity, new FavouriteMoviesAdapter.MovieAdapterOnClickHandler() {
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
        recyclerViewFavouriteMovies.setAdapter(favouriteMoviesAdapter);

        setUpRecyclerView(recyclerViewFavouriteMovies);
        return view;
    }

    private void getMovies(){

        ArrayList<MoviesData> list = new ArrayList<>(MoviesProviderMethods.getMovieList(activity));
        movieList.clear();

        if(list.size() != 0) {
            textEmpty.setVisibility(View.GONE);
            imageEmpty.setVisibility(View.GONE);

            for (MoviesData movie : list) {
                movieList.add(movie);
            }
        }else{
            textEmpty.setVisibility(View.VISIBLE);
            imageEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMovies();
        favouriteMoviesAdapter.notifyDataSetChanged();
    }

    private void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerViewFavouriteMovies.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //Hide or show FAB on scroll
                if(MainActivity.twoPane == false) {
                    if (dy > 0) {
                        ((MainActivity) activity).hideFloatingActionButton();
                    } else if (dy < 0) {
                        ((MainActivity) activity).showFloatingActionButton();
                    }
                }
            }
        });
        recyclerView.setAdapter(favouriteMoviesAdapter);
    }
}
