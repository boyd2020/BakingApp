package com.example.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bakingapp.R;
import com.example.bakingapp.adapters.RecipeStepPagerAdapter;
import com.example.bakingapp.interfaces.OnRecipeStepListener;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.utils.RecipeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment {

    //Constants
    private static final int DEFAULT_POSITION = 0;

    //Views
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Nullable
    @BindView(R.id.backButton)
    Button backButton;

    @Nullable
    @BindView(R.id.nextButton)
    Button nextButton;

    //Objects/Variables
    private int position;
    private Unbinder unbinder;
    private ArrayList<Step> steps;
    private RecipeStepPagerAdapter adapter;
    private OnRecipeStepListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnRecipeStepListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recipe_step_detail_fragment, container, false);

        unbinder = ButterKnife.bind(this, v);

        position = getArguments().getInt(RecipeUtils.ARGS_KEY_POSITION, DEFAULT_POSITION);
        steps = (ArrayList<Step>) getArguments().getSerializable(RecipeUtils.ARGS_KEY_STEPS);

        addViewPager();

        return v;
    }

    private void addViewPager()
    {
        //Initialize and add ViewPager Adapter
        adapter = new RecipeStepPagerAdapter(getChildFragmentManager(), steps);
        viewPager.setAdapter(adapter);

        //Add OnPageChangeListener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                position = pos;

                //Button Visibility
                if(backButton != null && nextButton != null)
                {
                    //Back Button Visibility
                    if(position == 0)
                        backButton.setVisibility(View.INVISIBLE);
                    else
                        backButton.setVisibility(View.VISIBLE);


                    //Next Button Visibility
                    if(position != adapter.getCount() - 1)
                        nextButton.setText(getString(R.string.next));
                    else
                        nextButton.setText(getString(R.string.finish));
                }

                viewPager.setCurrentItem(position);

                //Update the position for the steps (1 makes up for the ingredient tab)
                callback.onRecipeStepChanged(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Set ViewPager Position
        viewPager.setCurrentItem(position);
    }

    @Optional
    @OnClick(R.id.backButton)
    void back()
    {
        if(position != 0)
            viewPager.setCurrentItem(--position);
    }

    @Optional
    @OnClick(R.id.nextButton)
    void next()
    {
        if(position != adapter.getCount() - 1)
            viewPager.setCurrentItem(++position);
        else
            getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
