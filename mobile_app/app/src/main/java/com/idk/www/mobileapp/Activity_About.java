package com.idk.www.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class Activity_About extends AppCompatActivity {
    private Button bakBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        overridePendingTransition(R.anim.left_to_right_fadein,R.anim.right_to_left_fadeout);

        bakBtn =(Button) findViewById(R.id.bakbtn1);
    }
    public void onbackclick(View v){
        Intent bakHomePage = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(bakHomePage);
    }
}
