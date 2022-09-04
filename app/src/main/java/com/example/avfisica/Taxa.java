package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.TaxaModel;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Peso;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.TaxaResource;
import com.example.avfisica.resources.CorporalResource;
import com.example.avfisica.resources.PesoResource;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Taxa extends AppCompatActivity {

    EditText editText_dias, editText_kilo, editText_peso, editText_kcal;
    TextView textView_imc, textView_resultado_tmb, textView_descanso, textView_treino, textView_dash_pro, textView_dash_gor, textView_dash_carb, textView_dash_seek_pro, textView_dash_seek_gor,textView_dash_seek_carb;
    CheckBox checkBox_descanso, checkBox_treino;
    RadioButton radioButton_ect, radioButton_mes, radioButton_end, radioButton_perder, radioButton_manter, radioButton_ganhar;
    ImageButton imageButton_taxa_info, imageButton_taxa_info2, imageButton_taxa_info3;
    Button btn_salvar;
    BottomNavigationView bottomNavigation;
    ImageView imageView_macro_alert;
    Switch switch_taxa;

    Float resultado, taxa_treino, taxa_descanso;
    double tbm = 1.1;
    int obj = 1;
    int seekBarValue = 5, seekBarValue_2 = 5, seekBarValue_3 = 10;
    String tipo = "mesomorfo",option = "perder";

    CorporalResource helperCorporal;
    CorporalModel corporal = new CorporalModel();
    List<CorporalModel> lcorporal_dados = new ArrayList<CorporalModel>();

    PesoResource helperPeso;
    Peso peso = new Peso();
    List<Peso> lpeso_dados = new ArrayList<Peso>();
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();


    TaxaResource helpertaxa;
    TaxaModel taxa = new TaxaModel();
    List<TaxaModel> ltaxa_dados = new ArrayList<TaxaModel>();
    TaxaModel taxa_edit = new TaxaModel();

    PieChart grafico_pizza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxa_metabolica);

        editText_kilo = (EditText) findViewById(R.id.textview_kilo);
        editText_kilo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_dias = (EditText) findViewById(R.id.textview_kcal_dias);
        editText_dias.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_kcal = (EditText) findViewById(R.id.textview_kcal);
        editText_kcal.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});

        radioButton_ect = (RadioButton) findViewById(R.id.radioButton_ect);
        radioButton_mes = (RadioButton) findViewById(R.id.radioButton_mes);
        radioButton_end = (RadioButton) findViewById(R.id.radioButton_end);
        radioButton_mes.setChecked(true);
        radioButton_perder = (RadioButton) findViewById(R.id.radioButton_perder);
        radioButton_manter = (RadioButton) findViewById(R.id.radioButton_manter);
        radioButton_ganhar = (RadioButton) findViewById(R.id.radioButton_ganhar);

        textView_resultado_tmb = (TextView) findViewById(R.id.textView_resultado_tmb);
        textView_treino = (TextView) findViewById(R.id.textView_treino);
        textView_descanso = (TextView) findViewById(R.id.textView_descanso);
        textView_dash_pro = (TextView) findViewById(R.id.textView_dash_pro);
        textView_dash_gor = (TextView) findViewById(R.id.textView61_dash_gor);
        textView_dash_carb = (TextView) findViewById(R.id.textView62_dash_carb);
        textView_dash_seek_carb = (TextView) findViewById(R.id.textView_dash_seek_carb);

        imageButton_taxa_info = (ImageButton) findViewById(R.id.imageButton_taxa_info);
        imageButton_taxa_info2 = (ImageButton) findViewById(R.id.imageButton_taxa_info2);
        imageButton_taxa_info3 = (ImageButton) findViewById(R.id.imageButton_taxa_info3);

        checkBox_descanso = (CheckBox) findViewById(R.id.checkBox_descanso);
        checkBox_descanso.setChecked(true);
        checkBox_treino = (CheckBox) findViewById(R.id.checkBox_treino);

        imageView_macro_alert = (ImageView) findViewById(R.id.imageView_macro_alert);
        imageView_macro_alert.setVisibility(View.INVISIBLE);

        btn_salvar = (Button) findViewById(R.id.button_salvar_taxa);

        grafico_pizza = (PieChart) findViewById(R.id.grafico_pizza_macro);

        helperAluno = new AlunoResource(this);
        aluno = helperAluno.getData(Register.id_login);
        helpertaxa = new TaxaResource(this);
        ltaxa_dados = helpertaxa.getData(Register.id_login);

