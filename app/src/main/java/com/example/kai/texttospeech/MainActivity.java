package com.example.kai.texttospeech;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Button bt1;
    Button bt2;
    String input;
    String output;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtSpeechInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1 = (EditText)findViewById(R.id.editText);
        bt1 = (Button)findViewById(R.id.speak);
        bt2 = (Button)findViewById(R.id.record);
        txtSpeechInput = (TextView) findViewById(R.id.textView);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.US);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("")!=true) {
                    input = ed1.getText().toString();
                    output = input;
                }
                else {
                    input = txtSpeechInput.getText().toString();
                    /*if (input.indexOf("search") != -1) {
                        output = "searching" + input.substring(6);
                        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                        intent.putExtra(SearchManager.QUERY, input.substring(6));
                        startActivity(intent);

                    } else
                        output = input;*/
                    output = AI(input);
                }

                Toast.makeText(getApplicationContext(), output, Toast.LENGTH_SHORT).show();
                t1.speak(output, TextToSpeech.QUEUE_FLUSH, null);




            }
        });
        bt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }


    private void promptSpeechInput() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    public String AI(String in){
        String out;
        if (in.indexOf("search") != -1) {
            out = "searching" + in.substring(6);
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, in.substring(6));
            startActivity(intent);

        } else
            out = in;
        return out;
    }

}
