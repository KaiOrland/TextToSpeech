package com.example.kai.texttospeech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private MainActivity myActivity;
    public String out;
    public int battery;
    double longitude;
    double latitude;
    public ActionHandler(MainActivity myActivity) {
        this.myActivity = myActivity;
    }

    public String AI(String in) {
        out = in;
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
        if ((in.contains("what")) || (in.contains("why")) || (in.contains("who")) || (in.contains("when") || (in.contains("how")) || (in.contains("where")))) {
            if (in.contains("what")) {
                if (in.contains("battery")) {
                    out = "your battery is at " + battery + " percent";
                } else if (in.contains("time")) {
                    Calendar c = Calendar.getInstance();
                    out = "the time is " + c.get(Calendar.HOUR_OF_DAY)
                            + ":" + c.get(Calendar.MINUTE);
                } else if (in.contains("date")) {
                    Calendar c = Calendar.getInstance();
                    String monthname = (String) android.text.format.DateFormat.format("MMMM", new Date());
                    out = "the date is the " + c.get(Calendar.DAY_OF_MONTH)
                            + "th of " + monthname + ", " + c.get(Calendar.YEAR);
                } else
                    defaultQuestionAction(in);
            } else if (in.contains("how")) {
                if (in.contains("are you")) {
                    int result = getRandomNumber(1, 3);
                    switch (result) {
                        case 1:
                            out = "I'm fine, thanks";
                            break;
                        case 2:
                            out = "couldn't be better";
                            break;
                        case 3:
                            out = "usually I'm Okay";
                            break;
                    }

                } else
                    defaultQuestionAction(in);
            } else if (in.contains("where")) {
                if (in.contains("am I")) {
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
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    myActivity.startActivity(intent);
                }
                else
                    defaultQuestionAction(in);
            }
            else {
                defaultQuestionAction(in);
            }


        }

        if(in.contains("call")){
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
        if(in.contains("music")){
            out = "opening music";
            openApp(myActivity, "com.google.android.music");
        }
        if(in.contains("weather")){
            out = "searching for weather";
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            if(in.contains("in"))
                intent.putExtra(SearchManager.QUERY, in.substring(in.indexOf("in") + 3 , in.length()) + " weather");
            else
                intent.putExtra(SearchManager.QUERY, "weather");
            myActivity.startActivity(intent);
        }
        if((in.contains("take") &&(in.contains("photo") || in.contains("picture")))||in.contains("camera")){
            out = "opening camera";
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            myActivity.startActivity(intent);
        }
        if(in.contains("open")){
            final PackageManager pm = myActivity.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            String[] apps = new String[0];
            for (int i = 0; i<packages.size(); i++){
                if(packages.get(i).nonLocalizedLabel!=null){
                    apps[i] = packages.get(i).nonLocalizedLabel.toString();
                }
                if(in.substring(5).equalsIgnoreCase(apps[i])){
                    Intent intent = myActivity.getPackageManager().getLaunchIntentForPackage(packages.get(i).dataDir);
                    if (intent != null) {//null pointer check in case package name was not found
                        out = "opening" + in.substring(5);
                        myActivity.startActivity(intent);
                    }
                }
            }


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
        int result = getRandomNumber(1, 3);
        switch (result) {
            case 1:
                out = "Let me check";
                break;
            case 2:
                out = "Let's see...";
                break;
            case 3:
                out = "good question!";
                break;
        }
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, in);
        myActivity.startActivity(intent);
    }

    public int getRandomNumber(int min, int max){ // generates random number for multi-answer questions
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }
}
