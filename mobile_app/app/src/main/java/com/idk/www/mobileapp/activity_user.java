package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Activity_User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        final TextView defaultMessage = (TextView) findViewById(R.id.defmessage);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("username");

        String message = "Welcome " + userName + " !";
        defaultMessage.setText(message);
    }
}
