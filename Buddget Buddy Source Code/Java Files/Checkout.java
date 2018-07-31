package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Checkout extends AppCompatActivity {

    private Button buttonBack;
    private Button buttonCheckout;
    private String totalFromET = "";
    private String totalFromMain = "";
    private String transactionName = "";
    private double totalPrice = 0.0D;
    private double taxTotal = 0.0D;
    private double finalPrice = 0.0D;
    private boolean finalEntered = false;
    DecimalFormat df = new DecimalFormat("0.00");
    private boolean isCheckedOut = false;

    private Boolean isLoggedIn = false;
    String username = "";
    String token = "";
    String userID = "";
    private RequestQueue requestQueue;
    private static final String url = "https://budget-buddy-group2.herokuapp.com/api/transactions/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        isCheckedOut = false;

        // Login info from LoginActivity
        isLoggedIn = LoginActivity.isLoggedIn;
        username = LoginActivity.sendUsername;
        token = LoginActivity.sendToken;
        userID = LoginActivity.sendUserID;

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");

        final String formattedDate = dateFormat.format(today);
        transactionName = formattedDate;

        totalFromMain = this.getIntent().getStringExtra("total");
        totalPrice = Double.parseDouble(totalFromMain);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonCheckout = (Button) findViewById(R.id.buttonCheckout);

        final TextView tvTransactionName = (TextView) findViewById(R.id.tvTransactionName);
        tvTransactionName.setText("Transaction on " + formattedDate);

        TextView tvTotal = (TextView) findViewById(R.id.tvTotal);
        final TextView tvAfterTax = (TextView) findViewById(R.id.tvAfterTax);

        tvTotal.setText("Total Before Tax: $" + df.format(totalPrice));
        tvAfterTax.setVisibility(View.GONE);

        final EditText etAfterTax = (EditText) findViewById(R.id.etAfterTax);

        etAfterTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvAfterTax.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvAfterTax.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                totalFromET = etAfterTax.getText().toString();

                if(etAfterTax.getText().toString().contains(".")){
                    if(etAfterTax.getText().toString().length() - etAfterTax.getText().toString().indexOf(".") > 3){
                        String replacementStr = etAfterTax.getText().toString();

                        while(replacementStr.length() - replacementStr.indexOf(".") > 3){
                            replacementStr = replacementStr.substring(0, replacementStr.length() - 1);
                        }
                        etAfterTax.setText(replacementStr);
                        etAfterTax.setSelection(replacementStr.length());
                    }
                }

                try{
                    taxTotal = Double.parseDouble(totalFromET);
                    tvAfterTax.setText("Total After Tax: $" + df.format(taxTotal));
                    finalEntered = true;
                }catch (Exception e){
                    tvAfterTax.setVisibility(View.GONE);
                    taxTotal = 0;
                    finalEntered = false;
                }
            }
        });

        final EditText etTransactionName = (EditText) findViewById(R.id.etTransactionName);
        etTransactionName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvTransactionName.setText(transactionName);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                transactionName = etTransactionName.getText().toString();
                tvTransactionName.setText(transactionName);
                if (tvTransactionName.getText().equals("")) {
                    transactionName = formattedDate;
                    tvTransactionName.setText("Transaction on " + formattedDate);
                }
            }
        });

    }

    public void onCheckoutClick(View v){
        if (finalEntered) {
            finalPrice = taxTotal;
        } else {
            finalPrice = totalPrice;
        }

        // Only send data to the website if the user is logged in
        if(isLoggedIn) {
            requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.names().get(0).equals("id")) {
                            Toast.makeText(Checkout.this, transactionName + " Checkout Complete.\nFinal Price: $" + df.format(finalPrice), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Checkout.this, "Unable to submit transaction", Toast.LENGTH_LONG).show();
                        }
                        Log.d("working", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Checkout.this, "Cannot connect to server. Try again!", Toast.LENGTH_LONG).show();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    String auth = "JWT " + token;
                    headers.put("Authorization", auth);
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user", userID);
                    params.put("title", transactionName);
                    params.put("total", Double.toString(finalPrice));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
        // Give final price and notify user they are not logged in
        else
         Toast.makeText(this, "Final Price: $" + df.format(finalPrice) +"\n(You are not signed in)", Toast.LENGTH_LONG).show();

        isCheckedOut = true;
    }



    public void onBackClick(View v){

        // If the user is checked out and goes back, clear the list
        if(isCheckedOut){
            isCheckedOut = false;
            Intent i = new Intent(Checkout.this, Activity_User.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
        }
        else{
            finish();
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
        }

    }

    @Override
    public void onBackPressed() {

        // If the user is checked out and goes back, clear the list
        if(isCheckedOut){
            isCheckedOut = false;
            Intent i = new Intent(Checkout.this, Activity_User.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter_left, R.anim.slide_exit_right);
        }
    }
}
