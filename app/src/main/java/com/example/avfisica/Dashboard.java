package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.TaxaModel;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.CorporalResource;
import com.example.avfisica.resources.PesoResource;
import com.example.avfisica.resources.TaxaResource;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    CorporalResource helperCorporal;
    PesoResource helperPeso;
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();
    CorporalModel corporal = new CorporalModel();
    Peso peso = new Peso();
    List<CorporalModel> lcorporal_dados = new ArrayList<CorporalModel>();
    List<Peso> lpeso_dados = new ArrayList<Peso>();

    TaxaResource helpertaxa;
    TaxaModel taxa = new TaxaModel();
    List<TaxaModel> ltaxa_dados = new ArrayList<TaxaModel>();

    PieChart grafico_pizza;
    LineChart grafico;

    final int GORDURA = 7;

    TextView textView_dash_peso, textView_dash_obj, textView_dash_imc, textView_dash_tbm;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        grafico = (LineChart) findViewById(R.id.grafico_dash);
        grafico_pizza = (PieChart) findViewById(R.id.grafico_pizza);
        textView_dash_peso = (TextView) findViewById(R.id.textView_dash_peso);
        textView_dash_obj = (TextView) findViewById(R.id.textView_dash_obj);
        textView_dash_imc = (TextView) findViewById(R.id.textView_dash_imc);
        textView_dash_tbm = (TextView) findViewById(R.id.textView_dash_tbm);


        helperCorporal = new CorporalResource(this);
        helperPeso = new PesoResource(this);
        helperAluno = new AlunoResource(this);
        helpertaxa = new TaxaResource(this);
        aluno = helperAluno.getData(Register.id_login);

        ltaxa_dados = helpertaxa.getData(Register.id_login);
        lcorporal_dados = helperCorporal.getData(Register.id_login);
        lpeso_dados = helperPeso.getData(Register.id_login);

        if (ltaxa_dados.size()>0)
            taxa = ltaxa_dados.get(ltaxa_dados.size()-1); // pega ultima atulização

        if (lpeso_dados.size()>0)
            peso = lpeso_dados.get(lpeso_dados.size()-1); // pega ultima atulização

        textView_dash_peso.setText("ATUAL:" + "\n" + (Float.toString(peso.getPeso()))+" kg");
        textView_dash_obj.setText("OBJETIVO:" + "\n" + (Float.toString(peso.getObjetivo()))+" kg");

        calculoGrafico(lcorporal_dados,GORDURA);

        calculoGrafico_peso(lpeso_dados);

        calculoGrafico_massa(lcorporal_dados,GORDURA);

        calculoImc();


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

    }

    //############################ GRAFICO GORDURA ############################


    public void calculoGrafico(List<CorporalModel> lcorpo, int type){

        grafico = (LineChart) findViewById(R.id.grafico_dash);
        List<Entry> entradaGrafico = new ArrayList<>();
        List<String> entradaGraficox = new ArrayList<>();

        String legend = "Corporal (cm)";

        int i_=0;
        for (int i = 0; i < (lcorpo.size()); i++)
        {
            i_=i;
            switch (type)
            {
                case 0: //abdominal
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getAbdominal()));
                    legend = "Abdominal (cm)";
                    break;
                case 1: //pescoco
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getPescoco()));
                    legend = "Pescoço (cm)";
                    break;
                case 2: //quadril
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getQuadril()));
                    legend = "Quadril (cm)";
                    break;
                case 3: //bicepsDir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getBicepsDir()));
                    legend = "Biceps Dir (cm)";
                    break;
                case 4: //bicepsEsq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getBicepsEsq()));
                    legend = "Biceps Esq (cm)";
                    break;
                case 5: //coxaDir
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getCoxaDir()));
                    legend = "Coxa Dir (cm)";
                    break;
                case 6: //coxaEsq
                    entradaGrafico.add(new Entry(i,lcorpo.get(i).getCoxaEsq()));
                    legend = "Coxa Esq (cm)";
                    break;
                case 7: //gordura
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

    //############################ GRAFICO PESO############################

    public void calculoGrafico_peso( List<Peso> lpeso){

        grafico = (LineChart) findViewById(R.id.grafico_dash_peso);
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
        LineDataSet dataSetLine = new LineDataSet(entradaGraficoLine, "Objetivo (Kg)");
        dataSetLine.setDrawCircles(true);
        dataSetLine.setColor(Color.GREEN);
        dataSetLine.setCircleColor(Color.GREEN);
        dataSetLine.setDrawFilled(true);
        dataSetLine.setValueTextSize(12);
        dataSetLine.setFillColor(Color.GREEN);
        dataSetLine.setValueTextColor(Color.BLACK);

        //######### Grafico principal ##########
        LineDataSet dataSet = new LineDataSet(entradaGrafico, "Peso (Kg)");
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
            xAxis_0.setLabelRotationAngle(0); // ROTAÇÃO DO EIXO X
            xAxis_0.setAxisMaximum(i_);
        }

        lineDataSets.add(dataSetLine);
        lineDataSets.add(dataSet);

        grafico.setScaleYEnabled(true);
        grafico.setScaleXEnabled(true);
        grafico.setVisibleXRangeMaximum(5);
        grafico.moveViewToX(i_);
        grafico.getDescription().setEnabled(false);
        // background color
        grafico.setBackgroundColor(Color.WHITE);

        grafico.setData(new LineData(lineDataSets));
    }

    //############################ GRAFICO MASSA ############################

    public void calculoGrafico_massa(List<CorporalModel> lcorpo, int type){

        grafico_pizza = (PieChart) findViewById(R.id.grafico_pizza);
        List<PieEntry> entradaGrafico = new ArrayList<>();

        if(lcorporal_dados.size()>=1) {
            corporal = lcorporal_dados.get(lcorporal_dados.size()-1); // pega ultima atulizaÃƒÂ§ÃƒÂ£o
            float magra = (aluno.getPeso()-(aluno.getPeso()*((corporal.getGordura()/100)+1)-aluno.getPeso()));
            float gorda = aluno.getPeso()-magra;
            entradaGrafico.add(new PieEntry(magra,"Massa Magra (kg)"));
            entradaGrafico.add(new PieEntry(gorda, "Massa Gorda (kg)"));
        }

        PieDataSet dataSet = new PieDataSet(entradaGrafico, "");
        //dataSet.setColors(new int[]{Color.rgb(220, 0, 0), Color.rgb(0, 0, 220), Color.rgb(0, 220, 0), Color.rgb(255, 255, 0), Color.rgb(220, 0, 0)});
        dataSet.setColors(new int [] {Color.rgb(51, 128, 255), Color.rgb(249, 100, 114)});
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14);
        PieData pieData = new PieData(dataSet);
        //Grafico 0
        grafico_pizza.notifyDataSetChanged();
        // grafico_pizza.setUsePercentValues(true);// PORCENTAGEM
        grafico_pizza.setDrawHoleEnabled(false);
        grafico_pizza.getLegend().setEnabled(true);
        grafico_pizza.setDrawEntryLabels(false);
        //grafico_pizza.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //grafico_pizza.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        grafico_pizza.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        grafico_pizza.getLegend().setDrawInside(false);
        grafico_pizza.getLegend().setTextSize(12);
        grafico_pizza.getLegend().setTextColor(Color.BLACK);
        grafico_pizza.setData(pieData);
        grafico_pizza.getDescription().setEnabled(false);
        //        grafico_pizza.getDescription().setText("Zonas de Treino");
