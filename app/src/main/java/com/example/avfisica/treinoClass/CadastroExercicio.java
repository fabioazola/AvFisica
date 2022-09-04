package com.example.avfisica.treinoClass;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.avfisica.R;
import com.example.avfisica.Register;
import com.example.avfisica.models.treino.Exercicio;
import com.example.avfisica.resources.treino.ExercicioResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static com.example.avfisica.treinoClass.CadastroFicha.tipo_exercicio;
import static com.example.avfisica.treinoClass.CustomAdpaterExercicio.ind_position;

public class CadastroExercicio extends AppCompatActivity {
    Button btn_salvar;
    EditText editText_nome;
    ImageView imageView_Exercicio;
    ImageButton btn_edit, btn_novo, btn_excluir;
    Spinner spinnerTipo;

    ExercicioResource helperExercicio;
    Exercicio exercicio;
    List<Exercicio> lExercicio;
    boolean flagUpdate = false;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    boolean flagImageUriGif = false;

    static int indice_table[];

    final long TAMANHO_ARQUIVO = 3000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_exercicio);

        btn_salvar = (Button) findViewById(R.id.button_cadastrar);
        editText_nome = (EditText) findViewById(R.id.editText_name);
        imageView_Exercicio = (ImageView) findViewById(R.id.imageView_Exercicio);
        btn_edit = (ImageButton) findViewById(R.id.imageButton_Exercicio_Editar);
        btn_novo = (ImageButton) findViewById(R.id.imageButton_Exercicio_Novo);
        btn_excluir = (ImageButton) findViewById(R.id.imageButton_Exercicio_Excluir);
        spinnerTipo = (Spinner) findViewById(R.id.spnnerTipo);

        helperExercicio = new ExercicioResource(this);
        exercicio = new Exercicio();
        lExercicio = new ArrayList<Exercicio>();

        askCameraPermissions();

        //carrega o tipo do exercicio
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.clear();
        spinnerArray.add("Abdominal");
        spinnerArray.add("Aerobico");
        spinnerArray.add("Braço");
        spinnerArray.add("Costa");
        spinnerArray.add("Ombro");
        spinnerArray.add("Peito");
        spinnerArray.add("Perna");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);
        spinnerTipo.setSelection(setPositionTipo(spinnerTipo,tipo_exercicio));

        //####################################################################################################

        //Button de salvar
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    exercicio.setNome(String.valueOf(editText_nome.getText().toString()));
                    exercicio.setTipo(spinnerTipo.getSelectedItem().toString().toLowerCase());
                    exercicio.setStatus("PENDENTE");
                    exercicio.setId_login(Register.id_login);

                    if (((exercicio.getId_excercicio() > 0) || (flagUpdate)) && (!exercicio.getNome().isEmpty())) //update ou novo
                    {
                        exercicio.setPath_gif(getPathFoto(imageView_Exercicio)); //carrega o nome e caminho da foto

                        if (helperExercicio.updateName(exercicio) > 0)
                        {
                            if (exercicio.getPath_gif().equals("-"))
                                exercicio.setDrawable("treino"); //imagem default
                            else if (saveToInternalStorage(imageView_Exercicio) != "-") {
                                exercicio.setDrawable(""); //limpa imagem default
                                Toast.makeText(getApplicationContext(), "Exercício alterado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                exercicio.setDrawable("treino"); //imagem default
                        } else
                            Toast.makeText(getApplicationContext(), "Erro ao alterar o exercício!", Toast.LENGTH_SHORT).show();
                    }
                    else if (!exercicio.getNome().isEmpty()) //novo
                    {
                        exercicio.setId_excercicio(helperExercicio.getNextId());
                        exercicio.setPath_gif(getPathFoto(imageView_Exercicio)); //carrega o nome e caminho da foto

                        exercicio.setId_excercicio(helperExercicio.insertData(exercicio));

                        if (exercicio.getId_excercicio() > 0) //novo exercicio
                        {
                            if (exercicio.getPath_gif().equals("-"))
                                exercicio.setDrawable("treino"); //imagem default
                            else if (saveToInternalStorage(imageView_Exercicio) != "-")
                                Toast.makeText(getApplicationContext(), "Exercício cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            else
                                exercicio.setDrawable("treino"); //imagem default
                        } else
                            Toast.makeText(getApplicationContext(), "Erro ao Salvar o exercício!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Colocar o nome do exercício!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao Salvar o exercício!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //button imagem
        imageView_Exercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                        openGallery();

                } catch (Exception e) {
                }
            }
        });

        //button editar
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editExercicios();
                } catch (Exception e) {
                }
            }
        });

        //button novo exercicio
        btn_novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exercicio.setId_excercicio(0);
                    exercicio.setNome(null);
                    exercicio.setPath_gif(null);
                    exercicio.setStatus(null);
                    exercicio.setTipo(null);

                    lExercicio.clear();

                    editText_nome.setText("");
                    imageView_Exercicio.setImageBitmap(null);
                    imageView_Exercicio.destroyDrawingCache();
                    imageView_Exercicio.setImageURI(null);
                    int resourceId = getResources().getIdentifier("treino", "drawable", getPackageName());
                    Glide.with(CadastroExercicio.this).load(resourceId)
                            .apply(new RequestOptions().override(100, 100))
                            .into(imageView_Exercicio);

                    flagUpdate = false;
                } catch (Exception e) {
                }
            }
        });

        //button excluir
        btn_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (helperExercicio.delete(exercicio.getId_excercicio()) > 0) {
                        editText_nome.setText("");
                        deleteToInternalStorage(imageView_Exercicio, exercicio.getPath_gif().contains(".gif"));
                        imageView_Exercicio.setImageURI(null);
                        int resourceId = getResources().getIdentifier("treino", "drawable", getPackageName());
                        Glide.with(CadastroExercicio.this).load(resourceId)
                                .apply(new RequestOptions().override(100, 100))
                                .into(imageView_Exercicio);
                        exercicio = null;
                        Toast.makeText(getApplicationContext(), "Exercicio excluido!", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplicationContext(), "Falha ao excluir o exercicio!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                }
            }
        });
    }

    private void openGallery() {
        imageView_Exercicio.setImageBitmap(null);
        imageView_Exercicio.destroyDrawingCache();
        imageView_Exercicio.setImageURI(null);

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       try {

            flagImageUriGif = false;

            if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
            {
                //reseta o objeto front-end
                imageView_Exercicio.setImageBitmap(null);
                imageView_Exercicio.destroyDrawingCache();
                imageView_Exercicio.setImageURI(null);

                imageUri = data.getData();
                flagImageUriGif = imageUri.getPath().contains("gif");
                imageView_Exercicio.setImageURI(imageUri);
            }
            else //carrega imagem default
                {
                    int resourceId = getResources().getIdentifier("treino", "drawable", getPackageName());
                    Glide.with(CadastroExercicio.this).load(resourceId)
                            .apply(new RequestOptions().override(100, 100))
                            .into(imageView_Exercicio);
               }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erro ao gravar a imagem/gif do exercício!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveToInternalStorage(ImageView imgView) {
        String pathFoto = "-";
        imgView.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgView.getDrawingCache();
        File root = Environment.getExternalStorageDirectory();
        File file;
        long tamanhoArqSelecionado = 0;


        try {
            //remove a imagem existente
            file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg");
            file.delete();
            file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif");
            file.delete();

            //carrega o novo path
            if (!flagImageUriGif) {
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg");
                pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg";
            } else {
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif");
                pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif";
            }

            //salvar ou copia o arquivo
            if (!flagImageUriGif) {
                //adiciona o novo
                file.createNewFile();

                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
                pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg";
            } else {
                final File selecionada = new File(getRealPathFromURI(imageUri));
                final File novaImagem = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/", exercicio.getId_excercicio() + ".gif");//selecionada.getName());
                tamanhoArqSelecionado = selecionada.length();
                if (tamanhoArqSelecionado <= TAMANHO_ARQUIVO) {
                    moveFile(selecionada, novaImagem);
                    pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif";
                }
                else
                {
                    pathFoto="-";
                    Toast.makeText(getApplicationContext(), "Arquivo excedeu tamanho de 3 MB permitido!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erro ao gravar a imagem/gif do exercício!", Toast.LENGTH_SHORT).show();
            pathFoto = "-"; //erro
        }
        return pathFoto;
    }

    public String getPathFoto(ImageView imgView) {
        String pathFoto = "-";
        try {
            File root = Environment.getExternalStorageDirectory();
            File file;

            if (!flagImageUriGif) {
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getNome() + ".jpg");
                pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg";
            } else {
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getNome() + ".gif");
                pathFoto = root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pathFoto;
    }

    public boolean deleteToInternalStorage(ImageView imgView, boolean flagGif) {
        boolean flag = false;

        try {
            File root = Environment.getExternalStorageDirectory();
            File file;
            if (flagGif)
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".gif");
            else
                file = new File(root.getAbsolutePath() + "/Android/data/com.example.avfisica/files/" + exercicio.getId_excercicio() + ".jpg");
            //remove a imagem existente
            if (file.delete())
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    //editar
    public void editExercicios() {
        List<String> spinnerArray_data = new ArrayList<String>();
        lExercicio = helperExercicio.getData(Register.id_login);

        for (int i = 0; i < (lExercicio.size()); i++) {
            spinnerArray_data.add(lExercicio.get(i).getNome());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroExercicio.this);

        View mView = CadastroExercicio.this.getLayoutInflater().inflate(R.layout.spinner_aux, null);
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner_edit);

        ArrayAdapter<String> adapter_1;
        adapter_1 = new ArrayAdapter<String>(CadastroExercicio.this,
                android.R.layout.simple_spinner_item, spinnerArray_data); // Chamar o ARRAY
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Atribui o adapter ao spinner
        mSpinner.setAdapter(adapter_1);
        builder.setTitle("Escolha o exercício");
        builder.setView(mView);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int getid = mSpinner.getSelectedItemPosition();
                if (lExercicio.size() > 0) {
                    exercicio.setId_excercicio(lExercicio.get(getid).getId_excercicio());
                    exercicio.setNome(lExercicio.get(getid).getNome());
                    exercicio.setPath_gif(lExercicio.get(getid).getPath_gif());
                    exercicio.setDrawable(lExercicio.get(getid).getDrawable());
                    exercicio.setId_login(lExercicio.get(getid).getId_login());
                    exercicio.setTipo(lExercicio.get(getid).getTipo());
                    spinnerTipo.setSelection(setPositionTipo(spinnerTipo,exercicio.getTipo()));
                    exercicio.setStatus(lExercicio.get(getid).getStatus());
                    editText_nome.setText(exercicio.getNome());

                    imageView_Exercicio.setImageBitmap(null);
                    imageView_Exercicio.destroyDrawingCache();
                    imageView_Exercicio.setImageURI(null);

                    if (exercicio.getPath_gif().equals("-")) {
                        int resourceId = getResources().getIdentifier(exercicio.getDrawable(), "drawable", getPackageName());
                        imageView_Exercicio.setImageResource(resourceId);
                    } else {
                        Bitmap myBitmap = BitmapFactory.decodeFile(exercicio.getPath_gif());
                        imageView_Exercicio.setImageBitmap(myBitmap);
                    }
                }
                flagUpdate = true;
                Toast.makeText(getApplicationContext(), "Clicked OK!", Toast.LENGTH_SHORT);
                return;
            }
        });
        builder.show();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void moveFile(File sourceFile, File destFile) throws IOException {
        FileInputStream inStream = new FileInputStream(sourceFile);
        FileOutputStream outStream = new FileOutputStream(destFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    public int setPositionTipo(Spinner spinner, String nome)
    {
        int position = 0;
        boolean flag = false;

        for (position=0; position<spinner.getCount(); position++)
        {
            spinner.setSelection(position);
            if (spinner.getSelectedItem().toString().toUpperCase().equals(nome.toUpperCase()))
            {
                flag  = true;
                break;
            }
        }

        if (!flag)
            position = 0;
        return position;
    }
}
