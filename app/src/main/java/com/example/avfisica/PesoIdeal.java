package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Peso;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.CorporalResource;
import com.example.avfisica.resources.PesoResource;
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

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class PesoIdeal extends AppCompatActivity {
    Button btn_salvar;
    EditText editText_peso, editText_objetivo;
    TextView textView_peso, textView_diff, textView_objetivo, textView_peso_date;
    BottomNavigationView bottomNavigation;
    ImageButton btn_edit;
    CheckBox checkBox_imc, checkBox_gordura;

    Float resultado;
    int kg,gr,pes,obj;
    AlunoResource helperAluno;
    PesoResource helperPeso;
    CorporalResource helperCorporal;
    CorporalModel corporal = new CorporalModel();
    Aluno aluno = new Aluno();
    Peso peso = new Peso();
    List<Peso> lpeso_dados = new ArrayList<Peso>();
    List<CorporalModel> lcorporal_dados = new ArrayList<CorporalModel>();
    Peso peso_edit = new Peso();



    boolean update = false;
    boolean flag = false;

    LineChart grafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesoideal);


        grafico = (LineChart) findViewById(R.id.graficoID);
        btn_salvar = (Button) findViewById(R.id.button_salvar_peso);
        editText_peso = (EditText) findViewById(R.id.editText_peso_peso);
        editText_peso.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        editText_objetivo = (EditText) findViewById(R.id.editText_objetivo);
        editText_objetivo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        textView_peso = (TextView) findViewById(R.id.textView_resultado_peso);
        textView_peso.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
        textView_peso_date = (TextView) findViewById(R.id.textView_peso_date);
        textView_peso_date.setVisibility(View.INVISIBLE);
        // textView_diff = (TextView) findViewById(R.id.textView_diff_peso);
        textView_objetivo = (TextView) findViewById(R.id.textView_objetivo);
        btn_edit = (ImageButton) findViewById(R.id.imageButton_peso_edit);
        checkBox_imc = (CheckBox) findViewById(R.id.checkBox_imc);
        checkBox_gordura = (CheckBox) findViewById(R.id.checkBox_gordura);
        checkBox_imc.setChecked(true);

        helperAluno = new AlunoResource(this);
        helperPeso = new PesoResource(this);
        helperCorporal = new CorporalResource(this);


        aluno = helperAluno.getData(Register.id_login);
        lpeso_dados = helperPeso.getData(Register.id_login);
        lcorporal_dados = helperCorporal.getData(Register.id_login);

        if (lpeso_dados.size()>0)
            peso = lpeso_dados.get(lpeso_dados.size()-1); // pega ultima atulizaÃ§Ã£o

        if((peso.getId_login()!=0) && aluno.getId_login()!=0)
        {
            editText_peso.setText(Float.toString(aluno.getPeso()));
            editText_objetivo.setText(Float.toString(peso.getObjetivo()));

            if (lpeso_dados.size() >= 1) {
                try {
                    calculoGrafico(lpeso_dados);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else
            editText_peso.setText(Float.toString(aluno.getPeso()));

        if (lpeso_dados.size() < 2) {
            showCase("PESO", "Adicione o seu PESO!", editText_peso, 1);
        }

        checkBox_imc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkBox_imc.isChecked())
                {
                    checkBox_gordura.setChecked(false);
                    calculaPeso();
                }
                else
                {
                    //Perform action when you touch on checkbox and it change to unselected state
                }
            }
        });


        checkBox_gordura.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkBox_gordura.isChecked())
                {
                    checkBox_imc.setChecked(false);
                    calculaPeso();
                    //Perform action when you touch on checkbox and it change to selected state
                }
                else
                {
                    //Perform action when you touch on checkbox and it change to unselected state
                }
            }
        });

        calculaPeso();

        //Salvar
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String currentTime = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());

                    aluno.setPeso(Float.parseFloat(editText_peso.getText().toString())); //Peso
                    if (helperAluno.updateName(aluno)>0)
                    {
                        //############ 3 TABELA PESO ############
                        String d1 = currentTime; //Data
                        String d2 = editText_peso.getText().toString(); //Peso
                        String d3 = editText_objetivo.getText().toString(); //Objetivo

                        // peso.setData(d1);
                        peso.setPeso(Float.parseFloat(d2));
                        peso.setObjetivo(Float.parseFloat(d3));
                        peso.setId_login(Register.id_login);
                        peso.setStatus("PENDENTE");


                        if (((peso.getData()!=null) && (peso.getData().equals(currentTime))) ||
                                ((peso.getData()!=null) && update))
                        {
                            if (update) //flag de controle
                                update = false;

                            if (helperPeso.updateName(peso) >= 0) {
                                Toast.makeText(getApplicationContext(), "Alterado", Toast.LENGTH_SHORT).show();
                                textView_peso_date.setVisibility(View.VISIBLE);
                                textView_peso_date.setText(currentTime);
                            }
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao alterar o registro no banco local!", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            peso.setData(currentTime);

                            if (helperPeso.insertData(peso) >= 0)
                                Toast.makeText(getApplicationContext(), "Salvo", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local!", Toast.LENGTH_SHORT).show();
                        }

                        lpeso_dados.clear();
                        lpeso_dados = helperPeso.getData(Register.id_login);
                        peso = lpeso_dados.get(lpeso_dados.size()-1); // pega ultima atulizaÃ§Ã£o

                        if (lpeso_dados.size() >= 1)
                            calculoGrafico(lpeso_dados);
                        else if (lpeso_dados.size() == 0)
                            Toast.makeText(getApplicationContext(), "Falha ao carregar os dados do graficos!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "NÃo foi possivel gravar as informações", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Edit PESO ATUAL
        editText_peso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog(true);
                    flag=false;
                    calculaPeso();
                } catch (Exception e) {
                }
            }
        });

        // Edit OBJETIVO
        editText_objetivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showPickerDialog(false);
                    flag=false;
                    calculaPeso();
                } catch (Exception e) {
                }
            }
        });

        // PESO IDEAL
        textView_peso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(PesoIdeal.this)
                            .setTitle("Peso Ideal")
                            .setMessage("O peso ideal apresenta o peso calculado pelo IMC ou pelo indice de gordura ideal!" + "\n" + "\n" +"É apenas uma ajuda!")
                            .setIcon(R.drawable.ic_tag_faces_blue_24dp)
                            .show();
                } catch (Exception e) {
                }
            }
        });

