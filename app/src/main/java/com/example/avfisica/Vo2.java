package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.Vo2Model;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.PesoResource;
import com.example.avfisica.resources.Vo2Resource;
import com.github.anastr.speedviewlib.ProgressiveGauge;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;


public class Vo2 extends AppCompatActivity {
    Button btn_salvar;
    EditText editText_distancia;
    TextView textView_resultado, textView_vo2;
    ImageButton btn_info,btn_edit;
    ImageView btn_obj;
    BottomNavigationView bottomNavigation;
    public static String textoSeparado [];
    public long idade = 0;
    public float distancia = 0;
    boolean update = false, flag_obj=false;
    float resultado, objetivo;

    AlunoResource helperAluno;
    Aluno aluno = new Aluno();

    Vo2Resource helperVo2;
    Vo2Model vo2 = new Vo2Model();
    List<Vo2Model> lvo2_dados = new ArrayList<Vo2Model>();
    Vo2Model vo2_edit = new Vo2Model();

    LineChart grafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vo2);

        grafico = (LineChart) findViewById(R.id.graficoID);
        btn_salvar = (Button) findViewById(R.id.button_salvar_vo2);
        //  btn_salvar.setVisibility (View.INVISIBLE);
        editText_distancia = (EditText) findViewById(R.id.editText_distancia_vo2);
        editText_distancia.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
        textView_resultado = (TextView) findViewById(R.id.textView_resultado_vo2);
        textView_vo2 = (TextView) findViewById(R.id.textView_vo2);
        btn_info = (ImageButton) findViewById(R.id.imageButton_vo2_info2);
        btn_edit = (ImageButton) findViewById(R.id.imageButton_vo2_edit);
        btn_obj = (ImageView) findViewById(R.id.btn_obj);

        helperAluno = new AlunoResource(this);
        aluno = helperAluno.getData(Register.id_login);
        helperVo2 = new Vo2Resource(this);
        lvo2_dados = helperVo2.getData(Register.id_login);

        final String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());


        final SpeedView progressiveGauge= (SpeedView) findViewById(R.id.progressiveGauge);

        // change speed to 50 Km/h
        progressiveGauge.speedTo(50);
        progressiveGauge.setTrembleData(0, 0);
        progressiveGauge.setUnit("Vo2");
        progressiveGauge.setSpeedTextColor(Color.TRANSPARENT);


// change the color of progress
        //progressiveGauge.setSpeedometerColor(Color.parseColor("#1B6BB3"));
