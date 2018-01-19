package com.example.kai.texttospeech;

/**
 * Created by Madaim on 15/01/2018.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class ListeningActivity extends Service implements IVoiceControl {

    protected SpeechRecognizer sr;
    protected Context context;
    protected AudioManager amanager;
    protected int current_volume;


    // starts the service
    protected void startListening() {
        try {
            //mute audio
            amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            initSpeech();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
            sr.startListening(intent);
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);


        } catch(Exception ex) {
            Log.d("SpeechRecognitService", "Bei der Initialisierung des SpeechRecognizers ist ein Fehler aufgetreten");
        }
    }

    // stops the service
    protected void stopListening() {
        //mute audio
        current_volume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        amanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        sr = null;
       // amanager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

    }

    protected void initSpeech() {
        if (sr == null) {
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech Recognition is not available",
                        Toast.LENGTH_LONG).show();
            }
           sr.setRecognitionListener(VoiceRecognitionListener.getInstance());
        }
    }



    //is abstract so the inheriting classes need to implement it. Here you put your code which should be executed once a command was found
    @Override
    public void processVoiceCommands(String  voiceCommands){
        Toast.makeText(getApplicationContext(), voiceCommands, Toast.LENGTH_SHORT).show();
        if(voiceCommands.contains("hi Caillou")||voiceCommands.contains("hi Kyle")){
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.putExtra("startListenning", current_volume);
            context.startActivity(startIntent);
        }
        else
         restartListeningService();
    }

    @Override
    public void restartListeningService() {
        stopListening();
        startListening();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

