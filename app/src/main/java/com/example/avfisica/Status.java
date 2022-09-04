package com.example.avfisica;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.avfisica.models.Login;

import static com.example.avfisica.MainActivity.activities;

public class Status extends AppCompatActivity {

    Button btn_login, btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        btn_login = (Button) findViewById(R.id.button_login);
        btn_register = (Button) findViewById(R.id.button_register);

        //fecha todas actives abertas
        for(Activity activity:activities)
            activity.finish();

        // Button Register
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityRegister();

                } catch (Exception e) {
                }
            }
        });

        // Button Login
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityLogin();

                } catch (Exception e) {
                }
            }
        });

    }

    public void openActivityRegister()
    {
        Intent intent = new Intent(this,Register.class);
        startActivity(intent);
    }
    public void openActivityLogin()
    {
        Intent intent = new Intent(this, LoginClass.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        finishAffinity(); //close all the app
        //activities.remove(this);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
