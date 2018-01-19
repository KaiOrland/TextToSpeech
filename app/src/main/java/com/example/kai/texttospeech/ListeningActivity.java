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

public class ListeningActivity extends Service implements IVoiceControl {

    protected SpeechRecognizer sr;
    protected Context context;
    private AudioManager amanager;
    private int current_volume;


    // starts the service
    protected void startListening() {
        try {
            initSpeech();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
            sr.startListening(intent);
            //unmute audio
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, 0);
        } catch(Exception ex) {
            Log.d("SpeechRecognitService", "Bei der Initialisierung des SpeechRecognizers ist ein Fehler aufgetreten");
        }
    }

    // stops the service
    protected void stopListening() {
        if (sr != null) {
            sr.stopListening();
            sr.cancel();
            sr.destroy();
        }
        sr = null;
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
            context.startActivity(startIntent);
        }
        else
         restartListeningService();
    }

    @Override
    public void restartListeningService() {
        //mute audio
        amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        current_volume = amanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        amanager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        stopListening();
        startListening();

        //unmute audio
      //  amanager.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, 0);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

