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
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    public boolean isPromptSpeechActivated = false;
    private final  int GET_CONTACT_PERMISSION = 1;
    private final  int GET_CALL_PERMISSION = 2;
    private final  int GET_CAMERA_PERMISSION = 3;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtSpeechInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check permissions
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

        //get layout elements
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

                if(ed1.getText().toString().equals("")==false) {
                    input = ed1.getText().toString();
                    txtSpeechInput.setText(input);
                    output = AI(input);
                }
                else if (input!=null){
                    txtSpeechInput.setText(input);
                    output = AI(input);
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
        isPromptSpeechActivated = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
    //get result from recognizer intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    input = result.get(0);
                }
                break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isPromptSpeechActivated){
            input = null;
            output = null;
            txtSpeechInput.setText("");
        }
        else{
            isPromptSpeechActivated = false;
            txtSpeechInput.setText(input);
        }
    }

    public String AI(String in){
        String out = in;
        if (in.indexOf("search") != -1) {
            out = "searching" + in.substring(6);
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, in.substring(6));
            startActivity(intent);
        }

        if(in.indexOf("call") != -1){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            boolean hasDigit = in.matches(".*\\d+.*");
            if(hasDigit) {
                String phoneNumber = getPhoneNumber(in);
                intent.setData(Uri.parse("tel:" + phoneNumber));
            }
            out = "openning phone";
            if (in.length()>4 && !hasDigit){
                String contactName = getContactName(in);
                String phoneNumber = getPhoneNumberByName(contactName, this);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                out = "calling " + contactName;
            }

            startActivity(intent);
        }
        if(in.indexOf("music") != -1){
            out = "openning music";
            openApp(this, "com.google.android.music");
        }
        if(in.indexOf("weather") != -1){
            out = "searching for weather";
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, "weather");
            startActivity(intent);
        }
        if((in.indexOf("take")!=-1 &&(in.indexOf("photo")!=-1 || in.indexOf("picture")!=-1))||in.indexOf("camera")!=-1 ){
            out = "openning camera";
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        }

        return out;
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;

        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }

    public String getPhoneNumber(String str){
        String phoneNumber = "";
        for(int i =0;i<str.length();i++){
            if(Character.isDigit(str.charAt(i)))
                phoneNumber = phoneNumber + str.charAt(i);
        }
        return phoneNumber;
    }

    public String getContactName (String str){
        String name = "";
        for (int i = 5; i<str.length(); i++){
            name = name + str.charAt(i);
        }
        return name;
    }

    public String getPhoneNumberByName(String name,Context context)

    {String number="";


        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = context.getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();
        do {
            String Name   = people.getString(indexName);
            String Number = people.getString(indexNumber);
            if(Name.contains(name)){return Number.replace("-", "");}
            // Do work...
        } while (people.moveToNext());


        if(!number.equalsIgnoreCase("")){return number.replace("-", "");}
        else return number;
    }
}
