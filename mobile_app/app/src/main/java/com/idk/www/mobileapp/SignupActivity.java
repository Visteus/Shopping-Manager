package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText usernameid = (EditText) findViewById(R.id.userid);
        final EditText passwordid = (EditText) findViewById(R.id.pswid);
        final EditText emailid = (EditText) findViewById(R.id.email);
        final EditText addressid = (EditText) findViewById(R.id.address);
        final Button registerbtn = (Button) findViewById(R.id.registerbtn);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String userid = usernameid.getText().toString();
                String pswid = passwordid.getText().toString();
                String email = emailid.getText().toString();
                String address = addressid.getText().toString();

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jresponse = new JSONObject(response);
                            boolean success = jresponse.getBoolean("Success!");

                            if(success){
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            }
                            else {
                                AlertDialog.Builder message = new AlertDialog.Builder(SignupActivity.this);
                                message.setMessage("Failed.").setNegativeButton("Please Retry!", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                SignupRequest request = new SignupRequest(userid, pswid, email, address, listener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(request);
            }
        });


        //To go back home page
        TextView returntext =(TextView) findViewById(R.id.returntxt);
    }

    public void onsigninclick(View v){
        Intent signinpage = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(signinpage);
    }
}
