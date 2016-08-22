package udacity.prithvi.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.Random;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.model.MoviesData;
import udacity.prithvi.movies.utils.VolleySingleton;

import static udacity.prithvi.movies.model.Endpoints.*;

public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MoviesData movie;
    private ArrayList<String> trailerInfo = new ArrayList<>();
    private ArrayList<String> movieReviews = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private String dataZero = null;

    public MovieDetailsAdapter(MoviesData movie, ArrayList<String> trailerInfo, ArrayList<String> movieReviews, Context context){
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getsInstance();
        imageLoader = volleySingleton.getImageLoader();
        this.context = context;
        this.movie = movie;
        this.trailerInfo = trailerInfo;
        this.movieReviews = movieReviews;
    }

    public MovieDetailsAdapter(MoviesData movie, Context context){
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getsInstance();
        this.context = context;
        this.movie = movie;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.holder_movie_details, parent, false);
            viewHolder = new DetailsViewHolder(view);
            return viewHolder;
        }

        if (viewType == 1) {
            View view = layoutInflater.inflate(R.layout.holder_movie_trailers, parent, false);
            viewHolder = new TrailersViewHolder(view);
            return viewHolder;
        }

        if (viewType == 2) {
            View view = layoutInflater.inflate(R.layout.holder_movie_reviews, parent, false);
            viewHolder = new MovieReviewsHolder(view);
            return viewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case 0:
                ((DetailsViewHolder) holder).movieName.setText(movie.getTitle());
                if(!movie.getTagLine().equals("")) {
                    ((DetailsViewHolder) holder).movieTagLine.setText("\" " + movie.getTagLine() + " \"");
                }else if(movie.getTagLine().equals("")) {
                    ((DetailsViewHolder) holder).movieTagLine.setVisibility(View.GONE);
                }
                ((DetailsViewHolder) holder).movieReleaseDate.setText(movie.getReleasedate() + "(" + movie.getStatus() + ")");

                int durationInMin = movie.getDuration();
                int hours = durationInMin / 60;
                int minutes = durationInMin % 60;
                ((DetailsViewHolder) holder).movieDuration.setText(context.getResources().getString(R.string.duration) + " " + hours + " " + context.getResources().getString(R.string.hour) + " " + minutes + " " + context.getResources().getString(R.string.minutes));

                ((DetailsViewHolder) holder).movieGenre.setText(context.getResources().getString(R.string.genre) + " " + movie.getGenre());
                ((DetailsViewHolder) holder).movieLanguage.setText(String.format(context.getResources().getString(R.string.language) + " " + movie.getLanguage()));
                ((DetailsViewHolder) holder).moviePopularity.setText(String.format("%.1f", movie.getPopularity()) + "");
                ((DetailsViewHolder) holder).movieRating.setText(String.format((movie.getRating()) + ""));
                ((DetailsViewHolder) holder).movieSynopsis.setText(movie.getOverview());
                break;

            case 1:
                final String[] data = trailerInfo.get(position - 1).split(",,");

                dataZero = data[0];
                String coverImage = YOUTUBE_THUMB + data[0] + YOUTUBE_MEDIUM_QUALITY;
                if (coverImage != null) {
                    imageLoader.get(coverImage, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            ((TrailersViewHolder) holder).trailerImageView.setImageBitmap(response.getBitmap());
                        }
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                }

                ((TrailersViewHolder) holder).trailerTitle.setText(data[1]);
                ((TrailersViewHolder) holder).trailerSite.setText(data[2]);
                ((TrailersViewHolder) holder).materialRippleLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + data[0])));
                    }
                });
                break;

            case 2:
                String author = (movieReviews.get(position - 1 - trailerInfo.size())
                        .substring(0, movieReviews.get(position - 1 - trailerInfo.size()).indexOf("-")));
                String authorFirstLetter = String.valueOf(author.charAt(0));

                Random circleColor = new Random();
                int i = circleColor.nextInt(5 - 1) + 1;

                TextDrawable drawable;

                if(i == 1){
                    drawable = TextDrawable.builder()
                            .buildRound(authorFirstLetter.toUpperCase(), context.getResources().getColor(R.color.circleColor1));
                }else if(i == 2){
                    drawable = TextDrawable.builder()
                            .buildRound(authorFirstLetter.toUpperCase(), context.getResources().getColor(R.color.circleColor2));
                }else if(i == 3){
                    drawable = TextDrawable.builder()
                            .buildRound(authorFirstLetter.toUpperCase(), context.getResources().getColor(R.color.circleColor3));
                }else if(i == 4){
                    drawable = TextDrawable.builder()
                            .buildRound(authorFirstLetter.toUpperCase(), context.getResources().getColor(R.color.circleColor4));
                }else{
                    drawable = TextDrawable.builder()
                            .buildRound(authorFirstLetter.toUpperCase(), context.getResources().getColor(R.color.circleColor3));
                }

                ((MovieReviewsHolder) holder)
                        .reviewAuthor
                        .setText(author);

                ((MovieReviewsHolder) holder)
                        .exp
                        .setText(movieReviews.get(position - 1 - trailerInfo.size()).substring(movieReviews.get(position - 1 - trailerInfo.size()).indexOf("-") + 1));

                ((MovieReviewsHolder) holder)
                        .authorImage
                        .setImageDrawable(drawable);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 1 + trailerInfo.size() + movieReviews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        if (position > 0 && position <= trailerInfo.size())
            return 1;
        if(position > trailerInfo.size() && position <= trailerInfo.size() + movieReviews.size())
            return 2;
        return 9999;
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView movieName, movieTagLine, movieReleaseDate, movieDuration, movieGenre, moviePopularity, movieSynopsis, movieRating, movieLanguage;
        private RelativeLayout share;
        private Typeface typeface;

        public DetailsViewHolder(View itemView) {
            super(itemView);

            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Geomanist_Regular.otf");
            movieName = (TextView) itemView.findViewById(R.id.tvMovieTitle);
            movieTagLine = (TextView) itemView.findViewById(R.id.tvMovieTagLine);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.tvMovieReleaseDate);
            movieDuration = (TextView) itemView.findViewById(R.id.tvMovieDuration);
            movieGenre = (TextView) itemView.findViewById(R.id.tvMovieGenre);
            moviePopularity = (TextView) itemView.findViewById(R.id.tvMoviePopularity);
            movieRating = (TextView) itemView.findViewById(R.id.tvMovieRating);
            movieSynopsis = (TextView) itemView.findViewById(R.id.tvMovieSynopsis);
            movieLanguage = (TextView) itemView.findViewById(R.id.tvMovieLanguage);
            share = (RelativeLayout) itemView.findViewById(R.id.rlShare);

            movieName.setTypeface(typeface);
            movieTagLine.setTypeface(typeface);
            movieReleaseDate.setTypeface(typeface);
            movieDuration.setTypeface(typeface);
            movieGenre.setTypeface(typeface);
            moviePopularity.setTypeface(typeface);
            movieRating.setTypeface(typeface);
            movieSynopsis.setTypeface(typeface);
            movieLanguage.setTypeface(typeface);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dataZero != null) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, YOUTUBE_URL + dataZero);
                        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_via)));
                    }else if(dataZero == null){
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.watch) + " " + movie.getTitle() + " " + context.getResources().getString(R.string.releasing_on) + " " + movie.getReleasedate());
                        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_via)));
                    }
                }
            });
        }
    }

    public class TrailersViewHolder extends RecyclerView.ViewHolder{

        private TextView trailerTitle, trailerSite;
        private ImageView trailerImageView;
        private Typeface typeface;
        private MaterialRippleLayout materialRippleLayout;

        public TrailersViewHolder(View itemView) {
            super(itemView);

            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Geomanist_Regular.otf");
            trailerTitle = (TextView) itemView.findViewById(R.id.tvTrailerTitle);
            trailerSite = (TextView) itemView.findViewById(R.id.tvTrailerSite);
            trailerImageView = (ImageView) itemView.findViewById(R.id.ivTrailerImage);
            materialRippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id.trailersRipple);

            trailerTitle.setTypeface(typeface);
            trailerSite.setTypeface(typeface);
        }
    }

    public class MovieReviewsHolder extends RecyclerView.ViewHolder{

        private TextView reviewAuthor, movieReview;
        private ImageView authorImage;
        private Typeface typeface;
        private ExpandableTextView exp;

        public MovieReviewsHolder(View itemView) {
            super(itemView);

            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Geomanist_Regular.otf");
            reviewAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
            movieReview = (TextView) itemView.findViewById(R.id.expandable_text);
            authorImage = (ImageView) itemView.findViewById(R.id.ivAuthor);
            exp = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);

            reviewAuthor.setTypeface(typeface);
            movieReview.setTypeface(typeface);
        }
    }
}
