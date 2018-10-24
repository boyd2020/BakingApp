package com.example.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bakingapp.R;
import com.example.bakingapp.interfaces.OnRecipeClickedListener;
import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.utils.RecipeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipes;
    private OnRecipeClickedListener callback;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes, OnRecipeClickedListener callback) {
        this.context = context;
        this.recipes = recipes;
        this.callback = callback;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.recipeServings.setText(context.getString(R.string.recipe_servings, recipe.getServings()));

        if(RecipeUtils.isValidImage(recipe.getImage()))
            Picasso.with(context).load(recipe.getName()).into(holder.recipeImage);
        else
            holder.recipeImage.setImageResource(R.drawable.arrow);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setCursor(List<Recipe> r)
    {
        recipes.clear();
        recipes.addAll(r);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.recipeName)
        TextView recipeName;

        @BindView(R.id.recipeServings)
        TextView recipeServings;

        @BindView(R.id.recipeImage)
        ImageView recipeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Pulls the selected recipe
                    callback.onRecipeClicked(recipes.get(getAdapterPosition()));
                }
            });
        }
    }
}