// change the color of background progress
        //progressiveGauge.setSpeedometerBackColor(Color.GRAY);


        if (lvo2_dados.size()>0) {
            vo2 = lvo2_dados.get(lvo2_dados.size() - 1); // pega ultima atulizaÃ§Ã£o
            textView_vo2.setText(Float.toString ((float) (Math.round(vo2.getVo2()*10.0)/10.0)));
            editText_distancia.setText((String.valueOf(vo2.getDistancia())));
            editText_distancia.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
            progressiveGauge.speedTo(vo2.getVo2());
            calculoVo2();
            calculoGrafico(lvo2_dados);
        }

        //######################################
        //Salvar
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String currentTime = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());

                    if (helperVo2.updateName(vo2)>=0)
                    {
                        vo2.setVo2(resultado);
                        vo2.setDistancia(Float.parseFloat(editText_distancia.getText().toString()));
                        if(flag_obj==true){
                            vo2.setObjetivo(objetivo);
                        }else vo2.setObjetivo(0);
                        vo2.setId_login(Register.id_login);
                        vo2.setStatus("PENDENTE");


                        if (((vo2.getData()!=null) && (vo2.getData().equals(currentTime))) ||
                                ((vo2.getData()!=null) && update))
                        {
                            if (update) //flag de controle
                                update = false;

                            if (helperVo2.updateName(vo2) >= 0) {
                                Toast.makeText(getApplicationContext(), "Alterado", Toast.LENGTH_SHORT).show();
                                // textView_peso_date.setVisibility(View.VISIBLE);
                                //textView_peso_date.setText(currentTime);
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao alterar o registro no banco local!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            vo2.setData(currentTime);

                            if (helperVo2.insertData(vo2) >= 0)
                                Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                        }

                        lvo2_dados.clear();
                        lvo2_dados = helperVo2.getData(Register.id_login);
                        vo2 = lvo2_dados.get(lvo2_dados.size()-1); // pega ultima atulizaÃ§Ã£o

                        if (lvo2_dados.size() >= 1)
                            //Toast.makeText(getApplicationContext(), "ok ao carregar os dados do graficos!", Toast.LENGTH_SHORT).show();
                            calculoGrafico(lvo2_dados);
                        else if (lvo2_dados.size() == 0)
                            Toast.makeText(getApplicationContext(), "Falha ao carregar os dados do graficos!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "NÃo foi possivel gravar as informações", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //######################################

        //Imagem de info
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Vo2.this)
                            .setTitle("Ajuda")
                            .setMessage("VO2 COOPER"+ "\n"+ "\n" + "Corra por 12 min a maior distância que conseguir!!" + "\n" + "\n"+ "Depois preencha a distância percorrida e descubra o seu indice!!")
                            .show();
                } catch (Exception e) {
                }
            }
        });

        //Imagem de info
        btn_obj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editObjetivo(); //CHAMA O DIALOG OBJ
                } catch (Exception e) {
                }
            }
        });

        //Imagem de info
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editMedidas();
                } catch (Exception e) {
                }
            }
        });

        //######################### NAVIGATION ##################################
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.page_1:
                                openActivityDashboard();
                                return true;
                            case R.id.page_2:
                                openActivityComparativo();
                                return true;
                            case R.id.page_3:
                                openActivityCadastro();
                                return true;
                        }
                        return false;
                    }
                });


        editText_distancia.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                calculoVo2();

// Aqui você coloca o evento

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //Imagem de pace para tela principal
//        btn_voltar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    openActivityMain();
//                } catch (Exception e) {
//                }
//            }
//        });

    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    public void openActivityDashboard()
    {
        Intent intent = new Intent(this,Dashboard.class);
        startActivity(intent);
    }

    public void openActivityComparativo()
    {
        Intent intent = new Intent(this,Comparativo.class);
        startActivity(intent);
    }

    public void openActivityCadastro()
    {
        Intent intent = new Intent(this,Cadastro.class);
        startActivity(intent);
    }

    public void calculoVo2(){

        final SpeedView progressiveGauge= (SpeedView) findViewById(R.id.progressiveGauge);

        // change speed to 50 Km/h
        progressiveGauge.speedTo(50);
        progressiveGauge.setTrembleData(0, 0);
        progressiveGauge.setUnit("");

// change the color of progress
        //  progressiveGauge.setSpeedometerColor(Color.parseColor("#1B6BB3"));
// change the color of background progress
        //  progressiveGauge.setSpeedometerBackColor(Color.GRAY);

        if(editText_distancia.getText().toString().isEmpty()){

        }
        else {
            distancia = (Float.parseFloat(editText_distancia.getText().toString()));
            resultado = (float) ((distancia - 504.9) / 44.73);

            if (resultado > 0 && resultado < 100) {
                DecimalFormat df = new DecimalFormat("0.00");
                textView_vo2.setText(df.format(resultado) + "ml 1/(kg.min)");
                progressiveGauge.speedTo(resultado);
                idade = aluno.getIdade();
            }else textView_vo2.setText("Distância inválida");

        }
        if(aluno.getSexo().equals("M")) {

            if(idade >=13 && idade <=19) {
                progressiveGauge.setMinMaxSpeed(33,55);
                //Muito Fraco
                if (distancia < 2090) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 2090 && distancia <2210) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 2210 && distancia <2510) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 2510 && distancia <2770) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2770 && distancia <3000) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 3000) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 20 - 29
            if(idade >=20 && idade <=29) {
                progressiveGauge.setMinMaxSpeed(33,55);
                //Muito Fraco
                if (distancia < 1960) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1960 && distancia <2210) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 2210 && distancia <2410) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 2410 && distancia <2650) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2650 && distancia <2830) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2830) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 30 - 39
            if(idade >=30 && idade <=39) {
                progressiveGauge.setMinMaxSpeed(31,49);
                //Muito Fraco
                if (distancia < 1900) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1900 && distancia <2100) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 2100 && distancia <2410) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 2410 && distancia <2520) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2520 && distancia <2720) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2720) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 40 - 49
            if(idade >=40 && idade <=49) {
                progressiveGauge.setMinMaxSpeed(29,48);
                //Muito Fraco
                if (distancia < 1830) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1830 && distancia <2000) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 2000 && distancia <2250) {
                    resultVo2 ("Média",GREEN,50);

                }

                if (distancia >= 2250 && distancia <2470) {
                    resultVo2 ("Ótimo",YELLOW,70);

                }

                if (distancia >= 2470 && distancia <2660) {
                    resultVo2 ("Excelente",RED,90);

                }

                if (distancia >= 2660) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 50 - 59
            if(idade >=50 && idade <=59) {

                //Muito Fraco
                if (distancia < 1660) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1660 && distancia <1880) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1880 && distancia <2100) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 2100 && distancia <2330) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2330 && distancia <2540) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2540) {
                    resultVo2 ("Superior",RED,100);
                }
            }


            // IDADE > 60
            if(idade >=60) {

                //Muito Fraco
                if (distancia < 1400) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1400 && distancia <1650) {
                    resultVo2 ("Fraco",GREEN,30);

                }

                if (distancia >= 1650 && distancia <1940) {
                    resultVo2 ("Média",GREEN,50);

                }

                if (distancia >= 1940 && distancia <2130) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2130 && distancia <2490) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2490) {
                    resultVo2 ("Superior",RED,100);
                }
            }
        }
