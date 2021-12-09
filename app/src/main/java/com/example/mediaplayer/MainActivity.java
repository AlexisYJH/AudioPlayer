package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MPlayer";
    private static final String AUDIO_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()
            + "/input.mp3";
    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private Button mBtnPlay, mBtnPause, mBtnEnd;
    private MediaPlayer mPlayer;
    private boolean mReleased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();
        bindViews();
    }

    private void verifyStoragePermissions() {
        //检测是否有写的权限
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 0);
        }
    }


    private void bindViews() {
        mBtnPlay = findViewById(R.id.btn_play);
        mBtnPause = findViewById(R.id.btn_pause);
        mBtnEnd = findViewById(R.id.btn_end);

        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnEnd.setOnClickListener(this);
        resetEnabled();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (mPlayer == null || mReleased) {
                    mPlayer = new MediaPlayer();
                    Log.d(TAG, "MediaPlayer准备工作：" + mPlayer + "," + mReleased);
                    try {
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.d(TAG, "播放完成");
                                resetEnabled();
                            }
                        });
                        mPlayer.setDataSource(AUDIO_PATH);
                        mPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mReleased = false;
                }
                //开始播放
                mPlayer.start();
                Log.d(TAG, "开始播放");
                setEnabled(false, true, true);
                break;
            case R.id.btn_pause:
                //暂停播放
                mPlayer.pause();
                Log.d(TAG, "暂停播放");
                setEnabled(true, false, true);
                break;
            case R.id.btn_end:
                //终止播放
                mPlayer.reset();
                mPlayer.release();
                mReleased = true;
                Log.d(TAG, "终止播放");
                resetEnabled();
                break;
            default:
                break;
        }
    }

    private void resetEnabled() {
        setEnabled(true, false, false);
    }

    private void setEnabled(boolean playEnabled, boolean pauseEnabled, boolean endEnabled) {
        mBtnPlay.setEnabled(playEnabled);
        mBtnPause.setEnabled(pauseEnabled);
        mBtnEnd.setEnabled(endEnabled);
    }
}