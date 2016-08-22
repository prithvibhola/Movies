package udacity.prithvi.movies.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bowyer.app.fabtoolbar.FabToolbar;

import udacity.prithvi.movies.R;
import udacity.prithvi.movies.fragments.FavouriteMoviesFragment;
import udacity.prithvi.movies.fragments.MoviesFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private FabToolbar fabToolbar;
    private FloatingActionButton fab;
    private ImageView popular, highestRated, favourite;

    private Fragment fragment = null;
    private Class fragmentClass;

    private int optionSelected = 0;
    private boolean fabStatus = false;
    public static boolean twoPane;
    private FragmentManager fragmentManager;
    private SharedPreferences sp;
    private static final String SP_NAME = "option";
    private int optionValue;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

        //CUSTOM TOOLBAR
        setSupportActionBar(toolbar);

        fabToolbar.setFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabToolbar.expandFab();
                fabStatus = true;
            }
        });

        bundle = new Bundle();

        if (savedInstanceState == null) {
            if (optionValue == 0) {
                bundle.putInt("sort_value", 0);
                fragmentClass = MoviesFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .commit();

            }else if (optionValue == 1) {
                bundle.putInt("sort_value", 1);
                fragmentClass = MoviesFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .commit();

            }else if (optionValue == 2) {
                fragmentClass = FavouriteMoviesFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .commit();
            }
        }
    }

    private void initialization() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fabToolbar = (FabToolbar) findViewById(R.id.fabtoolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        popular = (ImageView) findViewById(R.id.ivPopular);
        highestRated = (ImageView) findViewById(R.id.ivHighestRated);
        favourite = (ImageView) findViewById(R.id.ivFavourite);

        sp = getSharedPreferences(SP_NAME, 0);
        optionValue = sp.getInt("optionValue", 0);
        if(optionValue == 0){
            toolbar.setTitle(R.string.popular);
        }else if(optionValue == 1){
            toolbar.setTitle(R.string.highestRated);
        }else if(optionValue == 2){
            toolbar.setTitle(R.string.favourite);
        }

        if(findViewById(R.id.movie_detail_container) != null){
            twoPane = true;
        }

        popular.setOnClickListener(this);
        highestRated.setOnClickListener(this);
        favourite.setOnClickListener(this);
    }

    public void showFloatingActionButton(){
        fabToolbar.slideInFab();
    }

    public void hideFloatingActionButton(){
        fabToolbar.slideOutFab();
    }

    @Override
    public void onBackPressed() {
        if(fabStatus){
            fabToolbar.slideOutFab();
            fabStatus = false;
        }else if(!fabStatus){
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("option", optionSelected);
    }

    @Override
    public void onClick(View v) {

        fragmentManager = getSupportFragmentManager();
        SharedPreferences.Editor spEditor = sp.edit();

        switch(v.getId()){
            case R.id.ivPopular:
                bundle.putInt("sort_value", 0);
                spEditor.putInt("optionValue", 0);
                fragmentClass = MoviesFragment.class;
                toolbar.setTitle(R.string.popular);
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ivHighestRated:
                bundle.putInt("sort_value", 1);
                spEditor.putInt("optionValue", 1);
                fragmentClass = MoviesFragment.class;
                toolbar.setTitle(R.string.highestRated);
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ivFavourite:
                spEditor.putInt("optionValue", 2);
                fragmentClass = FavouriteMoviesFragment.class;
                toolbar.setTitle(R.string.favourite);
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
        spEditor.commit();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .commit();
        fabToolbar.slideOutFab();
        fabStatus = false;
    }
}
