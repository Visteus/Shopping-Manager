package com.idk.www.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class Activity_About extends AppCompatActivity {
    private Button bakbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        bakbtn =(Button) findViewById(R.id.bakbtn1);
    }
    public void onbackclick(View v){
        Intent bakhomepage = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(bakhomepage);
    }
}
