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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        overridePendingTransition(R.anim.left_to_right_fadein,R.anim.right_to_left_fadeout);

        final EditText userNameId = (EditText) findViewById(R.id.userid);
        final EditText passWordId = (EditText) findViewById(R.id.pswid);
        final EditText emailId = (EditText) findViewById(R.id.email);
        final EditText addressId = (EditText) findViewById(R.id.address);
        final Button registerBtn = (Button) findViewById(R.id.registerbtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String userId = userNameId.getText().toString();
                String pswId = passWordId.getText().toString();
                String email = emailId.getText().toString();
                String address = addressId.getText().toString();

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jResponse = new JSONObject(response);
                            boolean success = jResponse.getBoolean("Success!");

                            if(success){
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            }
                            else { //ADD TOAST POP UP MESSAGE (USER ALREADY EXIST OR USERNAME IS INVALID)
                                AlertDialog.Builder message = new AlertDialog.Builder(SignupActivity.this);
                                message.setMessage("Fail to Register.").setNegativeButton("Please Retry!", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                SignupRequest request = new SignupRequest(userId, pswId, email, address, listener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(request);
            }
        });


        //To go back home page
        TextView returntext =(TextView) findViewById(R.id.returntxt);
    }

    public void onsigninclick(View v){
        Intent signinpage = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(signinpage);

    }
}
