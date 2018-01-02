package com.example.kai.texttospeech;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    public MyService() {
    }

    private SpeechRecognizer sr;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class listener implements RecognitionListener
    {
        String input;
        public void onReadyForSpeech(Bundle params){}
        public void onBeginningOfSpeech(){}
        public void onRmsChanged(float rmsdB){}
        public void onBufferReceived(byte[] buffer){}
        public void onEndOfSpeech() {}
        public void onError(int error)
        {
            input = "error " + error;
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            input = data.get(0);
        }
        public void onPartialResults(Bundle partialResults){}
        public void onEvent(int eventType, Bundle params){}
        public String getInput(){return input;}
    }

}