//########################## PESO ##########################
        helperPeso = new PesoResource(this);
        lpeso_dados = helperPeso.getData(Register.id_login);

        // SETTING VARIAVEIS -> PUXANDO DO BANCO
        if (ltaxa_dados.size()>0) {
            taxa = ltaxa_dados.get(ltaxa_dados.size() - 1); // pega ultima atulizaÃ§Ã£o
            editText_kilo.setText(Float.toString (taxa.getPeso()));
            editText_dias.setText((Integer.toString((int) taxa.getTempo())));
            editText_kcal.setText(Float.toString(taxa.getGasto_treino()));
            seekBarValue = (int) taxa.getProteina();
            seekBarValue_2 = (int) taxa.getGordura();
            seekBarValue_3 = (int) taxa.getCarboidrato();
            if (taxa.getTipo().equals("ectomorfo")){radioButton_ect.setChecked(true); tbm=1.2;}
            if (taxa.getTipo().equals("mesomorfo")){radioButton_mes.setChecked(true); tbm=1.1;}
            if (taxa.getTipo().equals("endomorfo")){radioButton_end.setChecked(true); tbm=1;}
            if(taxa.getOption()>0){
                obj = (int) taxa.getOption();
                if (taxa.getOption() == 1){ // perder
                    radioButton_perder.setChecked(true);
                }
                if (taxa.getOption() == 2){ //manter
                    radioButton_manter.setChecked(true);
                }
                if (taxa.getOption() == 3){ // ganhar
                    radioButton_ganhar.setChecked(true);
                }
            }}
        else{
            if (lpeso_dados.size()>0)
                peso = lpeso_dados.get(lpeso_dados.size()-1); // pega ultima atulização

            if (peso.getPeso()>peso.getObjetivo()){
                option = "perder";
                obj = 1;
                radioButton_perder.setChecked(true);
            }else {
                option = "ganhar";
                obj=3;
                radioButton_ganhar.setChecked(true);
            }
            if (peso.getPeso()==peso.getObjetivo()){
                option = "manter";
                obj=2;
                radioButton_manter.setChecked(true);
            }
        }

//########################## CORPORAL ##########################

        helperCorporal = new CorporalResource(Taxa.this);
        lcorporal_dados = helperCorporal.getData(Register.id_login);
        if(lcorporal_dados.size()>0) {
            corporal= lcorporal_dados.get(lcorporal_dados.size()-1); // pega ultima atulização
            calculaTmb();
        }

        //########### CALL METODOS ##########
        calculaTmb();
        calcularKcal();
        calculoMacro();

        //Imagem de info
        imageButton_taxa_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imagePescoco(R.drawable.ectomorfo);
