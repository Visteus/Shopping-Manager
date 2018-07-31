package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String url = "https://budget-buddy-group2.herokuapp.com/api/users/";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Prepare for a request
        requestQueue = Volley.newRequestQueue(this);

        final EditText usernameid = (EditText) findViewById(R.id.userid);
        final EditText passwordid = (EditText) findViewById(R.id.pswid);
        final EditText emailid = (EditText) findViewById(R.id.email);
        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final Button registerbtn = (Button) findViewById(R.id.registerbtn);

        //To go back home page
        TextView returntext =(TextView) findViewById(R.id.returntxt);
    }

    public void onRegistrationClick(View v){

        final EditText usernameid = (EditText) findViewById(R.id.userid);
        final EditText passwordid = (EditText) findViewById(R.id.pswid);
        final EditText emailid = (EditText) findViewById(R.id.email);
        final EditText firstName = (EditText) findViewById(R.id.firstName);
        final Button registerbtn = (Button) findViewById(R.id.registerbtn);

        final String userid = usernameid.getText().toString();
        final String pswid = passwordid.getText().toString();
        final String email = emailid.getText().toString();
        final String name = firstName.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.names().get(0).equals("id")) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
                        Toast.makeText(SignupActivity.this, "Create new account successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "Something wrong. Try again!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignupActivity.this, "Username or email taken. Please try again!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", userid);
                params.put("password", pswid);
                params.put("email", email);
                params.put("first_name", name);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void onsigninclick(View v){
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
    }
}
