package com.example.bakingapp.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bakingapp.R;

public class PreferenceManager {


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    //Context Mode
    private static final int CONTEXT_MODE = Context.MODE_PRIVATE;

    //Shared Preferences Keys
    private static final String PREFERENCES_NAME = "baking_app_preferences";
    private static final String PREF_KEY_RECIPE_NAME = "recipe_name";
    private static final String PREF_KEY_INGREDIENTS = "recipe_ingredients";

    public PreferenceManager(Context context)
    {
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCES_NAME, CONTEXT_MODE);
        editor = preferences.edit();
    }

    public String getPrefKeyIngredients()
    {
        return preferences.getString(PREF_KEY_INGREDIENTS, context.getString(R.string.ingredients_not_found));
    }

    public void setPrefKeyIngredients(String ingredients)
    {
        editor.putString(PREF_KEY_INGREDIENTS, ingredients).commit();
    }

    public String getPrefKeyRecipeName()
    {
        return preferences.getString(PREF_KEY_RECIPE_NAME, "");
    }

    public void setPrefKeyRecipeName(String recipeName)
    {
        editor.putString(PREF_KEY_RECIPE_NAME, recipeName).commit();
    }
}
