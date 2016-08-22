package udacity.prithvi.movies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import udacity.prithvi.movies.model.MoviesData;

import static udacity.prithvi.movies.model.ResponseKeys.*;

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies_Database";
    public static final String MOVIES_TABLE = "movies_Details";

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_URL = "CREATE TABLE " + MOVIES_TABLE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + TITLE + " TEXT,"
                + TAGLINE + " TEXT,"
                + STATUS + " TEXT,"
                + RELEASE_DATE + " TEXT,"
                + GENRE_NAME + " TEXT,"
                + IMAGE + " TEXT,"
                + COVER_IMAGE + " TEXT,"
                + OVERVIEW + " TEXT,"
                + DURATION + " TEXT,"
                + POPULARITY + " TEXT,"
                + RATING + " TEXT,"
                + LANGUAGE + " TEXT"
                + ")";

        db.execSQL(CREATE_TABLE_URL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIES_TABLE);
        onCreate(db);
    }

    //METHODS
    public void insertMovie(MoviesData movie){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues movieValues = new ContentValues();
        movieValues.put(ID, movie.getId());
        movieValues.put(TITLE, movie.getTitle());
        movieValues.put(TAGLINE, movie.getTagLine());
        movieValues.put(STATUS, movie.getStatus());
        movieValues.put(RELEASE_DATE, movie.getReleasedate());
        movieValues.put(GENRE_NAME, movie.getGenre());
        movieValues.put(IMAGE, movie.getImage());
        movieValues.put(COVER_IMAGE, movie.getCoverImage());
        movieValues.put(DURATION, movie.getDuration());
        movieValues.put(POPULARITY, movie.getPopularity());
        movieValues.put(OVERVIEW, movie.getOverview());
        movieValues.put(RATING, movie.getRating());
        movieValues.put(LANGUAGE, movie.getLanguage());

        db.insert(MOVIES_TABLE, null, movieValues);
        db.close();
    }

    public MoviesData getMovie(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor movieCursor = db.query(MOVIES_TABLE,
                new String[]{ID, TITLE,
                        TAGLINE, STATUS,
                        RELEASE_DATE, GENRE_NAME,
                        IMAGE, COVER_IMAGE,
                        DURATION, POPULARITY,
                        OVERVIEW, RATING,
                        LANGUAGE
                },
                ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );

        if(movieCursor != null)
            movieCursor.moveToFirst();

        MoviesData movie = new MoviesData(Integer.parseInt(movieCursor.getString(0)), movieCursor.getString(1),
                movieCursor.getString(2), movieCursor.getString(3),
                movieCursor.getString(4), movieCursor.getString(5),
                movieCursor.getString(6), movieCursor.getString(7),
                Integer.parseInt(movieCursor.getString(8)), Float.parseFloat(movieCursor.getString(9)),
                movieCursor.getString(10), Float.parseFloat(movieCursor.getString(11)),
                movieCursor.getString(12));
        return movie;
    }

    public List<MoviesData> getAllMovies(){

        List<MoviesData> moviesList = new ArrayList<MoviesData>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + MOVIES_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                MoviesData movie = new MoviesData();
                movie.setId(Integer.parseInt(cursor.getString(0)));
                movie.setTitle(cursor.getString(1));
                movie.setTagLine(cursor.getString(2));
                movie.setStatus(cursor.getString(3));
                movie.setReleasedate(cursor.getString(4));
                movie.setGenre(cursor.getString(5));
                movie.setImage(cursor.getString(6));
                movie.setCoverImage(cursor.getString(7));
                movie.setDuration(cursor.getInt(9));
                movie.setPopularity(cursor.getFloat(10));
                movie.setOverview(cursor.getString(8));
                movie.setRating(cursor.getFloat(11));
                movie.setLanguage(cursor.getString(12));
                moviesList.add(movie);
            }while(cursor.moveToNext());
        }
        return moviesList;
    }

    public void deleteMovie(MoviesData movie){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MOVIES_TABLE, ID + "=?" , new String[]{String.valueOf(movie.getId())});
        db.close();
    }

    public int numberOfRows(){
        String query = "SELECT  * FROM " + MOVIES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int numRows = cursor.getCount();
        cursor.close();
        return numRows;
    }
}
