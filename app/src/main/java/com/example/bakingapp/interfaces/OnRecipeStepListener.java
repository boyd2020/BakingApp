package com.example.bakingapp.interfaces;

import com.example.bakingapp.model.Step;

public interface OnRecipeStepListener {
    void onRecipeStepClicked(int position);
    void onRecipeStepChanged(int position);
}
