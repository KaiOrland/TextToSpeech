package com.example.kai.texttospeech;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by kayor on 1/1/2018.
 */

public class ActionHandler extends Activity{
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
