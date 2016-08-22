package udacity.prithvi.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.activities.MainActivity;
import udacity.prithvi.movies.activities.MovieDetailActivity;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.VolleySingleton;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.ViewHolderMovies>{

    private LayoutInflater layoutInflater;
    private ArrayList<MoviesData> movieList = new ArrayList<>();
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private Context context;
    private boolean mTwoPane = false;
    private MovieAdapterOnClickHandler mClickHandler;

    public FavouriteMoviesAdapter(ArrayList<MoviesData> movieList, Context context, MovieAdapterOnClickHandler vh){
        this.movieList = movieList;
        this.context = context;
        volleySingleton = VolleySingleton.getsInstance();
        imageLoader = volleySingleton.getImageLoader();
        layoutInflater = LayoutInflater.from(context);
        this.mTwoPane = MainActivity.twoPane;
        this.mClickHandler = vh;
    }

    @Override
    public ViewHolderMovies onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.adapter_favourite_movies, parent, false);
        ViewHolderMovies viewHolder = new ViewHolderMovies(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderMovies holder, int position) {

        MoviesData currentMovie = movieList.get(position);

        holder.textTitle.setText(currentMovie.getTitle());
        holder.textGenre.setText(currentMovie.getGenre());
        holder.textPopularity.setText(String.format("%.1f", currentMovie.getPopularity()) + "");
        holder.textRating.setText(String.format("%.1f", currentMovie.getRating()) + "");

        String thumbnail = currentMovie.getImage();

        if(thumbnail != null) {
            Uri imageUri = Uri.parse(thumbnail);
            holder.movieThumbnail.setImageURI(imageUri);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolderMovies extends RecyclerView.ViewHolder implements View.OnClickListener{

        private SimpleDraweeView movieThumbnail;
        private TextView textTitle, textGenre, textPopularity, textRating;
        private Typeface typeface;

        public ViewHolderMovies(View view) {
            super(view);
            view.setOnClickListener(this);
            movieThumbnail = (SimpleDraweeView) view.findViewById(R.id.ivThumbnail);
            textTitle = (TextView) view.findViewById(R.id.tvTitle);
            textGenre = (TextView) view.findViewById(R.id.tvGenre);
            textPopularity = (TextView) view.findViewById(R.id.tvMoviePopularity);
            textRating = (TextView) view.findViewById(R.id.tvMovieRating);

            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Geomanist_Regular.otf");

            textTitle.setTypeface(typeface);
            textGenre.setTypeface(typeface);
            textPopularity.setTypeface(typeface);
        }

        @Override
        public void onClick(View v) {
            if(mTwoPane == false){
                Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
                intent.putExtra("movieID", movieList.get(getAdapterPosition()).getId()+"");
                intent.putExtra("fragmentId", 2);
                v.getContext().startActivity(intent);

            }else if(mTwoPane == true){
                mClickHandler.onClick(movieList.get(getAdapterPosition()).getId() + "", this);
            }
        }
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(String movieId, RecyclerView.ViewHolder vh);
    }
}
