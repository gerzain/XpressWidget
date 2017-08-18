package com.widgetxpress.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.widgetxpress.model.GoogleUser;

/**
 * Created by Irving on 20/07/2017.
 */

public class SessionPrefs
{

    public static final String PREFS_NAME="XPRESS_PREFS";
    public static final String PREF_ID_GOOGLE="PREF_USER_ID";

    private final SharedPreferences mPrefs;

    private boolean mIsLoggedIn = false;
    private static SessionPrefs INSTANCE;
    public static SessionPrefs get(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new SessionPrefs(context);
        }
        return INSTANCE;
    }
    private SessionPrefs(Context context)
    {
        mPrefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

       // mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_ID_GOOGLE, null));
    }
    public boolean isLoggedIn()
    {
        return mIsLoggedIn;
    }

    public void guardar_id(String id)
    {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_ID_GOOGLE, id);
        editor.apply();
        mIsLoggedIn = true;
    }
    public String getIdGoogle()
    {
        return  mPrefs.getString(PREF_ID_GOOGLE,null);
    }
    public void logOut()
    {
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_ID_GOOGLE,null);
        editor.apply();

    }




}