//        editText_peso.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//                calculaPeso();
//                showPickerDialog();
//
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });

//
//        editText_objetivo.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//
//               if (!editText_objetivo.getText().toString().isEmpty()) {
//                    calculaPeso();
//                }
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });

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

        //###################### ALERTA DIALOG ######################

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

    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void calculaPeso() {
        if (editText_peso.getText().toString().isEmpty()) {
        } else {

            float peso = Float.parseFloat(editText_peso.getText().toString());


            if (!editText_objetivo.getText().toString().isEmpty()) {

                float obj = Float.parseFloat(editText_objetivo.getText().toString());
                float objetivo = (peso - obj);

                if (objetivo > 0) {
                    DecimalFormat dfi = new DecimalFormat("0.0");
                    textView_objetivo.setText("-" + dfi.format(objetivo) + "Kg");
                } else {
                    DecimalFormat dfi = new DecimalFormat("0.0");
                    textView_objetivo.setText("+" + dfi.format(objetivo * -1) + "Kg");
                    objetivo = objetivo*-1;
                }

                // PARABENS
                if(flag==true) {
                    if (objetivo > 0 && objetivo <= 1) {
                        new AlertDialog.Builder(PesoIdeal.this)
                                .setTitle("FALTA POUCO")
                                .setMessage("Você está muito proximo do seu objetivo!")
                                .setIcon(R.drawable.ic_tag_faces_blue_24dp)
                                .show();
                    }
                    // PARABENS
                    if (objetivo == 0) {
                        new AlertDialog.Builder(PesoIdeal.this)
                                .setTitle("PARABÉNS")
                                .setMessage("O objetivo alcançado!!")
                                .setIcon(R.drawable.ic_tag_faces_blue_24dp)
                                .show();
                    }
                    if(objetivo>=1.1){
                        new AlertDialog.Builder(PesoIdeal.this)
                                .setTitle("Bora alcançar o seu objetivo!")
                                .setMessage("Ajuste sua dieta no menu: CALORIA" + "\n" + "\n" +"E continue acompanhado suas MEDIDAS!")
                                .setIcon(R.drawable.ic_tag_faces_blue_24dp)
                                .show();
                    }
                }
            }


            //float altura = (Float.parseFloat(aluno.getAltura()));
//            if (aluno.getSexo().equals("M")) { //Homem
//                resultado = (float) (52 + (((altura - 152.4) / 2.54) * 1.9)); //FORMULA ROBINSON 1983
//            }
//
//            if (aluno.getSexo().equals("F")) { //Mulher
//                resultado = (float) (49 + (((altura - 152.4) / 2.54) * 1.7));
//            }

            DecimalFormat df = new DecimalFormat("0.0");
            // IMC
            if(checkBox_imc.isChecked()) {
                float altura = (float) (Float.parseFloat(aluno.getAltura()) / 100.00);
                float peso_ = Float.parseFloat(editText_peso.getText().toString());
                float resultado_min = (float) (18.50 * (altura * altura));
                float resultado_max = (float) (24.90 * (altura * altura));

                textView_peso.setText(String.valueOf(df.format(resultado_min)) + "  à  " + String.valueOf(df.format(resultado_max)));
            }
            else {
                // PESO POR INDICE DE GORDURA
                if (lcorporal_dados.size() >= 1) {
                    corporal = lcorporal_dados.get(lcorporal_dados.size() - 1); // pega ultima atualização
                    {
                        long idade = aluno.getIdade();
                        int indice = 0;
                        if (aluno.getSexo().equals("M")) {
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
                        } else {    // mulher
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
                        float ind_gordura = 0;
                        float peso_indice = 0;
                        float peso_ = Float.parseFloat(editText_peso.getText().toString());
                        if (corporal.getGordura() > indice) { // Subtrair
                            peso_indice = peso_ - ((peso_ * (((corporal.getGordura() - indice) / 100) + 1)) - peso_);
                        } else { // Soma
                            peso_indice = peso_ + ((peso_ * (((indice - corporal.getGordura()) / 100) + 1)) - peso_);
                        }
                        textView_peso.setText("Indice: "+String.valueOf(df.format(indice)) + "%"+" --> Peso " + String.valueOf(df.format(peso_indice)));
//                float numeroConvertido = peso - resultado;
//
//                if (numeroConvertido > 0) {
//                    DecimalFormat dfi = new DecimalFormat("0.0");
//                    textView_diff.setText("-" + dfi.format(numeroConvertido) + "Kg");
//                } else {
//                    DecimalFormat dfi = new DecimalFormat("0.0");
//                    textView_diff.setText("+" + dfi.format(numeroConvertido * -1) + "Kg");
//                }
                    }
                }else {
                    new AlertDialog.Builder(PesoIdeal.this)
                            .setTitle("Peso pelo Indice de Gordura")
                            .setMessage("Primeiro preencha as suas medidas corporais do menu --> Corporal!" + "\n" + "\n" +"Depois retorne aqui!")
                            .setIcon(R.drawable.ic_tag_faces_blue_24dp)
                            .show();
                }
            }
        }
    }

    //############################ GRAFICO ############################

    public void calculoGrafico( List<Peso> lpeso){

        grafico = (LineChart) findViewById(R.id.graficoID);
        List<Entry> entradaGrafico = new ArrayList<>();
        List<String> entradaGraficox = new ArrayList<>();
        List<Entry> entradaGraficoLine = new ArrayList<>();
        int i_=0;
        for (int i = 0; i < (lpeso.size()); i++)
        {
            entradaGrafico.add(new Entry(i,lpeso.get(i).getPeso()));
            entradaGraficox.add(lpeso.get(i).getData());
            entradaGraficoLine.add(new Entry(i,lpeso.get(i).getObjetivo()));
            i_=i;
        }


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        //######### Grafico Linha de Objetivo ##########
        LineDataSet dataSetLine = new LineDataSet(entradaGraficoLine, "Objetivo Kg");
        dataSetLine.setDrawCircles(true);
        dataSetLine.setColor(Color.GREEN);
        dataSetLine.setCircleColor(Color.GREEN);
        dataSetLine.setDrawFilled(true);
        dataSetLine.setValueTextSize(12);
        dataSetLine.setFillColor(Color.GREEN);
        dataSetLine.setValueTextColor(Color.BLACK);



        //######### Grafico principal ##########
        LineDataSet dataSet = new LineDataSet(entradaGrafico, "Peso Kg");
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

    public void showPickerDialog(final boolean type_peso ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_picker, null);
        builder.setView(view);
        //builder.setTitle();
        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.picker_first);
        picker.setMinValue(0);
        picker.setMaxValue(200);
        if(type_peso==true) {
            int peso_picker = (int) (Float.parseFloat(editText_peso.getText().toString()));
            picker.setValue(peso_picker);
        }
        else{ //OBJETIVO
            if (editText_objetivo.getText().toString().isEmpty()){
                picker.setValue(50);
            }
            else {
                int objetivo_picker = (int) (Float.parseFloat(editText_objetivo.getText().toString()));
                picker.setValue(objetivo_picker);
            }
        }



        final NumberPicker picker_2 = (NumberPicker) view.findViewById(R.id.picker_second);
        picker_2.setMinValue(0);
        picker_2.setMaxValue(9);



        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //positive button action
                int kg = picker.getValue();
                int gr = picker_2.getValue();
                if(type_peso==true) {
                    editText_peso.setText(kg + "." + gr);
                }else
                    editText_objetivo.setText(kg + "." + gr);
                flag=true;
                calculaPeso();
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

    public void editMedidas()
    {
        List<String> spinnerArray_data = new ArrayList<String>();
        lpeso_dados = helperPeso.getData(Register.id_login);

        for (int i = 0; i < (lpeso_dados.size()); i++)
        {
            spinnerArray_data.add( lpeso_dados.get(i).getData());
        }
//        for (CorporalModel comp:lcorporal_dados)
//        {
//            spinnerArray_data.add(corporal.getData());
//        }
        AlertDialog.Builder builder = new AlertDialog.Builder(PesoIdeal.this);
        //Cria a view a ser utilizada no dialog
        View mView  = PesoIdeal.this.getLayoutInflater().inflate(R.layout.spinner_aux,null);
        //ObtÃ©m uma referencia ao Spinner
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);
        //Cria o Adapter
        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(PesoIdeal.this,
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
                if (lpeso_dados.size()>0) {
                    peso_edit = lpeso_dados.get(getid); // pega ultima atulizaÃ§Ã£o
                    peso.setData(lpeso_dados.get(getid).getData()); //salva a data para alterar
                    peso.setId_peso(lpeso_dados.get(getid).getId_peso());
                    DecimalFormat df = new DecimalFormat("0.00");
                    editText_peso.setText(Float.toString(peso_edit.getPeso()));
                    editText_objetivo.setText(Float.toString(peso_edit.getObjetivo()));
                    textView_peso_date.setVisibility(View.VISIBLE);
                    textView_peso_date.setText(lpeso_dados.get(getid).getData());
                    calculaPeso();
                }
                update = true;
                Toast.makeText(getApplicationContext(),  "Clicked OK!", Toast.LENGTH_SHORT);
                // corporal = lcorporal_dados.get(Integer.parseInt(data)); // pega ultima atulizaÃ§Ã£o
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


    public void showCase(String title,String subtitle,View view, final int type ){
        new GuideView.Builder(PesoIdeal.this)
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
                            showCase("Objetivo","Adicione o seu objetivo!",editText_objetivo,2);
                        } else if (type == 2) {
                            showCase("Salvar", "Clique para Salvar!", btn_salvar, 3);
                        } else if (type == 3) {
                            showCase("Edit","Selecione a data e altere o peso!",btn_edit,4);
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
