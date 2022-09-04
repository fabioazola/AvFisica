package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.CorporalResource;
import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.Speedometer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;


public class Corporal extends AppCompatActivity {
    Button btn_salvar;
    EditText editText_abdominal, editText_pescoco,editText_quadril,editText_bicpesdir,editText_bicepsesq,editText_coxadir,editText_coxaesq;
    EditText editText_torax, editText_cintura, editText_pantDir, editText_pantEsq, editText_antbDir, editText_antbEsq;
    TextView textView_view, textView_all, textView_resultado, textView_data, textView_pesc,textView_torax,textView_cintura,textView_abd,textView_quadril,textView_bcps_dir,textView_bcps_esq,textView_ant_dir,textView_ant_esq,textView_coxa_dir,textView_coxa_esq,textView_pant_dir,textView_pant_esq;
    BottomNavigationView bottomNavigation;
    ImageButton imageButton_corp_edit, imageButton_corp_info,imageButton_corporal_info_pesc;

    CorporalResource helperCorporal;
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();
    CorporalModel corporal = new CorporalModel();
    CorporalModel corporal_edit = new CorporalModel();
    List<CorporalModel> lcorporal_dados = new ArrayList<CorporalModel>();
    List<CorporalModel> lcorporal_update = new ArrayList<CorporalModel>();
    //constantes Grafico
    final int PESCOCO = 0;
    final int TORAX = 1;
    final int CINTURA = 2;
    final int ABDOMINAL = 3;
    final int QUADRIL = 4;
    final int BICEPSESQ = 5;
    final int BICEPSDIR = 6;
    final int ANTDIR = 7;
    final int ANTESQ = 8;
    final int COXADIR = 9;
    final int COXAESQ = 10;
    final int PANDIR = 11;
    final int PANESQ = 12;
    final int GORDURA = 13;

    boolean flag_edit [];
    boolean flag_dialog = false;

    int pos = 0;
    int getid = 0;
    boolean update = false;

    LineChart grafico;

