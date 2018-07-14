package com.idk.www.mobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MainActivity extends AppCompatActivity {

    private Button btnsignin;
    private Button btnsignup;
    private Button btnguest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        final EditText textsignin = (EditText) findViewById(R.id.loginname);
        final EditText pswsignin = (EditText) findViewById(R.id.loginpsw);
        final Switch offlinemode = (Switch) findViewById(R.id.offlineswitch);
        btnsignin = (Button) findViewById(R.id.signinbtn);
        btnsignup = (Button) findViewById(R.id.signupbtn);
        btnguest = (Button) findViewById(R.id.guestbtn);
    }

    public void onsignupclick(View v) {
        Intent signuppage = new Intent(getApplicationContext(), SignupActivity.class);
        finish();
        startActivity(signuppage);
    }
    public void onguestpageclick(View v) {
        Intent guestpage = new Intent(getApplicationContext(), Guest.class);
        finish();
        startActivity(guestpage);
    }
    public void onaboutpageclick(View v){
        Intent aboutpage = new Intent(getApplicationContext(), AboutPage.class);
        finish();
        startActivity(aboutpage);
    }






}
