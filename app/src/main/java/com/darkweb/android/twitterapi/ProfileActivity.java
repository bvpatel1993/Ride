package com.darkweb.android.twitterapi;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ProfileActivity extends AppCompatActivity {


    private ImageLoader imageLoader;


    private NetworkImageView profileImage;


    private TextView textViewUsername;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initializing views
        profileImage = (NetworkImageView) findViewById(R.id.profileImage);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,ViewData.class);
                startActivity(i);
            }
        });



//
    }
}