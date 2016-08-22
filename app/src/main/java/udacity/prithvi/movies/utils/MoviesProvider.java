package udacity.prithvi.movies.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class MoviesProvider extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "udacity.prithvi.movies";
    public static final String URL = "content://" + CONTENT_AUTHORITY + "/movies";
    public static final Uri BASE_CONTENT_URI = Uri.parse(URL);

    private MoviesDbHelper mDatabaseHelper;
    private static final int ALL_MOVIES = 1;
    private static final int SINGLE_MOVIE = 2;
    private static HashMap<String, String> PROJECTION_MAP;

//    }
//    private static final UriMatcher uriMatcher;
//    static{
//        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        uriMatcher.addURI(CONTENT_AUTHORITY, "movies", ALL_MOVIES);
//        uriMatcher.addURI(CONTENT_AUTHORITY, "movies/#", SINGLE_MOVIE);

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new MoviesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MoviesDbHelper.MOVIES_TABLE);
        queryBuilder.setProjectionMap(PROJECTION_MAP);
//        switch(uriMatcher.match(uri)){
//            case ALL_MOVIES:
//                //do nothing
//                break;
//
//            case SINGLE_MOVIE:
//                String id = uri.getPathSegments().get(1);
//                queryBuilder.appendWhere(MoviesDbHelper.);
//                break;
//        }
        Cursor cursor  = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

//        switch(uriMatcher.match(uri)){
//            case ALL_MOVIES:
//                return "vnd.android.cursor.dir/vnd.udacity.prithvi.movies";
//            case SINGLE_MOVIE:
//                return "vnd.android.cursor.item/vnd.udacity.prithvi.movies";
//            default:
//                throw new IllegalArgumentException("Unsupported URI: " +  uri);
//        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

//        switch(uriMatcher.match(uri)){
//            case ALL_MOVIES:
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported URI: " + uri);
//        }
        long id = db.insert(MoviesDbHelper.MOVIES_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int deleteCount = db.delete(MoviesDbHelper.MOVIES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int updateCount = db.update(MoviesDbHelper.MOVIES_TABLE,
                values,
                selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}
