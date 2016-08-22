package udacity.prithvi.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.activities.MainActivity;
import udacity.prithvi.movies.activities.MovieDetailActivity;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.VolleySingleton;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolderMovies> {

    private LayoutInflater layoutInflater;
    private ArrayList<MoviesData> moviesList = new ArrayList<>();
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private Context context;
    private boolean mTwoPane = false;
    private MovieAdapterOnClickHandler clickHandler;
    private int value;

    public MoviesAdapter(ArrayList<MoviesData> moviesList, Context context, MovieAdapterOnClickHandler vh){
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getsInstance();
        imageLoader = volleySingleton.getImageLoader();
        this.moviesList = moviesList;
        this.context = context;
        this.mTwoPane = MainActivity.twoPane;
        this.clickHandler = vh;
        value = 0;
    }

    @Override
    public ViewHolderMovies onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.adapter_movies, parent, false);
        ViewHolderMovies viewHolder = new ViewHolderMovies(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderMovies holder, int position) {

        MoviesData currentMovie = moviesList.get(position);

        String thumbnail = currentMovie.getImage();

        if(thumbnail != null) {
            Uri imageUri = Uri.parse(thumbnail);
            holder.movieThumbnail.setImageURI(imageUri);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class ViewHolderMovies extends RecyclerView.ViewHolder implements View.OnClickListener{

        private SimpleDraweeView movieThumbnail;

        public ViewHolderMovies(View view) {
            super(view);
            view.setOnClickListener(this);
            movieThumbnail = (SimpleDraweeView) view.findViewById(R.id.ivThumbnail);


            if(value == 0 && mTwoPane){
                clickHandler.onClick(moviesList.get(0).getId() + "", this);
                value = 1;
            }
        }

        @Override
        public void onClick(View v) {

            if(!mTwoPane) {
                Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
                intent.putExtra("movieID", moviesList.get(getAdapterPosition()).getId() + "");
                intent.putExtra("fragmentId", 0);
                v.getContext().startActivity(intent);
            }else if(mTwoPane){
                clickHandler.onClick(moviesList.get(getAdapterPosition()).getId() + "", this);
            }
        }
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(String movieId, RecyclerView.ViewHolder vh);
    }
}
