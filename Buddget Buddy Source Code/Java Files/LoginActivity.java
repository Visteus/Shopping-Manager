package com.idk.www.mobileapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.RelativeLayout;
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
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor loginPrefEditor;
    String saveUser = "";
    public static Boolean isLoggedIn = false;

    private Button btnSignIn;
    private Button btnSignUp;
    private Button btnGuest;
    RelativeLayout currentBackGrnd;
    int backGrdRoulette[];
    private static final String url = "https://budget-buddy-group2.herokuapp.com/api-token-auth/";
    private RequestQueue requestQueue;
    public static String sendUsername = "No Username";
    public static String sendToken = "No Token";
    public static String sendUserID = "No User Id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Prepare a request to the server
        requestQueue = Volley.newRequestQueue(this);

        // Get past login info if available
        sharedPreferences = this.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefEditor = sharedPreferences.edit();

        currentBackGrnd = (RelativeLayout) findViewById(R.id.relativelayout1);
        backGrdRoulette = new int[]{R.drawable.background, R.drawable.background3, R.drawable.background4, R.drawable.background5, R.drawable.background6};

        final EditText textsignin = (EditText) findViewById(R.id.loginname);
        final EditText pswsignin = (EditText) findViewById(R.id.loginpsw);

        textsignin.setText(sharedPreferences.getString("username", ""));

        btnSignIn = (Button) findViewById(R.id.signinbtn);
        btnSignUp = (Button) findViewById(R.id.signupbtn);
        btnGuest = (Button) findViewById(R.id.guestbtn);
    }

    public void onSignupClick(View v) {
        Intent signuppage = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(signuppage);
        overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
    }

    public void onSignInClick(View v){

        EditText etTextSignIn = (EditText) findViewById(R.id.loginname);
        EditText etPassword = (EditText) findViewById(R.id.loginpsw);

        final String username = etTextSignIn.getText().toString();
        final String password = etPassword.getText().toString();
        sendUsername = username;

        if(!etTextSignIn.toString().equals(""))
            saveUser = etTextSignIn.getText().toString();

        loginPrefEditor.putString("username", saveUser);
        loginPrefEditor.commit();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.names().get(0).equals("token")) {
                        sendToken = jsonResponse.get("token").toString();
                        sendUserID = jsonResponse.get("user").toString();
                        isLoggedIn = true;
                        Intent i = new Intent(getApplicationContext(), Activity_User.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
                    } else {
                        Toast.makeText(LoginActivity.this, "No token", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
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
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void onGuestPageClick(View v) {
        isLoggedIn = false;
        Intent i = new Intent(getApplicationContext(), Activity_User.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
    }

    //background image roulette
    protected void onResume() {
        if(currentBackGrnd!=null)
            currentBackGrnd.setBackgroundResource(backGrdRoulette[randomimg()]);
        super.onResume();
    }

    private int randomimg(){
        return new Random().nextInt(5);
    }

}
