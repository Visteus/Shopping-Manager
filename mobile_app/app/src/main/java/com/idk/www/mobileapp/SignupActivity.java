package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView textsignin =(TextView) findViewById(R.id.textView2);
    }

    public void onsigninclick(View v){
        Intent signinpage = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(signinpage);
    }
}
