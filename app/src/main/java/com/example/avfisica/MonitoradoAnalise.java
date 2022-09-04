package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/* ESTRUTURA DO ARQUIVO
#-23.0874683;-45.68867858;537.7579345703125;0.0;82;22:36:02;0,021*
|   |       |   |           |                |   |   |       |   |_delimetador final de linha
|   |       |   |           |                |   |   |       |_____Km
| 	|		|	|			|				 |   |   |_____________timestamp
|	|		|	|			|				 |	 |_________________heartRate (bpm)
|	|		|	|			|				 |_____________________velocidade
|	|		|	|			|______________________________________altitude
|	|		|	|__________________________________________________longitude
|	|       |______________________________________________________delimetador de coluna
|   |______________________________________________________________latitude
|__________________________________________________________________delimetador de linha

 */

public class MonitoradoAnalise extends AppCompatActivity {

    Spinner spinerArquivo;
    LineChart graficoKm, graficoBpm;
    TextView txtBpmMaxDados, txtKmMaxDados, txtDistKmDados;
    ProgressBar progressBarAnalise;
    CheckBox checkTipoGrafico;

    String  fileNamePath = "";
    String  fileName = "";
    ArrayList<Double>  lLongitude = new ArrayList<Double>();
    ArrayList<Double>  lLatitude  = new ArrayList<Double>();
    ArrayList<Double>  lAtitude  = new ArrayList<Double>();
    ArrayList<Double>  lAceleracao = new ArrayList<Double>(); //km/h
    ArrayList<Double>  lKm = new ArrayList<Double>(); //Kilometragem
    ArrayList<Long>    lbpm = new ArrayList<Long>();
    ArrayList<String>  ltimeStamp = new ArrayList<String>();
    ArrayList<String>  ltimeStampAcel = new ArrayList<String>();
    ArrayList<String>  ltimeStampBpm = new ArrayList<String>();
    ArrayList<StringBuilder>  lLinhaFile  = new ArrayList<StringBuilder>();
    double aceleracaoMax = 0;
    long bpmMax = 0;
    double distMax = 0;
    boolean flagcheckTipoGrafico = false;

    Handler handler = null;

