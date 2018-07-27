package com.idk.www.mobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

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

                try{
                    taxTotal = Double.parseDouble(totalFromET);
                    tvAfterTax.setText("Total After Tax: $" + df.format(taxTotal*totalPrice+totalPrice));
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

        Toast.makeText(this, transactionName + " Checkout Complete.\nFinal Price: $" + df.format(finalPrice), Toast.LENGTH_LONG).show();

    }

    public void onBackClick(View v){
        finish();
    }

}
