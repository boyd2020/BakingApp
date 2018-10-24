package com.example.bakingapp.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bakingapp.BakingAppWidgetProvider;
import com.example.bakingapp.managers.PreferenceManager;
import com.example.bakingapp.utils.RecipeUtils;

public class BakingRecipeService extends IntentService {

    public static final String ACTION_UPDATE_INGREDIENTS = "com.example.bakingapp.action.update_ingredients";

    public BakingRecipeService() {
        super("BakingRecipeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        switch (action)
        {
            case ACTION_UPDATE_INGREDIENTS:
                handleActionUpdateIngredients();
                break;

            default:
                handleActionUpdateIngredients();
                break;
        }
    }

    //Initialize the service to update the BakingApp Widget
    public static void startActionUpdateIngredients(Context context, String recipeName, String ingredients)
    {
        PreferenceManager manager = new PreferenceManager(context);

        //Add Ingredients list to the Shared Preferences
        manager.setPrefKeyIngredients(ingredients);
        manager.setPrefKeyRecipeName(recipeName);

        Intent intent = new Intent(context, BakingRecipeService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        context.startService(intent);
    }

    private void handleActionUpdateIngredients()
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));

        //Update all associated Widgets
        BakingAppWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds);
    }
}
