package com.example.avfisica.treinoClass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.avfisica.BlueToothBLE;
import com.example.avfisica.Chronometro;
import com.example.avfisica.MainActivity;
import com.example.avfisica.Monitorado;
import com.example.avfisica.R;
import com.example.avfisica.Register;
import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.treino.CargaPeso;
import com.example.avfisica.models.treino.Exercicio;
import com.example.avfisica.models.treino.Ficha;
import com.example.avfisica.models.treino.FichaItens;
import com.example.avfisica.models.treino.TreinoModel;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.treino.CargaPesoResource;
import com.example.avfisica.resources.treino.ExercicioResource;
import com.example.avfisica.resources.treino.FichaItensResource;
import com.example.avfisica.resources.treino.FichaResource;
import com.example.avfisica.resources.treino.TreinoResource;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Treino extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int TIMEOUT_SEMAFARO_CONNECT = 10;
    private static final int TIMEOUT_CONNECT = 10;

    Button  btn_iniciar;
    ImageButton btn_edit;
    TableLayout table_exercicio_itens;

    TreinoResource helperTreino;
    FichaResource helperFicha;
    FichaItensResource helperFichaItens;
    ExercicioResource helperExercicio;
    CargaPesoResource helperCargaPeso;
    AlunoResource helperAluno;

    List<TreinoModel> ltreino;
    List<Ficha> lficha;
    List<FichaItens> lfichaItens;
    List<CargaPeso> lcargaPesoItens;
    List<Repeticao> lrepeticao;

    TreinoModel treino;
    Ficha ficha;
    FichaItens fichaItens;
    Aluno aluno ;

    BlueToothBLE deviceBle = null;
    BluetoothAdapter mBluetoothAdapter;
    Thread mHandlerFrontEnd = null;
    static int loopThread = 0;
    boolean flagThreadFronEnd = false;
    int countSemafaroConnect = TIMEOUT_SEMAFARO_CONNECT;
    int nextIndiceDevice = 0; //próximo indice da fila a se conectar
    int indice_bkp = 0;
    TextView mHearRate;


    boolean flagUpdate = false;
    long exercicio_id=0; //utilizado na tela de repetições

    long idLastTreino = 0;
    byte linha = 0;
    int getid_Treino = 0;
    int id_imageView;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treino);

        btn_iniciar = (Button) findViewById(R.id.button_iniciar);
        table_exercicio_itens = (TableLayout) findViewById(R.id.table_exercicio_itens);
        btn_edit = (ImageButton) findViewById(R.id.imageButton_Ficha_Editar);

        helperTreino = new TreinoResource(this);
        helperCargaPeso = new CargaPesoResource(this);
        helperFicha = new FichaResource(this);
        helperFichaItens = new FichaItensResource(this);
        helperExercicio = new ExercicioResource(this);
        helperAluno = new AlunoResource(this);

        ficha = new Ficha();
        fichaItens = new FichaItens();
        treino = new TreinoModel();
        aluno = new Aluno();

        ltreino = new ArrayList<TreinoModel>();
        lficha = new ArrayList<Ficha>();
        lfichaItens = new ArrayList<FichaItens>();
        lcargaPesoItens = new ArrayList<CargaPeso>();
        lrepeticao = new ArrayList<Repeticao>();

        this.deviceBle = new BlueToothBLE(this);

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
        {
            this.deviceBle.cintaDevice.device = mBluetoothAdapter.getRemoteDevice(aluno.getCintaMac()); //"C2:ED:E2:32:16:22"

        }


        //seleção treino
        btn_iniciar.setVisibility(View.VISIBLE);
        preencheListaFicha();
        if(CustomAdapterTreino.id_treino_adpter>=0) {
            getid_Treino = CustomAdapterTreino.id_treino_adpter;
            CustomAdapterTreino.id_treino_adpter = -1;
            consultaFicha(getid_Treino);
        }

        //Button iniciar
        btn_iniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override

            public void onClick(View v)
            {
                try
                {
                    if (btn_iniciar.getText().equals("Iniciar")) {
                        final String currentTime = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                        treino.setId_ficha(ficha.getId_ficha());
                        treino.setId_login(Register.id_login);
                        treino.setStatus("PENDENTE");

                        //novo treino
                        treino.setData(currentTime);
                        treino.setId_treino(helperTreino.insertData(treino));
                        if (treino.getId_treino() == 0) //salva treino
                            Toast.makeText(getApplicationContext(), "Erro ao Iniciar o treino!", Toast.LENGTH_SHORT).show();
                        else {
                            btn_iniciar.setVisibility(View.INVISIBLE);
                            recordRepetAndChangeMode();
                        }
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Erro ao Iniciar o treino!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //button editar
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editFichas();
                } catch (Exception e) {
                }
            }
        });

        //### Tread para atualizaçao do batimento cardiaco #############
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

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        preencheListaFicha();
    }

    public void preencheListaFicha() {
        lficha = helperFicha.getFichaAtivo(1); //consulta da ficha ativas
    }


    //############# tabela de ficha itens ####################
    public void cabecalhoTabela() {
        //cabeçalho da tabela
        TableRow tr_ = new TableRow(this);
        tr_.setBackgroundColor(Color.rgb(179, 206, 250));
        table_exercicio_itens.addView(tr_);

        TextView tv0 = new TextView(this);//exibir imagem
        table_exercicio_itens.setColumnStretchable(0, true);
        TextView tv1 = new TextView(this);//Exercicio

        //tv0.setText("Img");
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setGravity(Gravity.CENTER);

        //tv1.setText("Exercício");
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setGravity(Gravity.CENTER);

        tr_.addView(tv0);
        tr_.addView(tv1);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")
    public void preencherDados(final FichaItens fichaItens, byte linha, long idLastTreino) {
        Exercicio exercicio_aux = new Exercicio();
        int qtdExecutado = 0;

        TableRow tr = new TableRow(this);
        TableRow.LayoutParams lp = new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

        lp.setMargins(15, 15, 10, 15);
        tr.setLayoutParams(lp);

        CardView cardViewCol1 = new CardView(this);
        CardView cardViewCol2 = new CardView(this);
        CardView cardViewCol3 = new CardView(this);
        final ImageView imageViewExibir = new ImageView(this);
        final TextView tv1 = new TextView(this);//exercicio
        final TextView tv2 = new TextView(this);//quantidade

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (180 * scale + 0.5f);

        //Colunas
        CardView.LayoutParams lpCardCol1 = new CardView.LayoutParams(width/5,pixels/3);
        cardViewCol1.setRadius(0);
        cardViewCol1.setPadding(15, 15, 10, 15);

        imageViewExibir.setPadding(20, 20, 20, 20);
        imageViewExibir.setId(linha);
        imageViewExibir.setMaxWidth(pixels);
        imageViewExibir.setMaxHeight(pixels/3);
        imageViewExibir.setForegroundGravity(Gravity.CENTER);
        imageViewExibir.setLayoutParams(lpCardCol1);

        CardView.LayoutParams lpCardCol2 = new CardView.LayoutParams(width/2,pixels/3);
        tv1.setPadding(20, 20, 20, 20);
        tv1.setId(linha);
        tv1.setGravity(Gravity.LEFT);
        tv1.setText((helperExercicio.getExercicioID(fichaItens.getId_exercicio())).getNome());
        tv1.setLines(2);
        tv1.setWidth(pixels);
        tv1.setHeight(pixels/3);
        tv1.setAutoSizeTextTypeUniformWithConfiguration(2,20,2, TypedValue.COMPLEX_UNIT_DIP);
        tv1.setLayoutParams(lpCardCol2);
        tv1.setTextColor(Color.BLUE);
        tv1.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));

        FichaItens fichaItensRept = new FichaItens();
        fichaItensRept = lfichaItens.get(tv1.getId());
        CardView.LayoutParams lpCardCol3 = new CardView.LayoutParams(width/5, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv2.setPadding(20, 20, 20, 20);
        tv2.setId(linha);
        tv2.setGravity(Gravity.LEFT);
        tv2.setText("série: " + Long.toString(fichaItensRept.getQuantidade()) + "\n"+"rept: " + Long.toString(fichaItensRept.getRepeticao()));
        tv2.setLines(2);
        tv2.setWidth(pixels/2);
        tv2.setHeight(pixels/3);
        tv2.setAutoSizeTextTypeUniformWithConfiguration(2,20,2, TypedValue.COMPLEX_UNIT_DIP);
        tv2.setLayoutParams(lpCardCol3);
        tv2.setTextColor(Color.BLUE);
        tv2.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));

        //muda a cor da linha se todas as series forem executadas
        qtdExecutado = (helperCargaPeso.getCargaPesoExecutado(Long.toString(treino.getId_treino()),
                Long.toString(helperExercicio.getExercicioID(fichaItens.getId_exercicio()).getId_excercicio())));
        if ((qtdExecutado >= fichaItens.getQuantidade()) && (qtdExecutado!=0)) //igualdade
        {
            ShapeDrawable border = new ShapeDrawable(new RectShape());
            border.getPaint().setStyle(Paint.Style.STROKE);
            border.getPaint().setColor(Color.GREEN);
            border.getPaint().setStrokeWidth(5);
            tv1.setBackground(border);
        }
        else if ((qtdExecutado>0) && (qtdExecutado < fichaItens.getQuantidade())) //range
        {
            ShapeDrawable border = new ShapeDrawable(new RectShape());
            border.getPaint().setStyle(Paint.Style.STROKE);
            border.getPaint().setColor(Color.YELLOW);
            border.getPaint().setStrokeWidth(5);
            tv1.setBackground(border);
        }

        try {
            exercicio_aux = helperExercicio.getExercicioID(fichaItens.getId_exercicio());

            if (exercicio_aux.getPath_gif().contains(".gif")) {
                Glide.with(Treino.this).load(exercicio_aux.getPath_gif())
                        .apply(new RequestOptions().override(100, 100))
                        .into(imageViewExibir);
            }
            else if (exercicio_aux.getPath_gif().contains(".jpg"))
            {
                Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(exercicio_aux.getPath_gif()), 100,100, true);
                imageViewExibir.setImageBitmap(myBitmap);
            }
            else
            {
                int resourceId = getResources().getIdentifier(exercicio_aux.getDrawable(), "drawable", getPackageName());
                Glide.with(Treino.this).load(resourceId)
                        .apply(new RequestOptions().override(100, 100))
                        .into(imageViewExibir);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        //exibe a tabela
        cardViewCol1.addView(imageViewExibir);
        tr.addView(cardViewCol1);
        cardViewCol2.addView(tv1);
        tr.addView(cardViewCol2);
        cardViewCol3.addView(tv2);
        tr.addView(cardViewCol3);

        table_exercicio_itens.addView(tr);

        //os dos metodos abaixo tem a mesma função possibilitando o usuario clicar na imagem e no texto
        //botão exibir
        imageViewExibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Exercicio exercicio = new Exercicio();
                    long idLastTreino = helperTreino.getLastTreino(Long.toString(treino.getId_ficha()));
                    FichaItens fichaItensRept = new FichaItens();

                    exercicio = helperExercicio.getExercicioID(lfichaItens.get(imageViewExibir.getId()).getId_exercicio());
                    fichaItensRept = lfichaItens.get(imageViewExibir.getId());

                    id_imageView = (imageViewExibir.getId()); // ID PARA PUXAR REPETIÇÃO
                    alertTreino(id_imageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //botão exibir
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Exercicio exercicio = new Exercicio();
                    long idLastTreino = helperTreino.getLastTreino(Long.toString(treino.getId_ficha()));
                    FichaItens fichaItensRept = new FichaItens();

                    exercicio = helperExercicio.getExercicioID(lfichaItens.get(tv1.getId()).getId_exercicio());
                    fichaItensRept = lfichaItens.get(tv1.getId());

                    id_imageView = (tv1.getId()); // ID PARA PUXAR REPETIÇÃO
                    alertTreino(id_imageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //editar
    public void editFichas() {
        List<String> spinnerArray_data = new ArrayList<String>();
        ltreino = helperTreino.getData(Register.id_login);

        for (int i = 0; i < (ltreino.size()); i++)
        {
            //parse data
            String datatreino = ltreino.get(i).getData();
            String datatreinoParse="";
            if (datatreino.length()==8) { //ex 20210626
                datatreinoParse = (datatreino.substring(6, 8));
                datatreinoParse += "/" + (datatreino.substring(4, 6));
                datatreinoParse += "/" + (datatreino.substring(0, 4));
            }
            else //ex 2021062
            {
                datatreinoParse = (datatreino.substring(7, 8));
                datatreinoParse += "/" + (datatreino.substring(5, 6));
                datatreinoParse += "/" + (datatreino.substring(0, 4));
            }

            spinnerArray_data.add(datatreinoParse + "-"+ helperFicha.getFichaByIdFicha(Long.toString(ltreino.get(i).getId_ficha())).getNome());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Treino.this);

        View mView = Treino.this.getLayoutInflater().inflate(R.layout.spinner_aux, null);
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);

        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(Treino.this,
                android.R.layout.simple_spinner_item, spinnerArray_data); // Chamar o ARRAY
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Atribui o adapter ao spinner
        mSpinner.setAdapter(adapter_1);
        builder.setTitle("Escolha o treino realizado");
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int getid = mSpinner.getSelectedItemPosition();
                flagUpdate = false;
                updateTreino(getid);
                return;
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void updateTreino(int indiceLtreino)
    {
        if (ltreino.size() > 0)
        {
            if (!flagUpdate) {
                treino.setId_treino(ltreino.get(indiceLtreino).getId_treino());
                treino.setId_ficha(ltreino.get(indiceLtreino).getId_ficha());
                treino.setData(ltreino.get(indiceLtreino).getData());
                treino.setId_login(ltreino.get(indiceLtreino).getId_login());
                treino.setStatus(ltreino.get(indiceLtreino).getStatus());
            }
            ficha = helperFicha.getFichaByIdFicha(Long.toString(treino.getId_ficha()));

            lfichaItens.clear();
            lfichaItens = helperFichaItens.getFichaItens(Long.toString(treino.getId_ficha()));

            table_exercicio_itens.removeAllViews();
            //cabecalhoTabela();
            byte linha = 0;
            for (FichaItens fichaItens : lfichaItens) {
                preencherDados(fichaItens, linha,treino.getId_treino());
                linha++;
            }
        }
        flagUpdate = true;
        btn_iniciar.setVisibility(View.INVISIBLE);
    }

    private String verifyTreino(Ficha ficha)
    {
        //Tratamento, para consultar por (data e ficha) e avisar a usuario caso ja exista este treino
        final String currentTime_aux = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String indiceLtreino = "null";

        ltreino = helperTreino.getData(Register.id_login);
        int indice = 0;
        for (TreinoModel treino_aux:ltreino)
        {
            if (treino_aux.getData().equals(currentTime_aux))
            {
                if(ficha.getId_ficha()== treino_aux.getId_ficha())
                {
                    indiceLtreino = Long.toString(indice);
                    break;
                }
            }
            indice++;
        }
        return  indiceLtreino;
    }

    public void cabecalhoTabelaRepeticao(TableLayout table) {
        //cabecalho
        TableRow tr_ = new TableRow(this);
        tr_.setBackgroundColor(Color.rgb(179, 206, 250));
        tr_.setPadding(4,2,2,2);

        TextView tv0 = new TextView(this);//Repetição
        table.setColumnStretchable(0, true);
        TextView tv1 = new TextView(this);//peso
        table.setColumnStretchable(1, true);
        TextView tv2 = new TextView(this);//Status

        tv0.setText("Rept");
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setGravity(Gravity.CENTER);

        tv1.setText("Peso");
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setGravity(Gravity.CENTER);

        tr_.addView(tv0);
        tr_.addView(tv1);

        table.addView(tr_);
    }

    public void preencherDadosRepeticao(TableLayout table, FichaItens fichaIt, TextView exercicoName, long idLastTreino, Button mButtonSalvar, CheckBox checkBox_all, TextView obs)
    {
        List<CargaPeso> lcargaPeso =  new ArrayList<CargaPeso>();
        boolean flagUltimoExerc = false;
        Exercicio exercicio = new Exercicio();

        exercicio = helperExercicio.getExercicioID(fichaIt.getId_exercicio());
        exercicio_id = exercicio.getId_excercicio();
        exercicoName.setText(exercicio.getNome());
        exercicoName.setTextSize(15);
        exercicoName.setLines(2);
        exercicoName.setGravity(View.TEXT_ALIGNMENT_CENTER);

        obs.setText(fichaIt.getObs());
        obs.setTextSize(12);
        obs.setLines(2);
        obs.setGravity(View.TEXT_ALIGNMENT_CENTER);
        obs.setTextColor(Color.RED);

        //lista da carga referente ao ultimo treino
        if (flagUpdate) //treino atual
        {
            lcargaPeso = helperCargaPeso.getCargaPeso(Long.toString(treino.getId_treino()), Long.toString(exercicio_id));
            if (lcargaPeso.size()==0) //carrega o ultimo treino
            {
                lcargaPeso = helperCargaPeso.getCargaPeso(Long.toString(idLastTreino), Long.toString(exercicio_id));
                flagUltimoExerc = true;
            }
        }
        else if (idLastTreino>0) //verifica se houve o ultimo treino da lista selecionada
        {
            lcargaPeso = helperCargaPeso.getCargaPeso(Long.toString(idLastTreino), Long.toString(exercicio_id));
            flagUltimoExerc = true;
        }

        if (lrepeticao.size()>0)
            lrepeticao.removeAll(lrepeticao);

        for (int linha=0; linha<fichaIt.getQuantidade(); linha++)
        {
            //cria  linha
            Repeticao rept = new Repeticao();
            lrepeticao.add(rept);
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams lp = new
                    TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT ,TableRow.LayoutParams.WRAP_CONTENT);

            tr.setLayoutParams(lp);
            tr.setPadding(2,2,2,2);

            //Colunas
            final EditText editTextRept = new EditText(this);
            editTextRept.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextRept.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
            editTextRept.setWidth(200);
            if ((lcargaPeso!=null) && (linha<lcargaPeso.size()) && lcargaPeso.size()>0)
            {
                editTextRept.setText(Long.toString(lcargaPeso.get(linha).getRepeticao()));
                lrepeticao.get(linha).id_carga_peso = (int)lcargaPeso.get(linha).getId_carga_peso();
            }
            else {
                lfichaItens = helperFichaItens.getFichaItens(Long.toString(ficha.getId_ficha()));
                editTextRept.setText(Long.toString(lfichaItens.get(id_imageView).getRepeticao()));
                //editTextRept.setText("0");
                lrepeticao.get(linha).id_carga_peso = 0;
            }
            editTextRept.setId(linha);
            editTextRept.setGravity(Gravity.CENTER);
            lrepeticao.get(linha).rept = Integer.parseInt(editTextRept.getText().toString());

            final EditText editTextPeso = new EditText(this);
            editTextPeso.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextPeso.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
            editTextPeso.setWidth(200);
            if ((lcargaPeso!=null) && (linha<lcargaPeso.size())&& lcargaPeso.size()>0)
            {
                editTextPeso.setText(Float.toString(lcargaPeso.get(linha).getPeso()));
            }
            else {
                editTextPeso.setText("0");
            }
            lrepeticao.get(linha).peso = Float.parseFloat(editTextPeso.getText().toString());
            editTextPeso.setId(linha);
            editTextPeso.setGravity(Gravity.CENTER);

            final RadioButton radioStatus = new RadioButton(this);
            final RadioButton radioStatus_aux = new RadioButton(this);
            radioStatus.setGravity(Gravity.CENTER);

            if ((lcargaPeso!=null) && (linha<lcargaPeso.size()) && lcargaPeso.size()>0)
            {
                if ((lcargaPeso.get(linha).getFlagExecutado()==1) & (!flagUltimoExerc)) {
                    radioStatus.setChecked(true);
                    radioStatus_aux.setChecked(false); //próxima ação
                }
                else {
                    radioStatus.setChecked(false);
                    radioStatus_aux.setChecked(true); // próxima ação
                }
            }else{
                radioStatus.setChecked(false);
                radioStatus_aux.setChecked(true);
            }
            radioStatus.setId(linha);
            radioStatus_aux.setId(linha);
            lrepeticao.get(linha).flagExecutado = radioStatus.isChecked()?1 : 0;

            tr.addView(editTextRept);
            tr.addView(editTextPeso);
            tr.addView(radioStatus);

            table.addView(tr);

            //radio button da lista
            radioStatus.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v)
                {
                    int status_checked = 0;
                    if (radioStatus_aux.isChecked() == radioStatus.isChecked())
                    {
                        radioStatus.setChecked(true);
                        radioStatus_aux.setChecked(false);
                    }
                    else
                    {
                        radioStatus.setChecked(false);
                        radioStatus_aux.setChecked(true);
                    }
                    status_checked = radioStatus.isChecked()?1 : 0;
                    if (!((editTextPeso.getText().equals("")) ||
                            (editTextPeso.getText().equals(""))))
                        updateListRepeticao(editTextRept, editTextPeso, status_checked);
                }
            });

            editTextRept.addTextChangedListener(new TextWatcher()
            {
                String aux = editTextRept.getText().toString();

                public void afterTextChanged(Editable s)
                {
                    if (!s.equals(aux)) {
                        try {
                            updateListRepeticao(editTextRept, editTextPeso, radioStatus.isChecked() ? 1 : 0);
                        }catch(Exception ex){};
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after){}

                public void onTextChanged(CharSequence s, int start, int before, int count){}
            });


            editTextPeso.addTextChangedListener(new TextWatcher()
            {
                String aux = editTextPeso.getText().toString();

                public void afterTextChanged(Editable s)
                {
                    if (!s.equals(aux)) {
                        try {
                            updateListRepeticao(editTextRept, editTextPeso, radioStatus.isChecked() ? 1 : 0);
                        }catch(Exception ex){};
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after){}

                public void onTextChanged(CharSequence s, int start, int before, int count){}
            });


            mButtonSalvar.setOnClickListener(new View.OnClickListener()
            {
                @Override

                public void onClick(View v)
                {
                    try
                    {
                        if (lrepeticao.size()>0)
                        {
                            processCrudRepeticao(lrepeticao);
                        }
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Erro ao Iniciar o treino!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final int finalLinha = linha;
            checkBox_all.setOnClickListener(new View.OnClickListener()
            {
                @Override

                public void onClick(View v)
                {
                    try
                    {
                        for (Repeticao rept:lrepeticao)
                        {
                            rept.flagExecutado = 1;
                        }
                        if (lrepeticao.size()>0)
                            processCrudRepeticao(lrepeticao);

                        //alertTreino(id_imageView); //atualiza a tela
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Erro ao Iniciar o treino!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    public void updateListRepeticao(EditText editTextRept, EditText editTextPeso, int status )
    {
        lrepeticao.get(editTextRept.getId()).rept = Long.parseLong(editTextRept.getText().toString());
        lrepeticao.get(editTextPeso.getId()).peso = Float.parseFloat(editTextPeso.getText().toString());
        lrepeticao.get(editTextRept.getId()).flagExecutado = status;
    }

    public boolean processCrudRepeticao(List<Repeticao> lrepeticao ) {
        boolean flag = true;
        for (int linha=0; linha< lrepeticao.size(); linha++)
        {
            CargaPeso cargaPeso = new CargaPeso();

            if (lrepeticao.get(linha).id_carga_peso == 0) //novo registro
            {
                try {

                    cargaPeso.setId_treino(treino.getId_treino());
                    cargaPeso.setId_exercicio(exercicio_id);
                    cargaPeso.setRepeticao(lrepeticao.get(linha).rept);
                    cargaPeso.setPeso(lrepeticao.get(linha).peso);
                    cargaPeso.setId_login(Register.id_login);
                    cargaPeso.setStatus("PENDENTE");
                    cargaPeso.setFlagExecutado(lrepeticao.get(linha).flagExecutado);
                    if (!(helperCargaPeso.insertData(cargaPeso) > 0)) {
                        Toast.makeText(getApplicationContext(), "Erro ao inserir a repetição na lista!", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao inserir a repetição na lista!", Toast.LENGTH_SHORT).show();
                    flag = false;
                }

            } else  //update
            {
                try {
                    cargaPeso.setId_carga_peso(lrepeticao.get(linha).id_carga_peso);
                    cargaPeso.setId_treino(treino.getId_treino());
                    cargaPeso.setId_exercicio(exercicio_id);
                    cargaPeso.setRepeticao(lrepeticao.get(linha).rept);
                    cargaPeso.setPeso(lrepeticao.get(linha).peso);
                    cargaPeso.setId_login(Register.id_login);
                    cargaPeso.setStatus("PENDENTE");
                    cargaPeso.setFlagExecutado(lrepeticao.get(linha).flagExecutado);
                    if (helperCargaPeso.updateName(cargaPeso) == 0) {
                        Toast.makeText(getApplicationContext(), "Erro ao Salvar a lista de exercício!", Toast.LENGTH_SHORT).show();
                        flag = false;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar a lista de exercício!", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
        }
        return  flag;
    }

    public class Repeticao
    {
        long id_carga_peso=0;
        long rept = 0;
        float peso = 0;
        int flagExecutado = 0; //0= não executado , 1 - executado
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void consultaFicha(int getid_){
        String indiceLtreino = verifyTreino(lficha.get(getid_));

        if (indiceLtreino.equals("null")) //novo treino
        {
            ficha = helperFicha.getFicha(lficha.get(getid_).getNome());
            if (ficha.getId_ficha() > 0)//existe a ficha
            {
                table_exercicio_itens.removeAllViews();

                btn_iniciar.setText("Iniciar");
                btn_iniciar.setEnabled(true);

                lfichaItens.clear();
                lfichaItens = helperFichaItens.getFichaItens(Long.toString(ficha.getId_ficha()));

                for (FichaItens fichaItens : lfichaItens) {
                    preencherDados(fichaItens, linha, idLastTreino);
                    linha++;
                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Treino já existente no dia!", Toast.LENGTH_SHORT).show();
            btn_iniciar.setEnabled(false);
            flagUpdate = false;
            updateTreino(Integer.parseInt(indiceLtreino));

        }
    }

    public void alertTreino(int id){
        try {
            Exercicio exercicio = new Exercicio();
            long idLastTreino = helperTreino.getLastTreino(Long.toString(treino.getId_ficha()));
            FichaItens fichaItensRept = new FichaItens();

            exercicio = helperExercicio.getExercicioID(lfichaItens.get(id).getId_exercicio());
            fichaItensRept = lfichaItens.get(id);

            id_imageView = (id); // ID PARA PUXAR REPETIÇÃO

            final AlertDialog.Builder builder = new AlertDialog.Builder(Treino.this);
            View mView = Treino.this.getLayoutInflater().inflate(R.layout.dialog_exercicio_image, null);

            final ImageView mImageExercicio = (ImageView) mView.findViewById(R.id.imageViewExercicio);
            final TableLayout mtable_exercicio = (TableLayout) mView.findViewById(R.id.table_exercicio_repeticao);
            final TextView mExercicioName = (TextView) mView.findViewById(R.id.txtExercicioName);
            final Button mbuttonSalvar = (Button) mView.findViewById(R.id.button_salvar);
            final CheckBox checkBox_all = (CheckBox) mView.findViewById(R.id.checkBox_all_exercicio);
            final TextView mtxtObs = (TextView) mView.findViewById(R.id.txtObs);
            final Chronometro chronometro = new Chronometro(this, (Chronometer) (Chronometer)mView.findViewById(R.id.chronoTreino));
            final ImageButton mbuttonResetCrono = (ImageButton) mView.findViewById(R.id.imageCronometro);

            startCintaBle((TextView) mView.findViewById(R.id.txtHeartRate));

            //reset do objeto front-end
            mImageExercicio.setImageBitmap(null);
            mImageExercicio.destroyDrawingCache();
            mImageExercicio.setImageURI(null);
            mbuttonSalvar.setVisibility(View.INVISIBLE);
            mtxtObs.setText(fichaItens.getObs());
            chronometro.createChronometer();
            chronometro.start();

            //gif default de instalação
            if ((exercicio.getPath_gif()==null) || (exercicio.getPath_gif().equals("-")) )
            {
                int resourceId = getResources().getIdentifier(exercicio.getDrawable(), "drawable", getPackageName());
                Glide.with(Treino.this).load(resourceId).into(mImageExercicio);
            }
            else if (exercicio.getPath_gif().contains(".gif")) //gif customizado
            {
                Glide.with(Treino.this).load(exercicio.getPath_gif()).into(mImageExercicio);
            }
            else//imagem customizada
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(exercicio.getPath_gif());
                mImageExercicio.setImageBitmap(myBitmap);
            }

            //preenche a tabela de repetições se o treino for iniciado
            if (treino.getId_treino()>0)
            {
                cabecalhoTabelaRepeticao(mtable_exercicio);
                preencherDadosRepeticao(mtable_exercicio, fichaItensRept, mExercicioName, idLastTreino, mbuttonSalvar,checkBox_all, mtxtObs);
                builder.setView(mView);
                builder.setPositiveButton("Próximo", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (lrepeticao.size()>0)
                            processCrudRepeticao(lrepeticao);

                        if ((id_imageView+1) < lfichaItens.size())
                            alertTreino(id_imageView+1); // AVALIAR ESSA SOLUÇÃO
                        else
                            updateTreino(getid_Treino);

                    }
                });

                builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (lrepeticao.size()>0)
                            processCrudRepeticao(lrepeticao);

                        if ((id_imageView-1) >= 0)
                            alertTreino(id_imageView-1); // AVALIAR ESSA SOLUÇÃO
                        else
                            updateTreino(getid_Treino);
                    }
                });

                builder.setNeutralButton("Sair", new DialogInterface.OnClickListener()     {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (lrepeticao.size()>0)
                            processCrudRepeticao(lrepeticao);

                        updateTreino(getid_Treino);

                        dialog.cancel();
                    }
                });

                builder.show();
            }
            else
                Toast.makeText(getApplicationContext(), "Treino não iniciado!", Toast.LENGTH_SHORT).show();

            //reset do cronometro
            mbuttonResetCrono.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v)
                {
                    chronometro.reset();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recordRepetAndChangeMode()
    {
        List<CargaPeso> lcargaPeso =  new ArrayList<CargaPeso>();

        long idLastTreino = helperTreino.getLastTreino(Long.toString(treino.getId_ficha()));
        if (idLastTreino>0)
        {
            lrepeticao.removeAll(lrepeticao);

            for (FichaItens fitens:lfichaItens)
            {
                lcargaPeso = helperCargaPeso.getCargaPeso(Long.toString(idLastTreino), Long.toString(fitens.getId_exercicio()));
                if (lcargaPeso.size()==0) //preenche Lista com informaçoes da fichaItens
                {
                    for (int loop=0; loop<=fitens.getQuantidade(); loop++)
                    {
                        Repeticao objRept = new Repeticao();

                        objRept.id_carga_peso = 0;
                        objRept.rept = fitens.getRepeticao();
                        objRept.peso = 0;
                        objRept.flagExecutado = 0;

                        lrepeticao.add(objRept);
                    }
                }
                else //preenche lista com informações de peso do ultimo treino
                {
                    for (CargaPeso cargaPeso: lcargaPeso)
                    {
                        Repeticao objRept = new Repeticao();

                        objRept.id_carga_peso = 0;
                        objRept.rept = cargaPeso.getRepeticao();
                        objRept.peso = cargaPeso.getId_carga_peso();
                        objRept.flagExecutado = 0;

                        lrepeticao.add(objRept);
                    }
                }
            }
        }
        else //nao existe ultimo treino ou é a primeira vez que executa este modulo apos a instalação
        {
            for (FichaItens fitens:lfichaItens)
            {
                for (int loop=0; loop<=fitens.getQuantidade(); loop++)
                {
                    Repeticao objRept = new Repeticao();

                    objRept.id_carga_peso = 0;
                    objRept.rept = fitens.getRepeticao();
                    objRept.peso = 0;
                    objRept.flagExecutado = 0;

                    lrepeticao.add(objRept);
                }
            }
        }

        if (lrepeticao.size()>=0) {
            if (processCrudRepeticao(lrepeticao))
                flagUpdate = true; //muda o modo para update
        }
        else
            flagUpdate = false; //muda o modo para insert
    }

    private void startCintaBle(TextView mHearRate)
    {
        this.mHearRate = mHearRate;
        //libera a thread
        this.flagThreadFronEnd = true;
        askPermissions();
        runThread(true);
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
       } catch (Exception e) {
            Log.v("erro", e.toString());
        }
    }

    private void updateCinta(BlueToothBLE blueToothBLE) {
        try {
            if (blueToothBLE.cintaDevice.flagConect)
            {
                //Atualiza o Heartrate
                setLineCor(this.mHearRate, blueToothBLE.cintaDevice);
                this.mHearRate.setText(String.valueOf(blueToothBLE.cintaDevice.heartRate) + "bpm");
            }

        } catch (Exception e) {
            Log.v("Update Front End Erro=", e.toString());
        }
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

    private void setLineCor(TextView txt, Monitorado.Device cintaDevice) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Selecao.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}