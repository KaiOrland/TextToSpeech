package com.example.kai.texttospeech;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Kai on 1/26/2017.
 */

public class AI {


    String record = MainActivity.getToSpeak();

    public void webSearch(String s){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH );
        intent.putExtra(SearchManager.QUERY, s);
        //startActivity(intent);
    }



}
