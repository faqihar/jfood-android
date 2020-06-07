    package com.example.jfood_android;

    import android.content.Intent;
    import android.os.Handler;
    import android.os.Bundle;
    import android.view.Window;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.app.AppCompatDelegate;

    public class SplashScreen extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            super.onCreate(savedInstanceState);

            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_splaschscreen);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }, 3000L); //3000L = 3S
        }
    }