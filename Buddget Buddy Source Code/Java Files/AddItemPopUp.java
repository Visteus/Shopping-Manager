package com.idk.www.mobileapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddItemPopUp extends Activity {

    private EditText etPrice;
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

        etPrice = (EditText) findViewById(R.id.etPrice);
        etPrice.setInputType(8194);

        etAmount = (EditText) findViewById(R.id.etAmount);
        etName = (EditText) findViewById(R.id.etName);

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {

                if(etPrice.getText().toString().contains(".")){
                    if(etPrice.getText().toString().length() - etPrice.getText().toString().indexOf(".") > 3){
                        String replacementStr = etPrice.getText().toString();
                        while(replacementStr.length() - replacementStr.indexOf(".") > 3){
                            replacementStr = replacementStr.substring(0, replacementStr.length() - 1);
                        }
                        etPrice.setText(replacementStr);
                        etPrice.setSelection(replacementStr.length());
                    }
                }

                if(etPrice.getText().toString().length() > 0)
                    etPrice.setGravity(Gravity.CENTER);
                else
                    etPrice.setGravity(Gravity.START);

            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {

                if(etName.getText().toString().length() > 0)
                    etName.setGravity(Gravity.CENTER);
                else
                    etName.setGravity(Gravity.START);
            }
        });

    }

    public void onAddClick(View v){
        String itemPrice = etPrice.getText().toString();
        String itemName = etName.getText().toString();
        String itemAmount = etAmount.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("itemPrice", itemPrice);
        returnIntent.putExtra("itemName", itemName);
        returnIntent.putExtra("itemAmount", itemAmount);
        this.setResult(-1, returnIntent);
        finish();
        overridePendingTransition(R.anim.slide_enter_down, R.anim.slide_exit_up);
    }
}
