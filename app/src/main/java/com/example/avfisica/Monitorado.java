package com.example.avfisica;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.resources.AlunoResource;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class Monitorado extends AppCompatActivity {

    //##### CONSTANTES #####
    private static final int REQUEST_ENABLE_BT = 0;
    private static final int TIMEOUT_SEMAFARO_CONNECT = 10;
    private static final int TIMEOUT_CONNECT = 10;

    //### VARIAVEIS ####
    private BluetoothAdapter mBluetoothAdapter;
    private ScanSettings settings;
    private List<ScanFilter> filters;




    //variaveis para preenchimento da classe Device
    BluetoothGattCharacteristic characteristic_bkp;
    BluetoothGattDescriptor descriptor_bkp;
    BluetoothDevice device_bkp;
    BluetoothAdapter bluetoothAdapter;
    //public Device cintaDevice = new Device();
    public BlueToothBLE deviceBle = null;
    int nextIndiceDevice = 0; //próximo indice da fila a se conectar
    int indice_bkp = 0;
    int countSemafaroConnect = TIMEOUT_SEMAFARO_CONNECT;
    int min, max;
    int i;
    public int bufferhr[] = new int[7200];
    double kcal_before = 0;
    boolean flag_ble=true;

    //Declaração variaveis thread
    Thread mHandlerFrontEnd = null;
    static int loopThread = 0; //teste thread
    static int x = 0; //teste thread
    boolean flagThreadFronEnd = false;

    ListView deviceList;
    ArrayAdapter<String> btArrayAdapter;

    //Declaração das veriveis iteração de front-end
    TextView mHeartRate, txtCaloria, txtKM, txtMinKM, txtDistKM;
    ImageView imagem_ble;
    Chronometro chronometro;
    ToggleButton toggleBtnIniciar;
    FloatingActionButton floatingActionButton_ble;
    ImageButton imageAnalise;

    //aluno
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();

    //grafico
    LineChart grafico;

    //GPS
    final GPS gps = new GPS(this);
    LocationListener listener;
    LocationManager locationManager;
    private double[] latLongDouble = new double[4];
    private static Double dRaioTrans = 0.005; // (equivale a 1km / 0.030 = 30 metros)
    //Incialização das das variáveis de controle Gps para atualização periódica
    final static long tempo = 100; // 1minutos
    final static float distancia = 5;   //5metros
    public static Double lat = 0.00;
    public static Double lng = 0.00;
    public static Double Lat_bkp = 0.00;
    public static Double Lng_bkp = 0.00;
    public static Double Alt = 0.0;
    public static Double Speed = 0.0;
    public static Double countDistancia = 0.00;

    private ListView lstvw;
    private ArrayAdapter aAdapter;
    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();

    String [] mac_vector = new String[20]; //  VECTOR


    //Cronometro
    public long pauseOffset;

    //FILE
    FileOutputStream fileWrite = null;
    File file = null;
    ListView listDevicesFound;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorado);

        this.deviceBle = new BlueToothBLE(this);

        mHeartRate = (TextView) findViewById(R.id.textHeartRate);
        mHeartRate.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mHeartRate.setTextSize(70);
        mHeartRate.setGravity(Gravity.LEFT);
        mHeartRate.setTypeface(Typeface.DEFAULT_BOLD); //NEGRITO
        mHeartRate.setBackgroundColor(Color.WHITE);

        txtCaloria = (TextView) findViewById(R.id.txtCaloria);
        txtCaloria.setTextSize(20);

        imagem_ble = (ImageView) findViewById(R.id.imageView_ble);
        imagem_ble.setVisibility(View.INVISIBLE);
        txtKM = (TextView) findViewById(R.id.txtKM);
        txtKM.setVisibility(View.INVISIBLE); // PROXIMA VERSÃO
        toggleBtnIniciar = (ToggleButton) findViewById(R.id.toggleBtnIniciar);
        txtMinKM = (TextView) findViewById(R.id.txtMinKm);
        txtMinKM.setVisibility(View.INVISIBLE); // PROXIMA VERSÃO
        txtDistKM = (TextView) findViewById(R.id.txtDistancia);
        txtDistKM.setVisibility(View.INVISIBLE); // PROXIMA VERSÃO
        imageAnalise = (ImageButton) findViewById(R.id.imageButton_Analise);

        floatingActionButton_ble = (FloatingActionButton) findViewById(R.id.floatingActionButton_ble);

        btArrayAdapter = new ArrayAdapter<String>(Monitorado.this, android.R.layout.simple_list_item_1);
        CheckBlueToothState();
        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));

        //##############chronometer###########
        try {
            chronometro = new Chronometro(this, (Chronometer) findViewById(R.id.chronometer));
            if (!chronometro.createChronometer())
                Toast.makeText(getApplicationContext(),"Falha no cronometro!",Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        //##############Bluetooth###########
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //##############Cinta com os dados do aluno###########
        helperAluno = new AlunoResource(this);
        aluno = helperAluno.getData(Register.id_login);

        this.deviceBle.cintaDevice.nomeAluno = aluno.getNome();
        this.deviceBle.cintaDevice.idade = aluno.getIdade();
        this.deviceBle.cintaDevice.peso = aluno.getPeso();
        this.deviceBle.cintaDevice.sexo = aluno.getSexo();
        if ((aluno.getCintaMac() != null) && (!aluno.getCintaMac().equals("-")))
            this.deviceBle.cintaDevice.device = mBluetoothAdapter.getRemoteDevice(aluno.getCintaMac()); //"C2:ED:E2:32:16:22"

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        // Text
        floatingActionButton_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(bAdapter==null){
                        Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
                        ArrayList list = new ArrayList();
                        if (pairedDevices.size() > 0) {
                            for (BluetoothDevice device : pairedDevices) {
                                String devicename = device.getName();
                                String macAddress = device.getAddress();
                                list.add("Name: " + devicename + "MAC Address: " + macAddress);
                            }
                            /*lstvw = (ListView) findViewById(R.id.deviceList);*/
                            aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                            lstvw.setAdapter(aAdapter);
                        }
                    }
                    mBluetoothAdapter.startDiscovery();
                } catch (Exception e) {
                }
            }
        });

        grafico = (LineChart) findViewById(R.id.graficoID);

        askPermissions();

        locationManager = gps.getLocationManager();
        //ATUALIZAÇÃO DOS VALORES DO GPS A CADA VARAIAÇÃO CONFORME VARIÁVEIS  tempo distancia
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latLongDouble = gps.updateWithNewLocation(location);
                lat = latLongDouble[0];
                lng = latLongDouble[1];
                Alt = latLongDouble[2];
                Speed = latLongDouble[3];
                if (Lng_bkp != 0) {
                    Double dist = getDistancia(lat, lng, Lat_bkp, Lng_bkp);
                    countDistancia = (countDistancia + dist);
                }

                Lat_bkp = lat;
                Lng_bkp = lng;

                refresh();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        //configura a atualização do GSP
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempo, distancia, this.listener, null);


        toggleBtnIniciar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (toggleBtnIniciar.getText().equals("INICIAR")) {
                    chronometro.start();
                    toggleBtnIniciar.setBackgroundColor(Color.RED);
                    toggleBtnIniciar.setTextColor(Color.WHITE);
                    //reseta o front e  vairavel
                    countDistancia = 0.00;
                    txtDistKM.setText("0,00");
                    txtKM.setText("0");
                    txtMinKM.setText("0,00");

                    //libera a thread
                    flagThreadFronEnd = true;
                    askPermissions();
                    runThread(true);

                    //criar um novo arquvio
                    final String currentTime = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                    file = createFile(file, currentTime +"-"+new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())); //NOME DO ARQUIVO
                    try {
                        fileWrite = new FileOutputStream(file); //libera a escrita
                        //fileWrite = openFileOutput(currentTime,MODE_APPEND);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } else if (toggleBtnIniciar.getText().equals("FINALIZAR")) {
                    //stop
                    chronometro.stop();
                    toggleBtnIniciar.setBackgroundColor(Color.GREEN);
                    toggleBtnIniciar.setTextColor(Color.BLACK);

                    //para a thread
                    flagThreadFronEnd = false;
                    runThread(false);

                    //reseta o clonometro
                    chronometro.reset();
                    try {
                        if (file.exists())
                            fileWrite.close();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });

        //button analise
        imageAnalise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openMonitoradoAnalise();
                } catch (Exception e) {
                }
            }
        });

        //######## Thread Atualiza o front-end #######
        if (this.deviceBle.cintaDevice.device != null) {  //evita ligar a thread sem mac cadastrado
            mHandlerFrontEnd = new Thread() {
                public void run() {
                    while (loopThread==0) {
                        try {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (flagThreadFronEnd)
                                        update_front_end();
                                }
                            });
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        } else
            Toast.makeText(getApplicationContext(), "Cinta não cadastrada!", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flagThreadFronEnd = false;
        runThread(false);
    }

    @Override
    public void onBackPressed() {
        flagThreadFronEnd = false;
        runThread(false);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        //Log.w(TAG, "App stopped");

        super.onStop();
    }



    //################# Update front end #################################
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void update_front_end() {
        updateCinta(this.deviceBle);
        this.deviceBle.notify_blue();

        //PERIÓDICO
        try {
            //semafaroConnetDevice
            if (countSemafaroConnect != TIMEOUT_SEMAFARO_CONNECT) {
                //liga a contagem do semáfaro (sinal vermelho)
                countSemafaroConnect++;
            } else if (countSemafaroConnect >= TIMEOUT_SEMAFARO_CONNECT) {
                //para a contagem (sinal verde)
                countSemafaroConnect = TIMEOUT_SEMAFARO_CONNECT;
            }

            //inclementa o contador de timeout para todas cintas ja cadastradas com serviço
            //caso aconteça o timeout libera a posicao na  lista
            if (countSemafaroConnect == TIMEOUT_SEMAFARO_CONNECT) {
                if ((!this.deviceBle.cintaDevice.flagConect) &&
                        (this.deviceBle.cintaDevice.device != null)) {
                    countSemafaroConnect = 0; //LIGA O SEMÁFARO
                    indice_bkp = nextIndiceDevice;
                    this.deviceBle.connectToDevice();
                }

                //a cada timeout definido na constante TIMEOUT_CONNECT é feita uma verificação de conexão
                this.deviceBle.cintaDevice.timeout++;
                if (this.deviceBle.cintaDevice.timeout >= TIMEOUT_CONNECT) {
                    this.deviceBle.cintaDevice.flagConect = false;
                    this.deviceBle.cintaDevice.timeout = 0;
                }
            }

            // grava informaçao no arquivo
            if (fileWrite!=null) {
                writeFile(fileWrite, "#"+lat.toString()+";"+lng.toString()+";"+Alt.toString()+";"+Speed.toString()+";"+
                        this.deviceBle.cintaDevice.heartRate+";"+
                        new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())+";"+
                        txtDistKM.getText()+"*");
            }

        } catch (Exception e) {
            Log.v("erro", e.toString());
        }
    }

    private void updateCinta(BlueToothBLE blueToothBLE) {
        try {
            if (blueToothBLE.cintaDevice.flagConect)
            {
                x++; // Contador para conta de caloria
                //Atualiza o Heartrate
                setLineCor(mHeartRate, blueToothBLE.cintaDevice);
                mHeartRate.setText(String.valueOf(blueToothBLE.cintaDevice.heartRate) + "bpm");

                // Porcentagem da Frequencia
                int freqporc = (blueToothBLE.cintaDevice.heartRate * 100 / (220 - (int) blueToothBLE.cintaDevice.idade)); // Porcentagem BPM x Idade
                DecimalFormat format = new DecimalFormat("#.#"); //Travar em duas casas decimais

                if (blueToothBLE.cintaDevice.sexo.equals("m") || blueToothBLE.cintaDevice.sexo.equals("M")) {
                    // Caloria Homem
                    blueToothBLE.cintaDevice.kcal = ((((-55.09 + (0.63 * blueToothBLE.cintaDevice.heartRate) + (0.19 * blueToothBLE.cintaDevice.peso)) + (0.20 * blueToothBLE.cintaDevice.idade)) / 4.18) * (60 * (x / 2) / 3600)); // Caloria Homem
                    if(blueToothBLE.cintaDevice.kcal> kcal_before){
                        blueToothBLE.cintaDevice.kcal = Math.round((blueToothBLE.cintaDevice.kcal*100.0)/100.0);               // '' ''
                        kcal_before = blueToothBLE.cintaDevice.kcal; // Tratativa para não diminuir a caloria qnd diminuir o batimento
                    }else blueToothBLE.cintaDevice.kcal = Math.round((kcal_before*100.0)/100);


                } else {
                    //Caloria Mulher
                    blueToothBLE.cintaDevice.kcal = ((((-20.4022 + (0.4472 * blueToothBLE.cintaDevice.heartRate) - (0.1263 * blueToothBLE.cintaDevice.peso)) + (0.074 * blueToothBLE.cintaDevice.idade)) / 4.184) * (60 * (x / 2) / 3600));
                    if(blueToothBLE.cintaDevice.kcal> kcal_before){
                        blueToothBLE.cintaDevice.kcal = Math.round((blueToothBLE.cintaDevice.kcal*100.0)/100.0);                // '' ''
                        kcal_before = blueToothBLE.cintaDevice.kcal; // Tratativa para não diminuir a caloria qnd diminuir o batimento
                    }else blueToothBLE.cintaDevice.kcal = Math.round((kcal_before*100.0)/100);

                }

                if (blueToothBLE.cintaDevice.kcal < 0) {
                    blueToothBLE.cintaDevice.kcal = 0;
                }

                min = 400;

                //#################### FREQ MIN && MAX ################################
                for (int i = 0; i <= this.deviceBle.cintaDevice.indexBufferHeartRate; i++) {
                    int convertedValues_[] = this.deviceBle.cintaDevice.bufferheartrate;

                    if (convertedValues_[i] > 30) {
                        bufferhr[i] = convertedValues_[i];
                        min = Math.min(min, bufferhr[i]);
                        max = Math.max(max, bufferhr[i]);
                    }
                }

                if (min == 400) { // GATO P/ NÃO FICAR SEMPRE COMO ZERO O MINIMO
                    min = 0;
                }

                //#####################################################################

                this.deviceBle.cintaDevice.contFreqMedia++; // Contador
                // Frequencia media
                double fcmedia = (this.deviceBle.cintaDevice.heartRate + this.deviceBle.cintaDevice.fcmedia_bkp);
                this.deviceBle.cintaDevice.fcmedia_bkp = fcmedia;
                this.deviceBle.cintaDevice.fcmedia2 = (int) fcmedia / this.deviceBle.cintaDevice.contFreqMedia;
                txtCaloria.setText("Calorias :" + String.valueOf(this.deviceBle.cintaDevice.kcal) + "kcal" + "\n" + "FCmin: " + min + "\n" + "FCmax: " + max + "\n" + "FCmedia: " + String.valueOf(blueToothBLE.cintaDevice.fcmedia2));
            }

        } catch (Exception e) {
            Log.v("Update Front End Erro=", e.toString());
        }
    }

    private void setLineCor(TextView txt, Device cintaDevice) {
        int idade = (int) cintaDevice.idade;
        int heartRate = cintaDevice.heartRate;
        int freqporc = (heartRate * 100 / (220 - idade));
        // < 114 = Branca
        if (freqporc <= 60) {
            txt.setBackgroundColor(Color.WHITE);
            txt.setTextColor(Color.BLACK);
        }
        // 115-133 = Azul
        else if (freqporc >= 61 && freqporc <= 70) {

            txt.setBackgroundColor(Color.BLUE);
            txt.setTextColor(Color.WHITE);
        }
        // 134-152 = Verde
        else if (freqporc >= 71 && freqporc <= 80) {
            txt.setBackgroundColor(Color.GREEN);
            txt.setTextColor(Color.BLACK);
        }
        // 153-171 = Amarelo
        else if (freqporc >= 81 && freqporc <= 90) {
            txt.setBackgroundColor(Color.YELLOW);
            txt.setTextColor(Color.BLACK);
        }
        // >172 = Vermelho
        if (freqporc >= 91) {
            txt.setBackgroundColor(Color.RED);
            txt.setTextColor(Color.WHITE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void notify(Device deviceCinta) {
        try {
            if ((deviceCinta.characteristic != null) &&
                    (deviceCinta.descriptor != null)) {
                deviceCinta.gatt.setCharacteristicNotification(deviceCinta.characteristic, true);
                deviceCinta.gatt.writeDescriptor(deviceCinta.descriptor);
            }
        } catch (Exception e) {
        }
    }

    private void askPermissions() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Configura as permissões
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }

        //liga gps
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GPSEnabled){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

    }

    //########## Abertura de Tela ################
    public void openMainActivy() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //############### METODO DO GPS ########################
    public boolean getbolLatLong(Double raioTRANS, Double Lat, Double Lng, Double bkpLat, Double bkp_lng) {
        //A finalidade desta função é ativar o envio via SMS da nova coordenada GPS se a nova posição for maior que area referente ao raio configurado
        //Raio da Terra = 6371 km (valor médio)
        //raio de transmissão = configurado (entrada) 1 = 1KM / 0,030 = 30 metros
        //fórumla:
        //deltaLAT = (raioTrans/RaiodaTerra)*(180/PI) - resultado em graus
        //deltaLONG = (rioTrans/(RaiodaTerra * cos(deltaLAT))*(180/PI) - resultado em graus

        Integer iRaioTerra = 6383; //raio da terra - valor calibrado (vide arquivo matlab na pasta doc calibracaoRTerra.mat)

        Double DeltaLAT = ((raioTRANS / iRaioTerra) * (180 / Math.PI));  //resultado em graus

        Double DeltaLNG = (raioTRANS / (iRaioTerra * Math.cos(DeltaLAT))) * (180 / Math.PI);//resultado em graus

        Boolean flag = false;

        if ((Math.abs(Lat) < (Math.abs(bkpLat) - DeltaLAT)) || (Math.abs(Lat) > (Math.abs(bkpLat) + DeltaLAT))) { //verifica se esta fora do raio em latitude
            if ((Math.abs(Lat) < (Math.abs(bkp_lng) - DeltaLNG)) || (Math.abs(Lng) > (Math.abs(bkp_lng) + DeltaLNG))) {
                flag = true;
            }
        }

        return flag;
    }


    public void refresh() {
        Double km = (Speed * 3.6);
        txtDistKM.setText(new DecimalFormat("#.###").format(countDistancia));
        //txtMinKM.setText(calculoPace(chronometer, countDistancia));
        txtKM.setText(new DecimalFormat("#.##").format(km));
    }

    public String calculoPace(Chronometer chronometer, Double distancia) {
        int hora, minuto, segundo, soma, result_fin = 0;
        float result, result_seg, soma_pace = 0;
        String ret = "0";

        try {

            String mChronoText = chronometer.getText().toString();
            String array[] = mChronoText.split(":");

            if (array.length == 3) //hora, minuto, segundo
            {
                hora = Integer.parseInt(array[0]);
                minuto = Integer.parseInt(array[1]);
                segundo = Integer.parseInt(array[2]);
            } else {
                hora = 0;//Integer.parseInt(array[0]);
                minuto = Integer.parseInt(array[0]);
                segundo = Integer.parseInt(array[1]);
            }

            soma = ((hora * 60) + minuto + (segundo / 60));
            result = (float) (soma / distancia);
            result_fin = (int) result;
            result_seg = (result - result_fin) * 60 / 100;

            soma_pace = (float) (result_seg + result_fin);
            if (soma_pace > 0) {
                DecimalFormat df = new DecimalFormat("0.00");
                ret = (df.format((soma_pace)));
            } else
                ret = "0";
        } catch (NumberFormatException e) { //IMPORTANTISSIMO
            e.printStackTrace();
        }
        return ret;
    }

    public double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto) {
        int EARTH_RADIUS_KM = 6378140;
        double firstLatToRad = Math.toRadians(Math.abs(latitudePto));
        double secondLatToRad = Math.toRadians(Math.abs(latitude));

        // Diferença das longitudes
        double deltaLongitudeInRad = Math.toRadians(Math.abs(longitude) - Math.abs(longitudePto));

        // Cálcula da distância entre os pontos
        return Math.abs((Math.acos(Math.cos(firstLatToRad) * Math.cos(secondLatToRad)
                * Math.cos(deltaLongitudeInRad) + Math.sin(firstLatToRad)
                * Math.sin(secondLatToRad))
                * EARTH_RADIUS_KM) / 1000); //converte para km
    }

    public File createFile(File file, String arquivoNome) {
        try {
            File root = Environment.getExternalStorageDirectory();
            file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/monitorado/" + arquivoNome + ".txt");
            file.getParentFile().mkdir(); //abre o arquivo para escrita

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void writeFile(FileOutputStream fileWrite, String txt)
    {
        try {
            //Escreve no arquivo
            fileWrite.write(txt.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closerFile(FileOutputStream fileWrite)
    {
        try
        {
           fileWrite.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void openMonitoradoAnalise() throws InterruptedException {
        if (!flagThreadFronEnd) {
            runThread(false);
            Intent intent = new Intent(this, MonitoradoAnalise.class);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Clique no Botão Finalizar para liberar a análise", Toast.LENGTH_SHORT).show();

    }

    private void runThread(boolean flag) {
        try {
            if (mHandlerFrontEnd != null) {
                if (flag) //on thread
                    mHandlerFrontEnd.start();
                else if (mHandlerFrontEnd.isAlive())//off thread
                    mHandlerFrontEnd.interrupt();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Não foi possivel iniciar ou finalizar o arquivo de análise, tente novamente", Toast.LENGTH_SHORT).show();
        }
    }

    //##################### Classe de device (mac)###############################
    public static class Device {

        public BluetoothGattCharacteristic characteristic;
        public BluetoothGattDescriptor descriptor;
        public BluetoothDevice device;
        public BluetoothGatt gatt;
        public int heartRate = 0;
        public int bufferheartrate[] = new int[7200];
        public int indexBufferHeartRate = 0;
        public String sexo = "M";
        public float peso = 0;
        public double fcmedia_bkp = 1;
        public int fcmedia2, contFreqMedia = 0;
        public double kcal = 0;
        public int freq_branca, freq_azul, freq_verde, freq_amarelo, freq_vermelho = 0;
        public int indiceFrontEnd = 0;
        public String nomeAluno = null;
        public long idade = 0;
        public int tipoGrafico = 0;
        public long tempo_aula;
        public int timeout = 0;
        public boolean flagConect = false;
        public boolean flagLastConnect = false; //guarda o esta da ultima conexão


        public boolean getdevice(BluetoothDevice device, Device cintaDevice) {
            boolean flag = false;
            if ((cintaDevice.device != null) && (cintaDevice.device.equals(device)))
                flag = true;

            return flag;
        }
    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
        }else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                i++;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
                mac_vector[i] = (device.getName() + "\n" + device.getAddress());
                if(flag_ble) {
                    alertListBle();
                }
            }
        }};

    public void alertListBle(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View view = this.getLayoutInflater().inflate(R.layout.dialog_list_ble, null);
            final ListView device_list = (ListView) view.findViewById(R.id.device_list);
            builder.setView(view);
            builder.setTitle("Escolha a Cinta Cardiaca");
            flag_ble = false;

            device_list.setAdapter(btArrayAdapter);

            device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String mac = btArrayAdapter.getItem(position);
                    String[] splitArray = mac.split("\n");
                    String new_mac = splitArray[1];
                    aluno.setCintaMac(new_mac);
                    aluno.setMatricula(aluno.getMatricula()); //matricula statica
                    aluno.setNome(aluno.getNome());
                    aluno.setIdade(aluno.getIdade());
                    aluno.setAltura(aluno.getAltura());
                    aluno.setPeso(aluno.getPeso());
                    aluno.setStatus("PENDENTE");
                    aluno.setSexo(aluno.getSexo());

                    aluno.setEmail(aluno.getEmail());
                    aluno.setId_login(aluno.getId_login());
                    aluno.setPathFoto(aluno.getPathFoto());
                    flag_ble = true;
                    if (helperAluno.updateName(aluno) >= 0) {
                        Toast.makeText(getApplicationContext(), "Salvo com sucesso", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Erro ao salvar", Toast.LENGTH_SHORT).show();
                    //device_list.(id);
                }
            });
            builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    i=0;
                    device_list.removeAllViewsInLayout();
                    btArrayAdapter.notifyDataSetChanged();
                    flag_ble = true;
                    btArrayAdapter = new ArrayAdapter<String>(Monitorado.this, android.R.layout.simple_list_item_1); // ZERAR A LISTA
                    dialog.dismiss();
                    //do things
                }
            });
            builder.create().show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Aguarde...", Toast.LENGTH_SHORT).show();
        }
        }
    }


