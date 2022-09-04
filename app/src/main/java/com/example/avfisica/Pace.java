package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Corrida;
import com.example.avfisica.models.Peso;
import com.example.avfisica.resources.CorridaResource;
import com.example.avfisica.resources.PesoResource;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pace extends AppCompatActivity {
    Button btn_voltar, btn_calcular, btn_salvar, btn_vo2;
    TextView textView_pace, textView_pace_5, textView_pace_5m, textView_pace_10, textView_pace_km, textView_pace_kcal,textView_predicao;
    TableLayout table_pace;
    EditText editText_distancia, editText_tempo, editText_minuto, editText_segundo, editText_data;
    String t1,Hr="0",Min="0",Seg="0";
    ListView listView;
    BottomNavigationView bottomNavigation;
    ImageView imageButton_corrida_info;

    Chip chip5km, chip10km, chip21km, chip42km, chipultrakm;

    float result_seg, segundo, soma, soma_pace = (float) 0.00;
    int hora, minuto = 0;
    int tipo_distancia =0;
    float tempo_millis;

    CorridaResource helperCorrida;
    Corrida corrida = new Corrida();
    List<Corrida> lcorrida_dados = new ArrayList<Corrida>();
    //constantes
    final int MENOR5KM = 0;
    final int MAIOR5KM = 1;
    final int MAIOR10KM = 2;
    final int MAIOR21KM = 3;
    final int MAIOR42KM = 4;

    PesoResource helperPeso;
    Peso peso = new Peso();
    List<Peso> lpeso_dados = new ArrayList<Peso>();

    final Calendar myCalendar = Calendar.getInstance();

    RecyclerView rv; //NEW
    Integer drawableArray [];
    String titleArray [];
    String subtitleArray [];
    String subtitleArray_ [];
    String tempoArray [];
    String kcalArray [];
    String array_1[];
    String array_2[];
    String array_3[];
    String array_4[];
    Integer array_5[];
    String array_6[];

     int [] km_vector = new int[]{5,10,15,21,42}; //  VECTOR
     String [] time_vector = new String[5]; //  VECTOR

    CustomAdapter ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pace);
        btn_voltar = (Button) findViewById(R.id.button_salvar);
        btn_salvar = (Button) findViewById(R.id.btn_salvar);
        // btn_vo2 = (Button) findViewById(R.id.button_imc);
        textView_pace = (TextView) findViewById(R.id.textView_pace);
        textView_pace_km = (TextView) findViewById(R.id.textView_pace_km);
        textView_pace_kcal = (TextView) findViewById(R.id.textView_pace_kcal);
        textView_predicao = (TextView) findViewById(R.id.textView_predicao);
        editText_distancia = (EditText) findViewById(R.id.editText_distancia);
        editText_distancia.setFilters(new InputFilter[]{new Pace.DecimalDigitsInputFilter(3, 2)});
        editText_tempo = (EditText) findViewById(R.id.editText_tempo);
        editText_tempo.setFilters(new InputFilter[]{new Pace.DecimalDigitsInputFilter(2, 1)});
        editText_data = (EditText) findViewById(R.id.editText_data);
        //table_pace = (TableLayout)findViewById(R.id.table_pace);
        imageButton_corrida_info = (ImageView) findViewById(R.id.imageButton_corrida_info);

        chip5km = (Chip) findViewById(R.id.chipCpp);
        chip10km = (Chip)findViewById(R.id.chipJava);
        chip21km = (Chip)findViewById(R.id.chipPython);
        chip42km = (Chip)findViewById(R.id.chip42km);
        chipultrakm = (Chip)findViewById(R.id.chipultrakm);

        // table_pace.isScrollContainer();

        helperCorrida = new CorridaResource(this);
        lcorrida_dados = helperCorrida.getData(Register.id_login);
        helperPeso = new PesoResource(this);
        lpeso_dados = helperPeso.getData(Register.id_login);
        if (lpeso_dados.size()>0)
            peso = lpeso_dados.get(lpeso_dados.size()-1); // pega ultima atulização

        final String currentTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // ###############################################################

        if (lcorrida_dados.size()>0) {
            corrida = lcorrida_dados.get(lcorrida_dados.size() - 1); // pega ultima atulizaÃ§Ã£o
        }
        viewPace();
        // ###############################################################


        //######################### DATA ################################
        EditText edittext= (EditText) findViewById(R.id.editText_data);
        edittext.setText(currentTime);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Pace.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        editText_distancia.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui você coloca o evento
                calculoPace();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        textView_predicao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Aqui você coloca o evento
                try {
                    double distancia = Float.parseFloat(editText_distancia.getText().toString());
                    long segundos = 0;
                    Duration total = null;
                    for (int i = 0; i < km_vector.length; i++) {
                        float pred = (float) (Math.pow(km_vector[i] / distancia, 1.06) * soma); // Calculo de predição
                        float pred__ = ((pred - (int) pred) * 60);  // Resto do segundos

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            total = Duration.ofMinutes((long) pred); // Transforma em segundos
                            segundos = total.getSeconds() + (long) pred__; // Soma os segundos
                            String time = ConvertSecondToHHMMSSString((int) segundos); // Convert em HH:MM:SS
                            time_vector[i] = time; // Encapsula no vector String
                        }
                    }

                    new AlertDialog.Builder(Pace.this)
                            .setTitle("Predição de Tempo:")
                            .setMessage("5Km: " + time_vector[0] +"\n" +"\n" + "10Km: " + time_vector[1] +"\n" +"\n"+ "15Km: "
                                    + time_vector[2]+"\n"+"\n"+ "21Km: " + time_vector[3]+"\n" +"\n"+ "42Km: " + time_vector[4])
                            .show();
                } catch (Exception e) {
                }
            }
        });


        // Edit hora
        editText_tempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog();
                    calculoPace();

                } catch (Exception e) {
                }
            }
        });


        // chip-5km
        chip5km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip5km.isChecked()) {
                        chip10km.setChecked(false);
                        chip21km.setChecked(false);
                        chip42km.setChecked(false);
                        chipultrakm.setChecked(false);
                        tipo_distancia = 0;
                        viewPace();
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-10km
        chip10km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip10km.isChecked()) {
                        chip5km.setChecked(false);
                        chip21km.setChecked(false);
                        chip42km.setChecked(false);
                        chipultrakm.setChecked(false);
                        tipo_distancia = 1;
                        viewPace();
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-21km
        chip21km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip21km.isChecked()) {
                        chip5km.setChecked(false);
                        chip10km.setChecked(false);
                        chip42km.setChecked(false);
                        chipultrakm.setChecked(false);
                        tipo_distancia = 2;
                        viewPace();
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-42km
        chip42km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip42km.isChecked()) {
                        chip5km.setChecked(false);
                        chip10km.setChecked(false);
                        chip21km.setChecked(false);
                        chipultrakm.setChecked(false);
                        tipo_distancia = 3;
                        viewPace();
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-ultra km
        chipultrakm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chipultrakm.isChecked()) {
                        chip5km.setChecked(false);
                        chip10km.setChecked(false);
                        chip21km.setChecked(false);
                        chip42km.setChecked(false);
                        tipo_distancia = 4;
                        viewPace();
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        //Imagem de info
        imageButton_corrida_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Pace.this)
                            .setTitle("Ajuda")
                            .setMessage("Registre suas corridas e caminhadas!!"+ "\n"+ "\n" + "Basta colocar o tempo e a distância percorrida!" + "\n" + "\n"+ "O resto é por nossa conta!")
                            .show();
                } catch (Exception e) {
                }
            }
        });

        //Button de salvar as inf. de pace
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (soma_pace > 0) {

                        final String currentTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                        if (editText_data.getText().toString().isEmpty()) {
                            t1 = currentTime;
                        } else {
                            t1 = editText_data.getText().toString();
                        }


                        DecimalFormat df = new DecimalFormat("0.00");
                        String t2 = String.valueOf(editText_distancia.getText().toString());
                        String t3 = String.valueOf(soma);
                        String t4 = String.valueOf(df.format(soma_pace));
                        String t5 = String.valueOf(0);
                        String t6 = String.valueOf(0);
                        //String t6 = ed
                        corrida.setData(t1);
                        corrida.setDistancia(Float.parseFloat(t2));
                        corrida.setTempo(Float.toString(tempo_millis));
                        corrida.setPace(soma_pace);
                        corrida.setVo2(Float.parseFloat(t5));
                        corrida.setNivel(Long.parseLong(t6));
                        corrida.setId_login(Register.id_login);
                        corrida.setStatus("PENDENTE");


                        if (helperCorrida.insertData(corrida) >= 0) {
                            Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                            viewPace();
                        } else
                            Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                } catch(Exception e){
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



//        //Button de calcular para tela principal
//        btn_vo2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    openActivityVo2();
//                } catch (Exception e) {
//                }
//            }
//        });



////###########################//SPINNER//##############################################//
//        // Spinner element
//        /*for fill your Spinner*/
//        List<String> spinnerArray = new ArrayList<String>();
//        spinnerArray.add("<5Km");
//        spinnerArray.add(">=5Km");
//        spinnerArray.add(">=10Km");
//
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, spinnerArray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner spinner = (Spinner) findViewById(R.id.spinner_pace);
//        spinner.setAdapter(adapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view,
//                                       int position, long id) {
//                Object item = adapterView.getItemAtPosition(position);
//                //if (item != null) {
//                //    Toast.makeText(getApplicationContext(), "Organizado", Toast.LENGTH_SHORT).show();
//                // }
//                if (position == 0){
//                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
//                    tipo_distancia = 0;
//                    viewPace();
//                    // comparacaoFoto();
//                }
//                if (position == 1){
//                    //Toast.makeText(getApplicationContext(), "Tras", Toast.LENGTH_SHORT).show();
//                    tipo_distancia = 1;
//                    viewPace();
//                    // comparacaoFoto();
//                }
//
//                if (position == 2){
//                    //Toast.makeText(getApplicationContext(), "Lado Direito", Toast.LENGTH_SHORT).show();
//                    tipo_distancia = 2;
//                    viewPace();
//                    // comparacaoFoto();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                // TODO Auto-generated method stub
//
//            }
//        });

    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

//    public void openActivityVo2()
//    {
//        Intent intent = new Intent(this,Vo2.class);
//        startActivity(intent);
//    }


    public void calculoPace()
    {
        if( editText_distancia.getText().toString().isEmpty()||editText_tempo.getText().toString().isEmpty()){
        }
        else {
            try {

                hora = Integer.parseInt(Hr);

                minuto = Integer.parseInt(Min);

                segundo = Float.parseFloat(Seg);

                soma = ((hora * 60) + minuto + (segundo / 60));

                tempo_millis = (hora*3600000) + (minuto*60000) +(segundo*1000);


                if (editText_distancia.getText().toString() == ".") {
                }
                else {
                    float result = (soma / Float.parseFloat(editText_distancia.getText().toString())); // minutos/distancia
                    int result_fin = (int) result;
                    result_seg = (result - result_fin) * 60 / 100;
                    soma_pace = (float) (result_seg + result_fin);

                    DecimalFormat df = new DecimalFormat("0.00");
                    textView_pace.setText(df.format((soma_pace)) + " Min/Km"); // PACE
                    soma_pace= (float) (Math.round(soma_pace * 100.0)/100.0); // SALVA COM DUAS CASAS DECIMAIS

                    float distancia = Float.parseFloat(editText_distancia.getText().toString());
                    float km = (float) (distancia/(soma/60.00)); // KM/H
                    DecimalFormat df_ = new DecimalFormat("0.00");
                    textView_pace_km.setText(df.format(km)+"Km/h");
                    float kcal = (float) (km*peso.getPeso()* 0.0175)*soma;
                    DecimalFormat df__ = new DecimalFormat("0.0");
                    textView_pace_kcal.setText(df__.format(kcal)+"  calorias");  // CALORIAS

                    //textView_pace.setText(String.valueOf((result_seg+result_fin) + " Min/KM"));
//
//            DecimalFormat df_= new DecimalFormat("#.##");
//            df_.setRoundingMode(RoundingMode.FLOOR);
//            Float soma_pace_df = new Float(df_.format(soma_pace));
//            soma_pace=soma_pace_df;
                }
            } catch (NumberFormatException e) { //IMPORTANTISSIMO
                e.printStackTrace();
            }
        }
    }


    public void viewPace(){

        //  table_pace.removeAllViews();
        if(tipo_distancia==0)
            viewPaceFiltro(MENOR5KM);
        else if (tipo_distancia==1)
            viewPaceFiltro(MAIOR5KM);
        else if (tipo_distancia==2)
            viewPaceFiltro(MAIOR10KM);
        else if (tipo_distancia==3)
            viewPaceFiltro(MAIOR21KM);
        else if (tipo_distancia==4)
            viewPaceFiltro(MAIOR42KM);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText_data.setText(sdf.format((myCalendar.getTime())));
    }

    private void viewPaceFiltro(int distancia)
    {
        lcorrida_dados = helperCorrida.getData(Register.id_login);
        int i = 0;
        int i_= 0;
        boolean flag = false;

        cabecalhoTabela();

        for (Corrida corrida_:lcorrida_dados)
        {

            switch (distancia)
            {
                case MENOR5KM:
                    if (corrida_.getDistancia() <= 5) {
                        i_++;
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR5KM:
                    if (corrida_.getDistancia() > 5 && corrida_.getDistancia() <= 10){
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR10KM:
                    if (corrida_.getDistancia() > 10 && corrida_.getDistancia() <=21) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR21KM:
                    if (corrida_.getDistancia() > 21 && corrida_.getDistancia() <=42) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR42KM:
                    if (corrida_.getDistancia() > 42) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                default:
                    break;
            }
            if(flag) {
                flag = false;
                i++;
            }
        }

        array_1 = new String[i];
        array_2 = new String[i];
        array_3 = new String[i];
        array_4 = new String[i];
        array_6 = new String[i];
        array_5 = new Integer[i];
        i=0;
        for (Corrida corrida_:lcorrida_dados)
        {
            switch (distancia)
            {
                case MENOR5KM:
                    if (corrida_.getDistancia() <= 5) {
                        i_++;
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR5KM:
                    if (corrida_.getDistancia() > 5 && corrida_.getDistancia() <= 10){
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR10KM:
                    if (corrida_.getDistancia() > 10 && corrida_.getDistancia() <=21) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR21KM:
                    if (corrida_.getDistancia() > 21 && corrida_.getDistancia() <=42) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                case MAIOR42KM:
                    if (corrida_.getDistancia() > 42) {
                        preencherDados(corrida_, i);
                        flag = true;
                    }
                    break;
                default:
                    break;
            }
            if(flag) {
//########################## TEMPO - parse ################################
                int hour = (int) (Float.parseFloat (corrida_.getTempo())/3600000);
                String hr="0";
                if(hour<10){
                    hr = "0"+hour;
                }else hr = String.valueOf(hour);

                int min = (int) (Float.parseFloat (corrida_.getTempo())/60000)%60;
                String min_ = "0";

                if(min<10){
                    min_ = "0"+min;
                }else min_= String.valueOf(min);

                String seg_="0";
                int seg = (int) ((Float.parseFloat (corrida_.getTempo())/1000))%60;
                if(seg<10){
                    seg_="0"+seg;
                }else seg_= String.valueOf(seg);

                String tempo_ = hr+':'+min_+":"+seg_;
//######################################################################################
                array_1 [i]= Float.toString( corrida_.getDistancia());
                array_2 [i]= Float.toString( corrida_.getPace());
                array_3 [i]= (corrida_.getData());
                array_4 [i] = (tempo_);
                array_5 [i] = R.drawable.corrida;

                float tempo = Float.parseFloat(corrida_.getTempo());
                float kcal = (float) Math.round((((corrida_.getDistancia()/(tempo/60))*peso.getPeso()* 0.0175)*tempo*100.0)/100.0); // Calculo kcal
                array_6 [i] = Float.toString(kcal);
                flag = false;
                i++;
            }
        }

        titleArray = new String[array_1.length];
        subtitleArray = new String[array_1.length];
        subtitleArray_ = new String[array_1.length];
        tempoArray = new String[array_1.length];
        kcalArray = new String[array_1.length];
        drawableArray = new Integer[array_1.length];
        //############################################
        titleArray = array_1;
        subtitleArray = array_2;
        subtitleArray_ = array_3;
        tempoArray = array_4;
        drawableArray = array_5;
        kcalArray = array_6;

        RecyclerView rv; //NEW
        rv = (RecyclerView)findViewById(R.id.rv);
        ad = new CustomAdapter(Pace.this,drawableArray,titleArray,subtitleArray,subtitleArray_,tempoArray,kcalArray);
        rv.setAdapter(ad);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
    }

    public void cabecalhoTabela()
    {
//        //cabeçalho da tabela
//        TableRow tr_ = new TableRow(this);
//        tr_.setBackgroundColor(Color.rgb(179,206,250));
//        table_pace.addView(tr_);
//
//        TextView tv0 = new TextView(this);//distancia
//        table_pace.setColumnStretchable(0,true);
//        TextView tv1 = new TextView(this);//pace
//        table_pace.setColumnStretchable(1,true);
//        TextView tv2 = new TextView(this);//data
//        table_pace.setColumnStretchable(2,true);
//
//        tv0.setText("Distância (Km)");
//        tv0.setTypeface(null, Typeface.BOLD);
//        tv0.setGravity(Gravity.CENTER);
//        tv1.setText("Pace (min/Km)");
//        tv1.setTypeface(null, Typeface.BOLD);
//        tv2.setText("Data");
//        tv2.setTypeface(null, Typeface.BOLD);
//
//        tr_.addView(tv0);
//        tr_.addView(tv1);
//        tr_.addView(tv2);
    }

    public void preencherDados(Corrida corrida, int linha)
    {

//        //linha
//        TableRow tr = new TableRow(this);
//        if ((linha % 2) ==0) //par
//            tr.setBackgroundColor(Color.rgb(18,136,232));
//        else //impar
//            tr.setBackgroundColor(Color.rgb(245,222,179));
//        table_pace.addView(tr);
//
//        //Coluna
//        TextView tv0 = new TextView(this);//distancia
//        TextView tv1 = new TextView(this);//pace
//        TextView tv2 = new TextView(this);//data
//
//        tv0.setText(Float.toString(corrida.getDistancia()));
//        tv0.setGravity(Gravity.CENTER);
//        tv1.setText(Float.toString(corrida.getPace()));
//        tv1.setGravity(Gravity.CENTER);
//        tv2.setText(corrida.getData());
//
//
//        tr.addView(tv0);
//        tr.addView(tv1);
//        tr.addView(tv2);
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

    private void showPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_run, null);
        builder.setView(view);
        //builder.setTitle();
        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.picker_first);
        picker.setMinValue(0);
        picker.setMaxValue(23);
        picker.setValue(Integer.parseInt(Hr));

        final NumberPicker picker_2 = (NumberPicker) view.findViewById(R.id.picker_second);
        picker_2.setMinValue(0);
        picker_2.setMaxValue(59);
        picker_2.setValue(Integer.parseInt(Min));

        final NumberPicker picker_3 = (NumberPicker) view.findViewById(R.id.picker_second2);
        picker_3.setMinValue(0);
        picker_3.setMaxValue(59);
        picker_3.setValue(Integer.parseInt(Seg));

//        picker.setValue(Integer.parseInt(editText_peso.getText().toString()));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //positive button action
                Hr = String.valueOf(picker.getValue());
                Min = String.valueOf(picker_2.getValue());
                Seg = String.valueOf(picker_3.getValue());
                if(picker.getValue()<10){
                    Hr="0"+Hr;
                }
                if(picker_2.getValue()<10){
                    Min="0"+Min;
                }
                if(picker_3.getValue()<10){
                    Seg="0"+Seg;
                }
                editText_tempo.setText(Hr+":" + Min + ":" + Seg);
                //editText_minuto.setText(Min);
                // editText_segundo.setText(Seg);
                calculoPace();

            }
        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //negative button action
                    }
                });
        builder.create().show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    private String ConvertSecondToHHMMSSString(int nSecondTime) {
        return LocalTime.MIN.plusSeconds(nSecondTime).toString();
    }

}
