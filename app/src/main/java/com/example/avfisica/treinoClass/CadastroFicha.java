package com.example.avfisica.treinoClass;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.avfisica.R;
import com.example.avfisica.Register;
import com.example.avfisica.models.treino.Exercicio;
import com.example.avfisica.models.treino.Ficha;
import com.example.avfisica.models.treino.FichaItens;
import com.example.avfisica.resources.treino.ExercicioResource;
import com.example.avfisica.resources.treino.FichaItensResource;
import com.example.avfisica.resources.treino.FichaResource;
import com.google.android.material.chip.Chip;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CadastroFicha extends Activity implements PickiTCallbacks {
    Button btn_salvar, btn_exportar, btn_importar;
    ImageButton btn_edit;
    TableLayout table_ficha_itens;
    EditText editText_nome;
    Chip chip_peito,chip_ombro,chip_costas,chip_perna, chip_braço, chip_abd, chip_aero, chip_exibir;


    FichaResource helperFicha;
    FichaItensResource helperFichaItens;
    ExercicioResource helperExercicio;
    List<Exercicio> lExercicio;
    List<Ficha> lficha;
    List<FichaItens> lfichaItens;

    Ficha ficha;
    FichaItens fichaItens;

    static String tipo_exercicio = "default";
    boolean flagUpdate = false;
    boolean flag_dial_exe = false;
    final public long DEFAULT_EDITAR = 1000;
    long fichaItens_id_editar = DEFAULT_EDITAR;
    int getid;
    int getid_tv;
    boolean flag_teste = false;


    //FILE
    FileOutputStream fileWrite = null;
    File file = null;
    private static final int PICK_TXT_FILE = 2;

    //Declare PickiT
    PickiT pickiT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_ficha);

        btn_salvar = (Button) findViewById(R.id.button_cadastrar);
        btn_importar = (Button) findViewById(R.id.btnImportar);
        btn_exportar = (Button) findViewById(R.id.btnExportar);
        editText_nome = (EditText) findViewById(R.id.editText_name);
        table_ficha_itens = (TableLayout) findViewById(R.id.table_ficha_itens);
        btn_edit = (ImageButton) findViewById(R.id.imageButton_Ficha_Editar);

        chip_peito = (Chip) findViewById(R.id.chip_peito);
        chip_ombro = (Chip) findViewById(R.id.chip_ombro);
        chip_costas = (Chip) findViewById(R.id.chip_costas);
        chip_perna = (Chip) findViewById(R.id.chip_perna);
        chip_braço = (Chip) findViewById(R.id.chip_braço);
        chip_abd = (Chip) findViewById(R.id.chip_abdominal);
        chip_aero = (Chip) findViewById(R.id.chip_aerobico);
        chip_exibir = (Chip) findViewById(R.id.chipExibir);

        helperFicha = new FichaResource(this);
        helperFichaItens = new FichaItensResource(this);
        helperExercicio = new ExercicioResource(this);
        ficha = new Ficha();
        fichaItens = new FichaItens();

        lExercicio = new ArrayList<Exercicio>();
        lficha = new ArrayList<Ficha>();
        lfichaItens = new ArrayList<FichaItens>();


        //Initialize PickiT
        //context, listener, activity
        pickiT = new PickiT(this, this, this);
        isReadStoragePermissionGranted();
        isWriteStoragePermissionGranted();

        //###### Inicialização ##########
        cabecalhoTabela();

        chip_exibir.setChecked(true);

        //seleção treino --> Verifica com Azola
        if(CustomAdpaterExercicio.id_exercicio_adpter>=0) {
            getid = CustomAdpaterExercicio.id_exercicio_adpter;
        }else dialogFicha(); // Inserir nome e descrição --> tentei ser intuitivo p/ usuario
        // chip-5km
        chip_peito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_peito.isChecked()) {
                        flag_dial_exe=true;
                        tipo_exercicio = "peito";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_peito.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-5km
        chip_abd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_abd.isChecked()) {
                        tipo_exercicio = "abdominal";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_abd.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-costa
        chip_costas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_costas.isChecked()) {
                        tipo_exercicio = "costa";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_costas.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-ombro
        chip_ombro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_ombro.isChecked()) {
                        tipo_exercicio = "ombro";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_ombro.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        // chip-5km
        chip_perna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_perna.isChecked()) {
                        tipo_exercicio = "perna";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_perna.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        //chip-braço
        chip_braço.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_braço.isChecked()) {
                        tipo_exercicio = "braço";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_braço.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        //chip-aerobico
        chip_aero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // not and give the suitable Toast message
                    if (chip_aero.isChecked()) {
                        tipo_exercicio = "aerobico";
                        openCadastroExercicioQuery(tipo_exercicio);
                        chip_aero.setChecked(false);
                    } else {

                    }
                } catch (Exception e) {
                }
            }
        });

        //chip-exibir
        chip_exibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ficha.setFlagAtivo(0);
                    if (chip_exibir.isChecked())
                        ficha.setFlagAtivo(1);
                } catch (Exception e) {
                }
            }
        });


        //Button salvar
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!editText_nome.getText().toString().isEmpty()) {
                    //    if (!(helperFicha.getFicha(editText_nome.getText().toString()).getId_ficha() > 0)){
                        boolean flagFichaItens = true;
                        final String currentTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                        ficha.setData(currentTime);
                        ficha.setNome(String.valueOf(editText_nome.getText().toString()));
                        ficha.setStatus("PENDENTE");
                        ficha.setId_login(Register.id_login);
                        ficha.setFlagAtivo(chip_exibir.isChecked() ? 1 : 0);

                        if (flagUpdate) //update ou novo
                        {
                            if (helperFicha.updateName(ficha) > 0) {
                                for (FichaItens fichaItens : lfichaItens) {
                                    if (fichaItens.getId_ficha_itens() > 0) //atualiza o dados existente no banco
                                    {
                                        if (!(helperFichaItens.updateName(fichaItens) > 0)) {
                                            Toast.makeText(getApplicationContext(), "Erro ao alterar o exercicio na lista!", Toast.LENGTH_SHORT).show();
                                            flagFichaItens = false;
                                            break;
                                        }
                                    } else  //inseri novos dados na ficha atualizada
                                    {
                                        fichaItens.setId_ficha(ficha.getId_ficha());
                                        fichaItens.setId_ficha_itens(helperFichaItens.insertData(fichaItens));
                                        if (!(fichaItens.getId_ficha_itens() > 0)) {
                                            Toast.makeText(getApplicationContext(), "Erro ao inserir o novo exercicio na lista!", Toast.LENGTH_SHORT).show();
                                            flagFichaItens = false;
                                            break;
                                        }
                                    }
                                }

                                if (flagFichaItens)
                                    Toast.makeText(getApplicationContext(), "Ficha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (!(helperFicha.getFicha(editText_nome.getText().toString()).getId_ficha() > 0)) {
                                ficha.setId_ficha(helperFicha.insertData(ficha));
                                if (ficha.getId_ficha() > 0) //salva ficha
                                {
                                    for (FichaItens fichaItens : lfichaItens) //salva a lista de exercicio
                                    {
                                        fichaItens.setId_ficha(ficha.getId_ficha());
                                        fichaItens.setId_ficha_itens(helperFichaItens.insertData(fichaItens));
                                        if (fichaItens.getId_ficha_itens() == 0) {
                                            Toast.makeText(getApplicationContext(), "Erro ao Salvar no banco local a lista de exercício!", Toast.LENGTH_SHORT).show();
                                            flagFichaItens = false;
                                            break;
                                        }
                                    }
                                    if (flagFichaItens) {
                                        flagUpdate = true; //muda o modo para update
                                        Toast.makeText(getApplicationContext(), "Ficha Salva com sucesso!", Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "Erro ao Salvar a ficha de treino!", Toast.LENGTH_SHORT).show();
                            }
                            else
//                            alertName();
                            Toast.makeText(getApplicationContext(), "Existe um treino com o mesmo nome", Toast.LENGTH_SHORT).show();
                        }
//                    }
//
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Faltou o nome do treino!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar a ficha de treino!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Button exportar
        btn_exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exportFicha();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Falha ao exportar a ficha de treino!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Button importar
        btn_importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    flagUpdate = false;
                    openFichaImport();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Falha ao importar a ficha de treino!", Toast.LENGTH_SHORT).show();
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


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        Uri uri = intent.getData();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
            else if(type.equalsIgnoreCase("application/pdf")){
                handlePdfFile(intent);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }

        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        byte linha = 0;
        Uri file=intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(file!=null) {
            Log.d(" File Path : ", "" + file.getPath());
        }
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            flag_teste=false;
            if (ProcessFile(file.getPath()))
            {
                editText_nome.setText(ficha.getNome());
                //atualiza a tabela
                table_ficha_itens.removeAllViews();
                cabecalhoTabela();
                for (FichaItens fItens : lfichaItens) {
                    preencherDados(fItens, linha);
                    linha++;
                }
            }
            else
                Toast.makeText(getApplicationContext(), "Falha ao importar a ficha de treino!", Toast.LENGTH_SHORT).show();
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    private void handlePdfFile(Intent intent) {
        Uri pdffile=intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if(pdffile!=null) {
            Log.d("Pdf File Path : ", "" + pdffile.getPath());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //############# tabela de ficha itens ####################
    public void cabecalhoTabela() {
        //cabeçalho da tabela
        TableRow tr_ = new TableRow(this);
        tr_.setBackgroundColor(Color.rgb(179, 206, 250));
        table_ficha_itens.addView(tr_);

        TextView tv0 = new TextView(this);//Exercicio
        table_ficha_itens.setColumnStretchable(0, true);
        TextView tv1 = new TextView(this);//Descrição
        table_ficha_itens.setColumnStretchable(1, true);

        tv0.setText("Exercício");
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setGravity(Gravity.CENTER);
        tv1.setText("Quantidade");
        tv1.setTypeface(null, Typeface.BOLD);

        // tr_.addView(tv0);
        //  tr_.addView(tv1);
    }

    public void preencherDados(final FichaItens fichaItens, byte linha) {

        Exercicio exercicio_aux = new Exercicio();
        int qtdExecutado = 0;

        TableRow tr = new TableRow(this);

        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        tr.setOrientation(LinearLayout.VERTICAL);
        tr.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        layout.setPadding(15, 15, 10, 15);


        CardView cardViewCol1 = new CardView(this);
        CardView cardViewCol2 = new CardView(this);
        CardView cardViewCol3 = new CardView(this);
        CardView cardViewCol4 = new CardView(this);

        final ImageView imageViewExibir = new ImageView(this);
        final TextView tv1 = new TextView(this);//exercicio
        final TextView tv2 = new TextView(this);//quantidade
        final ImageButton imageEditar = new ImageButton(this);//editar
        imageEditar.setId(linha);
        final ImageButton imageExcluir = new ImageButton(this);//excluir
        imageExcluir.setId(linha);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (180 * scale + 0.5f);
        //Colunas
        CardView.LayoutParams lpCardCol1 = new CardView.LayoutParams(width/5,pixels/3);
        cardViewCol1.setPadding(15, 15, 10, 15);
        imageViewExibir.setPadding(15, 15, 10, 15);
        imageViewExibir.setId(linha);
        imageViewExibir.setMaxWidth(200);
        imageViewExibir.setMaxHeight(pixels/3);
        imageViewExibir.setLayoutParams(lpCardCol1);



        CardView.LayoutParams lpCardCol2 = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        tv1.setPadding(20, 20, 20, 20);
        tv1.setId(linha);
        tv1.setGravity(Gravity.LEFT);
        tv1.setText((helperExercicio.getExercicioID(fichaItens.getId_exercicio())).getNome());
        tv1.setLines(2);
        tv1.setWidth(pixels);
        tv1.setHeight(pixels/3);
       // tv1.setWidth(200);
        tv1.setAutoSizeTextTypeUniformWithConfiguration(2,20,2,TypedValue.COMPLEX_UNIT_DIP);
        tv1.setTextColor(Color.BLUE);
        tv1.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
        tv1.setLayoutParams(lpCardCol2);

        CardView.LayoutParams lpCardCol3 = new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv2.setPadding(20, 20, 20, 20);
        tv2.setId(linha);
        tv2.setGravity(Gravity.LEFT);
        tv2.setText("série: " + Long.toString(fichaItens.getQuantidade()) + "\n" +"rept: " + Long.toString(fichaItens.getRepeticao()));
        tv2.setLines(2);
        tv2.setWidth(pixels/3);
        tv2.setHeight(pixels/3);
        tv2.setAutoSizeTextTypeUniformWithConfiguration(2,20,2, TypedValue.COMPLEX_UNIT_DIP);
        tv2.setTextColor(Color.BLUE);
        tv2.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
        tv2.setLayoutParams(lpCardCol3);

        CardView.LayoutParams lpCardCol4 = new CardView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageExcluir.setImageResource(R.drawable.ic_delete);
        imageExcluir.setPadding(20,20,20,20);
        imageViewExibir.setMaxWidth(pixels/5);
        imageViewExibir.setMaxHeight(pixels/3);
        imageExcluir.setBackgroundColor(Color.WHITE);
        imageExcluir.setLayoutParams(lpCardCol4);


        try {
            exercicio_aux = helperExercicio.getExercicioID(fichaItens.getId_exercicio());

            if (exercicio_aux.getPath_gif().contains(".gif")) {
                Glide.with(CadastroFicha.this).load(exercicio_aux.getPath_gif())
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
                Glide.with(CadastroFicha.this).load(resourceId)
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
        cardViewCol4.addView(imageExcluir);
        tr.addView(cardViewCol4);

        table_ficha_itens.addView(tr);

        //Os dois eventos abaixo fazem a mesma coisa, fois adicionado para o usuario poder clicar
        //tanto na imagem como no texto (nome do exercicio)
        //botão exibir
        imageViewExibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FichaItens fichaItensRept = new FichaItens();
                    getid_tv = imageViewExibir.getId();
                    fichaItensRept = lfichaItens.get(getid_tv);
                    int picker_number= (int) fichaItensRept.getQuantidade();
                    int picker_number_2 = (int) fichaItensRept.getRepeticao();
                    int get_id = (int) fichaItensRept.getId_exercicio();
                    String obs = fichaItensRept.getObs();
                    showPickerDialog(picker_number, picker_number_2, obs,get_id); //alterar item na lista

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FichaItens fichaItensRept = new FichaItens();
                    getid_tv = tv1.getId();
                    fichaItensRept = lfichaItens.get(getid_tv);
                    int picker_number= (int) fichaItensRept.getQuantidade();
                    int picker_number_2 = (int) fichaItensRept.getRepeticao();
                    int get_id = (int) fichaItensRept.getId_exercicio();
                    String obs = fichaItensRept.getObs();
                    showPickerDialog(picker_number, picker_number_2, obs, get_id); //alterar item na lista

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //botao excluir da lista
        imageExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte linha=0;

                try {
                    if (lfichaItens.get(imageExcluir.getId()).getId_ficha_itens()>0)
                    {
                        if (helperFichaItens.delete(lfichaItens.get(imageExcluir.getId()).getId_ficha_itens())>0) {
                            Toast.makeText(getApplicationContext(), "Exercicio excluido!", Toast.LENGTH_SHORT).show();
                            lfichaItens.remove(imageExcluir.getId());
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Falha ao excluir o exercicio da lista!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        lfichaItens.remove(imageExcluir.getId()); //não existe no banco somente retira da lista


                    table_ficha_itens.removeAllViews();
                    cabecalhoTabela();
                    for (FichaItens fichaItens : lfichaItens) {
                        preencherDados(fichaItens, linha);
                        linha++;
                    }

                } catch (Exception e) {
                }
            }
        });
    }

    //editar
    public void editFichas() {
        List<String> spinnerArray_data = new ArrayList<String>();
        lficha = helperFicha.getData(Register.id_login);

        for (int i = 0; i < (lficha.size()); i++) {
            spinnerArray_data.add(lficha.get(i).getNome());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFicha.this);

        View mView = CadastroFicha.this.getLayoutInflater().inflate(R.layout.spinner_aux, null);
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);

        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(CadastroFicha.this,
                android.R.layout.simple_spinner_item, spinnerArray_data); // Chamar o ARRAY
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Atribui o adapter ao spinner
        mSpinner.setAdapter(adapter_1);
        builder.setTitle("Escolha o Treino");
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                byte linha = 0;
                int getid = mSpinner.getSelectedItemPosition();

                if (lficha.size() > 0) {
                    ficha.setData(lficha.get(getid).getData()); //salva a data para alterar
                    ficha.setNome(lficha.get(getid).getNome());
                    ficha.setId_ficha(lficha.get(getid).getId_ficha());
                    ficha.setFlagAtivo(lficha.get(getid).getFlagAtivo());
                    editText_nome.setText(ficha.getNome());
                    chip_exibir.setChecked(ficha.getFlagAtivo()==1?true:false);

                    lfichaItens.clear();
                    lfichaItens = helperFichaItens.getFichaItens(Long.toString(helperFicha.getFicha(mSpinner.getSelectedItem().toString()).getId_ficha()));
                    table_ficha_itens.removeAllViews();
                    cabecalhoTabela();
                    for (FichaItens fichaItens : lfichaItens) {
                        preencherDados(fichaItens, linha);
                        linha++;
                    }
                }
                flagUpdate = true;
                Toast.makeText(getApplicationContext(), "Clicked OK!", Toast.LENGTH_SHORT);
                return;
            }
        });
        builder.show();
    }

    public boolean verifyListAndUpdate(FichaItens fichaItens, boolean flagInsert, int lastIdExercicio)
    {
        boolean flagUpdateList = false;
        int indice = 0;
        for (FichaItens fItens:lfichaItens) //evita duplicaçao na lista
        {
            if (fichaItens.getId_exercicio()!=lastIdExercicio) //verifica se é para substituir o novo exercicio na mesma posição do anterior
            {
                if (fItens.getId_exercicio() == lastIdExercicio)
                {
                    lfichaItens.get(indice).setId_ficha(fichaItens.getId_ficha());
                    lfichaItens.get(indice).setId_exercicio(fichaItens.getId_exercicio());
                    lfichaItens.get(indice).setStatus(fichaItens.getStatus());
                    lfichaItens.get(indice).setQuantidade(fichaItens.getQuantidade());
                    lfichaItens.get(indice).setRepeticao(fichaItens.getRepeticao());
                    lfichaItens.get(indice).setObs(fichaItens.getObs());
                    flagUpdateList = true;
                    break;
                }
            }
            else if (fItens.getId_exercicio() == fichaItens.getId_exercicio()) //apenas altera as informações do exercicio na posiçao atual da lista
            {
                lfichaItens.get(indice).setId_ficha(fichaItens.getId_ficha());
                lfichaItens.get(indice).setId_exercicio(fichaItens.getId_exercicio());
                lfichaItens.get(indice).setStatus(fichaItens.getStatus());
                lfichaItens.get(indice).setQuantidade(fichaItens.getQuantidade());
                lfichaItens.get(indice).setRepeticao(fichaItens.getRepeticao());
                lfichaItens.get(indice).setObs(fichaItens.getObs());
                flagUpdateList = true;
                break;
            }

            indice ++;
        }

        if ((!flagUpdateList) & (flagInsert))
            lfichaItens.add(fichaItens);

        return flagUpdateList;
    }

    public void openCadastroExercicio() {
        Intent intent = new Intent(this, CadastroExercicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void openCadastroExercicioQuery(String strMembro) {
        List<Exercicio> lExercicio;

        lExercicio = helperExercicio.getDataMembro(Register.id_login, strMembro );

        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFicha.this);
            final View mView = CadastroFicha.this.getLayoutInflater().inflate(R.layout.activity_query_exercicio, null);

            final TableLayout table_exercicio = (TableLayout) mView.findViewById(R.id.table_exercicio);
            final ImageView imageView = (ImageView) mView.findViewById(R.id.imageView_new);

            builder.setTitle("Selecione os exercicios:");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            for (Exercicio exercicio:lExercicio)
            {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                float scale = getResources().getDisplayMetrics().density;
                int pixels = (int) (180 * scale + 0.5f);

                //cria  linha
                TableRow tr = new TableRow(this);
                TableRow.LayoutParams lp = new
                        TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

                lp.setMargins(20, 20, 20, 20);
                tr.setLayoutParams(lp);

                CardView cardViewCol1 = new CardView(this);
                CardView cardViewCol2 = new CardView(this);
                ImageView imageViewExibir = new ImageView(this);
                final TextView tv1 = new TextView(this);//exercicio

                //Colunas
                CardView.LayoutParams lpCardCol1 = new CardView.LayoutParams(width/5,pixels/3);
                cardViewCol1.setRadius(0);
                cardViewCol1.setPadding(20, 20, 20, 20);

                imageViewExibir.setPadding(20, 20, 20, 20);
                imageViewExibir.setId((int)exercicio.getId_excercicio());
                imageViewExibir.setMaxWidth(200);
                imageViewExibir.setMaxHeight(pixels/3);
                imageViewExibir.setLayoutParams(lpCardCol1);

                CardView.LayoutParams lpCardCol2 = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                tv1.setPadding(20, 20, 20, 20);
                tv1.setId((int)exercicio.getId_excercicio());
                tv1.setGravity(Gravity.LEFT);
                tv1.setText(exercicio.getNome());
                tv1.setLines(2);
                tv1.setWidth(pixels);
                tv1.setHeight(pixels/3);
                // tv1.setWidth(200);
                tv1.setAutoSizeTextTypeUniformWithConfiguration(6,20,2,TypedValue.COMPLEX_UNIT_DIP);
                tv1.setLayoutParams(lp);
                tv1.setTextColor(Color.BLUE);
                tv1.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_border));
                tv1.setLayoutParams(lpCardCol2);


                try {
                    if (exercicio.getPath_gif().contains(".gif")) {
                        Glide.with(mView).load(exercicio.getPath_gif())
                                .apply(new RequestOptions().override(100, 100))
                                .into(imageViewExibir);
                    } else if (exercicio.getPath_gif().contains(".jpg")) {
                        Bitmap myBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(exercicio.getPath_gif()), 100, 100, true);
                        imageViewExibir.setImageBitmap(myBitmap);
                    } else {
                        int resourceId = getResources().getIdentifier(exercicio.getDrawable(), "drawable", getPackageName());
                        Glide.with(mView).load(resourceId)
                                .apply(new RequestOptions().override(100, 100))
                                .into(imageViewExibir);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                cardViewCol1.addView(imageViewExibir);
                tr.addView(cardViewCol1);
                cardViewCol2.addView(tv1);
                tr.addView(cardViewCol2);

                table_exercicio.addView(tr);


                tv1.setOnClickListener(new View.OnClickListener()
                {
                    @Override

                    public void onClick(View v)
                    {
                        try
                        {
                            // numberPicker();
                            showPickerDialog(0,0, "-", tv1.getId());
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Erro ao Iniciar o treino!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            final AlertDialog dialog = builder.create();
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override

                public void onClick(View v)
                {

                    try
                    {
                        dialog.cancel();
                        openCadastroExercicio();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Erro!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            dialog.setView(mView);
           // builder.show();
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Falha na consulta de exercício!", Toast.LENGTH_SHORT);
        }


    }

    public void dialogFicha(){  // DIALOG INICIAL
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFicha.this);
        //Cria a view a ser utilizada no dialog
        View mView  = CadastroFicha.this.getLayoutInflater().inflate(R.layout.dialog_ficha,null);
        final EditText editText_nome_ = (EditText) mView.findViewById(R.id.editText_dialog_nome);
        final EditText editText_descr = (EditText) mView.findViewById(R.id.editText_dialog_descr);
        builder.setTitle("Monte o seu Treino");
        builder.setView(mView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editText_nome.setText(editText_nome_.getText().toString());
                Toast.makeText(getApplicationContext(), "Treino Iniciado!", Toast.LENGTH_SHORT);

                //flag_dial_exe = true;
                //dialogSelecExercicio();
                return;
            }
        });
        builder.show();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        switch(tipo_exercicio) {
            case "aerobico":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            case "peito":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            case "ombro":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            case "costa":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            case "braço":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            case "abdominal":
                openCadastroExercicioQuery(tipo_exercicio);
                break;
            default:
                break;
        }
        //Refresh your stuff here
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFicha.this);
        builder.setMessage("Retornar para pagina principal?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tipo_exercicio ="";
                Intent intent = new Intent(CadastroFicha.this, Selecao.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent); // RETORNA PARA PAGINA SELEÇÃO
         }
        });

                 builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });

                 builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener()     {
            public void onClick(DialogInterface dialog, int id) {
                //do things
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void showPickerDialog(int picker_number, int picker_number_2, final String obs, int id_exercicio ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_cadastro_ficha, null);
        builder.setView(view);

        final Spinner spinnerExercicio = (Spinner) view.findViewById(R.id.spinnerExercicio);
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.clear();
        lExercicio = helperExercicio.getData(Register.id_login);

        int position = 0;
        boolean flag = false;
        for (Exercicio exercio : lExercicio) {
            spinnerArray.add(exercio.getNome());
            if (id_exercicio==0)
            {
                if (lfichaItens.get(getid_tv).getId_exercicio() == exercio.getId_excercicio())
                flag = true;
            }
            else if (id_exercicio == exercio.getId_excercicio())
                flag = true;

            if (!flag)
                position++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercicio.setAdapter(adapter);
        spinnerExercicio.setSelection(position);

        final NumberPicker picker = (NumberPicker) view.findViewById(R.id.picker_first);
        picker.setMinValue(0);
        picker.setMaxValue(100);
        picker.setValue(picker_number);


        final NumberPicker picker_2 = (NumberPicker) view.findViewById(R.id.picker_second);
        picker_2.setMinValue(0);
        picker_2.setMaxValue(100);
        picker_2.setValue(picker_number_2);

        final EditText editText_obs = (EditText) view.findViewById(R.id.editText_obs);
        // trativa para deixar o HINT = OBSERVAÇÃO
        if(obs.equals("-")){
        }else {
            editText_obs.setText(obs);
        }
        editText_obs.setId(id_exercicio);

        builder.setTitle("Série e Repetições");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                byte linha = 0;

                //atualiza a tabela
                try{
                    FichaItens fichaItens = new FichaItens();
                    fichaItens.setId_exercicio((helperExercicio.getExercicioNome(spinnerExercicio.getSelectedItem().toString())).getId_excercicio());
                    fichaItens.setId_ficha(ficha.getId_ficha());
                    fichaItens.setQuantidade(picker.getValue());
                    fichaItens.setRepeticao(picker_2.getValue());
                    fichaItens.setObs(editText_obs.getText().toString());
                    fichaItens.setStatus("PENDENTE");
                    fichaItens.setId_login(Register.id_login);

                    //verifica se é para substituir na lista e atualiza a tabela
                    if (fichaItens.getId_exercicio()>0)
                    {
                        verifyListAndUpdate(fichaItens, true, editText_obs.getId());
                    }

                    //atualiza a tabela
                    table_ficha_itens.removeAllViews();
                    cabecalhoTabela();
                    for (FichaItens fItens : lfichaItens) {
                        preencherDados(fItens, linha);
                        linha++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Selecione o exercicio!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   Toast.makeText(getContext(), "You have not selected anything", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        builder.show();
    }


    //##### Exportar Ficha de treino #######
    private void exportFicha()
    {
        String strFicha = "";
        int lastItens = 0;

        try {
            if (lfichaItens.size() > 0) {
                final String currentTime = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                file = createFile(file, ficha.getNome()+"-"+currentTime +"-"+new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())); //NOME DO ARQUIVO
                fileWrite = new FileOutputStream(file); //libera a escrita

                strFicha = "#" + ficha.getNome() + ";" +
                        ficha.getData() + ";" +
                        ficha.getObs() + ";" +
                        ficha.getFlagAtivo() + ";";

                for (FichaItens fItens : lfichaItens) {
                    if (lastItens == (lfichaItens.size()-1)) {
                        strFicha += helperExercicio.getExercicioID(fItens.getId_exercicio()).getNome() + ";" +
                                helperExercicio.getExercicioID(fItens.getId_exercicio()).getTipo() + ";" +
                                fItens.getQuantidade() + ";" +
                                fItens.getRepeticao() + ";" +
                                fItens.getObs() + ";*";
                    } else {
                        strFicha += helperExercicio.getExercicioID(fItens.getId_exercicio()).getNome() + ";" +
                                helperExercicio.getExercicioID(fItens.getId_exercicio()).getTipo() + ";" +
                                fItens.getQuantidade() + ";" +
                                fItens.getRepeticao() + ";" +
                                fItens.getObs() + ";" ;
                    }

                    lastItens++;
                }

                if ((strFicha != "") && (fileWrite != null))//grava o arquivo
                {
                    if (fileWrite != null) {
                        writeFile(fileWrite, strFicha);
                        shareFicha(file);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Falha ao gerar a ficha do treino!", Toast.LENGTH_SHORT).show();
                }
                if (file.exists())
                    fileWrite.close();

            } else
                Toast.makeText(getApplicationContext(), "Selecione o treino e tente novamente!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Falha ao gerar a ficha de treino!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean writeFile(FileOutputStream fileWrite, String txt)
    {
        boolean flag = false;
        try {
            //Escreve no arquivo
            fileWrite.write(txt.getBytes());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public File createFile(File file, String arquivoNome) {
        try {
            File root = Environment.getExternalStorageDirectory();
            file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/fichaTreino/" + arquivoNome + ".txt");
            file.getParentFile().mkdir(); //abre o arquivo para escrita

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void shareFicha(@NonNull File file)
    {
        Uri myImageFileUri;
        //---Share File---//
        //get file uri
        myImageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

        //create a intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
        intent.setType("application/txt");
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    //########## importar ficha ##################
    private void openFichaImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, "Abrir a diretorio"), PICK_TXT_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Uri uriFicha = null;
        File file;
        byte linha = 0;

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_TXT_FILE) {
            pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
            uriFicha = data.getData();
        }
    }

    private boolean ProcessFile(String fileName)
    {
        boolean flag = false;
        try {
            BufferedReader buffRead;
            StringBuilder linha ;
            String subLinha;
            Exercicio exercicio;
            if(!flag_teste) {
                buffRead = new BufferedReader(new FileReader(fileName));
                linha = new StringBuilder();
                subLinha = "";
                exercicio = new Exercicio();
            }
            else {
                buffRead = new BufferedReader(new StringReader(fileName));
                linha = new StringBuilder();
                subLinha = "";
                exercicio = new Exercicio();
                flag_teste = false;
            }

            linha.append(buffRead.readLine());
            try
            {
                final String currentTime_ = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(new Date());
                if (linha.toString() !="")
                {
                    subLinha=linha.substring(linha.indexOf("#") + 1, linha.indexOf(";"));
                    linha.delete(0, linha.indexOf(";") + 1);
                    ficha = helperFicha.getFicha(subLinha);
                    if (ficha.getId_ficha()!=0){
                        //flagUpdate = false;
                        subLinha+=currentTime_;
                        ficha.setId_ficha(0); //nova
                        ficha.setNome(subLinha);
                    }else
                        ficha.setId_ficha(0); //nova
                    ficha.setNome(subLinha);

                    subLinha = linha.substring(0, linha.indexOf(";"));
                    ficha.setData(subLinha);
                    linha.delete(0, linha.indexOf(";") + 1);

                    subLinha = linha.substring(0, linha.indexOf(";"));
                    ficha.setObs(subLinha);
                    linha.delete(0, linha.indexOf(";") + 1);

                    subLinha = linha.substring(0, linha.indexOf(";"));
                    ficha.setFlagAtivo(Integer.parseInt(subLinha));
                    linha.delete(0, linha.indexOf(";") + 1);


                    //itens da ficha
                    lfichaItens.clear();
                    while (linha.indexOf("*")>=0)
                    {
                        FichaItens fItens = new FichaItens();

                        subLinha = linha.substring(0, linha.indexOf(";"));
                        linha.delete(0, linha.indexOf(";") + 1);
                        //verificar se o exercicio existe ou precisa ser cadastrado
                        exercicio = helperExercicio.getExercicioNome(subLinha);
                        if (exercicio.getId_excercicio()!=0) //existe
                        {
                            fItens.setId_exercicio(exercicio.getId_excercicio());
                            linha.delete(0, linha.indexOf(";") + 1); //deleta o tipo
                        }
                        else //cadastrar
                        {
                            exercicio.setId_excercicio(0);
                            exercicio.setDrawable("treino");
                            exercicio.setId_login(Register.id_login);
                            exercicio.setStatus("PENDENTE");
                            exercicio.setNome(subLinha);
                            exercicio.setPath_gif("-");

                            subLinha = linha.substring(0, linha.indexOf(";"));
                            exercicio.setTipo(subLinha);
                            linha.delete(0, linha.indexOf(";") + 1);

                            //grava o exercicio
                            exercicio.setId_excercicio(helperExercicio.getNextId());
                            exercicio.setId_excercicio(helperExercicio.insertData(exercicio));
                            fItens.setId_exercicio(exercicio.getId_excercicio());
                        }

                        if (exercicio.getId_excercicio()!=0) {
                            subLinha = linha.substring(0, linha.indexOf(";"));
                            fItens.setQuantidade(Integer.parseInt(subLinha));
                            linha.delete(0, linha.indexOf(";") + 1);

                            subLinha = linha.substring(0, linha.indexOf(";"));
                            fItens.setRepeticao(Integer.parseInt(subLinha));
                            linha.delete(0, linha.indexOf(";") + 1);

                            subLinha = linha.substring(0, linha.indexOf(";"));
                            fItens.setObs(subLinha);
                            linha.delete(0, linha.indexOf(";") + 1);

                            fItens.setStatus("PENDENTE");
                            fItens.setId_ficha_itens(0);
                            fItens.setId_login(Register.id_login);

                            lfichaItens.add(fItens);

                            //verifica se chegou no final do arquivo
                            subLinha = linha.substring(0, linha.indexOf("*") + 1);
                            if (subLinha.equals("*")) {
                                flag = true;
                                break;
                            }
                        }
                        else //falha ao gravar o novo exercicio
                            break;
                    }

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            buffRead.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        //remove a imagem existente
        file = new File(path);
        byte linha = 0;

        if (ProcessFile(file.getAbsolutePath()))
        {
            editText_nome.setText(ficha.getNome());
            //atualiza a tabela
            table_ficha_itens.removeAllViews();
            cabecalhoTabela();
            for (FichaItens fItens : lfichaItens) {
                preencherDados(fItens, linha);
                linha++;
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Falha ao importar a ficha de treino!", Toast.LENGTH_SHORT).show();
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                // Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                //  Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            // Log.v(TAG,"Permission is granted2");
            return true;
        }
    }
}