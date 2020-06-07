package com.example.jfood_android;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etEmail = findViewById(R.id.login_email);
        final EditText etPassword = findViewById(R.id.login_password);
        Button btnLogin = findViewById(R.id.login_button);
        TextView tvRegister = findViewById(R.id.login_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

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


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            if(jsonResponse != null){
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("currentUserId", jsonResponse.getInt("id"));
                                startActivity(intent);
                                finish();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                queue.add(loginRequest);
                            }
                        });

                        tvRegister.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                startActivity(intent);
                            }
                        });
    }
}

