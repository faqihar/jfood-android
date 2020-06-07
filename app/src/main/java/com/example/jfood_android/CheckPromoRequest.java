package com.example.jfood_android;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckPromoRequest extends StringRequest {

    private static String URL = "http://10.0.2.2:8080/promo";
    private Map<String, String> params;

    public CheckPromoRequest(Response.Listener<String> listener) {
        super(Method.GET, URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}