//                    new AlertDialog.Builder(Taxa.this)
//                            .setTitle("Ajuda")
//                            .setMessage("Ectomorfo  = Facilidade para emagrecer"+ "\n"+ "\n" + "Mesomorfo = Normal" + "\n" + "\n"+ "Endomorfo = Dificuldade para emagrecer")
//                            .show();
                } catch (Exception e) {
                }
            }
        });

        //Imagem de info !
        imageView_macro_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Taxa.this)
                            .setTitle("Ajuda")
                            .setMessage("A distribuição não está correta!")
                            .show();
                } catch (Exception e) {
                }
            }
        });

        //Btn Salvar
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String currentTime = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
                    // helpertaxa.updateName(taxa);
                    taxa.setData(currentTime);
                    taxa.setTipo(tipo);
                    taxa.setPeso(Float.parseFloat(editText_kilo.getText().toString()));
                    taxa.setTempo(Float.parseFloat(editText_dias.getText().toString()));
                    taxa.setGasto_treino(Float.parseFloat(editText_kcal.getText().toString()));
                    taxa.setOption(obj);
                    taxa.setProteina(seekBarValue);
                    taxa.setGordura(seekBarValue_2);
                    taxa.setCarboidrato(seekBarValue_3);
                    taxa.setId_login(Register.id_login);
                    taxa.setStatus("PENDENTE");

                    if (ltaxa_dados.size()>0) {
                        taxa.setId_taxa(taxa.getId_taxa());
                    }



                    // ######################################## UPDATE #################################################
                    if (helpertaxa.updateName(taxa) > 0) {
                        Toast.makeText(getApplicationContext(), "Alterado", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // ######################################### INSERT ################################################
                        if (taxa.getData()!=null){
                            if (helpertaxa.insertData(taxa) >= 0)
                                Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro - exception", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Imagem de info2
        imageButton_taxa_info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Taxa.this)
                            .setTitle("Ajuda")
                            .setMessage("->Defina quanto kilos perder ou ganhar!"+ "\n"+ "\n" + "->Em quanto tempo!" + "\n" + "\n"+ "->E quantas calorias você gasta em treino!")
                            .show();
                } catch (Exception e) {
                }
            }
        });



        //Imagem de info3
        imageButton_taxa_info3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Taxa.this)
                            .setTitle("Ajuda")
                            .setMessage("Defina a proporção de Proteina, Gordura e Carboidrato"+ "\n"+ "\n" + "Basta alterar para o valor desejado!" + "\n" + "\n"+ "Bora ajustar a dieta!")
                            .show();
                } catch (Exception e) {
                }
            }
        });


        final RadioGroup radio = (RadioGroup) findViewById(R.id.radioGroup2);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button
                        tipo = "ectomorfo";
                        tbm = 1.2;
                        //Toast.makeText(getApplicationContext(), "Ectomorfo", Toast.LENGTH_SHORT).show();
                        //########### CALL METODOS ##########
                        calculaTmb();
                        calcularKcal();
                        if(lcorporal_dados.size()>0) {
                            calculoMacro();
                        }
                        break;

                    case 1: // secondbutton
                        tipo = "mesomorfo";
                        tbm = 1.1;
                        //Toast.makeText(getApplicationContext(), "Mesomorfo", Toast.LENGTH_SHORT).show();
                        //########### CALL METODOS ##########
                        calculaTmb();
                        calcularKcal();
                        if(lcorporal_dados.size()>0) {
                            calculoMacro();
                        }
                        break;

                    case 2: // Thirdbutton
                        tipo = "endomorfo";
                        tbm = 1;
                        //Toast.makeText(getApplicationContext(), "Endomorfo", Toast.LENGTH_SHORT).show();
                        //########### CALL METODOS ##########
                        calculaTmb();
                        calcularKcal();
                        if(lcorporal_dados.size()>0) {
                            calculoMacro();
                        }
                        break;
                }
            }
        });


        final RadioGroup radio_ = (RadioGroup) findViewById(R.id.radioGroup);
        radio_.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio_.findViewById(checkedId);
                int index = radio_.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button
                        option = "perder";
                        obj = 1;
                        // Toast.makeText(getApplicationContext(), "Perder", Toast.LENGTH_SHORT).show();
                        calcularKcal();
                        calculoMacro();
                        break;

                    case 1: // secondbutton
                        option = "manter";
                        obj = 2;
                        // Toast.makeText(getApplicationContext(), "Manter", Toast.LENGTH_SHORT).show();
                        calcularKcal();
                        calculoMacro();
                        break;

                    case 2: // Thirdbutton
                        option = "ganhar";
                        obj = 3;
                        //Toast.makeText(getApplicationContext(), "Ganhar", Toast.LENGTH_SHORT).show();
                        calcularKcal();
                        calculoMacro();
                        break;
                }
            }
        });


        editText_kcal.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // EVENTO
                calcularKcal();
                calculoMacro();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_dias.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // EVENTO
                calcularKcal();
                calculoMacro();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        editText_kilo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // EVENTO
                calcularKcal();
                calculoMacro();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Text Pro
        textView_dash_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog();
                } catch (Exception e) {
                }
            }
        });

        // Text Gord
        textView_dash_gor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog();
                } catch (Exception e) {
                }
            }
        });

        // Text Carb
        textView_dash_carb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog();
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

        // Text Carb
        textView_dash_seek_carb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog();
                } catch (Exception e) {
                }
            }
        });

        checkBox_descanso.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkBox_descanso.isChecked())
                {
                    checkBox_treino.setChecked(false);
                    calculoMacro();
                    //Perform action when you touch on checkbox and it change to selected state
                }
                else
                {
                    //Perform action when you touch on checkbox and it change to unselected state
                }
            }
        });

        checkBox_treino.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkBox_treino.isChecked())
                {
                    checkBox_descanso.setChecked(false);
                    calculoMacro();
                    //Perform action when you touch on checkbox and it change to selected state
                }
                else
                {
                    //Perform action when you touch on checkbox and it change to unselected state
                }
            }
        });



    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void calculaTmb() {

        float altura = (Float.parseFloat (aluno.getAltura()));
        float peso = (Float.parseFloat(String.valueOf(aluno.getPeso())));
        float idade = (Float.parseFloat(Long.toString(aluno.getIdade())));
        String sexo = String.valueOf(aluno.getSexo());

        if (aluno.getSexo().equals("M")){ //Homem
            resultado = (float) ((66 + (13.7 * peso) + (5 * altura) - (6.8 * idade))*tbm); //FORMULA
        }
        else { //Mulher
            resultado = (float) ((665 + (9.6 * peso) + (1.8 * altura) - (4.7 * idade))*tbm); //FORMULA
        }

        DecimalFormat df = new DecimalFormat("0.0");
        textView_resultado_tmb.setText("TMB = "+df.format(resultado) + " kcal");
    }


    public void calcularKcal(){

        if (((editText_kcal.getText().toString().isEmpty())) || (editText_kilo.getText().toString().isEmpty()) || (editText_dias.getText().toString().isEmpty())) {}
        else{
            try {
                float kcal = (Float.parseFloat(editText_kcal.getText().toString()));
                float perder = (Float.parseFloat(editText_kilo.getText().toString()));
                float dias = (Float.parseFloat(editText_dias.getText().toString()));

                switch (obj) {
                    case 1: // perder
                        float result = resultado - ((perder * 7700) / dias);
                        taxa_treino = result + kcal;
                        taxa_descanso = result;
                        DecimalFormat df = new DecimalFormat("0.0");
                        textView_treino.setText("Treino:  " + df.format((result + kcal)) + " kcal");
                        textView_descanso.setText("Descanso:  " + df.format((result)) + " kcal");
                        break;
                    case 2: //manter
                        taxa_treino = resultado + kcal;
                        taxa_descanso = resultado;
                        DecimalFormat df_ = new DecimalFormat("0.0");
                        textView_treino.setText("Treino:  " + df_.format((resultado + kcal)) + " kcal");
                        textView_descanso.setText("Descanso:  " + df_.format((resultado)) + " kcal");
                        break;
                    case 3: //ganhar
                        result = resultado + ((perder * 7700) / dias);
                        taxa_treino = result + kcal;
                        taxa_descanso = result;
                        DecimalFormat dff = new DecimalFormat("0.0");
                        textView_treino.setText("Treino:  " + dff.format(((result + kcal))) + " kcal");
                        textView_descanso.setText("Descanso:  " + dff.format((result)) + " kcal");
                        break;
                }
            }catch (NumberFormatException e) { //IMPORTANTISSIMO
                e.printStackTrace();
            }
        }
    }

    public void calculoMacro(){
        try {
            if (editText_kilo.getText().toString().isEmpty() || editText_dias.getText().toString().isEmpty() || editText_kcal.getText().toString().isEmpty()) {
            } else {
                float resultado_;
                if (checkBox_treino.isChecked()) {
                    resultado_ = taxa_treino;
                } else resultado_ = taxa_descanso;

                textView_dash_seek_carb.setText("Proteina = " + seekBarValue * 5 + "%    " + "Gordura = " + seekBarValue_2 * 5 + "%    " + "Carboidrato = " + seekBarValue_3 * 5 + "%");
                //float magra = (aluno.getPeso()-(aluno.getPeso()*((corporal.getGordura()/100)+1)-aluno.getPeso()));
                float pro = (((1 + (float) (seekBarValue * 5) / 100) * resultado_) - resultado_) / 4;
                float gor = (((1 + (float) (seekBarValue_2 * 5) / 100) * resultado_) - resultado_) / 9;
                float carb = (((1 + (float) (seekBarValue_3 * 5) / 100) * resultado_) - resultado_) / 4;
                DecimalFormat df = new DecimalFormat("0.0");

                int calc = seekBarValue * 5 + seekBarValue_2 * 5 + seekBarValue_3 * 5;
                if (calc == 100) {
                    //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                    textView_dash_pro.setTextColor(Color.parseColor("#1B6BB3"));
                    textView_dash_gor.setTextColor(Color.parseColor("#1B6BB3"));
                    textView_dash_carb.setTextColor(Color.parseColor("#1B6BB3"));
                    imageView_macro_alert.setVisibility(View.INVISIBLE);

                } else {
                    //Toast.makeText(getApplicationContext(), "A distribuição não está correta", Toast.LENGTH_SHORT).show();
                    textView_dash_pro.setTextColor(Color.RED);
                    textView_dash_gor.setTextColor(Color.RED);
                    textView_dash_carb.setTextColor(Color.RED);
                    imageView_macro_alert.setVisibility(View.VISIBLE);

                }
                textView_dash_pro.setText("Proteina: " + df.format(pro) + "g");
                textView_dash_gor.setText("Gordura: " + df.format(gor) + "g");
                textView_dash_carb.setText("Carboidrato: " + df.format(carb) + "g");
                calculoGrafico_massa();
            }
        }catch (NumberFormatException e) { //IMPORTANTISSIMO
            e.printStackTrace();
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

    private void showPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_macro, null);
        builder.setView(view);
        //builder.setTitle();
        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.picker_first);
        picker.setMinValue(0);
        picker.setMaxValue(20);
        picker.setDisplayedValues(new String[]{"0", "5", "10", "15","20", "25", "30", "35","40", "45", "50", "55","60", "65", "70", "75","80", "85", "90", "95","100"});
        picker.setValue(seekBarValue);

        final NumberPicker picker_2 = (NumberPicker) view.findViewById(R.id.picker_second);
        picker_2.setMinValue(0);
        picker_2.setMaxValue(20);
        picker_2.setDisplayedValues(new String[]{"0", "5", "10", "15","20", "25", "30", "35","40", "45", "50", "55","60", "65", "70", "75","80", "85", "90", "95","100"});
        picker_2.setValue(seekBarValue_2);

        final NumberPicker picker_3 = (NumberPicker) view.findViewById(R.id.picker_second2);
        picker_3.setMinValue(0);
        picker_3.setMaxValue(20);
        picker_3.setDisplayedValues(new String[]{"0", "5", "10", "15","20", "25", "30", "35","40", "45", "50", "55","60", "65", "70", "75","80", "85", "90", "95","100"});
        picker_3.setValue(seekBarValue_3);

