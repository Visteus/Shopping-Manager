package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_Guest extends AppCompatActivity {

    private Button bakbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        bakbtn =(Button) findViewById(R.id.bakbtn2);


    }

    public void onbackclick(View v){
        Intent bakhomepage = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(bakhomepage);
    }
}
