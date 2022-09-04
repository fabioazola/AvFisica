package com.example.avfisica;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Suporte extends AppCompatActivity {

    ImageView imageView_send;
    TextView textView_suporte, textView_assunto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporte);

        imageView_send = (ImageView) findViewById(R.id.imageView_send);
        textView_suporte = (TextView) findViewById(R.id.textView_suporte);
        textView_assunto = (TextView) findViewById(R.id.textView_assunto);

        // Evento SEND
        imageView_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendEmail();
                } catch (Exception e) {
                }
            }
        });
    }

    public void sendEmail(){

        String[] TO = {"wifit.suporte@gmail.com"};
        String[] CC = {"matheusfscotton@hotmail.com, fabioazola@gmail.com"};
        String emailbody = textView_suporte.getText().toString();
        String emailassunto = textView_assunto.getText().toString();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailassunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailbody);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            // Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Suporte.this,
                    "E-mail não instalado", Toast.LENGTH_SHORT).show();
        }
    }

}
