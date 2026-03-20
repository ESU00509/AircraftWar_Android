package com.example.aircraftwar.application;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Android 音乐播放器，替代原 Swing 版的 MusicThread/MusicPlayer。
 * 使用 Android MediaPlayer 实现 BGM 循环和一次性音效播放。
 */
public class MusicPlayer {
    private static final String TAG = "MusicPlayer";
    private Context context;
    private MediaPlayer loopPlayer;
    private volatile boolean playing = false;

    public MusicPlayer(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 循环播放背景音乐
     *
     * @param assetPath assets 目录下的文件路径，如 "videos/bgm.wav"
     */
    public void startLoop(String assetPath) {
        if (playing) return;
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(assetPath);
            loopPlayer = new MediaPlayer();
            loopPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            loopPlayer.setLooping(true);
            loopPlayer.prepare();
            loopPlayer.start();
            playing = true;
        } catch (IOException e) {
            Log.e(TAG, "startLoop failed: " + assetPath, e);
        }
    }

    /**
     * 停止当前循环播放
     */
    public void stop() {
        playing = false;
        if (loopPlayer != null) {
            try {
                if (loopPlayer.isPlaying()) {
                    loopPlayer.stop();
                }
                loopPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "stop failed", e);
            }
            loopPlayer = null;
        }
    }

    /**
     * 当前是否正在播放
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * 播放一次音效（非循环），在后台线程播放
     *
     * @param assetPath assets 目录下的文件路径
     */
    public void playOnce(String assetPath) {
        new Thread(() -> {
            try {
                AssetFileDescriptor afd = context.getAssets().openFd(assetPath);
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mp.setOnCompletionListener(MediaPlayer::release);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                Log.e(TAG, "playOnce failed: " + assetPath, e);
            }
        }).start();
    }

    /**
     * 释放所有资源
     */
    public void release() {
        stop();
    }
}

