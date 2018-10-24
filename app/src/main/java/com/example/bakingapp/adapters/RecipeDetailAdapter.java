package com.example.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bakingapp.R;
import com.example.bakingapp.interfaces.OnRecipeStepListener;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.utils.RecipeUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Constants
    private static final int INGREDIENT_CARD = 0, STEP_CARD = 1;

    //Objects
    private int position;
    private boolean twoPane;
    private Context context;
    private ArrayList<Object> listItems;
    private OnRecipeStepListener callback;

    public RecipeDetailAdapter(Context context, ArrayList<Object> listItems, int position, boolean twoPane, OnRecipeStepListener callback) {
        this.context = context;
        this.listItems = listItems;
        this.callback = callback;
        this.position = position;
        this.twoPane = twoPane;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder holder;

        switch (viewType)
        {
            case INGREDIENT_CARD:
                v = LayoutInflater.from(context).inflate(R.layout.ingredient_card, parent, false);
                holder = new IngredientHolder(v);
                break;

            default:
                v = LayoutInflater.from(context).inflate(R.layout.recipe_step_card, parent, false);
                holder = new StepHolder(v);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //Initialize Card based on view type
        if(getItemViewType(position) == INGREDIENT_CARD)
            addIngredientCard((IngredientHolder)holder, (String)listItems.get(position));
        else
            addStepCard((StepHolder)holder, (Step)listItems.get(position), position);
    }

    private void addIngredientCard(IngredientHolder holder, String ingredients)
    {
        holder.ingredientTextView.setText(ingredients);
    }

    private void addStepCard(StepHolder holder, Step step, int position)
    {
        holder.stepName.setText(step.getShortDescription());

        boolean isValidImage = RecipeUtils.isValidImage(step.getThumbnailUrl());

        if(isValidImage)
            Picasso.with(context).load(step.getThumbnailUrl()).into(holder.stepImage);
        else
            holder.stepImage.setImageResource(R.drawable.arrow);

        if(this.position == position && twoPane)
            holder.itemView.setSelected(true);
        else
            holder.itemView.setSelected(false);
    }

    public void updatePosition(int position)
    {
        this.position = position;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if(listItems.get(position) instanceof Step)
            return STEP_CARD;
        else
            return INGREDIENT_CARD;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }


    public class StepHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.stepImage)
        ImageView stepImage;

        @BindView(R.id.stepName)
        TextView stepName;

        public StepHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Gets the step position by subtracting 1 (ingredient card location)
                    callback.onRecipeStepClicked(getAdapterPosition() - 1);

                    //Update Position
                    position = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class IngredientHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.ingredientsTextView)
        TextView ingredientTextView;

        public IngredientHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
