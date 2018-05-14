package com.example.kai.texttospeech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by kayor on 1/1/2018.
 */

public class ActionHandler {

    public SharedPreferences sharedPreferences;
    private MainActivity myActivity;
    public String out;
    public int battery;
    double longitude;
    double latitude;
    public ActionHandler(MainActivity myActivity) {
        this.myActivity = myActivity;
    }

    public String AI(String in) {
        sharedPreferences = myActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if (in.contains("search")) {
            out = "searching" + in.substring(6);
            String search;
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            if (in.contains("about")) {
                search = in.replace("about", "");
                search = search.replace("search", "");
                intent.putExtra(SearchManager.QUERY, search);
            } else if (in.contains("for")) {
                search = in.replace("for", "");
                search = search.replace("search", "");
                intent.putExtra(SearchManager.QUERY, search);
            } else
                intent.putExtra(SearchManager.QUERY, in.substring(6));
            myActivity.startActivity(intent);
        }
        else if (in.contains("what")) {
                if (in.contains("battery")&& !in.contains(" a ")) {
                    out = "your battery is at " + battery + " percent";
                } else if (in.contains("the time")||in.contains("time is it")) {
                    Calendar c = Calendar.getInstance();
                    out = "the time is " + c.get(Calendar.HOUR_OF_DAY)
                            + ":" + c.get(Calendar.MINUTE);
                } else if (in.contains("the date")) {
                    Calendar c = Calendar.getInstance();
                    String monthname = (String) android.text.format.DateFormat.format("MMMM", new Date());
                    out = "the date is the " + c.get(Calendar.DAY_OF_MONTH)
                            + "th of " + monthname + ", " + c.get(Calendar.YEAR);
                } else if(in.contains("your name")){
                    out = getRandomResponse(new String[]{"My name is KaiU", "Don't you know? I'm KaiU", "You can call me KaiU"});
                } else if(in.contains("my name")){
                    out = "Your name is " + sharedPreferences.getString("name", "not set yet");
                } else if(in.contains("my age")){
                    out = "Your age is " + sharedPreferences.getString("age", "not set yet");
                } else if(in.contains("s up")){
                    out = getRandomResponse(new String[]{"Im Okay, thanks for asking", "I feel good, I knew that I would. So good, so good, I got you", "I'm alright no need to worry"});
                }
                else
                    defaultQuestionAction(in);
        }
        else if (in.contains("how")) {
            if (in.contains("are you")) {
                out = getRandomResponse(new String[]{"I'm fine, thanks", "couldn't be better", "usually I'm Okay"});
            } else
                defaultQuestionAction(in);
        }
        else if (in.contains("where")) {
                    out = "opening maps";
                    LocationManager lm = (LocationManager) myActivity.getSystemService(Context.LOCATION_SERVICE);
                    //request location permissions
                    if (ActivityCompat.checkSelfPermission(myActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(myActivity, Manifest.permission.ACCESS_COARSE_LOCATION)&&ActivityCompat.shouldShowRequestPermissionRationale(myActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                        }
                        else{
                            ActivityCompat.requestPermissions(myActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},5);
                            ActivityCompat.requestPermissions(myActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},6);
                        }
                    }
                     final LocationListener locationListener = new LocationListener() { // location listener in case current location isn't available
                        public void onLocationChanged(Location location) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {}
                        @Override
                        public void onProviderEnabled(String s) {}
                        @Override
                        public void onProviderDisabled(String s) {}
                    };

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                if (in.contains("am I")) {
                    Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    myActivity.startActivity(intent);
                }
                else if(in.contains("do I come from")){
                    out = "You come from " + sharedPreferences.getString("country", "an unknown place");
                }
                else if(in.contains("is")||in.contains("are")){
                    String target;
                    if(in.contains("is"))
                        target = in.substring(9);
                    else
                        target = in.substring(10);
                    Uri uri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + target);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    myActivity.startActivity(intent);
                }
                else
                    defaultQuestionAction(in);
        }
        else if(in.contains("who")){
                if(in.contains("are you")){
                    out = getRandomResponse(new String[]{"I'm KaiU, your virtual assistant", "Not Siri, that's for sure", "funny you should ask"});
                    if(out.equalsIgnoreCase("funny you should ask"))
                        myActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=QLbCedNKuxY")));
                }
                else if(in.contains("am I"))
                    out = "Your name is " + sharedPreferences.getString("name", "not set yet");
                else
                    defaultQuestionAction(in);
        }
        else if(in.contains("call")){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            boolean hasDigit = in.matches(".*\\d+.*");
            if(hasDigit) {
                String phoneNumber = getPhoneNumber(in);
                intent.setData(Uri.parse("tel:" + phoneNumber));
            }
            out = "opening phone";
            if (in.length()>4 && !hasDigit){
                String contactName = getContactName(in);
                String phoneNumber = getPhoneNumberByName(contactName, myActivity);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                out = "calling " + contactName;
            }

            myActivity.startActivity(intent);
        }
        else if(in.contains("weather")){
            out = "searching for weather";
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            if(in.contains("in"))
                intent.putExtra(SearchManager.QUERY, in.substring(in.indexOf("in") + 3 , in.length()) + " weather");
            else
                intent.putExtra(SearchManager.QUERY, "weather");
            myActivity.startActivity(intent);
        }
        else if((in.contains("take") &&(in.contains("photo") || in.contains("picture")))||in.contains("camera")){
            out = "opening camera";
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            myActivity.startActivity(intent);
        }
        else if(in.toLowerCase().contains("open")){
            final PackageManager pm = myActivity.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            String[] apps = new String[pm.getInstalledApplications(0).size()];
            int i = 0;
            Intent intent = null;
            for (ApplicationInfo packageInfo : packages) {
                apps[i] = packageInfo.packageName;
                String applicationName = (String) (packageInfo != null ? pm.getApplicationLabel(packageInfo) : "(unknown)");
                if(applicationName.equalsIgnoreCase(in.substring(5))){
                    out = "opening " + in.substring(5);
                    intent = pm.getLaunchIntentForPackage(apps[i]);
                    myActivity.startActivityForResult(intent, 3);
                }
                i++;
            }
            if(out == null || intent == null)
                out = "Sorry, I couldn't find this app";
        }
        else if ((in.contains("hi")) || (in.contains("hello")) || (in.contains("good morning")) || (in.contains("good evening") || (in.contains("hey")) || (in.contains("good afternoon")))) {
            out = getRandomResponse(new String[]{"hey there", "hi, how can I help?", "hello " + sharedPreferences.getString("name", "Master")});
        }
        else{
            out = getRandomResponse(new String[]{"I didn't get that", "come again please", "I'm sorry Dave, I'm afraid I can't do that", "I wasn't programmed yet to do that", "I can't help you with that"});
        }

        return out;
    }


    public String getPhoneNumber(String str){
        String phoneNumber = "";
        for(int i =str.indexOf("call")+5 ;i<str.length();i++){
            if(Character.isDigit(str.charAt(i)))
                phoneNumber = phoneNumber + str.charAt(i);
        }
        return phoneNumber;
    }

    public String getContactName (String str){
        String name = "";
        for (int i = str.indexOf("call")+5 ; i<str.length(); i++){
            name = name + str.charAt(i);
        }
        return name;
    }

    public String getPhoneNumberByName(String name,Context context){

        String number="";
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

    public BroadcastReceiver batteryInfoReciever = new BroadcastReceiver() {// broadcast reciever to check battery percentage
        @Override
        public void onReceive(Context context, Intent intent) {
            battery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };
    public void defaultQuestionAction(String in){
        out = getRandomResponse(new String[]{"Let me check", "Let's see...", "good question!"});
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, in);
        myActivity.startActivity(intent);
    }

    public String getRandomResponse(String[] options){ // generates random response for multi-answer questions
        Random random = new Random();
        int max = options.length;
        int i = random.nextInt(max);
        return options[i];
    }

}
