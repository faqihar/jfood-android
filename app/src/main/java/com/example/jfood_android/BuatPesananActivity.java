package com.example.jfood_android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuatPesananActivity extends AppCompatActivity {

    private static final String TAG = "BuatPesananActivity";

    private int currentUserId;
    private int id_food;
    private String foodName;
    private String foodCategory;
    private int foodPrice;

    private String promoCode;
    private String promoCodeRequest;
    private int priceRequest;

    private String foodList;
    private String newFoodList;
    private int foodPriceList;

    private String selectedPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pesanan);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            currentUserId = extras.getInt("currentUserId");
            id_food = extras.getInt("id_food");
            foodName = extras.getString("foodName");
            foodCategory = extras.getString("foodCategory");
            foodPrice = extras.getInt("foodPrice");
        }

        final EditText mPromoCode = findViewById(R.id.promo_code);
        final TextView mCode = findViewById(R.id.textCode);
        final TextView mFoodName = findViewById(R.id.food_name);
        final TextView mFoodCategory = findViewById(R.id.food_category);
        final TextView mFoodPrice = findViewById(R.id.food_price);
        final TextView mTotalPrice = findViewById(R.id.total_price);
        final RadioGroup mVia = findViewById(R.id.radioGroup);
        final Button mCount = findViewById(R.id.hitung);
        final Button mOrder = findViewById(R.id.pesan);
        final Button mOrderAgain = findViewById(R.id.pesanlagi);

        mOrder.setVisibility(View.GONE);
        mCode.setVisibility(View.GONE);
        mPromoCode.setVisibility(View.GONE);
        mCount.setEnabled(false);

        mFoodName.setText(foodName);
        mFoodCategory.setText(foodCategory);
        mFoodPrice.setText(String.valueOf(foodPrice));
        mTotalPrice.setText("0");

        foodPriceList = getIntent().getExtras().getInt("foodPriceList");
        foodPriceList = foodPriceList + foodPrice;
        Log.d("Ini harga total", foodPriceList+"");

        foodList = getIntent().getExtras().getString("foodList");
        if(foodList == null){
            foodList = "";
        }
        newFoodList = foodList + id_food + ",";
        Log.d("Ini mentahannn", newFoodList);

        mVia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton mRadioButton = findViewById(i);
                String selected = mRadioButton.getText().toString();
                switch (selected){
                    case "Via CASH":
                        mCode.setVisibility(View.GONE);
                        mPromoCode.setVisibility(View.GONE);
                        mCount.setEnabled(true);
                        break;

                    case "Via CASHLESS":
                        mCode.setVisibility(View.VISIBLE);
                        mPromoCode.setVisibility(View.VISIBLE);
                        mCount.setEnabled(true);
                        break;
                }
            }
        });

        mCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = mVia.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioId);
                String selected = radioButton.getText().toString();
                switch (selected){
                    case "Via CASH":
                        mTotalPrice.setText(String.valueOf(foodPriceList));
                        break;

                    case "Via CASHLESS":
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonResponse = new JSONArray(response);
                                    for(int i = 0; i <jsonResponse.length(); i++){
                                        JSONObject promo = jsonResponse.getJSONObject(i);
                                        if(mPromoCode.getText().toString().equals(promo.getString("code")) && promo.getBoolean("active")){
                                            if(foodPriceList > promo.getInt("minPrice")){
                                                priceRequest = promo.getInt("discount");
                                                mTotalPrice.setText(String.valueOf(foodPriceList - priceRequest));
                                            }
                                        }
                                    }
                                }
                                catch (JSONException e){
                                    Log.d(TAG, "Load data failed.");
                                }
                            }
                        };

                        CheckPromoRequest promoRequest = new CheckPromoRequest(responseListener);
                        RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                        queue.add(promoRequest);

                        break;
                }

                mCount.setVisibility(View.GONE);
                mOrder.setVisibility(View.VISIBLE);
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioId = mVia.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(radioId);
                String selected = radioButton.getText().toString();
                BuatPesananRequest pesananRequest = null;

                Log.d(TAG, selected);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (response != null){
                                Toast.makeText(BuatPesananActivity.this, "Order successful", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            Toast.makeText(BuatPesananActivity.this, "Order failed", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }
                    }
                };

                if(selected.equals("Via CASH")){
                    pesananRequest = new BuatPesananRequest(newFoodList.substring(0, newFoodList.length()-1), currentUserId+"", responseListener);
                }
                else if(selected.equals("Via CASHLESS")){
                    pesananRequest = new BuatPesananRequest(newFoodList.substring(0, newFoodList.length()-1), currentUserId+"", mPromoCode.getText().toString(), responseListener);
                }

                RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                queue.add(pesananRequest);

            }
        });

        mOrderAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BuatPesananActivity.this, MainActivity.class);
                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("foodlist", newFoodList);
                intent.putExtra("foodPricelist", foodPriceList);
                startActivity(intent);
            }
        });

    }
}