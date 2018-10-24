package com.example.bakingapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.bakingapp.adapters.RecipeAdapter;
import com.example.bakingapp.interfaces.OnRecipeClickedListener;
import com.example.bakingapp.loaders.RecipeTaskLoader;
import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.services.BakingRecipeService;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>>,
        OnRecipeClickedListener {

    //Views
    @BindView(R.id.recipeRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.emptyText)
    TextView emptyText;

    //Objects/Variables
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Add ToolBar
        setSupportActionBar(toolbar);

        adapter = new RecipeAdapter(this, new ArrayList<Recipe>(), this);
        recyclerView.setAdapter(adapter);

        //Used to retrieve the network state
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        //Used to verify if the device is connected to the internet
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if(isConnected) {
            getSupportLoaderManager().initLoader(0, null, this);
            emptyText.setVisibility(View.GONE);
        }
        else
        {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText(getString(R.string.no_connection));
        }

    }

    @Override
    public void onRecipeClicked(Recipe recipe) {

        //Gets the ingredients
        String ingredients = RecipeUtils.getIngredients(this, recipe.getIngredients());

        //Update Ingredients in widgets
        BakingRecipeService.startActionUpdateIngredients(this, recipe.getName(), ingredients);

        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeUtils.ARGS_KEY_RECIPE, recipe);
        startActivity(intent);
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        return new RecipeTaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {
        adapter.setCursor(new ArrayList<Recipe>());
    }
}
