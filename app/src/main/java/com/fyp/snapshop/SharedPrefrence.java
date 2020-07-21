package com.fyp.snapshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fyp.snapshop.Models.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SharedPrefrence {

    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor prefsEditor;

    public static SharedPrefrence myObj;

    private SharedPrefrence() {

    }

    public static SharedPrefrence getInstance(Context ctx) {
        if (myObj == null) {
            myObj = new SharedPrefrence();
            myPrefs = PreferenceManager.getDefaultSharedPreferences( ctx );
            prefsEditor = myPrefs.edit();
        }
        return myObj;
    }

    public void clearAllPreferences() {
        prefsEditor = myPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public void clearPreferences(String key) {
        prefsEditor.remove( key );
        prefsEditor.commit();
    }


    public void setIntValue(String Tag, int value) {
        prefsEditor.putInt( Tag, value );
        prefsEditor.apply();
    }

    public int getIntValue(String Tag) {
        return myPrefs.getInt( Tag, 0 );
    }

    public void setLongValue(String Tag, long value) {
        prefsEditor.putLong( Tag, value );
        prefsEditor.apply();
    }

    public long getLongValue(String Tag) {
        return myPrefs.getLong( Tag, 0 );
    }


    public void setValue(String Tag, String token) {

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString( Tag, token );
        editor.commit();
    }

    public String getValue(String Tag) {
        return myPrefs.getString( Tag, "" );
    }


    public boolean getBooleanValue(String Tag) {
        return myPrefs.getBoolean( Tag, false );

    }

    public void setBooleanValue(String Tag, boolean token) {
        prefsEditor.putBoolean( Tag, token );
        prefsEditor.commit();
    }

    public Model getParentUser() {
        String obj = myPrefs.getString( "user_dto", "defValue" );
        if (obj.equals( "defValue" )) {
            return null;
        } else {
            Gson gson = new Gson();
            String storedHashMapString = myPrefs.getString( "user_dto", "" );
            Type type = new TypeToken<Model>() {
            }.getType();
            return gson.fromJson( storedHashMapString, type );
        }
    }

    public void setParentUser(Model modelSignUp) {

        Gson gson = new Gson();
        String hashMapString = gson.toJson( modelSignUp );
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString( "user_dto", hashMapString );
        editor.apply();
    }
}
