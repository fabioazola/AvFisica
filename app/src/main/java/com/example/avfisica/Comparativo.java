package com.example.avfisica;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.avfisica.models.Comparacao;
import com.example.avfisica.resources.ComparacaoResource;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class Comparativo extends AppCompatActivity {

    SubsamplingScaleImageView view_foto, view_foto_2;
    ImageView btn_foto;
    ImageView btn_share;
    Button button_load;
    TextView textView_foto_1, textView_foto_2;
    BottomNavigationView bottomNavigation;
    ImageButton btn_info;
    Spinner spinner, spinner_1;

    Bitmap rotatedBitmap = null;
    Bitmap rotatedBitmap_2 = null;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath = "teste";
    private ImageView mImageView;
    public static String currentTime;
    public static String tipo_foto = "frente";
    public int ind,ind_2=2;
    public  File f;
    public Uri myImageFileUri;


    //myDbAdapter_5 helper;
    ComparacaoResource helper_comparacao;
    Comparacao comparacao = new Comparacao();
    List<Comparacao> lcomparacao_dados = new ArrayList<Comparacao>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparativo);

        btn_foto = (ImageView) findViewById(R.id.imageButton_foto);
        btn_share = (ImageView) findViewById(R.id.imageButton_share);
        //view_foto = (ImageView) findViewById(R.id.imageView_foto);

        textView_foto_1 = (TextView) findViewById(R.id.textView_foto_1);
        textView_foto_2 = (TextView) findViewById(R.id.textView_foto_2);
        btn_info = (ImageButton) findViewById(R.id.imageButton_comp_info);

        spinner = (Spinner) findViewById(R.id.spinner_2);
        spinner_1 = (Spinner) findViewById(R.id.spinner_data_1);

        helper_comparacao = new ComparacaoResource(this);
        lcomparacao_dados = helper_comparacao.getData(tipo_foto);

        if(lcomparacao_dados.size()<1){
            showCase("Posição", "Escolha a posição que irá bater a foto!", spinner, 1);
        }

        SubsamplingScaleImageView view_foto = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto);
        // Bitmap bitmap = BitmapFactory.decodeFile(lcomparacao_dados.get(3).getPath());
        //view_foto.setImage(ImageSource.bitmap(bitmap));

        SubsamplingScaleImageView view_foto_2 = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto2);
        //Bitmap bitmap_ = BitmapFactory.decodeFile(lcomparacao_dados.get(3).getPath());
        // view_foto_2.setImage(ImageSource.bitmap(bitmap_));


        ActivityCompat.requestPermissions(Comparativo.this, new String[]{permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(Comparativo.this, new String[]{permission.READ_EXTERNAL_STORAGE},1);


        //######################### NAVIGATION ##################################
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


        //Imagem de info
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(Comparativo.this)
                            .setTitle("Ajuda")
                            .setMessage("Escolha a posição (Frente, Lado, Costas) para bater a foto" + "\n" + "\n" + "Depois compare sua evolução" + "\n" + "\n" + "E compartilhe com seus amigos!")
                            .show();
                } catch (Exception e) {
                }
            }
        });

        //Imagem de foto para bater
        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    askCameraPermissions();
                } catch (Exception e) {
                }
            }
        });


        //Imagem de compartilhar
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openShare();
                    Toast.makeText(getApplicationContext(), "Aguarde! Share em processo", Toast.LENGTH_SHORT).show();
                    if (!(lcomparacao_dados.get(ind).getPath().isEmpty() && lcomparacao_dados.get(ind_2).getPath().isEmpty())) {

                        openShare();
                    } else
                        Toast.makeText(getApplicationContext(), "Não foi possivel compartilhar as fotos!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Chamar foto na galeria
        view_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openGallery();
                } catch (Exception e) {
                }
            }
        });

        //Chamar foto na galeria
        view_foto_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openGallery_2();
                } catch (Exception e) {
                }
            }
        });

