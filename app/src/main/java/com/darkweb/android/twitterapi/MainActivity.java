package com.darkweb.android.twitterapi;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import com.darkweb.android.twitterapi.Fragment.LoginFragment;


public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;

    private static String CONSUMER_KEY = "bzSvEH3wXA98N2DDJuCjWPOpZ";
    private static String CONSUMER_SECRET = "2tNzHFP3fnBsNDY9KofSjMCz6e6ObRGwHPW0hGLZf5VsZh52Pz";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getPreferences(0);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("CONSUMER_KEY", CONSUMER_KEY);
        edit.putString("CONSUMER_SECRET", CONSUMER_SECRET);
        edit.commit();

        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, login);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

}

