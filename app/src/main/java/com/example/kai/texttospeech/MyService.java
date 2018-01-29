package com.example.kai.texttospeech;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

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
        if(!MainActivity.isMainActivityRunning) {
            context = getApplicationContext();
            VoiceRecognitionListener.getInstance().setListener(this);
            amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            current_volume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            startListening();
        }
        else
            this.stopSelf();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        if(amanager!=null)
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        stopListening();
        this.stopSelf();

    }



}