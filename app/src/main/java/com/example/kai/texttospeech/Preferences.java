package com.example.kai.texttospeech;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Madaim on 30/01/2018.
 */

public class Preferences extends Activity implements MyDialog.OnCompleteListener {

    private TextView nameTv, ageTv, countryTv, greetingTv;
    private final int NAME = 1, AGE = 2, COUNTRY = 3, GREETING = 4;
    private int whatToChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        //get layout elements
        nameTv = (TextView) findViewById(R.id.nameTv);
        ageTv = (TextView) findViewById(R.id.ageTv);
        countryTv = (TextView) findViewById(R.id.countryTv);
        greetingTv = (TextView) findViewById(R.id.greetingTv);
        //set texts based on shared preferences
        nameTv.setText(getInfo("name"));
        ageTv.setText(getInfo("age"));
        countryTv.setText(getInfo("country"));
        greetingTv.setText(getInfo("greeting"));

    }

    public void changeInfo(View view){//called by onClick in xml
        FragmentManager fragmentManager = getFragmentManager();
        MyDialog myDialog = new MyDialog();
        switch (view.getId()){
            case R.id.nameBtn:
                myDialog.show(fragmentManager, "");
                whatToChange = NAME;
                break;
            case R.id.ageBtn:
                myDialog.show(fragmentManager, "");
                whatToChange = AGE;
                break;
            case R.id.countryBtn:
                myDialog.show(fragmentManager, "");
                whatToChange = COUNTRY;
                break;
            case R.id.greetingBtn:
                myDialog.show(fragmentManager, "");
                whatToChange = GREETING;
                break;


        }

    }


    public void saveInfo(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getInfo(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "Enter info");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.back:
               // onBackPressed();

              // Intent intent = new Intent(this, MainActivity.class);
              // startActivity(intent);
               finishActivity(1);
               break;

        }
        return true;
    }

    @Override
    public void onComplete(String input) {
        switch (whatToChange) {//switch on which type was set in order to change the right text view
            case NAME:
                nameTv.setText(input);//set text for text view
                saveInfo("name", input);//save info in shared preferences
                break;
            case AGE:
                ageTv.setText(input);
                saveInfo("age", input);
                break;
            case COUNTRY:
                countryTv.setText(input);
                saveInfo("country", input);
                break;
            case GREETING:
                greetingTv.setText(input);
                saveInfo("greeting", input);
                break;

        }
    }
}
