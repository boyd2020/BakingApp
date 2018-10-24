package com.example.bakingapp.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.bakingapp.fragments.StepFragment;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;

public class RecipeStepPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Step> steps;

    public RecipeStepPagerAdapter(FragmentManager fm, ArrayList<Step> steps) {
        super(fm);
        this.steps = steps;
    }

    @Override
    public Fragment getItem(int position) {
        Step s = steps.get(position);

        //Create Bundle and add step
        Bundle bundle = new Bundle();
        bundle.putSerializable(RecipeUtils.ARGS_KEY_STEP, s);

        //Create fragment and add bundle
        Fragment fragment = new StepFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return steps.size();
    }
}