//        grafico_pizza.getDescription().setTextColor(Color.BLUE);
    }


    public void calculoImc(){

        // IMC
        float altura = Float.parseFloat(aluno.getAltura())/100;
        float peso = aluno.getPeso();
        float resultado = peso / (altura * altura);
        float idade = aluno.getIdade();

        DecimalFormat df = new DecimalFormat("0.00");
        textView_dash_imc.setText("IMC ATUAL:" + "\n" + (df.format(resultado)));

        double tbm = 1.1;
        float resultado_tbm=0;
        if (ltaxa_dados.size()>0) {
            if (taxa.getTipo().equals("ectomorfo")) {tbm = 1.2;}
            if (taxa.getTipo().equals("mesomorfo")) {tbm = 1.1;}
            if (taxa.getTipo().equals("endomorfo")) {tbm = 1;}
        }

        //TBM
        if(aluno.getSexo().equals("M")){  //Homem
            resultado_tbm = (float) ((66 + (13.7 * peso) + (5 * altura*100) - (6.8 * idade))*tbm); //FORMULA
        }
        else { //Mulher
            resultado_tbm = (float) ((665 + (9.6 * peso) + (1.8 * altura) - (4.7 * idade))*tbm); //FORMULA
        }
        DecimalFormat dfi = new DecimalFormat("0.0");
        textView_dash_tbm.setText("TBM ATUAL:" + "\n" + (dfi.format(resultado_tbm))+" kcal");
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
