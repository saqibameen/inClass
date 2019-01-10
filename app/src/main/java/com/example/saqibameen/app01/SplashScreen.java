package com.example.saqibameen.app01;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {


    final private Context mContext = SplashScreen.this;
    final private static int SPLASH_TIME_OUT = 2000;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, Courses.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, SPLASH_TIME_OUT);
    }


    /* remove call back in on destroy */
    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    /* device back button click event */
    @Override
    public void onBackPressed() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onBackPressed();
    }
}
