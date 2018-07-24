package com.idk.www.mobileapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignin;
    private Button btnSignUp;
    private Button btnGuest;
    private EditText textSignIn;
    private EditText pswSignIn;
    RelativeLayout currentBakGrd;
    int bakGrdRoulette[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.left_to_right_fadein,R.anim.right_to_left_fadeout);
        currentBakGrd = (RelativeLayout) findViewById(R.id.relativelayout1);
        bakGrdRoulette = new int[]{R.drawable.background, R.drawable.background2, R.drawable.background3, R.drawable.background4, R.drawable.background5, R.drawable.background6};


        textSignIn = (EditText) findViewById(R.id.loginname);
        pswSignIn = (EditText) findViewById(R.id.loginpsw);
        btnSignin = (Button) findViewById(R.id.signinbtn);
        btnSignUp = (Button) findViewById(R.id.signupbtn);
        btnGuest = (Button) findViewById(R.id.guestbtn);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final String userName = textSignIn.getText().toString();
                final String passWord = pswSignIn.getText().toString();

                //listener
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonlogin = new JSONObject(response);
                            boolean success = jsonlogin.getBoolean("success!");

                            if(success){// user page with their username
                                Intent intent = new Intent(LoginActivity.this, Activity_User.class);
                                intent.putExtra("username", userName);
                            }
                            else {
                                AlertDialog.Builder message = new AlertDialog.Builder(LoginActivity.this);
                                message.setMessage("Login Failed.").setNegativeButton("Please Retry!", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest Login = new LoginRequest(userName, passWord, listener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(Login);
            }

        });
    }

    public void onsignupclick(View v) {
        Intent signuppage = new Intent(getApplicationContext(), SignupActivity.class);
        finish();
        startActivity(signuppage);
    }
    public void onguestpageclick(View v) { //default offline mode
        Intent guestpage = new Intent(getApplicationContext(), Activity_Guest.class);
        finish();
        startActivity(guestpage);
    }
    public void onaboutpageclick(View v){
        Intent aboutpage = new Intent(getApplicationContext(), Activity_About.class);
        finish();
        startActivity(aboutpage);
    }

    //background image roulette
    protected void onResume() {
        if(currentBakGrd!=null)
            currentBakGrd.setBackgroundResource(bakGrdRoulette[randomimg()]);
        super.onResume();
    }

    private int randomimg(){
        return new Random().nextInt(6);
    }

}
