package com.idk.www.mobileapp;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btnsignin;
    private Button btnsignup;
    private Button btnguest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //ButterKnife.bind(this);

        final EditText textsignin = (EditText) findViewById(R.id.loginname);
        final EditText pswsignin = (EditText) findViewById(R.id.loginpsw);
        btnsignin = (Button) findViewById(R.id.signinbtn);
        btnsignup = (Button) findViewById(R.id.signupbtn);
        btnguest = (Button) findViewById(R.id.guestbtn);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final String username = textsignin.getText().toString();
                final String password = pswsignin.getText().toString();

                //listener
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonlogin = new JSONObject(response);
                            boolean success = jsonlogin.getBoolean("success!");

                            if(success){// user page with their username
                                Intent intent = new Intent(LoginActivity.this, Activity_User.class);
                                intent.putExtra("username", username);
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

                LoginRequest Login = new LoginRequest(username, password, listener);
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






}
