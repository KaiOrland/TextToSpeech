package com.example.kai.texttospeech;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.List;
import java.util.Random;

/**
 * Created by kayor on 1/1/2018.
 */

public class ActionHandler {

    private MainActivity myActivity;

    public ActionHandler(MainActivity myActivity) {
        this.myActivity = myActivity;
    }

    public String AI(String in){
        String out = in;
        if (in.indexOf("search") != -1) {
            out = "searching" + in.substring(6);
            String search;
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            if(in.indexOf("about") != -1) {
                search = in.replace("about", "");
                search = search.replace("search", "");
                intent.putExtra(SearchManager.QUERY, search);
            }
            else if(in.indexOf("for") != -1){
                search = in.replace("for", "");
                search = search.replace("search", "");
                intent.putExtra(SearchManager.QUERY, search);
            }
            else
                intent.putExtra(SearchManager.QUERY, in.substring(6));
            myActivity.startActivity(intent);
        }
        if ((in.indexOf("what") != -1)||(in.indexOf("why") != -1)||(in.indexOf("who") != -1)||(in.indexOf("when") != -1)||(in.indexOf("how") != -1)) {
            int result = getRandomNumber(1,3);
            switch (result){
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

        if(in.indexOf("call") != -1){
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
        if(in.indexOf("music") != -1){
            out = "opening music";
            openApp(myActivity, "com.google.android.music");
        }
        if(in.indexOf("weather") != -1){
            out = "searching for weather";
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, "weather");
            myActivity.startActivity(intent);
        }
        if((in.indexOf("take")!=-1 &&(in.indexOf("photo")!=-1 || in.indexOf("picture")!=-1))||in.indexOf("camera")!=-1 ){
            out = "opening camera";
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            myActivity.startActivity(intent);
        }
        if(in.indexOf("open") != -1){
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

    public int getRandomNumber(int min, int max){
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }
}
