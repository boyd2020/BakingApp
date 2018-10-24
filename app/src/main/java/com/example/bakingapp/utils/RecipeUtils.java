package com.example.bakingapp.utils;

import android.content.Context;
import android.util.Log;

import com.example.bakingapp.R;
import com.example.bakingapp.managers.PreferenceManager;
import com.example.bakingapp.model.Ingredient;
import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecipeUtils {

    //Constants
    private static final String FORMAT_MP4 = ".mp4";

    //Keys
    public static final String ARGS_KEY_STEP = "step";
    public static final String ARGS_KEY_STEPS = "steps";
    public static final String ARGS_KEY_POSITION = "position";
    public static final String ARGS_KEY_RECIPE = "recipe";
    public static final String ARGS_KEY_RECIPE_NAME = "recipeName";

    //Urls
    private static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    //JSON Recipe Keys
    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_SERVINGS = "servings";
    private static final String JSON_IMAGE = "image";

    //JSON Ingredient Keys
    private static final String JSON_INGREDIENTS = "ingredients";
    private static final String JSON_QUANTITY = "quantity";
    private static final String JSON_MEASURE = "measure";
    private static final String JSON_INGREDIENT = "ingredient";

    //JSON Step Keys
    private static final String JSON_STEPS = "steps";
    private static final String JSON_SHORT_DESCRIPTION = "shortDescription";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_VIDEO_URL = "videoURL";
    private static final String JSON_THUMBNAIL_URL = "thumbnailURL";



    /**
     * Creates a URL Object
     */
    private static URL getUrl(String urlName)
    {
        try {
            URL url = new URL(urlName);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates a JSON response from the input stream
     */
    private static String readFromStream(InputStream inputStream) throws IOException
    {
        StringBuilder output = new StringBuilder();

        if(inputStream != null)
        {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line = bufferedReader.readLine();

            while(line != null)
            {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Creates HTTP Request
     */
    private static String getHttpRequest(URL url) throws IOException
    {
        String jsonResponse ="";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        if(url == null)
            return jsonResponse;

        try
        {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();

            if(inputStream != null)
                inputStream.close();
        }

        return jsonResponse;
    }

    public static ArrayList<Recipe> getRecipesFromServer()
    {
        URL url = getUrl(RECIPE_URL);

        String jsonResponse = "";

        try {
            jsonResponse = getHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Recipe> recipes = getRecipes(jsonResponse);
        return recipes;
    }

    //Method to extract the recipe data
    public static ArrayList<Recipe> getRecipes(String response)
    {
        if(response.isEmpty())
            return null;

        ArrayList<Recipe> recipes = new ArrayList<>();

        try {
            JSONArray jsonRecipes = new JSONArray(response);

            //Outer loop to iterate through array
            for(int i = 0; i < jsonRecipes.length(); i++)
            {
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);

                //Create new Recipe Object
                Recipe recipe = new Recipe();

                //Creates Ingredient List
                ArrayList<Ingredient> ingredients = new ArrayList<>();

                //Creates Step List
                ArrayList<Step> steps = new ArrayList<>();

                //Get ID, Name, and Servings
                recipe.setId(jsonRecipe.getInt(JSON_ID));
                recipe.setName(jsonRecipe.getString(JSON_NAME));
                recipe.setServings(jsonRecipe.getInt(JSON_SERVINGS));
                recipe.setImage(jsonRecipe.getString(JSON_IMAGE));

                //Retrieve Recipe Ingredients
                JSONArray jsonIngredients = jsonRecipe.getJSONArray(JSON_INGREDIENTS);

                for(int j = 0; j < jsonIngredients.length(); j++)
                {
                    JSONObject jsonIngredient = jsonIngredients.getJSONObject(j);

                    Ingredient ingredient = new Ingredient();
                    ingredient.setIngredient(jsonIngredient.getString(JSON_INGREDIENT));
                    ingredient.setMeasure(jsonIngredient.getString(JSON_MEASURE));
                    ingredient.setQuantity(jsonIngredient.getInt(JSON_QUANTITY));

                    //Adds Ingredient to the list
                    ingredients.add(ingredient);
                }


                //Retrieve Recipe Steps
                JSONArray jsonSteps = jsonRecipe.getJSONArray(JSON_STEPS);

                for(int k = 0; k < jsonSteps.length(); k++)
                {
                    JSONObject jsonStep = jsonSteps.getJSONObject(k);

                    Step step = new Step();
                    step.setShortDescription(jsonStep.getString(JSON_SHORT_DESCRIPTION));
                    step.setDescription(jsonStep.getString(JSON_DESCRIPTION));
                    step.setThumbnailUrl(jsonStep.getString(JSON_THUMBNAIL_URL));
                    step.setVideoUrl(jsonStep.getString(JSON_VIDEO_URL));

                    //Adds Step to the list
                    steps.add(step);
                }

                //Adds Ingredients and Steps to the recipe
                recipe.setIngredients(ingredients);
                recipe.setSteps(steps);

                //Adds recipe to the list
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return recipes;
    }

    //Method to retrieve and ingredient list
    public static String getIngredients(Context context, ArrayList<Ingredient> ingredientList)
    {
        StringBuilder build = new StringBuilder();

        for(int i = 0; i < ingredientList.size(); i++)
        {
            Ingredient ingredient = ingredientList.get(i);

            //Add Ingredient to the StringBuilder and create a new line
            build.append(context.getString(R.string.ingredient_name, ingredient.getIngredient(),
                    ingredient.getQuantity(), ingredient.getMeasure()) + "\n");
        }

        return build.toString();
    }

    public static boolean isValidImage(String image)
    {
        return !image.isEmpty() && !image.contains(FORMAT_MP4);
    }
}
