package com.example.bakingapp.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bakingapp.R;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.utils.RecipeUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class StepFragment extends Fragment {

    @Nullable
    @BindView(R.id.recipeStepDesc)
    TextView recipeStepDescTextView;

    @Nullable
    @BindView(R.id.recipeStepShortDesc)
    TextView recipeStepShortDescTextView;

    @BindView(R.id.emptyText)
    TextView emptyText;

    @BindView(R.id.exoPlayerView)
    SimpleExoPlayerView playerView;

    @BindView(R.id.videoLayout)
    RelativeLayout videoLayout;


    //Saved instance state keys
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    //Objects/Variables
    private int startWindow;
    private long startPosition;
    private boolean startAutoPlay;
    private Step step;
    private Unbinder unbinder;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_fragment, container, false);

        unbinder = ButterKnife.bind(this, v);

        if(savedInstanceState != null)
        {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
            step = (Step) savedInstanceState.getSerializable(RecipeUtils.ARGS_KEY_STEP);
        }
        else
        {
            step = (Step) getArguments().getSerializable(RecipeUtils.ARGS_KEY_STEP);
        }

        if(recipeStepDescTextView != null && !step.getDescription().isEmpty())
            recipeStepDescTextView.setText(step.getDescription());

        if(recipeStepShortDescTextView != null && !step.getShortDescription().isEmpty())
            recipeStepShortDescTextView.setText(step.getShortDescription());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        outState.putSerializable(RecipeUtils.ARGS_KEY_STEP, step);
    }

    private void initializePlayer() {
        //Used to retrieve the network state
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        //Used to verify if the device is connected to the internet
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (!step.getVideoUrl().isEmpty() && isConnected && player == null) {
            emptyText.setVisibility(View.GONE);
            playerView.setVisibility(View.VISIBLE);

            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);


            //Add Player to the ExoPlayerView
            player.setPlayWhenReady(startAutoPlay);
            playerView.setPlayer(player);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            mediaSource = new ExtractorMediaSource(Uri.parse(step.getVideoUrl()), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);


            boolean haveStartPosition = startWindow != C.INDEX_UNSET;

            if (haveStartPosition)
                player.seekTo(startWindow, startPosition);

            player.prepare(mediaSource);

            //Add Thumbnail to PlayerView
            boolean isValidImage = RecipeUtils.isValidImage(step.getThumbnailUrl());

            if(isValidImage)
            {
                Picasso.with(getContext()).load(step.getThumbnailUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        playerView.setDefaultArtwork(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }
        else
        {
            playerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
            videoLayout.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getCurrentPosition());
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(player != null) {
            updateStartPosition();
            player.stop();
            player.release();
            player = null;
            mediaSource = null;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        releasePlayer();
    }
}
