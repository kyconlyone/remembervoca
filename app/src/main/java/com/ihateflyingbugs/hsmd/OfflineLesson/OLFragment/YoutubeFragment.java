package com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment;

/**
 * Created by 영철 on 2016-06-28.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;


public class YoutubeFragment extends Fragment implements YouTubePlayer.OnInitializedListener{

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static String VIDEO_ID = "EGy39OMyHzw";


    public YoutubeFragment newInstance(){
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        if (youTubePlayerFragment != null) {
           init();
        }
        return youtubeFragment;
    }
    static private View view;
    static YouTubePlayerSupportFragment youTubePlayerFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_youtube, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.ll_youtube_list);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();


        for(int i=1; i<11;i++){
            FrameLayout frameLayout = new FrameLayout(getActivity().getApplicationContext());
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            frameLayout.setId((R.id.youtubeframelayout)+i);
            YouTubePlayerSupportFragment youTubePlayerFragment = new YouTubePlayerSupportFragment();
            transaction.add(frameLayout.getId(), youTubePlayerFragment);
            youTubePlayerFragment.initialize(VOCAconfig.DEVELOPER_KEY, this);

            linearLayout.addView(frameLayout);
        }

        transaction.commit();



//        YouTubePlayerSupportFragment youTubePlayerFragment = new YouTubePlayerSupportFragment();
//
//        YouTubePlayerSupportFragment youTubePlayerFragment1 = new YouTubePlayerSupportFragment();
//
//        transaction.add(R.id.youtube_layout1, youTubePlayerFragment1);
//        transaction.add(R.id.youtube_layout, youTubePlayerFragment).commit();
//        youTubePlayerFragment.initialize(VOCAconfig.DEVELOPER_KEY, this);
//        youTubePlayerFragment1.initialize(VOCAconfig.DEVELOPER_KEY, this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("test_youtube", "onDestroy" );
    }

    private void init(){

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e("test_youtube", "onStart" );
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("test_youtube", "onStop" );
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.loadVideo(VIDEO_ID);
            //youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}