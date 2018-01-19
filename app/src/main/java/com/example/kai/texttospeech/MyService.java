package com.example.kai.texttospeech;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

public class MyService extends ListeningActivity
{

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getApplicationContext();
        VoiceRecognitionListener.getInstance().setListener(this);

        //mute audio
       // AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
       // amanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        startListening();
        //unmute audio
      //  amanager.setStreamVolume(AudioManager.STREAM_MUSIC, amanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        stopListening();
    }



}