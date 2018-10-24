package com.example.bakingapp.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.List;

public class RecipeTaskLoader extends AsyncTaskLoader<List<Recipe>> {

    public RecipeTaskLoader(Context context) {
        super(context);
    }

    @Override
    public List<Recipe> loadInBackground() {
        return RecipeUtils.getRecipesFromServer();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
