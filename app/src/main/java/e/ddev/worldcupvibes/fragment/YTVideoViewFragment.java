package e.ddev.worldcupvibes.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import e.ddev.worldcupvibes.DataModel.YTVideoDTO;
import e.ddev.worldcupvibes.R;
import e.ddev.worldcupvibes.Utils.PagerFragmentBase;
import e.ddev.worldcupvibes.Utils.YouTubeConstant;
import e.ddev.worldcupvibes.YTVideoViewerActivity;
import e.ddev.worldcupvibes.callbacks.IActivityToFragmentCallback;

/**
 * Created by Sumaiya on 05-Nov-17.
 */

public class YTVideoViewFragment extends PagerFragmentBase implements View.OnClickListener, IActivityToFragmentCallback {

    public String TAG = "YTVideoViewFragment";
    private View viewMain;
    public static final String EXTRA_VIDEO_DTO = "yt_video_dto";
    private YTVideoDTO ytVideoDTO;
    private Bundle bundle;

    private static final int RECOVERY_DIALOG_REQUEST = 10;
    public static final String API_KEY = YouTubeConstant.getApiKey();
    private YouTubePlayer.OnInitializedListener listener;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    private YouTubePlayer mYouTubePlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewMain = inflater.inflate(R.layout.fragment_yt_video_viewer, container, false);
        return viewMain;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bundle = getArguments();
        getIntentData();
    }

    public YTVideoViewFragment(){
        setRetainInstance(true);
    }



    private void initYoutubeView() {
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        releaseYoutubePlayer(false);
        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                try{
                    if(getActivity()!=null){
                        ((YTVideoViewerActivity)getActivity()).isInitializationComplete = true;
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

                if (!wasRestored) {
                    try {
                        if(ytVideoDTO!=null && ytVideoDTO.getVideoID()!=null){
                            try{
                                youTubePlayer.loadVideo(ytVideoDTO.getVideoID());
                                mYouTubePlayer = youTubePlayer;
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }


                    } catch (Exception e){
                        e.printStackTrace();

                    }


                }
            }


            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                if (youTubeInitializationResult.isUserRecoverableError()) {
                    youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
                } else {
                   // String errorMessage = String.format("YouTube Error (%1$s)", youTubeInitializationResult.toString());
                  //  Toast.makeText(App.getContext(), ""+errorMessage, Toast.LENGTH_SHORT).show();
                }
            }



        };




        if(getUserVisibleHint()){
            try{
                youTubePlayerFragment.initialize(API_KEY,listener);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    private void getIntentData() {
        try {
            ytVideoDTO = (YTVideoDTO) bundle.getSerializable(EXTRA_VIDEO_DTO);
        } catch (Exception e) {
        }
    }


    public static YTVideoViewFragment newInstance(YTVideoDTO ytVideoDTO, boolean isSingleItem) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_VIDEO_DTO, ytVideoDTO);
        args.putBoolean(YTVideoViewerActivity.IS_SINGLE_ITEM, isSingleItem);
        YTVideoViewFragment fragment = new YTVideoViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onInvisible() {
        Log.e(TAG, "onInvisible");
        try {
         if(mYouTubePlayer!=null && mYouTubePlayer.isPlaying()){
             mYouTubePlayer.pause();
         }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onVisible() {
        Log.e(TAG, "onVisible");
        try {
            if(youTubePlayerFragment==null){
                initYoutubeView();
            }

            if(mYouTubePlayer!=null){
                mYouTubePlayer.play();
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void activityOnkeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void activityOnpause() {

    }

    @Override
    public void activityOnResume() {

    }

    @Override
    public void activityOnDestroy() {

    }

    @Override
    public void onPageSelected(int position) {


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        releaseYoutubePlayer(false);
    }



    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        try {
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            try{
                youTubePlayerFragment.initialize(API_KEY,listener);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    @Override
    public void activityOnConfigurationChanged(Configuration newConfig) {
    }



    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        try {
            releaseYoutubePlayer(true);
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onPause();
    }


    @Override
    public void onDestroy() {
        try {
            releaseYoutubePlayer(false);
        } catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "setUserVisibleHint");

        if (!isVisibleToUser && mYouTubePlayer != null) {
            Log.v (TAG, "Releasing youtube player, URL : " + (ytVideoDTO.getVideoID()));
            releaseYoutubePlayer(false);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(youTubePlayerFragment).commit();
        }
        if (isVisibleToUser && youTubePlayerFragment != null) {
            try{
                youTubePlayerFragment.initialize(API_KEY, listener);
                Log.v (TAG, "Initializing youtube player, URL : " + ytVideoDTO.getVideoID());
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.youtube_fragment, youTubePlayerFragment).commit();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void releaseYoutubePlayer(boolean onPause) {
        try {
            if(mYouTubePlayer!=null){
                Log.v (TAG, "Releasing youtube player");
                mYouTubePlayer.pause();
                if(!onPause) {
                    mYouTubePlayer.release();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }


}



