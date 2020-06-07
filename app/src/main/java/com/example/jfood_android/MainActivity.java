package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private ArrayList<Seller> listSeller = new ArrayList<>();
    private ArrayList<Food> foodIdList = new ArrayList<>();
    private HashMap<Seller, ArrayList<Food>> childMapping = new HashMap<>();

    private String foodlist;
    private int foodPriceList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int currentUserId = getIntent().getExtras().getInt("currentUserId");
        foodlist = getIntent().getExtras().getString("foodlist");
        foodPriceList = getIntent().getExtras().getInt("foodPricelist");

        refreshList();

        findViewById(R.id.pesanan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelesaiPesananActivity.class);
                intent.putExtra("currentUserId", currentUserId);
                startActivity(intent);
            }
        });

        expListView = findViewById(R.id.lvExp);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                Intent intent = new Intent(MainActivity.this, BuatPesananActivity.class);
                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("foodList", foodlist);
                intent.putExtra("foodPriceList", foodPriceList);
                intent.putExtra("id_food", childMapping.get(listSeller.get(i)).get(i1).getId());
                intent.putExtra("foodName", childMapping.get(listSeller.get(i)).get(i1).getName());
                intent.putExtra("foodCategory", childMapping.get(listSeller.get(i)).get(i1).getCategory());
                intent.putExtra("foodPrice", childMapping.get(listSeller.get(i)).get(i1).getPrice());
                startActivity(intent);

                return true;
            }
        });

    }

    protected void refreshList() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i = 0; i < jsonResponse.length(); i++) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);

                        JSONObject food = jsonResponse.getJSONObject(i);
                        JSONObject seller = food.getJSONObject("seller");
                        JSONObject location = seller.getJSONObject("location");

                        Location newLocation = new Location(
                                location.getString("province"),
                                location.getString("description"),
                                location.getString("city")
                        );

                        Seller newSeller = new Seller(
                                seller.getInt("id"),
                                seller.getString("name"),
                                seller.getString("email"),
                                seller.getString("phoneNumber"),
                                newLocation
                        );

                        Food newFood = new Food(
                                food.getInt("id"),
                                food.getString("name"),
                                food.getInt("price"),
                                food.getString("category"),
                                newSeller
                        );

                        foodIdList.add(newFood);

                        boolean tempStatus = true;
                        for (Seller sellerPtr : listSeller) {
                            if (sellerPtr.getId() == newSeller.getId()) {
                                tempStatus = false;
                            }
                        }
                        if (tempStatus) {
                            listSeller.add(newSeller);
                        }
                    }

                    for (Seller sellerPtr : listSeller) {
                        ArrayList<Food> tempFoodList = new ArrayList<>();
                        for (Food foodPtr : foodIdList) {
                            if (foodPtr.getSeller().getId() == sellerPtr.getId()) {
                                tempFoodList.add(foodPtr);
                            }
                        }
                        childMapping.put(sellerPtr, tempFoodList);
                    }
                    listAdapter = new MainListAdapter(MainActivity.this, listSeller, childMapping);
                    expListView.setAdapter(listAdapter);
                } catch (JSONException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Load Data Failed.").create().show();
                }
            }
        };

        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);

    }
}