package com.example.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.bakingapp.fragments.RecipeStepDetailFragment;
import com.example.bakingapp.interfaces.OnRecipeStepListener;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepActivity extends AppCompatActivity implements OnRecipeStepListener {

    //Constants
    private static final int DEFAULT_STEP_POSITION = 0;

    //Views
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //Objects
    private int position;
    private String recipeName;
    private ArrayList<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        ButterKnife.bind(this);

            //Used to maintain fragment state on orientation change
            if(savedInstanceState == null)
            {
                //Get the steps from the intent
                recipeName = getIntent().getStringExtra(RecipeUtils.ARGS_KEY_RECIPE_NAME);
                position = getIntent().getIntExtra(RecipeUtils.ARGS_KEY_POSITION, DEFAULT_STEP_POSITION);
                steps = (ArrayList<Step>) getIntent().getSerializableExtra(RecipeUtils.ARGS_KEY_STEPS);

                Bundle bundle = new Bundle();
                bundle.putInt(RecipeUtils.ARGS_KEY_POSITION, position);
                bundle.putSerializable(RecipeUtils.ARGS_KEY_STEPS, steps);

                Fragment fragment = new RecipeStepDetailFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.contentFrameLayout, fragment).commit();
            }
            else
            {
                //Retrieve the recipe name from the savedInstance State
                recipeName = savedInstanceState.getString(RecipeUtils.ARGS_KEY_RECIPE_NAME);
            }

            //Add Toolbar if applicable
            if(toolbar != null)
            {
                setSupportActionBar(toolbar);
                setTitle(getString(R.string.recipe_steps, recipeName));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RecipeUtils.ARGS_KEY_RECIPE_NAME, recipeName);
    }

    @Override
    public void onRecipeStepClicked(int position) {
        //Do Nothing
    }

    @Override
    public void onRecipeStepChanged(int position) {
        //Do Nothing
    }
}
