package com.example.smartexpenseanalyzer;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "login_session";
    private static final String KEY_LOGIN = "is_logged_in";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean value) {
        editor.putBoolean(KEY_LOGIN, value);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_LOGIN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