    //CONSTANTES
    final int GRAFICO_BPM = 1;
    final int GRAFICO_ACEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorado_analise);

        spinerArquivo = (Spinner) findViewById(R.id.spinerArquivo);
        graficoKm = (LineChart) findViewById(R.id.grafico_aceleracao);
        graficoBpm =  (LineChart) findViewById(R.id.grafico_bpm);
        txtBpmMaxDados = (TextView) findViewById(R.id.txtBpmMaxDados);
        txtKmMaxDados = (TextView) findViewById(R.id.txtKmMaxDados);
        txtKmMaxDados.setVisibility(View.INVISIBLE);
        progressBarAnalise = (ProgressBar) findViewById(R.id.progressBarAnalise);
        checkTipoGrafico = (CheckBox) findViewById(R.id.checkTipoGrafico);
        txtDistKmDados = (TextView) findViewById(R.id.txtDistKmDados);
        txtDistKmDados.setVisibility(View.INVISIBLE);

        progressBarAnalise.setVisibility(View.INVISIBLE);
        checkTipoGrafico.setChecked(false);
        flagcheckTipoGrafico = true;
        preencheSpinerArquivo();


        //evento onclik spiner Arquivo
        spinerArquivo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @SuppressLint("HandlerLeak")
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                try {
                    int getid = spinerArquivo.getSelectedItemPosition();
                    fileName = "";

                    if (getid>0)
                    {
                        fileName += fileNamePath+spinerArquivo.getItemAtPosition(getid).toString();
                        progressBarAnalise.setVisibility(View.VISIBLE);

                        new Thread() {
                           @Override
                            public void run() {
                                ProcessFile();
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        checkTipoGrafico.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkTipoGrafico.isChecked())
                {
                    flagcheckTipoGrafico = true;
                    checkTipoGrafico.setText("Simplificado");
                }
                else
                {
                    flagcheckTipoGrafico = false;
                    checkTipoGrafico.setText("Completo");
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    txtBpmMaxDados.setText(Long.toString(bpmMax) + " Bpm");
                    txtKmMaxDados.setText(new DecimalFormat("#.##").format(aceleracaoMax)+" Km/h");
                    txtDistKmDados.setText(new DecimalFormat("#.##").format(distMax)+" Km");

                    if (flagcheckTipoGrafico) {
                        //exibeGrafico(GRAFICO_ACEL, graficoKm, ltimeStampAcel.size(),flagcheckTipoGrafico); //km
                        exibeGrafico(GRAFICO_BPM, graficoBpm, ltimeStampBpm.size(), flagcheckTipoGrafico ); //bpm
                    }
                    else {
                       // exibeGrafico(GRAFICO_ACEL, graficoKm, ltimeStamp.size(), flagcheckTipoGrafico); //km
                        exibeGrafico(GRAFICO_BPM, graficoBpm, ltimeStamp.size(), flagcheckTipoGrafico); //bpm
                    }
                    progressBarAnalise.setVisibility(View.INVISIBLE);
                }
            }
        };

    }

    //spinerExercicio
    public void preencheSpinerArquivo() {
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.clear();
        fileName = "";
        spinnerArray.add("-");
        File root = Environment.getExternalStorageDirectory();
        File file;
        try {
            //remove a imagem existente
            file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/monitorado");
            fileNamePath = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/monitorado/";
            File[] arquivos = file.listFiles();
            for (File f : arquivos) {
                spinnerArray.add(f.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinerArquivo.setAdapter(adapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erro ao carregar os arquivos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void ProcessFile() {
            try {
                BufferedReader buffRead = new BufferedReader(new FileReader(fileName));
                StringBuilder  linha  = new StringBuilder ();
                int inicio = 0;
                int _final = 0;
                double dadosAcel = 0.00;
                double dadosAcel_aux = 0.00;
                long dadosBpm = 0;
                long dadosBpm_aux = 0;
                boolean flagTimeStampAcel = false;
                boolean flagTimeStampBpm = false;
                String timeStamp = "";
                double dadosDist = 0.00;

                aceleracaoMax = 0;
                bpmMax = 0;
                distMax = 0;
                ltimeStampBpm.clear();
                ltimeStampAcel.clear();
                ltimeStamp.clear();
                lAceleracao.clear();
                lLongitude.clear();
                lLatitude.clear();
                lKm.clear();

                lLinhaFile.trimToSize();
                linha.append(buffRead.readLine());
                while (true)
                {
                    if (!linha.toString().contains("null"))
                    {
                        if (linha.toString() !="")
                        {
                            while (linha.indexOf("#")>=0)
                            {
                                StringBuilder StringBuilderlinha = new StringBuilder();
                                StringBuilderlinha.append(linha.substring(linha.indexOf("#") + 1, linha.indexOf("*")));
                                lLinhaFile.add(StringBuilderlinha);
                                inicio = linha.indexOf("#");
                                _final = linha.indexOf("*")+1;
                                linha.delete(inicio, _final);
                            }
                        }
                    } else
                        break;
                    linha.append(buffRead.readLine());
                }
                buffRead.close();

                if (!flagcheckTipoGrafico) //completo
                {
                    for (StringBuilder linhaFile : lLinhaFile) {
                        try {
                            lLatitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            lLongitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            lAtitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);

                            dadosAcel = Double.parseDouble(linhaFile.substring(0, linhaFile.indexOf(";"))) * 3.2; //km/h
                            lAceleracao.add(dadosAcel);
                            if (aceleracaoMax < dadosAcel)
                                aceleracaoMax = dadosAcel;

                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            dadosBpm = Long.parseLong(linhaFile.substring(0, linhaFile.indexOf(";")));
                            lbpm.add(dadosBpm);
                            if (bpmMax < dadosBpm)
                                bpmMax = dadosBpm;

                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            ltimeStamp.add(linhaFile.substring(0, linhaFile.indexOf(";")));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);

                            dadosDist = Double.parseDouble(linhaFile.substring(0, linhaFile.length()));
                            lKm.add(dadosDist);
                            if (distMax < dadosDist)
                                distMax = dadosDist;


                        } catch (Exception c) {
                            c.printStackTrace();
                        }
                    }
                }
                else //simplificado
                {
                    for (StringBuilder linhaFile : lLinhaFile) {
                        try {
                            lLatitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            lLongitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            lAtitude.add(Double.parseDouble((linhaFile.substring(0, linhaFile.indexOf(";")))));
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);

                            dadosAcel = Double.parseDouble(linhaFile.substring(0, linhaFile.indexOf(";"))) * 3.2; //km/h
                            if (dadosAcel != dadosAcel_aux) {
                                dadosAcel_aux = dadosAcel; //backup
                                flagTimeStampAcel = true;
                                lAceleracao.add(dadosAcel);
                                if (aceleracaoMax < dadosAcel)
                                    aceleracaoMax = dadosAcel;
                            }

                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            dadosBpm = Long.parseLong(linhaFile.substring(0, linhaFile.indexOf(";")));
                            if (dadosBpm != dadosBpm_aux) {
                                dadosBpm_aux = dadosBpm;
                                flagTimeStampBpm = true;
                                lbpm.add(dadosBpm);
                                if (bpmMax < dadosBpm)
                                    bpmMax = dadosBpm;
                            }

                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);
                            timeStamp = linhaFile.substring(0, linhaFile.indexOf(";"));
                            ltimeStamp.add(timeStamp);
                            if (flagTimeStampAcel) {
                                flagTimeStampAcel = false;
                                ltimeStampAcel.add(timeStamp);
                            }
                            if (flagTimeStampBpm) {
                                flagTimeStampBpm = false;
                                ltimeStampBpm.add(timeStamp);
                            }
                            linhaFile.delete(0, linhaFile.indexOf(";") + 1);

                            dadosDist = Double.parseDouble(linhaFile.substring(0, linhaFile.length()));
                            lKm.add(dadosDist);
                            if (distMax < dadosDist)
                                distMax = dadosDist;

                        } catch (Exception c) {
                            c.printStackTrace();
                        }
                    }
                }

                Message msg = new Message();
                msg.what = 1; //processamento finalizado
                // envia msg para handler
                handler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void exibeGrafico(int type, LineChart grafico, int loopSize, boolean flagTipo){
        List<Entry> entradaGrafico = new ArrayList<>();
        List<String> entradaGraficox = new ArrayList<>();
        String legend = "";

        try {
            grafico.clear();

            int i_ = 0;
            for (int i = 0; i < loopSize; i++) {
                switch (type) {
                    case GRAFICO_ACEL: //km/h x timestamp
                        if (flagTipo) {
                            entradaGrafico.add(new Entry(i, (float) lAceleracao.get(i).doubleValue()));
                            entradaGraficox.add(ltimeStampAcel.get(i));
                        } else {
                            entradaGrafico.add(new Entry(i, (float) lAceleracao.get(i).doubleValue()));
                            entradaGraficox.add(ltimeStamp.get(i));
                        }
                        legend = "Km/h";
                        break;
                    case GRAFICO_BPM: //bpm x timestamp
                        if (flagTipo) {
                            entradaGrafico.add(new Entry(i, lbpm.get(i).intValue()));
                            entradaGraficox.add(ltimeStampBpm.get(i));
                        } else {
                            entradaGrafico.add(new Entry(i, lbpm.get(i).intValue()));
                            entradaGraficox.add(ltimeStamp.get(i));
                        }
                        legend = "Bpm";
                        break;

                    default:
                        break;
                }
                i_ = i;
            }


            LineDataSet dataSet = new LineDataSet(entradaGrafico, legend);
            dataSet.setColors(Color.parseColor("#1288E8"));
            dataSet.setCircleColor(Color.parseColor("#1288E8"));
            dataSet.setDrawCircleHole(false);
            dataSet.setFillColor(Color.parseColor("#1288E8"));
            dataSet.setDrawFilled(true);
            dataSet.setValueTextSize(12);
            LineData lineData = new LineData(dataSet);


            YAxis yAxis_0;
            {
                yAxis_0 = grafico.getAxisLeft();
                grafico.getAxisRight().setEnabled(false);
                grafico.getAxisLeft().setEnabled(false);
            }

            XAxis xAxis_0;
            {
                xAxis_0 = grafico.getXAxis();
                grafico.getXAxis().setEnabled(true);
                xAxis_0.setValueFormatter(new IndexAxisValueFormatter(entradaGraficox));
                xAxis_0.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis_0.setDrawGridLines(false);
                xAxis_0.setGranularity(1f);
                xAxis_0.setDrawLabels(true);
                xAxis_0.setDrawAxisLine(false);
                xAxis_0.setAxisMaximum(i_);
                xAxis_0.setLabelRotationAngle(0);
            }

            grafico.setScaleXEnabled(true);
            grafico.setScaleYEnabled(true);
            grafico.setVisibleXRangeMaximum(5); // DELIMITA A QUANTIDADE NO EIXO X
            grafico.moveViewToX(i_);
            grafico.getDescription().setEnabled(false);
            //grafico.setBackgroundColor(Color.WHITE);
            grafico.setData(lineData);
            grafico.refreshDrawableState();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Falha ao exibir o gráfico, tente novamente", Toast.LENGTH_SHORT).show();
        }
    }
}