//        picker.setValue(Integer.parseInt(editText_peso.getText().toString()));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //positive button action
                String Pr = String.valueOf(picker.getValue());
                String Gor = String.valueOf(picker_2.getValue());
                String Carb = String.valueOf(picker_3.getValue());
                seekBarValue = Integer.parseInt(Pr);
                seekBarValue_2 = Integer.parseInt(Gor);
                seekBarValue_3 = Integer.parseInt(Carb);
//                textView_dash_seek_pro.setText("Proteina = " + Pr+"%");
//                textView_dash_seek_gor.setText("Gordura = " + Gor+"%");
//                textView_dash_seek_carb.setText("Carboidrato = " + Carb+"%");
                calculoMacro();

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


    //############################ GRAFICO MASSA ############################

    public void calculoGrafico_massa(){

        grafico_pizza = (PieChart) findViewById(R.id.grafico_pizza_macro);
        List<PieEntry> entradaGrafico = new ArrayList<>();

        entradaGrafico.add(new PieEntry(seekBarValue*5,"Proteina (%)"));
        entradaGrafico.add(new PieEntry(seekBarValue_2*5, "Gordura (%)"));
        entradaGrafico.add(new PieEntry(seekBarValue_3*5, "Carboidrato (%)"));

        PieDataSet dataSet = new PieDataSet(entradaGrafico, "");
        //dataSet.setColors(new int[]{Color.rgb(220, 0, 0), Color.rgb(0, 0, 220), Color.rgb(0, 220, 0), Color.rgb(255, 255, 0), Color.rgb(220, 0, 0)});
        dataSet.setColors(new int [] {Color.rgb(51, 128, 255), Color.rgb(249, 100, 114), Color.rgb(3, 218, 197)});
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14);
        PieData pieData = new PieData(dataSet);
        //Grafico 0
        grafico_pizza.notifyDataSetChanged();
        // grafico_pizza.setUsePercentValues(true);// PORCENTAGEM
        grafico_pizza.setDrawHoleEnabled(false);
        grafico_pizza.getLegend().setEnabled(true);
        grafico_pizza.setDrawEntryLabels(false);
        grafico_pizza.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        grafico_pizza.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        grafico_pizza.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        grafico_pizza.getLegend().setDrawInside(false);
        grafico_pizza.getLegend().setTextSize(12);
        grafico_pizza.getLegend().setTextColor(Color.BLACK);
        grafico_pizza.setData(pieData);
        grafico_pizza.notifyDataSetChanged();
        grafico_pizza.invalidate();
        grafico_pizza.getDescription().setEnabled(false);
        //        grafico_pizza.getDescription().setText("Zonas de Treino");
//        grafico_pizza.getDescription().setTextColor(Color.BLUE);
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

    public void imagePescoco(int img)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Taxa.this);

        View mView  = Taxa.this.getLayoutInflater().inflate(R.layout.dialog_img_pesc,null);
        final ImageView subImageView = (ImageView)mView.findViewById(R.id.imageView_dialog_pesc);
        Drawable drawable = getResources().getDrawable(img);
        subImageView.setImageDrawable(drawable);

        //cabeçalho da popup
        builder.setTitle("Descubra o seu perfil");
        builder.setView(mView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
