package com.example.kai.texttospeech;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech t1;
    EditText ed1;
    Button recordBtn;
    Button clearBtn;
    String input;
    String output;
    private SpeechRecognizer sr;
    private ActionHandler actionHandler;
    private static final String TAG = "MyStt3Activity";
    private final  int GET_CONTACT_PERMISSION = 1;
    private final  int GET_CALL_PERMISSION = 2;
    private final  int GET_CAMERA_PERMISSION = 3;
    private final  int GET_AUDIO_PERMISSION = 4;
    private TextView txtSpeechInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check permissions
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.RECORD_AUDIO)) {
            /* do nothing*/
            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, GET_AUDIO_PERMISSION);
            }
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
            /* do nothing*/
            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, GET_CONTACT_PERMISSION);
            }
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)){
            /* do nothing*/
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},GET_CALL_PERMISSION);
            }
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)){
            /* do nothing*/
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},GET_CAMERA_PERMISSION);
            }
        }
        //blur background
        final View content = findViewById(android.R.id.content).getRootView();
        if (content.getWidth() > 0) {
            Bitmap image = BlurBuilder.blur(content);
            content.setBackground(new BitmapDrawable(getResources(), image));
        }

        //get layout elements and set parameters
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
        actionHandler = new ActionHandler(this);
        ed1 = (EditText)findViewById(R.id.editText);
        clearBtn = (Button)findViewById(R.id.clearBtn);
        recordBtn = (Button)findViewById(R.id.talk);
        txtSpeechInput = (TextView) findViewById(R.id.textView);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.US);
            }
        });
        //show/hide clear button
        ed1.addTextChangedListener(new TextWatcher() {
            boolean wasTextLength2 = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { if(ed1.getText().toString().length()==2) wasTextLength2 = true; else wasTextLength2 = false;}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(ed1.getText().toString().length()==1 && wasTextLength2 == false) {
                    ObjectAnimator.ofFloat(clearBtn, View.ALPHA, 0, 1).setDuration(500).start();
                    clearBtn.setVisibility(View.VISIBLE);
                }
                else if(ed1.getText().toString().length()==0){
                    ObjectAnimator.ofFloat(clearBtn, View.ALPHA, 1, 0).setDuration(500).start();
                    clearBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        //clear button
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed1.setText("");
            }
        });
        // record button
        recordBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(ed1.getText().toString().equals("")==false) { //if input is written in edit text
                    input = ed1.getText().toString();
                    txtSpeechInput.setText(input);
                    output = actionHandler.AI(input);
                }
                else if (input!=null){
                    txtSpeechInput.setText(input);
                    output = actionHandler.AI(input);
                }
                else
                    promptSpeechInput();

                if(output!=null) {
                    Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
                    t1.speak(output, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    //launch recognizer intent
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000);
        sr.startListening(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        input = null;
        output = null;
        txtSpeechInput.setText("");
    }

    class listener implements RecognitionListener
    {
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
            txtSpeechInput.setText(input);
        }
        public void onPartialResults(Bundle partialResults){}
        public void onEvent(int eventType, Bundle params){}
    }
}