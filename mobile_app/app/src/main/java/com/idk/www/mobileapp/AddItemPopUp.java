package com.idk.www.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddItemPopUp extends Activity {

    private EditText etNumber;
    private EditText etName;
    private EditText etAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        this.getWindow().setLayout((int)(width * 0.8), (int)(height * 0.35));

        Button buttonAdd = (Button) findViewById(R.id.buttonAdd);

        etNumber = (EditText) findViewById(R.id.etNumber);
        etNumber.setInputType(8194);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etName = (EditText) findViewById(R.id.etName);

    }

    public void onAddClick(View v){
        String itemPrice = etNumber.getText().toString();
        String itemName = etName.getText().toString();
        String itemAmount = etAmount.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("itemPrice", itemPrice);
        returnIntent.putExtra("itemName", itemName);
        returnIntent.putExtra("itemAmount", itemAmount);
        this.setResult(-1, returnIntent);
        finish();
    }
}
