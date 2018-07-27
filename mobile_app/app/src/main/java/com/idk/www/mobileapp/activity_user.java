package com.idk.www.mobileapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Activity_User extends AppCompatActivity {

    private Button buttonCheckout;
    private Button addItem;
    private Button removeItem;
    private TextView tvTotalPrice;
    private TextView tvItems;
    private ListView listview;
    private int numberOfItems = 0;
    private int itemNumber = 1;
    private String itemName = "";
    private double itemPrice = 0.0D;
    private Integer itemAmount = 1;
    private double totalPrice = 0.0D;
    DecimalFormat df = new DecimalFormat("0.00");
    ArrayList<String> backup = new ArrayList();
    ArrayList<String> listItems;
    ArrayList<Double> priceList = new ArrayList();
    ArrayList<Double> tempPriceList = new ArrayList();
    ArrayAdapter<String> adapter;
    int totalChecks = 0;
    private Button bakBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        overridePendingTransition(R.anim.left_to_right_fadein,R.anim.right_to_left_fadeout);
        bakBtn =(Button) findViewById(R.id.bakbtn2);

        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText("Total: $" + df.format(totalPrice));

        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        tvItems.setText("Items");


        buttonCheckout = (Button) findViewById(R.id.buttonCheckout);
        addItem = (Button) findViewById(R.id.addItem);
        removeItem = (Button) findViewById(R.id.remItem);
        removeItem.setVisibility(View.GONE);

        //welcomes the user!
        TextView defaultMessage = (TextView) findViewById(R.id.defmessage);
        Intent intent = getIntent();
        String message;
        String userName = intent.getStringExtra("username");
        if(userName!=null) {
            message = "Welcome " + userName + " ,";
            defaultMessage.setText(message);
        }
        listview = (ListView) findViewById(R.id.listView);
        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(Activity_User.this, android.R.layout.simple_list_item_multiple_choice, listItems);
        listview.setAdapter(adapter);


//        listItems.add("Test");
//        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView)view;
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if(checkedTextView.isChecked())
                    totalChecks++;
                else
                    totalChecks--;

                if(totalChecks > 0 && removeItem.isEnabled())
                    removeItem.setVisibility(View.VISIBLE);
                else
                    removeItem.setVisibility(View.GONE);
            }
        });
    }

    public void onbackclick(View v){
        Intent bakHomePage = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(bakHomePage);

    }

    public void onCheckoutClick(View v) {
        Intent i = new Intent(this.getApplicationContext(), Checkout.class);
        i.putExtra("total", Double.toString(this.totalPrice));
        this.startActivity(i);
    }

    public void onAddItemClick(View v) {
        Intent i = new Intent(Activity_User.this, AddItemPopUp.class);
        this.startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Toast.makeText(this, " Checkout Complete.\nFinal Price: $", Toast.LENGTH_LONG).show();
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == -1){
            if(!data.getStringExtra("itemPrice").equals("") && !data.getStringExtra("itemPrice").equals(".")){
                itemPrice = Double.parseDouble(data.getStringExtra("itemPrice"));
                itemName = data.getStringExtra("itemName");
                if(data.getStringExtra("itemAmount").equals("")){
                    itemAmount = 1;
                }else{
                    itemAmount = Integer.parseInt(data.getStringExtra("itemAmount"));
                }
                numberOfItems++;
                updatePrice(itemPrice, itemName, itemAmount);
            }
        }
    }

    //    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == 1 && resultCode == -1){
//            if(!data.getStringExtra("itemPrice").equals("") && !data.getStringExtra("itemPrice").equals(".")){
//                itemPrice = Double.parseDouble(data.getStringExtra("itemPrice"));
//                itemName = data.getStringExtra("itemName");
//                if(data.getStringExtra("itemAmount").equals("")){
//                    itemAmount = 1;
//                }else{
//                    itemAmount = Integer.parseInt(data.getStringExtra("itemAmount"));
//                }
//                numberOfItems++;
//                updatePrice(itemPrice, itemName, itemAmount);
//            }
//        }
//    }

    public void updatePrice(Double itemPrice, String itemName, Integer itemAmount){
        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);

        if(numberOfItems > 0)
            tvItems.setText("Items: " + numberOfItems);
        else
            tvItems.setText("Items");

        if(itemName.equals(""))
            itemName = "Item " + itemNumber;

        String timesAmount = "";
        String priceEach = "";

        if(itemAmount > 1){
            priceEach = "\n$" + df.format(itemPrice) + " each";
            timesAmount = "(x" + Integer.toString(itemAmount) + ")";
            itemPrice = itemPrice * (double)itemAmount;
        }

        totalPrice += itemPrice;
        tvTotalPrice.setText("Total: $" + df.format(totalPrice));
        itemNumber++;
        priceList.add(itemPrice);
        listItems.add(itemName + timesAmount + "\t\t-\t\t$" + df.format(itemPrice) + priceEach);
        adapter.notifyDataSetChanged();

    }

    public void onRemoveItemClick(View v){
        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);

        backup.clear();
        tempPriceList.clear();

        int len = listview.getCount();
        for(int i = 0; i < len; i++){
            CheckedTextView checkedTextView = (CheckedTextView) listview.getChildAt(i);
            if(!checkedTextView.isChecked()){
                backup.add(listItems.get(i));
                tempPriceList.add(priceList.get(i));
            }else{
                numberOfItems--;
            }
            checkedTextView.setChecked((false));
        }

        totalPrice = 0;
        listItems.clear();
        priceList.clear();

        for(int i = 0; i < backup.size(); i++){
            listItems.add(backup.get(i));
            priceList.add(tempPriceList.get(i));
            totalPrice += (Double) tempPriceList.get(i);
        }

        tvTotalPrice.setText("Total: $" + df.format(totalPrice));
        if(numberOfItems > 0){
            tvItems.setText("Items: " + numberOfItems);
        }else{
            tvItems.setText("Items");
        }

        adapter.notifyDataSetChanged();
        totalChecks = 0;
        removeItem.setVisibility(View.GONE);

    }
}
