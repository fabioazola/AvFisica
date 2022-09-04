package com.example.avfisica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.CorporalResource;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Atividades extends AppCompatActivity {

    Spinner spinner_modalidade, spinner_esforco, spinner_periodo, spinner_duracao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades);

        //###########################//SPINNER MODALIDADE//##############################################//
        // Spinner element
        /*for fill your Spinner*/
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Musculação");
        spinnerArray.add("Funcional");
        spinnerArray.add("Crossfit");
        spinnerArray.add("Corrida");
        spinnerArray.add("Natação");
        spinnerArray.add("Pilates");
        spinnerArray.add("Bike");
        spinnerArray.add("Spinning");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_modalidade);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);

                if (position == 0){
                    Toast.makeText(getApplicationContext(), "Musculação", Toast.LENGTH_SHORT).show();

                }
                if (position == 1){
                    Toast.makeText(getApplicationContext(), "Funcional", Toast.LENGTH_SHORT).show();

                }

                if (position == 2) {
                    Toast.makeText(getApplicationContext(), "Crossfit", Toast.LENGTH_SHORT).show();

                }
                if (position == 3){
                    Toast.makeText(getApplicationContext(), "Braço Dir", Toast.LENGTH_SHORT).show();

                }
                if (position == 4){
                    Toast.makeText(getApplicationContext(), "Braço Esq", Toast.LENGTH_SHORT).show();

                }
                if (position == 5){
                    Toast.makeText(getApplicationContext(), "Coxa Direita", Toast.LENGTH_SHORT).show();

                }
                if (position == 6){
                    Toast.makeText(getApplicationContext(), "Coxa Esquerda", Toast.LENGTH_SHORT).show();

                }
                if (position == 7){
                    Toast.makeText(getApplicationContext(), "Gordura", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });


    }

}
