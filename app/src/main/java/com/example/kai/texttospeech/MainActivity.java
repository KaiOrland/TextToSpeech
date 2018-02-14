package com.example.kai.texttospeech;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech t1; // responsible for speaking the output
    EditText ed1; //edittext for input
    Button clearBtn; //clear button for input
    String input;
    String output;
    public static boolean isMainActivityRunning = false; // responsible for informing the service when to end
    private SpeechRecognizer sr;// main speech recognizer
    private ActionHandler actionHandler;
    private final int REGISTER_REQUEST = 1;
    private final  int GET_CONTACT_PERMISSION = 1;
    private final  int GET_CALL_PERMISSION = 2;
    private final  int GET_CAMERA_PERMISSION = 3;
    private final  int GET_AUDIO_PERMISSION = 4;
    private final  int GET_COARSE_LOCATION_PERMISSION = 5;
    private final  int GET_FINE_LOCATION_PERMISSION = 6;
    private TextView txtSpeechInput;
    private  VideoView videoView;



    @SuppressLint("ClickableViewAccessibility")
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
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)){
            /* do nothing*/
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},GET_COARSE_LOCATION_PERMISSION);
            }
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
            /* do nothing*/
            }
            else{

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},GET_FINE_LOCATION_PERMISSION);
            }
        }

        //stop the service
        stopService(new Intent(this, MyService.class));

        //get layout elements and set parameters
        isMainActivityRunning = true;
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
        actionHandler = new ActionHandler(this);
        this.registerReceiver(actionHandler.batteryInfoReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        ed1 = (EditText)findViewById(R.id.editText);
        clearBtn = (Button)findViewById(R.id.clearBtn);
        txtSpeechInput = (TextView) findViewById(R.id.textView);
        videoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.seekTo(2);
        videoView.pause();

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.US);
            }
        });
        startRecWhenCalledByService();

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
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                videoView.seekTo(0);
                videoView.start();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                       videoView.pause();
                    }
                }, 1500);


                if(ed1.getText().toString().equals("")==false) { //if input is written in edit text
                    input = ed1.getText().toString();
                    txtSpeechInput.setText(input);
                    output = actionHandler.AI(input);
                }
                else if (input!=null){
                    input = null;
                    promptSpeechInput();

                }
                else {
                    promptSpeechInput();


                }

                return false;
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
    private void startRecWhenCalledByService(){// if activity is called from the service, start recording immediately
        Intent intent = getIntent();
        if (intent.hasExtra("startListenning")) {
            AudioManager amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            //unmute audio
            amanager.setStreamVolume(AudioManager.STREAM_MUSIC, intent.getIntExtra("startListenning", 0), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            videoView.seekTo(0);
            videoView.start();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    videoView.pause();
                }
            }, 2000);
            if(videoView.getCurrentPosition()==1000)
                videoView.pause();
            promptSpeechInput();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(!videoView.isPlaying())
                        videoView.start();
                }
            }, 3000);
        }
        else{
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String greeting = sharedPreferences.getString("greeting", "Welcome back " + sharedPreferences.getString("name", "")); //get greeting from shared preferences
            t1.speak(greeting, TextToSpeech.QUEUE_FLUSH, null); //speak the greeting
            Toast.makeText(getApplicationContext(), greeting, Toast.LENGTH_SHORT).show();//show the greeting
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, MyService.class));  //stop the service
            try {
                if(actionHandler.batteryInfoReciever!=null)
                    unregisterReceiver(actionHandler.batteryInfoReciever);
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            startService(new Intent(this, MyService.class));
            isMainActivityRunning = false;
            finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isMainActivityRunning = true;
        stopService(new Intent(this, MyService.class));  //stop the service
        videoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.seekTo(2);
        videoView.pause(); //set videoview to beginning

        input = null;
        output = null;
        txtSpeechInput.setText(""); // reset inputs and outputs
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.preferences:
                Intent intent = new Intent(this, Preferences.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityForResult(intent, 1);
                break;
            case R.id.always_listen:
                break;
        }
        return true;
    }


    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params){}
        public void onBeginningOfSpeech(){}
        public void onRmsChanged(float rmsdB){}
        public void onBufferReceived(byte[] buffer){if(!videoView.isPlaying()) videoView.start();}//continue video until the end
        public void onEndOfSpeech() {if(!videoView.isPlaying()) videoView.start();}
        public void onError(int error) {input = "error " + error;}
        public void onResults(Bundle results)
        {
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            input = data.get(0);
            txtSpeechInput.setText(input);
            output = actionHandler.AI(input);
            Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
            t1.speak(output, TextToSpeech.QUEUE_FLUSH, null);
        }
        public void onPartialResults(Bundle partialResults){if(!videoView.isPlaying()) videoView.start();}
        public void onEvent(int eventType, Bundle params){}
    }
}