//###########################//SPINNER//##############################################//
        // Spinner element
        /*for fill your Spinner*/

        List<String> spinnerArray = new ArrayList<String>();

        spinnerArray.add("Frente");
        spinnerArray.add("Costas");
        spinnerArray.add("Lado Direito");
        spinnerArray.add("Lado Esquerdo");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //if (item != null) {
                //    Toast.makeText(getApplicationContext(), "Organizado", Toast.LENGTH_SHORT).show();
                // }
                if (position == 0){
                    Toast.makeText(getApplicationContext(), "Frente", Toast.LENGTH_SHORT).show();
                    tipo_foto = "frente";
                    spinnerData();
                    spinnerData_2();
                    // comparacaoFoto();
                }
                if (position == 1){
                    Toast.makeText(getApplicationContext(), "Costas", Toast.LENGTH_SHORT).show();
                    tipo_foto = "tras";
                    spinnerData();
                    spinnerData_2();
                    // comparacaoFoto();
                }

                if (position == 2){
                    Toast.makeText(getApplicationContext(), "Lado Direito", Toast.LENGTH_SHORT).show();
                    tipo_foto = "lado direiro";
                    spinnerData();
                    spinnerData_2();
                    // comparacaoFoto();
                }
                if (position == 3){
                    Toast.makeText(getApplicationContext(), "Lado Esquerdo", Toast.LENGTH_SHORT).show();
                    tipo_foto = "lado esquerdo";
                    spinnerData();
                    spinnerData_2();
                    //comparacaoFoto();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

        spinnerData();
        spinnerData_2();

    }

    public void tirarfoto()
    {

        if (Build.VERSION.SDK_INT >= 24) {

            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                 Continue only if the File was successfully created
                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                Uri imageUri = FileProvider.getUriForFile(this, authorities, photoFile);

                // cameraIntent.setDataAndType(photoURI, mimeType);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                f = new File(mCurrentPhotoPath);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100){
            Uri imageUri = data.getData();
            boolean flag_view = false;
            setPic(imageUri,flag_view);
        }

        if (resultCode == RESULT_OK && requestCode == 200){
            Uri imageUri = data.getData();
            boolean flag_view = true;
            setPic(imageUri,flag_view);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            Bitmap bitmapReduzido = Bitmap.createScaledBitmap(bitmap, 300,300, true);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            //  view_foto.setImageBitmap(rotated);
            //################### SALVA OS PATHS DAS FOTOS (CONFIRMA QUE HOUVE FOTO) ###################
            currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
            String t1 = currentTime;
            String t2 = mCurrentPhotoPath;
            String t3 = tipo_foto;

            comparacao.setData(t1);
            comparacao.setPath(t2);
            comparacao.setTipo(t3);
            comparacao.setId_login(Register.id_login);
            if (helper_comparacao.insertData(comparacao)>0) {
                Toast.makeText(getApplicationContext(), "Foto gravada com sucesso!", Toast.LENGTH_SHORT).show();
                spinnerData();
                spinnerData_2();
            }
            else
                Toast.makeText(getApplicationContext(), "Falha ao gravar a foto!", Toast.LENGTH_SHORT).show();

            if(data!=null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                //   view_foto.setImageBitmap(rotated);
            }
        }
    }

    public void openActivityMain()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + " - WIFIT") ;
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        galleryAddPic();
        return image;
    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {permission.CAMERA}, 1);
        }else {
            tirarfoto();
            lcomparacao_dados=helper_comparacao.getData(tipo_foto);
            // spinnerData();
            //spinnerData_2();
        }
    }

    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_STARTED);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic(Uri imageUri,boolean flag_view) {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //###################### Verifica o angulo da foto (SELFIE) #########################
        ExifInterface ei = null;
        InputStream in;
        try {
            in = getContentResolver().openInputStream(imageUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ei = new ExifInterface(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        //Bitmap bitmap = BitmapFactory.decodeFile(lcomparacao_dados.get(ind).getPath(), bmOptions);
        //  Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        if (flag_view==false) {
            SubsamplingScaleImageView view_foto = (SubsamplingScaleImageView) findViewById(R.id.imageView_foto);
            view_foto.setImage(ImageSource.bitmap(rotatedBitmap));
        }else {
            SubsamplingScaleImageView view_foto_2 = (SubsamplingScaleImageView) findViewById(R.id.imageView_foto2);
            view_foto_2.setImage(ImageSource.bitmap(rotatedBitmap));
        }
        // view_foto.setImageBitmap(rotatedBitmap);
    }



    public void comparacaoFoto(){

        //##################### DATA #####################
        textView_foto_1.setText("ANTES:");
        if (lcomparacao_dados.size()>=ind)
        {
            //###################### Verifica o angulo da foto (SELFIE) #########################
            ExifInterface ei = null;
            int orientation = 0;
            Bitmap bitmap = null;
            try {
                ei = new ExifInterface(lcomparacao_dados.get(ind).getPath());
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                bitmap = BitmapFactory.decodeFile(lcomparacao_dados.get(ind).getPath());

                //  Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                //######################################################
                // Abre o bitmap a partir do caminho da foto Bitmap
                if (bitmap == null || bitmap.equals("")) {
                }
                else {
                    if (rotatedBitmap == null) {
                    } else {
                        SubsamplingScaleImageView view_foto = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto);
                        view_foto.setImage(ImageSource.bitmap(rotatedBitmap));
                        // setPic();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void comparacaoFoto_2(){

        //##################### DATA #####################
        textView_foto_2.setText("DEPOIS:");

        // Abre o bitmap a partir do caminho da foto Bitmap
        if (lcomparacao_dados.size()>=ind_2)
        {
            //###################### Verifica o angulo da foto (SELFIE) #########################
            ExifInterface ei = null;
            int orientation = 0;
            Bitmap bitmap_2=null;

            try {
                ei = new ExifInterface(lcomparacao_dados.get(ind_2).getPath());
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                bitmap_2 = BitmapFactory.decodeFile(lcomparacao_dados.get(ind_2).getPath());

                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap_2 = rotateImage(bitmap_2, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap_2 = rotateImage(bitmap_2, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap_2 = rotateImage(bitmap_2, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap_2 = bitmap_2;
                }

                //######################################################

                if (rotatedBitmap_2 == null || rotatedBitmap_2.equals("")) {
                    //  view_foto_2.setImageBitmap(null);
                }
                else {

                    if (rotatedBitmap_2 == null) {

                    } else {
                        SubsamplingScaleImageView view_foto_2 = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto2);
                        view_foto_2.setImage(ImageSource.bitmap(rotatedBitmap_2));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void spinnerData(){

        //###########################//SPINNER - DATA//##############################################//
        // Spinner element
        /*for fill your Spinner*/

        lcomparacao_dados = helper_comparacao.getData(tipo_foto);
        List<String> spinnerArray_data = new ArrayList<String>();

        if(lcomparacao_dados.size()<1){
            SubsamplingScaleImageView view_foto = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto);
            view_foto.setImage(ImageSource.resource(R.drawable.compare));
        }
        for (Comparacao comp:lcomparacao_dados)
        {
            spinnerArray_data.add(comp.getData());
        }

        ArrayAdapter<String> adapter_1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray_data);
        adapter_1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_1.setAdapter(adapter_1);

        spinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //if (item != null) {
                //    Toast.makeText(getApplicationContext(), "Organizado", Toast.LENGTH_SHORT).show();
                // }

                ind=position;
                comparacaoFoto();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });


    }

    public void spinnerData_2()
    {
        //###########################//SPINNER - DATA 2//##############################################//
        // Spinner element
        /*for fill your Spinner*/
        lcomparacao_dados = helper_comparacao.getData(tipo_foto);
        List<String> spinnerArray_data_2 = new ArrayList<String>();

        if(lcomparacao_dados.size()<1){
            SubsamplingScaleImageView view_foto_2 = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto2);
            view_foto_2.setImage(ImageSource.resource(R.drawable.compare));
        }
        for (Comparacao comp:lcomparacao_dados)
        {
            spinnerArray_data_2.add(comp.getData());
        }

        ArrayAdapter<String> adapter_2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray_data_2);
        adapter_2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner_2 = (Spinner) findViewById(R.id.spinner_data_2);
        spinner_2.setAdapter(adapter_2);

        spinner_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //if (item != null) {
                //    Toast.makeText(getApplicationContext(), "Organizado", Toast.LENGTH_SHORT).show();
                // }

                ind_2=position;
                comparacaoFoto_2();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

    }


    public void openShare(){

        SubsamplingScaleImageView view_foto = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto);
        loadBitmapFromView(view_foto);
        rotatedBitmap = loadBitmapFromView(view_foto);
        SubsamplingScaleImageView view_foto_2 = (SubsamplingScaleImageView)findViewById(R.id.imageView_foto2);
        loadBitmapFromView(view_foto_2);
        rotatedBitmap_2 = loadBitmapFromView(view_foto_2);
        //Merge two bitmaps to one
        Bitmap bitmapMerged = Bitmap.createBitmap(
                rotatedBitmap.getWidth()+view_foto_2.getWidth(),
                rotatedBitmap.getHeight(),
                rotatedBitmap.getConfig());
        Canvas canvasMerged = new Canvas(bitmapMerged);
        canvasMerged.drawBitmap(rotatedBitmap, 0f, 0f, null);
        canvasMerged.drawBitmap(rotatedBitmap_2, rotatedBitmap.getWidth(), 0f, null);


        //view_foto.setImageBitmap(bitmapMerged); --> CAUSA PROBLEMA
        shareBitmap(bitmapMerged);  // Chama o Metodo de compatilhar a foto merge
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void shareBitmap(@NonNull Bitmap bitmap)
    {
        //---Save bitmap to external cache directory---//
        //get cache directory
        File cachePath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_comparativos/");
        cachePath.mkdirs();

        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //create png file
        File file = new File(cachePath, "Image_123.png");
        FileOutputStream fileOutputStream;
        try
        {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //---Share File---//
        //get file uri
        myImageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

        //create a intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share with"));


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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);

    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    private void openGallery_2() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 200);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static Bitmap loadBitmapFromView(View v) {
        int width_  = v.getMeasuredWidth();
        Bitmap b = Bitmap.createBitmap(width_, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public void showCase(String title,String subtitle,View view, final int type ){
        new GuideView.Builder(Comparativo.this)
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
                            showCase("Bater Foto","Clique aqui para bater a foto!",btn_foto,2);
                        } else if (type == 2) {
                            showCase("Escolha a Data","Escolha a foto pela data que foi tirada",spinner_1,4);
                        } else if (type == 3) {
                            //showCase("Imagem", "Pode dar zoom, ou até escolher foto da galeria",view_foto, 4);
                        } else if (type == 4) {
                            showCase("Compartilhar", "Compartilhe sua evolução com os amigos ou com seu professor", btn_share, 5);
                        } else if (type == 5) {
                            showCase("Info", "Clique para mais informações",btn_info, 6);
                        }
                    }
                })
                .build()
                .show();
    }
}

