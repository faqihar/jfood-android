package com.example.jfood_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.Attributes;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName  = findViewById(R.id.register_name);
        etEmail = findViewById(R.id.register_email);
        etPassword = findViewById(R.id.register_password);
        btnRegister = findViewById(R.id.register_button);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email= etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (name.isEmpty()) {
                    etName.setError("Name Field Required");
                    etName.requestFocus();
                    return;
                }


                if (email.isEmpty()) {
                    etEmail.setError("Email field required");
                    etEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please Enter a valid email");
                    etEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password field required");
                    etPassword.requestFocus();
                    return;
                }

                if (password.length() < 8) {
                    etPassword.setError("Password should be at least 8 characters long");
                    etPassword.requestFocus();
                    return;
                }



                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                     public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Toast.makeText(RegisterActivity.this, "Register Sucessful", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Register failed!").create().show();
                            }
                        }
                    };
                RegisterRequest registerRequest = new RegisterRequest(name, email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
             }
        });

        final TextView loginClickable = (TextView) findViewById(R.id.loginClickable);
        loginClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

