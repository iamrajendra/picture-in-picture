package com.iamrajendra.pictureinpicture;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {
private PlayerView playerView;
    private SimpleExoPlayer player;
    private long videoPosition;
    private boolean isPIPModeeEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.playerView);
         player = new SimpleExoPlayer.Builder(getApplicationContext()).build();

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(),
                Util.getUserAgent(getApplicationContext(), "yourApplicationName"));
        MediaSource videoSource =
                new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"));
        player.prepare(videoSource);
        playerView.setPlayer(player);
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView.onPause();
    }

    private void enterPIPMode () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && getPackageManager()
                .hasSystemFeature(
                        PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            videoPosition = player.getCurrentPosition();

            playerView.setUseController(false);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                PictureInPictureParams.Builder param = new PictureInPictureParams.Builder();
                this.enterPictureInPictureMode(param.build());

            }else {
                this.enterPictureInPictureMode();
            }

        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    checkPiPPermission();
                }
            }
        },30);
    }

    @Override
    public void onBackPressed() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && getPackageManager()
                .hasSystemFeature(
                        PackageManager.FEATURE_PICTURE_IN_PICTURE)){
            enterPIPMode();

        }else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPIPMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private  void checkPiPPermission(){
        isPIPModeeEnabled = isInPictureInPictureMode();

        if(!isPIPModeeEnabled){
            onBackPressed();
        }
    }


}
