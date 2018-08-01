package com.idk.www.mobileapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idk.www.mobileapp.AddItemPopUp;
import com.idk.www.mobileapp.Checkout;
import com.idk.www.mobileapp.R;
import com.idk.www.mobileapp.YouRich;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText("Total: $" + df.format(totalPrice));

        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        tvItems.setText("Items");

        buttonCheckout = (Button) findViewById(R.id.buttonCheckout);
        addItem = (Button) findViewById(R.id.addItem);
        removeItem = (Button) findViewById(R.id.remItem);
        removeItem.setVisibility(View.GONE);

        listview = (ListView) findViewById(R.id.listView);
        listview.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listItems = new ArrayList<>();

        // This didnt help
        listItems.ensureCapacity(12);

        //adapter = new ArrayAdapter<String>(Activity_Guest.this, android.R.layout.simple_list_item_multiple_choice, listItems);
        adapter = new ArrayAdapter<String>(Activity_User.this, R.layout.custom_checked_list, R.id.list_content, listItems);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView)view;
                //checkedTextView.setChecked(!checkedTextView.isChecked());
                if(checkedTextView.isChecked())
                    removeItem.setVisibility(View.VISIBLE);
                    //totalChecks++;
                else{
                    removeItem.setVisibility(View.GONE);
                    checkedTextView.setChecked(false);
                }
            }
        });
    }

    // Go to checkout activity
    public void onCheckoutClick(View v) {
        Intent i = new Intent(this.getApplicationContext(), Checkout.class);
        i.putExtra("total", Double.toString(this.totalPrice));
        this.startActivity(i);
        overridePendingTransition(R.anim.slide_enter_right, R.anim.slide_exit_left);
    }

    // Pop up add item activity
    public void onAddItemClick(View v) {
        Intent i = new Intent(Activity_User.this, AddItemPopUp.class);
        this.startActivityForResult(i, 1);
        overridePendingTransition(R.anim.slide_enter_up, R.anim.slide_exit_down);
    }

    // Get info of added item
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    // Update price after adding item
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

            if(itemName.length() > 12)
                priceEach = " ($" + df.format(itemPrice) + " each)";
            else
                priceEach = "\n($" + df.format(itemPrice) + " each)";
            timesAmount = "(x" + Integer.toString(itemAmount) + ")";
            itemPrice = itemPrice * (double)itemAmount;
        }

        totalPrice += itemPrice;

        // That's a lot of money
        if(totalPrice >= 100000000) {
            Toast.makeText(Activity_User.this, "You Probably Don't Need Budget Buddy", Toast.LENGTH_SHORT).show();
            totalPrice -= itemPrice;
            numberOfItems--;
            if(numberOfItems <= 0) {
                numberOfItems = 0;
                tvItems.setText("Items");
            }
            else{
                tvItems.setText("Items: " + numberOfItems);
            }
            adapter.notifyDataSetChanged();
        }
        else{
            tvTotalPrice.setText("Total: $" + df.format(totalPrice));
            itemNumber++;
            priceList.add(itemPrice);
            listItems.add(itemName + timesAmount + "\t\t-\t\t$" + df.format(itemPrice) + priceEach);
            adapter.notifyDataSetChanged();
        }
    }

    // Remove an item
    public void onRemoveItemClick(View v){
        TextView tvItems = (TextView) findViewById(R.id.tvItems);
        TextView tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);

        backup.clear();
        tempPriceList.clear();

        SparseBooleanArray checked = listview.getCheckedItemPositions();

        // If the item is not checked, add it to a back up list
        int len = listItems.size();
        for(int i = 0; i < listview.getCount(); i++) {
            CheckedTextView checkedTextView = (CheckedTextView) listview.getChildAt(i);

              if(!checked.get(i)){
                    backup.add(listItems.get(i));
                    tempPriceList.add(priceList.get(i));
              } else {
                    numberOfItems--;
              }
              listview.setItemChecked(i , false);
        }

        totalPrice = 0;
        listItems.clear();
        priceList.clear();

        // After clearing main lists, add back saved items
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


    // Do not allow the user to go back to login
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(Activity_User.this, "Restart app to sign in again", Toast.LENGTH_SHORT).show();
    }
}
