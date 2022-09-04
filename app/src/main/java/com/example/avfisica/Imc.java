package com.example.avfisica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.resources.AlunoResource;

import java.text.DecimalFormat;

public class Imc extends AppCompatActivity {
    Button btn_calcular_imc, btn_voltar;
    EditText editText_idade, editText_peso, editText_altura, editText_data;
    TextView textView_imc, textView_faixa;
    ImageButton btn_pace;

    //myDbAdapter helper;
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        btn_calcular_imc = (Button) findViewById(R.id.button_calcular_imc);
        btn_voltar = (Button) findViewById(R.id.button_salvar);

        editText_altura = (EditText) findViewById(R.id.editText_altura);
        editText_idade = (EditText) findViewById(R.id.editText_cintaMac);
        editText_data = (EditText) findViewById(R.id.editText_data);
        editText_peso = (EditText) findViewById(R.id.editText_peso);
        textView_imc = (TextView) findViewById(R.id.textView_resultado_imc);
        textView_faixa = (TextView) findViewById(R.id.textView_faixa);

        helperAluno = new AlunoResource(this);
        aluno = helperAluno.getData(Register.id_login);

        if(aluno==null){

        }
        else {
            editText_idade.setText(Long.toString(aluno.getIdade()));
            editText_altura.setText(aluno.getAltura());
            editText_peso.setText(Float.toString(aluno.getPeso()));

            calculoImc();
        }

        editText_altura.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui você coloca o evento

                // int distancia = Integer.parseInt((String.valueOf(editText_distancia)));
                    calculoImc();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_peso.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui você coloca o evento

                // int distancia = Integer.parseInt((String.valueOf(editText_distancia)));

                    calculoImc();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_idade.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui você coloca o evento

                // int distancia = Integer.parseInt((String.valueOf(editText_distancia)));
                    calculoImc();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //Imagem de voltar para tela principal
        btn_calcular_imc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    calculoImc();

                } catch (Exception e) {
                }
            }
        });

        //Imagem de pace para tela principal
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityMain();
                } catch (Exception e) {
                }
            }
        });

    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void calculoImc() {


        if (editText_altura.getText().toString().isEmpty() || editText_peso.getText().toString().isEmpty() || editText_idade.getText().toString().isEmpty()) {
        }
        //textView_view.setText(data);
        else {
            float altura = (Float.parseFloat(editText_altura.getText().toString())) / 100;
            float peso = Float.parseFloat(editText_peso.getText().toString());
            float resultado = peso / (altura * altura);

            DecimalFormat df = new DecimalFormat("0.00");
            textView_imc.setText(df.format(resultado) + " IMC");

            if (resultado < 20) {
                textView_faixa.setText("Baixo Peso");
                textView_faixa.setTextColor(Color.RED);
            }

            if (resultado >= 20 && resultado < 25) {
                textView_faixa.setText("Peso Normal");
                textView_faixa.setTextColor(Color.GREEN);

            }

            if (resultado >= 25 && resultado < 30) {
                textView_faixa.setText("SobrePeso");
                textView_faixa.setTextColor(Color.YELLOW);
            }

            if (resultado >= 30 && resultado < 35) {
                textView_faixa.setText("Obesidade 1");
                textView_faixa.setTextColor(Color.BLUE);

            }

            if (resultado > 35) {
                textView_faixa.setText("Obesidade 2");
                textView_faixa.setTextColor(Color.RED);
            }

        }
    }
}
