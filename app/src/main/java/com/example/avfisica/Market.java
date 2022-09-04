package com.example.avfisica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Market extends AppCompatActivity {
    private Handler handler;
    public int x=0;
    int delay = 800;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        handler = new Handler();
        handler.postDelayed(new Runnable(){

            public void run(){
                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        }, delay);
    }



}