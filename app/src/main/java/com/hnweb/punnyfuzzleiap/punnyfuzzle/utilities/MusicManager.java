package com.hnweb.punnyfuzzleiap.punnyfuzzle.utilities;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;


import com.hnweb.punnyfuzzleiap.punnyfuzzle.R;

import java.util.Collection;
import java.util.HashMap;

/* Created by Priyanka Harmale on 29/12/2018. */

public class MusicManager {
    private static final String TAG = "MusicManager";
    public static final int MUSIC_PREVIOUS = -1;
    public static final int MUSIC_MENU = 0;
    public static final int MUSIC_GAME = 1;
    public static final int MUSIC_END_GAME = 2;

    private static HashMap players = new HashMap();
    private static int currentMusic = -1;
    private static int previousMusic = -1;

    public static void start(Context context, int music) {
        start(context, music, false);
    }

    public static void start(Context context, int music, boolean force) {
        if (!force && currentMusic > -1) {
// already playing some music and not forced to change
            return;
        }
        if (music == MUSIC_PREVIOUS) {
            music = previousMusic;
        }
        if (currentMusic == music) {
// already playing this music
            return;
        }
        if (currentMusic != -1) {
            previousMusic = currentMusic;
// playing some other music, pause it and change
            pause();
        }
        currentMusic = music;
        MediaPlayer mp = (MediaPlayer ) players.get(music);
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        } else {
            mp = MediaPlayer.create(context, R.raw.fly);
            players.put(music, mp);
            if (mp == null) {
                Log.e(TAG, "player was not created successfully");
            } else {
                try {
                    mp.setLooping(true);
                    mp.start();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    }

    public static void pause() {
        Collection<MediaPlayer> mps = players.values();
        for (MediaPlayer p : mps) {
            if (p.isPlaying()) {
                p.pause();
            }
        }
// previousMusic should always be something valid
        if (currentMusic != -1) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }

    public static void release() {
        Collection<MediaPlayer> mps = players.values();
        for (MediaPlayer mp : mps) {
            try {
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    mp.release();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        mps.clear();
        if (currentMusic != -1) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }
}