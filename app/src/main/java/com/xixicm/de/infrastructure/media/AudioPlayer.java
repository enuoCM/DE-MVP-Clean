/*
 * Copyright (C) 2016 mc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xixicm.de.infrastructure.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import com.xixicm.de.R;

import java.io.IOException;

/**
 * @author mc
 */
public class AudioPlayer {
    Context mContext;
    PlayCallback mPlayCallback;
    boolean mLooping;
    MediaPlayer mMediaPlayer;
    AudioManager mAudioManager;
    AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;

    public AudioPlayer(Context context) {
        mContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setLooping(boolean looping) {
        mLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    public void setPlayCallback(PlayCallback playCallback) {
        mPlayCallback = playCallback;
    }

    public void initAudioMediaPlayer(Uri audioUri) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(mLooping);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.start();
                }
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayCallback != null) {
                    mPlayCallback.onPlayCompletion();
                }
                releaseMediaPlayer();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (mContext != null) {
                    Toast.makeText(mContext, R.string.cannot_play, Toast.LENGTH_LONG).show();
                }
                if (mPlayCallback != null) {
                    mPlayCallback.onPlayError();
                }
                releaseMediaPlayer();
                return false;
            }
        });
        try {
            mMediaPlayer.setDataSource(mContext, audioUri);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            if (mPlayCallback != null) {
                mPlayCallback.onPrepareError();
            }
            releaseMediaPlayer();
        }
    }

    // http://developer.android.com/intl/ja/guide/topics/media/mediaplayer.html#audiofocus
    public void startMediaPlayer(final Uri audioUri) {
        if (mMediaPlayer == null) {
            if (audioUri != null) {
                mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                // resume playback
                                if (mMediaPlayer == null) {
                                    initAudioMediaPlayer(audioUri);
                                } else if (!mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.start();
                                }
                                mMediaPlayer.setVolume(1.0f, 1.0f);
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                                // Lost focus for an unbounded amount of time: stop playback and release media player
                                if (mPlayCallback != null) {
                                    mPlayCallback.onPlayFocusLoss();
                                }
                                releaseMediaPlayer();
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                // Lost focus for a short time, but we have to stop
                                // playback. We don't release the media player because playback
                                // is likely to resume
                                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.pause();
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                // Lost focus for a short time, but it's ok to keep playing
                                // at an attenuated level
                                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.setVolume(0.1f, 0.1f);
                                }
                                break;
                        }
                    }
                };
                int result = mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // could not get audio focus.
                    Toast.makeText(mContext, R.string.cannot_play, Toast.LENGTH_LONG).show();
                    if (mPlayCallback != null) {
                        mPlayCallback.onCannotGetPlayFocus();
                    }
                    releaseMediaPlayer();
                } else {
                    initAudioMediaPlayer(audioUri);
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
            } else {
                if (mPlayCallback != null) {
                    mPlayCallback.onPrepareError();
                }
                releaseMediaPlayer();
            }
        }
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
            mAudioFocusChangeListener = null;
        }
    }

    public interface PlayCallback {
        void onPrepareError();

        void onPlayCompletion();

        void onPlayError();

        void onPlayFocusLoss();

        void onCannotGetPlayFocus();
    }
}