    public static String [] bufferteste = new String[1000]; //  VECTOR
    float resultado =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporal);


        grafico = (LineChart) findViewById(R.id.graficoID);


        btn_salvar = (Button) findViewById(R.id.button_salvar);
        editText_abdominal = (EditText) findViewById(R.id.editText_abdominal);
        editText_abdominal.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
        editText_pescoco = (EditText) findViewById(R.id.editText_pescoço);
        editText_pescoco.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
        editText_quadril = (EditText) findViewById(R.id.editText_quadril);
        editText_quadril.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_bicpesdir = (EditText) findViewById(R.id.editText_bd);
        editText_bicpesdir.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_bicepsesq = (EditText) findViewById(R.id.editText_be);
        editText_bicepsesq.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_coxadir = (EditText) findViewById(R.id.editText_cd);
        editText_coxadir.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_coxaesq = (EditText) findViewById(R.id.editText_ce);
        editText_coxaesq.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_torax = (EditText) findViewById(R.id.editText_torax);
        editText_torax.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_cintura = (EditText) findViewById(R.id.editText_cintura);
        editText_cintura.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_pantDir = (EditText) findViewById(R.id.editText_pan_dir);
        editText_pantDir.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_pantEsq = (EditText) findViewById(R.id.editText_pan_esq);
        editText_pantEsq.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_antbDir = (EditText) findViewById(R.id.editText_ant_dir);
        editText_antbDir.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_antbEsq = (EditText) findViewById(R.id.editText_ant_esq);
        editText_antbEsq.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});

        textView_resultado = (TextView) findViewById(R.id.textView_resultado_corporal);
        textView_data = (TextView) findViewById(R.id.textView_corporal_data);
        textView_data.setVisibility(View.INVISIBLE);
        textView_pesc = (TextView) findViewById(R.id.textView_pescoco);
        textView_torax = (TextView) findViewById(R.id.textView_torax);
        textView_cintura = (TextView) findViewById(R.id.textView_cintura);
        textView_abd = (TextView) findViewById(R.id.textView_abd);
        textView_quadril = (TextView) findViewById(R.id.textView_quadril);
        textView_bcps_dir = (TextView) findViewById(R.id.textView_bcps_dir);
        textView_bcps_esq = (TextView) findViewById(R.id.textView_bcps_esq);
        textView_ant_dir = (TextView) findViewById(R.id.textView_ant_dir);
        textView_ant_esq = (TextView) findViewById(R.id.textView_ant_esq);
        textView_coxa_dir = (TextView) findViewById(R.id.textView_coxa_dir);
        textView_coxa_esq = (TextView) findViewById(R.id.textView_coxa_esq);
        textView_pant_dir = (TextView) findViewById(R.id.textView_pant_dir);
        textView_pant_esq = (TextView) findViewById(R.id.textView_pant_esq);



        imageButton_corp_edit = (ImageButton) findViewById(R.id.imageButton_corporal_edit);
        imageButton_corp_info = (ImageButton) findViewById(R.id.imageButton_corporal_info2);


        helperCorporal = new CorporalResource(this);
        helperAluno = new AlunoResource(this);
        aluno = helperAluno.getData(Register.id_login);

        flag_edit = new boolean[13];        // flag para zerar uma vez os edit ao clicar
        for(int i = 0;i==12;i++){
            flag_edit[i] = false;
        }
        //######################################## EDIT SET ############################################

        // Text Pescoço
        editText_pescoco.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[0]==false) {
                        editText_pescoco.setText(""); //Do your work
                        flag_edit[0] = true;
                    }
                }
            }
        });

        // Text torax
        editText_torax.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[1]==false) {
                        editText_torax.setText(""); //Do your work
                        flag_edit[1] = true;
                    }
                }
            }
        });

        // Text cintura
        editText_cintura.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[2]==false) {
                        editText_cintura.setText(""); //Do your work
                        flag_edit[2] = true;
                    }
                }
            }
        });

        // abdomen
        editText_abdominal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[3]==false) {
                        editText_abdominal.setText(""); //Do your work
                        flag_edit[3] = true;
                    }
                }
            }
        });


        // quadril
        editText_quadril.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[4]==false) {
                        editText_quadril.setText(""); //Do your work
                        flag_edit[4] = true;
                    }
                }
            }
        });

        // Text bicpes
        editText_bicpesdir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[5]==false) {
                        editText_bicpesdir.setText(""); //Do your work
                        flag_edit[5] = true;
                    }
                }
            }
        });

        // Text biceps esq
        editText_bicepsesq.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[6]==false) {
                        editText_bicepsesq.setText(""); //Do your work
                        flag_edit[6] = true;
                    }
                }
            }
        });

        // Text ante dir
        editText_antbDir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[7]==false) {
                        editText_antbDir.setText(""); //Do your work
                        flag_edit[7] = true;
                    }
                }
            }
        });

        // Text ante esq
        editText_antbEsq.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[8]==false) {
                        editText_antbEsq.setText(""); //Do your work
                        flag_edit[8] = true;
                    }
                }
            }
        });

        // Text coxa
        editText_coxadir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[9]==false) {
                        editText_coxadir.setText(""); //Do your work
                        flag_edit[9] = true;
                    }
                }
            }
        });

        // Text coxa esq
        editText_coxaesq.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[10]==false) {
                        editText_coxaesq.setText(""); //Do your work
                        flag_edit[10] = true;
                    }
                }
            }
        });

        // Text pant dir
        editText_pantDir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[11]==false) {
                        editText_pantDir.setText(""); //Do your work
                        flag_edit[11] = true;
                    }
                }
            }
        });

        // Text pant esq
        editText_pantEsq.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    if(flag_edit[12]==false) {
                        editText_pantEsq.setText(""); //Do your work
                        flag_edit[12] = true;
                    }
                }
            }
        });



        // Text Pescoço
        textView_pesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_pesc);
                } catch (Exception e) {
                }
            }
        });
        // Text torax
        textView_torax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_torax);
                } catch (Exception e) {
                }
            }
        });

        // Text cintura
        textView_cintura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_cintura);
                } catch (Exception e) {
                }
            }
        });
        // Text abdomen
        textView_abd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_abdomen);
                } catch (Exception e) {
                }
            }
        });
        // Text quadril
        textView_quadril.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_quadril);
                } catch (Exception e) {
                }
            }
        });

        // Text biceps dir
        textView_bcps_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_biceps);
                } catch (Exception e) {
                }
            }
        });

        // Text biceps esq
        textView_bcps_esq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_biceps);
                } catch (Exception e) {
                }
            }
        });

        // Text antbraco dir
        textView_ant_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_ant);
                } catch (Exception e) {
                }
            }
        });

        // Text antbraço esq
        textView_ant_esq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_ant);
                } catch (Exception e) {
                }
            }
        });

        // Text coxa dir
        textView_coxa_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_coxa);
                } catch (Exception e) {
                }
            }
        });
        // Text coxa esq
        textView_coxa_esq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_coxa);
                } catch (Exception e) {
                }
            }
        });

        // Text pant. dir
        textView_pant_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_pant);
                } catch (Exception e) {
                }
            }
        });

        // Text pant. esq
        textView_pant_esq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.corp_pant);
                } catch (Exception e) {
                }
            }
        });



        //######################################### CALCULO FAT #########################################

        lcorporal_dados = helperCorporal.getData(Register.id_login);

        // Text Indice de Gordura
        textView_resultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if((lcorporal_dados.size()>0) && (lcorporal_dados!=null))
                        indiceGordura(resultado);
                } catch (Exception e) {
                }
            }
        });

        if(lcorporal_dados.size()<2){
            showCase("Imagem","Clique para ver as imagens de como medir corretamente!",textView_pesc,1);
        }

        if((lcorporal_dados.size()>0) && (lcorporal_dados!=null))
        {

            calculoGrafico(lcorporal_dados,PESCOCO);

            corporal = lcorporal_dados.get(lcorporal_dados.size()-1); // pega ultima atulizaÃ§Ã£o

            //######################## FORMULA NAVY US ########################
            float resultado = (float) ((495 / ((1.0324 - (0.19077 * (Math.log10(50)))) + (0.15456 * (Math.log10(178))))) - 450);
            //float resultado = (float) ((86.010 * (Math.log10(abdominal - pescoco) - 70.041 * (Math.log10(altura))))+ 36.76);

            DecimalFormat df = new DecimalFormat("0.00");
            textView_resultado.setText("Indice de Gordura: "+df.format((resultado)) + " %");
            editText_abdominal.setText(Float.toString(corporal.getAbdominal()));
            editText_pescoco.setText(Float.toString(corporal.getPescoco()));
            editText_quadril.setText(Float.toString(corporal.getQuadril()));
            editText_bicpesdir.setText(Float.toString(corporal.getBicepsDir()));
            editText_bicepsesq.setText(Float.toString(corporal.getBicepsEsq()));
            editText_coxadir.setText(Float.toString(corporal.getCoxaDir()));
            editText_coxaesq.setText(Float.toString(corporal.getCoxaEsq()));
            editText_torax.setText(Float.toString(corporal.getTorax()));
            editText_cintura.setText(Float.toString(corporal.getCintura()));
            editText_pantDir.setText(Float.toString(corporal.getPantDir()));
            editText_pantEsq.setText(Float.toString(corporal.getPantEsq()));
            editText_antbDir.setText(Float.toString(corporal.getAntbDir()));
            editText_antbEsq.setText(Float.toString(corporal.getAntbEsq()));

            calculoFat();
        }
        else
            Toast.makeText(getApplicationContext(), "Não possui nenhuma medição ou houve falha no cadastro!", Toast.LENGTH_SHORT).show();

        //Imagem de Calcular
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String currentTime = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                    String t1 = currentTime;
                    String t2 = editText_abdominal.getText().toString();
                    String t3 = editText_pescoco.getText().toString();
                    String t4 = editText_quadril.getText().toString();
                    String t5 = editText_bicpesdir.getText().toString();
                    String t6 = editText_bicepsesq.getText().toString();
                    String t7 = editText_coxadir.getText().toString();
                    String t8 = editText_coxaesq.getText().toString();
                    String t9 = String.valueOf(resultado);
                    if(t2.isEmpty()||t3.isEmpty()||t4.isEmpty()||t5.isEmpty()||t6.isEmpty()||t7.isEmpty()||t8.isEmpty()||t9.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "ERRO", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        corporal.setAbdominal(Float.parseFloat(editText_abdominal.getText().toString()));
                        corporal.setPescoco(Float.parseFloat(editText_pescoco.getText().toString()));
                        corporal.setQuadril(Float.parseFloat(editText_quadril.getText().toString()));
                        corporal.setBicepsDir(Float.parseFloat(editText_bicpesdir.getText().toString()));
                        corporal.setBicepsEsq(Float.parseFloat(editText_bicepsesq.getText().toString()));
                        corporal.setCoxaDir(Float.parseFloat(editText_coxadir.getText().toString()));
                        corporal.setCoxaEsq(Float.parseFloat(editText_coxaesq.getText().toString()));
                        corporal.setGordura(resultado);
                        corporal.setTorax(Float.parseFloat(editText_torax.getText().toString()));
                        corporal.setCintura(Float.parseFloat(editText_cintura.getText().toString()));
                        corporal.setPantDir(Float.parseFloat(editText_pantDir.getText().toString()));
                        corporal.setPantEsq(Float.parseFloat(editText_pantEsq.getText().toString()));
                        corporal.setAntbDir(Float.parseFloat(editText_antbDir.getText().toString()));
                        corporal.setAntbEsq(Float.parseFloat(editText_antbEsq.getText().toString()));
                        corporal.setId_login(Register.id_login);
                        corporal.setStatus("PENDENTE");

                        if (((corporal.getData()!=null) && (corporal.getData().equals(currentTime))) ||
                                ((corporal.getData()!=null) && update))
                        {
                            if (update) //flag de controle
                                update = false;

                            if (helperCorporal.updateName(corporal) >= 0) {
                                Toast.makeText(getApplicationContext(), "Alterado", Toast.LENGTH_SHORT).show();
                                textView_data.setText(currentTime);
                            }

                            else
                                Toast.makeText(getApplicationContext(), "Erro ao alterar o registro no banco local!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            corporal.setData(currentTime);

                            if (helperCorporal.insertData(corporal) >= 0)
                                Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    lcorporal_dados.clear();
                    lcorporal_dados = helperCorporal.getData(Register.id_login);
                    corporal = lcorporal_dados.get(lcorporal_dados.size()-1); // pega ultima atulizaÃ§Ã£o

                    if(lcorporal_dados.size()>1) {
                        calculoGrafico(lcorporal_dados,pos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                }
                //   recreate();
            }
        });

        editText_pescoco.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui vocÃª coloca o evento
                calculoFat();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_abdominal.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui vocÃª coloca o evento
                calculoFat();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_quadril.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui vocÃª coloca o evento
                calculoFat();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_cintura.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Aqui vocÃª coloca o evento
                calculoFat();

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

//###########################//SPINNER//##############################################//
        // Spinner element
        /*for fill your Spinner*/
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Pescoco");
        spinnerArray.add("Torax");
        spinnerArray.add("Cintura");
        spinnerArray.add("Abdomen");
        spinnerArray.add("Quadril");
        spinnerArray.add("Biceps Esq");
        spinnerArray.add("Biceps Dir");
        spinnerArray.add("Ante Br Dir");
        spinnerArray.add("Ante Br Esq");
        spinnerArray.add("Coxa Dir");
        spinnerArray.add("Coxa Esq");
        spinnerArray.add("Pan Dir");
        spinnerArray.add("Pan Esq");
        spinnerArray.add("Gordura");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //if (item != null) {
                //    Toast.makeText(getApplicationContext(), "Organizado", Toast.LENGTH_SHORT).show();
                // }
                lcorporal_dados = helperCorporal.getData(Register.id_login);

                if (position == PESCOCO){
                    Toast.makeText(getApplicationContext(), "Pescoço", Toast.LENGTH_SHORT).show();
                    pos=PESCOCO;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,PESCOCO);
                }
                if (position == TORAX){
                    Toast.makeText(getApplicationContext(), "Torax", Toast.LENGTH_SHORT).show();
                    pos=TORAX;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,TORAX);
                }
                if (position == CINTURA) {
                    Toast.makeText(getApplicationContext(), "Cintura", Toast.LENGTH_SHORT).show();
                    pos=CINTURA;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,CINTURA);
                }
                if (position == ABDOMINAL){
                    Toast.makeText(getApplicationContext(), "Abdomen", Toast.LENGTH_SHORT).show();
                    pos=ABDOMINAL;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,ABDOMINAL);
                }
                if (position == QUADRIL){
                    Toast.makeText(getApplicationContext(), "Quadril", Toast.LENGTH_SHORT).show();
                    pos=QUADRIL;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,QUADRIL);
                }
                if (position == BICEPSDIR){
                    Toast.makeText(getApplicationContext(), "Biceps Dir", Toast.LENGTH_SHORT).show();
                    pos=BICEPSDIR;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,BICEPSDIR);
                }
                if (position == BICEPSESQ){
                    Toast.makeText(getApplicationContext(), "Biceps Esq", Toast.LENGTH_SHORT).show();
                    pos=BICEPSESQ;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,BICEPSESQ);
                }
                if (position == ANTDIR){
                    Toast.makeText(getApplicationContext(), "Ante Braço Dir", Toast.LENGTH_SHORT).show();
                    pos=ANTDIR;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,ANTDIR);
                }
                if (position == ANTESQ){
                    Toast.makeText(getApplicationContext(), "Ante Braço Esq", Toast.LENGTH_SHORT).show();
                    pos=ANTESQ;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,ANTESQ);
                }
                if (position == COXADIR){
                    Toast.makeText(getApplicationContext(), "Coxa Dir", Toast.LENGTH_SHORT).show();
                    pos=COXADIR;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,COXADIR);
                }
                if (position == COXAESQ){
                    Toast.makeText(getApplicationContext(), "Coxa Esq", Toast.LENGTH_SHORT).show();
                    pos=COXAESQ;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,COXAESQ);
                }
                if (position == PANDIR){
                    Toast.makeText(getApplicationContext(), "Panturrilha Dir", Toast.LENGTH_SHORT).show();
                    pos=PANDIR;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,PANDIR);
                }
                if (position == PANESQ){
                    Toast.makeText(getApplicationContext(), "Panturrilha Esq", Toast.LENGTH_SHORT).show();
                    pos=PANESQ;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,PANESQ);
                }
                if (position == GORDURA){
                    Toast.makeText(getApplicationContext(), "Gordura", Toast.LENGTH_SHORT).show();
                    pos=GORDURA;
                    if (lcorporal_dados.size()>1)
                        calculoGrafico(lcorporal_dados,GORDURA);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

        //###################### ALERTA DIALOG ######################

        //Imagem de info
        imageButton_corp_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editMedidas();
                } catch (Exception e) {
                }
            }
        });

        //###################### INFO ######################

        imageButton_corp_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Corporal.this)
                            .setTitle("Ajuda")
                            .setMessage("Medidas Antropométricas"+ "\n"+ "\n" + "Utilize uma fita métrica para medir algumas circuferências listadas !!" + "\n" + "\n"+ "Agora vc acompanha sua evolução de verdade!!"+ "\n"+"\n"+ "Duvida? Clique no texto da circuferência.")
                            .show();
                } catch (Exception e) {
                }
            }
        });
    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void calculoFat()
    {
        if (editText_pescoco.getText().toString().isEmpty()||editText_abdominal.getText().toString().isEmpty()||editText_cintura.getText().toString().isEmpty()||editText_quadril.getText().toString().isEmpty()) {}

        else {
            try {
                float abdominal = Float.parseFloat(editText_abdominal.getText().toString());
                float pescoco = Float.parseFloat(editText_pescoco.getText().toString());
                float cintura = Float.parseFloat(editText_cintura.getText().toString());
                float quadril = Float.parseFloat(editText_quadril.getText().toString());
                float altura = Float.parseFloat(aluno.getAltura());

                if(aluno.getSexo().equals("M")) { // HOMEM
                    resultado = (float) (((495 / (1.0324 - (0.19077 * (Math.log10(abdominal - pescoco))) + 0.15456 * (Math.log10(altura)))) - 450));
                }
                else{ //MULHER
                    resultado = (float) (((495 / (1.29579 - (0.35004 * (Math.log10(cintura + quadril - pescoco))) + 0.22100 * (Math.log10(altura)))) - 450));
                }
                resultado = (float) (Math.round(resultado*10.0)/10.0);

                if(resultado>0) {
                    if(flag_dialog==true) {
                        // indiceGordura(resultado);
                    }
                    flag_dialog=true;
                    DecimalFormat df = new DecimalFormat("0.00");
                    textView_resultado.setText("Indice de Gordura: " + df.format((resultado)) + " %");
                }else{
                    textView_resultado.setText("Medida incorreta");
                }
            } catch (NumberFormatException e) { //IMPORTANTISSIMO
                e.printStackTrace();
            }
        }
    }

    //############################ GRAFICO ############################

    public void calculoGrafico(List<CorporalModel> lcorpo, int type){

        grafico = (LineChart) findViewById(R.id.graficoID);
        List<Entry> entradaGrafico = new ArrayList<>();
        List<String> entradaGraficox = new ArrayList<>();

        String legend = "Corporal (cm)";

        int i_=0;
        for (int i = 0; i < (lcorpo.size()); i++)
        {
            i_=i;
            switch (type)
            {
                case PESCOCO: //pescoÃ§o
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getPescoco()));
                    legend = "Pescoço (cm)";
                    break;
                case TORAX: //torax
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getTorax()));
                    legend = "Torax (cm)";
                    break;
                case CINTURA: //cintura
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getCintura()));
                    legend = "Cintura (cm)";
                    break;
                case QUADRIL: //quadril
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getQuadril()));
                    legend = "Quadril (cm)";
                    break;
                case ABDOMINAL: //abdomen
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getAbdominal()));
                    legend = "Abdomen (cm)";
                    break;
                case BICEPSDIR: //biceps dir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getBicepsDir()));
                    legend = "Biceps Direita (cm)";
                    break;
                case BICEPSESQ: //bicepsEsq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getBicepsEsq()));
                    legend = "Biceps Esq (cm)";
                    break;
                case ANTDIR: //ante braÃ§o dir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getAntbDir()));
                    legend = "Ante braço direito (cm)";
                    break;
                case ANTESQ: //ante braÃ§o esq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getAntbEsq()));
                    legend = "Ante braço esquerdo (cm)";
                    break;
                case COXADIR: //coxa dir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getCoxaDir()));
                    legend = "Coxa direita (cm)";
                    break;
                case COXAESQ: //coxa esq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getCoxaEsq()));
                    legend = "Coxa esquerda (cm)";
                    break;
                case PANDIR: //panturrilha dir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getPantDir()));
                    legend = "Panturrilha Direita (cm)";
                    break;
                case PANESQ: //panturrilha esq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getPantEsq()));
                    legend = "Panturrilha Esquerda (cm)";
                    break;
                case GORDURA: //gordura
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getGordura()));
                    legend = "Gordura (%)";
                    break;

                default:
                    break;
            }
            entradaGraficox.add(lcorpo.get(i).getData());
        }


        LineDataSet dataSet = new LineDataSet(entradaGrafico, legend);
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
        LineData lineData = new LineData(dataSet);


        YAxis yAxis_0;
        {   // // Y-Axis Style // //
            yAxis_0 = grafico.getAxisLeft();
            // disable dual axis (only use LEFT axis)
            grafico.getAxisRight().setEnabled(false);
            grafico.getAxisLeft().setEnabled(false);
            // horizontal grid lines
            // axis range
        }

        XAxis xAxis_0;
        {   // // X-Axis Style // //
            xAxis_0 = grafico.getXAxis();
            grafico.getXAxis().setEnabled(true);
            // vertical grid lines
            //   xAxis_0.enableGridDashedLine(10f, 10f, 0f);
            //  List<String> xVals = entradaGraficox;
            xAxis_0.setValueFormatter(new IndexAxisValueFormatter(entradaGraficox));
            // xAxis_0.setGranularityEnabled(false);
            xAxis_0.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis_0.setDrawGridLines(false);
            xAxis_0.setGranularity(1f);
            xAxis_0.setDrawLabels(true);
            xAxis_0.setDrawAxisLine(false);
            //  xAxis_0.setGranularity(2f);
            //  xAxis_0.setDrawGridLines(true);
            xAxis_0.setAxisMaximum(i_);
            xAxis_0.setLabelRotationAngle(0);

        }

        // enable scaling and dragging
        //  grafico.setDragEnabled(true);
        // grafico.setScaleEnabled(true);
        grafico.setScaleXEnabled(true);
        grafico.setScaleYEnabled(true);
        grafico.setVisibleXRangeMaximum(5);
        grafico.moveViewToX(i_);
        grafico.getDescription().setEnabled(false);
        // background color
        grafico.setBackgroundColor(Color.WHITE);
        grafico.setData(lineData);
        //grafico.setPinchZoom(true);
        grafico.refreshDrawableState();
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

    public void editMedidas()
    {
        List<String> spinnerArray_data = new ArrayList<String>();
        lcorporal_dados = helperCorporal.getData(Register.id_login);

        for (int i = 0; i < (lcorporal_dados.size()); i++)
        {
            spinnerArray_data.add( lcorporal_dados.get(i).getData());
        }
//        for (CorporalModel comp:lcorporal_dados)
//        {
//            spinnerArray_data.add(corporal.getData());
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Corporal.this);
        //Cria a view a ser utilizada no dialog
        View mView  = Corporal.this.getLayoutInflater().inflate(R.layout.spinner_aux,null);
        //ObtÃ©m uma referencia ao Spinner
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);
        //Cria o Adapter
        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(Corporal.this,
                android.R.layout.simple_spinner_item, spinnerArray_data); // Chamar o ARRAY
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Atribui o adapter ao spinner
        mSpinner.setAdapter(adapter_1);
        builder.setTitle("Escolha a data");
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getid = mSpinner.getSelectedItemPosition();
                //lcorporal_dados = helperCorporal.searchData(data);
                if (lcorporal_dados.size()>0) {
                    corporal_edit = lcorporal_dados.get(getid); // pega ultima atulizaÃ§Ã£o
                    corporal.setData(lcorporal_dados.get(getid).getData()); //salva a data para alterar
                    corporal.setId_corporal(lcorporal_dados.get(getid).getId_corporal());
                    DecimalFormat df = new DecimalFormat("0.00");
                    textView_resultado.setText("Indice de Gordura: " + df.format((resultado)) + " %");
                    editText_abdominal.setText(Float.toString(corporal_edit.getAbdominal()));
                    editText_pescoco.setText(Float.toString(corporal_edit.getPescoco()));
                    editText_quadril.setText(Float.toString(corporal_edit.getQuadril()));
                    editText_bicpesdir.setText(Float.toString(corporal_edit.getBicepsDir()));
                    editText_bicepsesq.setText(Float.toString(corporal_edit.getBicepsEsq()));
                    editText_coxadir.setText(Float.toString(corporal_edit.getCoxaDir()));
                    editText_coxaesq.setText(Float.toString(corporal_edit.getCoxaEsq()));
                    editText_torax.setText(Float.toString(corporal_edit.getTorax()));
                    editText_cintura.setText(Float.toString(corporal_edit.getCintura()));
                    editText_pantDir.setText(Float.toString(corporal_edit.getPantDir()));
                    editText_pantEsq.setText(Float.toString(corporal_edit.getPantEsq()));
                    editText_antbDir.setText(Float.toString(corporal_edit.getAntbDir()));
                    editText_antbEsq.setText(Float.toString(corporal_edit.getAntbEsq()));
                    textView_data.setVisibility(View.VISIBLE);
                    textView_data.setText(lcorporal_dados.get(getid).getData());
                    calculoFat();
                }
                update = true;
                Toast.makeText(getApplicationContext(),  "Cliked OK!", Toast.LENGTH_SHORT);
                // corporal = lcorporal_dados.get(Integer.parseInt(data)); // pega ultima atulizaÃ§Ã£o
                return;
            }
        });
        builder.show();
    }

    public void editMedidas_(){

    }

    class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 0) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");// alterei p/ 4 casas
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void imagePescoco(int img)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Corporal.this);

        View mView  = Corporal.this.getLayoutInflater().inflate(R.layout.dialog_img_pesc,null);
        final ImageView subImageView = (ImageView)mView.findViewById(R.id.imageView_dialog_pesc);
        Drawable drawable = getResources().getDrawable(img);
        subImageView.setImageDrawable(drawable);

        //cabeçalho da popup
        builder.setTitle("Como medir");
        builder.setView(mView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    public void indiceGordura(float resultado)
    {
        long idade = aluno.getIdade();
        int indice=0;
        String txt = "Indice";
        if(aluno.getSexo().equals("M")) {
            if (idade <= 25) {
                indice = 13;
            }
            if (idade <= 35 && idade >= 26) {
                indice = 18;
            }
            if (idade <= 45 && idade >= 36) {
                indice = 21;
            }
            if (idade <= 55 && idade >= 46) {
                indice = 23;
            }
            if (idade >= 56) {
                indice = 23;
            }
//            if (resultado<=indice){
//                txt = "Parabéns seu indice de gordura esta abaixo do ideal"
//            }
        }
        else {
            if (idade <= 25) {
                indice = 22;
            }
            if (idade <= 35 && idade >= 26) {
                indice = 23;
            }
            if (idade <= 45 && idade >= 36) {
                indice = 26;
            }
            if (idade <= 55 && idade >= 46) {
                indice = 28;
            }
            if (idade >= 56) {
                indice = 29;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Corporal.this);
        View mView  = Corporal.this.getLayoutInflater().inflate(R.layout.dialog_gordura,null);
        final TextView textView_atual = (TextView) mView.findViewById(R.id.textView_dialog_gord);
        final TextView textView_ideal = (TextView) mView.findViewById(R.id.textView_dialog_atual);
        final SpeedView progressiveGauge_= (SpeedView) mView.findViewById(R.id.progressiveGauge_gor);
        // change speed to 50 Km/h

        progressiveGauge_.setStartDegree(180);
        progressiveGauge_.setEndDegree(360);
        progressiveGauge_.speedPercentTo((int) resultado);
        progressiveGauge_.setTrembleData(0, 0);
        progressiveGauge_.setUnit("");
        progressiveGauge_.setSpeedTextColor(Color.TRANSPARENT);


        DecimalFormat df = new DecimalFormat("0.00");
        textView_atual.setText("Indice de gordura atual: " + df.format((resultado)) + " %");
        textView_ideal.setText("Indice de gordura ideal: " + indice + " %");
        //cabeçalho da popup
        builder.setTitle("Indice de Gordura");
        builder.setView(mView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    public void showCase(String title,String subtitle,View view, final int type ){
        new GuideView.Builder(Corporal.this)
                .setTitle(title)
                .setContentText(subtitle)
                .setGravity(Gravity.auto) //optional
                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
                .setTargetView(view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        if (type == 1) {
                            showCase("Medidas","Adicione suas medidas utilizando um fita métrica",editText_pescoco,2);
                        } else if (type == 2) {
                            showCase("Editar", "Escolha a data e edit suas medidas!", imageButton_corp_edit, 3);
                        } else if (type == 3) {
                            showCase("Info","Clique para mais informações!",imageButton_corp_info,4);
                        } else if (type == 4) {
                            //showCase("Analise", "Analise seus resultados", grafico, 5);
                        } else if (type == 5) {
                            // showCase("Overlay", "Add your selected overlay effect on your video ", R.id.button_tool_overlay, 5);
                        }
                    }
                })
                .build()
                .show();
    }

}