//############################################################################3
        else{ //MULHER
            progressiveGauge.setMinMaxSpeed(10,55);
            if(idade >=13 && idade <=19) {
                progressiveGauge.setMinMaxSpeed(33,55);
                //Muito Fraco
                if (distancia < 1610) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1610 && distancia <1900) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1900 && distancia <2080) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 2080 && distancia <2300) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2300 && distancia <2430) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2430) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 20 - 29
            if(idade >=20 && idade <=29) {
                progressiveGauge.setMinMaxSpeed(33,55);
                //Muito Fraco
                if (distancia < 1550) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1550 && distancia <1790) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1790 && distancia <1970) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 1970 && distancia <2160) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2160 && distancia <2330) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2330) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 30 - 39
            if(idade >=30 && idade <=39) {
                progressiveGauge.setMinMaxSpeed(31,49);
                //Muito Fraco
                if (distancia < 1510) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1510 && distancia <1690) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1690 && distancia <1960) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 1960 && distancia <2080) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 2080 && distancia <2240) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2240) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 40 - 49
            if(idade >=40 && idade <=49) {
                progressiveGauge.setMinMaxSpeed(29,48);
                //Muito Fraco
                if (distancia < 1420) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1420 && distancia <1580) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1580 && distancia <1790) {
                    resultVo2 ("Média",GREEN,50);

                }

                if (distancia >= 1790 && distancia <2000) {
                    resultVo2 ("Ótimo",YELLOW,70);

                }

                if (distancia >= 2000 && distancia <2160) {
                    resultVo2 ("Excelente",RED,90);

                }

                if (distancia >= 2160) {
                    resultVo2 ("Superior",RED,100);
                }
            }

            // IDADE 50 - 59
            if(idade >=50 && idade <=59) {

                //Muito Fraco
                if (distancia < 1350) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1350 && distancia <1500) {
                    resultVo2 ("Fraco",GREEN,30);
                }

                if (distancia >= 1500 && distancia <1690) {
                    resultVo2 ("Média",GREEN,50);
                }

                if (distancia >= 1690 && distancia <1900) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 1900 && distancia <2090) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 2090) {
                    resultVo2 ("Superior",RED,100);
                }
            }


            // IDADE > 60
            if(idade >=60) {

                //Muito Fraco
                if (distancia < 1260) {
                    resultVo2 ("Muito Fraco",GREEN,15);
                }

                if (distancia >= 1260 && distancia <1390) {
                    resultVo2 ("Fraco",GREEN,30);

                }

                if (distancia >= 1390 && distancia <1590) {
                    resultVo2 ("Média",GREEN,50);

                }

                if (distancia >= 1590 && distancia <1750) {
                    resultVo2 ("Ótimo",YELLOW,70);
                }

                if (distancia >= 1750 && distancia <1900) {
                    resultVo2 ("Excelente",RED,90);
                }

                if (distancia >= 1900) {
                    resultVo2 ("Superior",RED,100);
                }
            }
        }
    }

    //############################ GRAFICO ############################

    public void calculoGrafico( List<Vo2Model> lvo2){

        grafico = (LineChart) findViewById(R.id.graficoID);
        List<Entry> entradaGrafico = new ArrayList<>();
        List<String> entradaGraficox = new ArrayList<>();
        List<Entry> entradaGraficoLine = new ArrayList<>();
        int i_=0;
        for (int i = 0; i < (lvo2.size()); i++)
        {
            entradaGrafico.add(new Entry(i,lvo2.get(i).getVo2()));
            entradaGraficox.add(lvo2.get(i).getData());
            entradaGraficoLine.add(new Entry(i,lvo2.get(i).getObjetivo()));
            i_=i;
        }


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        //######### Grafico Linha de Objetivo ##########
        LineDataSet dataSetLine = new LineDataSet(entradaGraficoLine, "Objetivo (ml 1/(kg.min))");
        dataSetLine.setDrawCircles(true);
        dataSetLine.setColor(GREEN);
        dataSetLine.setCircleColor(GREEN);
        dataSetLine.setDrawFilled(true);
        dataSetLine.setValueTextSize(12);
        dataSetLine.setFillColor(GREEN);
        dataSetLine.setValueTextColor(Color.BLACK);



        //######### Grafico principal ##########
        LineDataSet dataSet = new LineDataSet(entradaGrafico, "Vo2 (ml 1/(kg.min))");
        dataSet.setColors(Color.parseColor("#1288E8"));
        //DATASET_0
        // dataSet.setFormLineWidth(1f);
        dataSet.setCircleColor(Color.parseColor("#1288E8"));
        // dataSet.setCircleRadius(1f);
        dataSet.setDrawCircleHole(false);
        dataSet.setFillColor(Color.parseColor("#1288E8"));
        // draw selection line as dashed
        // dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setDrawFilled(true);
        dataSet.setValueTextSize(12);
        dataSet.setValueTextColor(Color.parseColor("#1288E8"));

        YAxis yAxis_0;
        {   // // Y-Axis Style // //
            yAxis_0 = grafico.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            grafico.getAxisRight().setEnabled(false);
            grafico.getAxisLeft().setEnabled(false);
//            yAxis_0.setAxisMinimum(0f);
//            yAxis_0.setAxisMaximum(100f);
            // horizontal grid lines
            // axis range
        }

        XAxis xAxis_0;
        {   // // X-Axis Style // //
            xAxis_0 = grafico.getXAxis();
            grafico.getXAxis().setEnabled(true);
            // vertical grid lines
            //xAxis_0.enableGridDashedLine(20f, 20f, 0f);
            //  List<String> xVals = entradaGraficox;
            xAxis_0.setValueFormatter(new IndexAxisValueFormatter(entradaGraficox));
            // xAxis_0.setGranularityEnabled(false);
            xAxis_0.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis_0.setDrawGridLines(false);
            xAxis_0.setGranularity(1f);
            xAxis_0.setDrawLabels(true);
            xAxis_0.setDrawAxisLine(true);
            //  xAxis_0.setGranularity(2f);
            //  xAxis_0.setDrawGridLines(true);
            xAxis_0.setLabelRotationAngle(0); // ROTAÃ‡ÃƒO EIXO X
            xAxis_0.setAxisMaximum(i_);
        }

        lineDataSets.add(dataSetLine);
        lineDataSets.add(dataSet);

        // grafico.setScaleYEnabled(true);
        grafico.setPinchZoom(true);
        grafico.setScaleXEnabled(true);
        grafico.setVisibleXRangeMaximum(4);
        grafico.moveViewToX(i_);
        grafico.getDescription().setEnabled(false);
        // background color
        grafico.setBackgroundColor(Color.WHITE);

        grafico.setData(new LineData(lineDataSets));
    }

    public void editMedidas()
    {
        List<String> spinnerArray_data = new ArrayList<String>();
        lvo2_dados = helperVo2.getData(Register.id_login);

        for (int i = 0; i < (lvo2_dados.size()); i++)
        {
            spinnerArray_data.add( lvo2_dados.get(i).getData());
        }
//        for (CorporalModel comp:lcorporal_dados)
//        {
//            spinnerArray_data.add(corporal.getData());
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Vo2.this);
        //Cria a view a ser utilizada no dialog
        View mView  = Vo2.this.getLayoutInflater().inflate(R.layout.spinner_aux,null);
        //ObtÃ©m uma referencia ao Spinner
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);
        //Cria o Adapter
        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(Vo2.this,
                android.R.layout.simple_spinner_item, spinnerArray_data); // Chamar o ARRAY
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Atribui o adapter ao spinner
        mSpinner.setAdapter(adapter_1);
        builder.setTitle("Escolha a data");
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int getid = mSpinner.getSelectedItemPosition();
                //lcorporal_dados = helperCorporal.searchData(data);
                if (lvo2_dados.size()>0) {
                    vo2_edit = lvo2_dados.get(getid); // pega ultima atulizaÃ§Ã£o
                    vo2.setData(lvo2_dados.get(getid).getData()); //salva a data para alterar
                    vo2.setId_vo2(lvo2_dados.get(getid).getId_vo2());
                    DecimalFormat df = new DecimalFormat("0.00");
                    textView_vo2.setText(Float.toString(vo2_edit.getVo2()));
                    editText_distancia.setText(Integer.toString((int) lvo2_dados.get(getid).getDistancia()));
                    //editText_objetivo.setText(Float.toString(vo2_edit.getObjetivo()));
                    //textView_peso_date.setVisibility(View.VISIBLE);
                    //textView_peso_date.setText(lpeso_dados.get(getid).getData());
                    calculoVo2();
                }
                update = true;
                Toast.makeText(getApplicationContext(),  "Clicked OK!", Toast.LENGTH_SHORT);
                // corporal = lcorporal_dados.get(Integer.parseInt(data)); // pega ultima atulizaÃ§Ã£o
                return;
            }
        });
        builder.show();
    }

    public void editObjetivo()
    {
        List<String> spinnerArray_data = new ArrayList<String>();
        lvo2_dados = helperVo2.getData(Register.id_login);


        AlertDialog.Builder builder = new AlertDialog.Builder(Vo2.this);
        //Cria a view a ser utilizada no dialog
        View mView  = Vo2.this.getLayoutInflater().inflate(R.layout.dialog_vo2,null);
        //ObtÃ©m uma referencia ao Spinner
        final EditText editText = (EditText) mView.findViewById(R.id.editText_dial_obj);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 2)});

        if (lvo2_dados.size()>0) {
            editText.setText(String.valueOf(vo2.getObjetivo()));
        }
        //Atribui o adapter ao spinner
        builder.setTitle("Determine seu objetivo");
        builder.setView(mView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                objetivo= Float.parseFloat(editText.getText().toString());
                //lcorporal_dados = helperCorporal.searchData(data);
                flag_obj = true;
                Toast.makeText(getApplicationContext(),  "Objetivo alterado!", Toast.LENGTH_SHORT);
                // corporal = lcorporal_dados.get(Integer.parseInt(data)); // pega ultima atulizaÃ§Ã£o
                return;
            }
        });
        builder.show();
    }

    public boolean resultVo2 (String text, int color, int porc){

        final SpeedView progressiveGauge= (SpeedView) findViewById(R.id.progressiveGauge);

        textView_resultado.setText(text);
        textView_resultado.setTextColor(color);
        progressiveGauge.speedPercentTo(porc);
        progressiveGauge.setUnit("Vo2");
        progressiveGauge.setSpeedTextColor(Color.TRANSPARENT);
        return false;
    }


}
