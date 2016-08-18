package com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ihateflyingbugs.hsmd.VOCAconfig;

/**
 * Created by 영철 on 2016-06-29.
 */

public class MeaningTreeFragment extends YouTubePlayerSupportFragment {

    private static String VIDEO_ID = "EGy39OMyHzw";
    private YouTubePlayer activePlayer;

    public static MeaningTreeFragment newInstance(String url) {

        MeaningTreeFragment playerYouTubeFrag = new MeaningTreeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        playerYouTubeFrag.setArguments(bundle);

        return playerYouTubeFrag;
    }

    private void init() {

        initialize(VOCAconfig.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                activePlayer = player;
                activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                if (!wasRestored) {
                    activePlayer.loadVideo(getArguments().getString("url"), 0);

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        activePlayer.pause();
    }
}