package com.example.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.example.bakingapp.adapters.RecipeDetailAdapter;
import com.example.bakingapp.fragments.RecipeStepDetailFragment;
import com.example.bakingapp.interfaces.OnRecipeStepListener;
import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.services.BakingRecipeService;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity implements OnRecipeStepListener {

    //Constants
    private static final int OFFSET = 0;

    //Views
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.contentFrameLayout)
    FrameLayout contentFrame;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //Objects/Variables
    private int position;
    private Recipe recipe;
    private RecipeDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        //Set Support ActionBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the Recipe from the Bundle
        recipe = (Recipe) getIntent().getExtras().getSerializable(RecipeUtils.ARGS_KEY_RECIPE);

        //Get the current position of the list
        if(savedInstanceState != null)
            position = savedInstanceState.getInt(RecipeUtils.ARGS_KEY_POSITION, 0);

        //Set Activity Title to recipe name
        setTitle(recipe.getName());

        //Create list for the RecipeDetailAdapter
        ArrayList<Object> listItems = new ArrayList<>();

        //Gets the ingredients
        String ingredients = RecipeUtils.getIngredients(this, recipe.getIngredients());

        //Get the Ingredient and Step List
        listItems.add(ingredients);
        listItems.addAll(recipe.getSteps());

        //Create the RecyclerView Adapter
        adapter = new RecipeDetailAdapter(this, listItems, position, contentFrame != null,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Scroll to current position in the RecyclerView
        updateRecyclerViewLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RecipeUtils.ARGS_KEY_POSITION, position);
    }

    @Override
    public void onRecipeStepClicked(int position) {
        this.position = position;

        if(contentFrame != null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(RecipeUtils.ARGS_KEY_POSITION, position);
            bundle.putSerializable(RecipeUtils.ARGS_KEY_STEPS, recipe.getSteps());

            Fragment fragment = new RecipeStepDetailFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.contentFrameLayout, fragment).commit();
        }
        else
        {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(RecipeUtils.ARGS_KEY_POSITION, position);
            intent.putExtra(RecipeUtils.ARGS_KEY_STEPS, recipe.getSteps());
            intent.putExtra(RecipeUtils.ARGS_KEY_RECIPE_NAME, recipe.getName());
            startActivity(intent);
        }

        //Update List Location
        updateRecyclerViewLocation();
    }

    @Override
    public void onRecipeStepChanged(int position) {
        this.position = position;

        adapter.updatePosition(position);
        updateRecyclerViewLocation();
    }

    private void updateRecyclerViewLocation()
    {
        ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, OFFSET);
    }
}
