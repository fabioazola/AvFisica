package com.example.avfisica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Login;
import com.example.avfisica.models.TaxaModel;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Corrida;
import com.example.avfisica.models.Pagamento;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.Vo2Model;
import com.example.avfisica.models.treino.Exercicio;
import com.example.avfisica.models.treino.LexercicioDefault;
import com.example.avfisica.repository.PagamentoRepository;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.LoginResource;
import com.example.avfisica.resources.TaxaResource;
import com.example.avfisica.resources.CorporalResource;
import com.example.avfisica.resources.CorridaResource;
import com.example.avfisica.resources.PesoResource;
import com.example.avfisica.resources.Vo2Resource;
import com.example.avfisica.resources.treino.ExercicioResource;
import com.example.avfisica.treinoClass.Selecao;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;


public class MainActivity extends AppCompatActivity {
    ImageView btn_entrar, btn_corporal, btn_comparativo, btn_corrida, btn_peso, btn_vo2, btn_ajuda, btn_monitorado, btn_treino, btn_dicas;
    ImageView  btn_imc, btn_taxa, btn_home, imageView_pic, btn_suporte;
    TextView textView_view, textView_view_name, textView_view_pass, textView_view_peso, textView_name_main;
    EditText Name, textView_view_data;
    BottomNavigationView bottomNavigation;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    public static long matricula = 0;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    //sincronismo
    boolean flag_Pagamento = false;
    boolean flag_Finished_sinc = false;
    static boolean flag_notify = false;
    byte flag_sinc_erro = 0; //0 - ok , 1 falha tb_aluno
    ProgressBar progressBarSincronismo;
    AlunoResource helperAluno;
    Aluno aluno = new Aluno();
    PesoResource helperPeso;
    CorporalResource helperCorporal;
    CorridaResource helperCorrida;
    Vo2Resource helperVo2;
    TaxaResource helperTaxa;
    LoginResource helperLogin;
    Util util = new Util();
    ExercicioResource helperExercicio;
    List<Exercicio> lExercicio;
    LexercicioDefault lexercicioDefault;

    private NotificationManager mNotificationManager;
    private static final String TAG=MainActivity.class.getName();
    public static ArrayList<Activity> activities=new ArrayList<Activity>();
    List<Peso> lpeso_dados = new ArrayList<Peso>();

    private int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        //adiciona na lista de actives abertas
//        if (activities.size()>0) {
//            for (Activity activity : activities) {
//                if (activity != this)
//                    activities.add(this);
//            }
//        }
//        else
//            activities.add(this);


        btn_entrar = (ImageView) findViewById(R.id.imageView_config);
        btn_monitorado = (ImageView) findViewById(R.id.imageView_monitorado);
        btn_corporal = (ImageView) findViewById(R.id.imageView_corporal);
        btn_comparativo = (ImageView) findViewById(R.id.imageView_dados);
        btn_peso = (ImageView) findViewById(R.id.imageView_peso);
        //btn_imc = (ImageView) findViewById(R.id.imageView_imc);
        btn_treino = (ImageView) findViewById(R.id.imageView_treino);
        btn_taxa = (ImageView) findViewById(R.id.imageView_peso_ideal);
        btn_home = (ImageView) findViewById(R.id.imageView_peso_home);
        btn_vo2 = (ImageView) findViewById(R.id.imageView_vo2);
        btn_corrida = (ImageView) findViewById(R.id.imageView_cardio);
        btn_dicas = (ImageView) findViewById(R.id.imageView_dicas);
        btn_suporte = (ImageView) findViewById(R.id.imageView_suporte);
        imageView_pic = (ImageView) findViewById(R.id.imageView_pic);
        progressBarSincronismo = (ProgressBar) findViewById(R.id.progressBarSincronismo);
        textView_name_main = (TextView) findViewById(R.id.textView_nome_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btn_ajuda = (ImageView) findViewById(R.id.imageView_dicas);

        helperAluno = new AlunoResource(this);
        helperPeso = new PesoResource(this);
        helperCorporal = new CorporalResource(this);
        helperCorrida = new CorridaResource(this);
        helperVo2 = new Vo2Resource(this);
        helperTaxa = new TaxaResource(this);
        helperLogin = new LoginResource(this);

        //tabela de exercicio default
        helperExercicio = new ExercicioResource(this);
        lExercicio = new ArrayList<Exercicio>();
        lexercicioDefault = new LexercicioDefault();

        scheduleNotification (); // Chama as notificações


//######################### INICIALIZAÇÃO ##################################
        try
        {
            aluno = helperAluno.getData_all();

            if (aluno.getId_login()==0){
                openActivityStatus();
            }
            else {
                matricula = aluno.getMatricula();
                Register.id_login = aluno.getId_login();
                Register.email = aluno.getEmail();

                if ((aluno.getId_login()>0) && (aluno.getPathFoto()!=null)) {

                    if (aluno.getPathFoto().equals("-")){
                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.avatar_2);
                        imageView_pic.setImageBitmap(icon);

                    }else {
                        Bitmap myBitmap = BitmapFactory.decodeFile(aluno.getPathFoto());
                        imageView_pic.setImageBitmap(myBitmap);
                    }
                    Register.pathFoto = aluno.getPathFoto();
                }
                else
                    Register.pathFoto = "-";

                new MyTaskSincronismo().execute();
            }

            textView_name_main.setText("Seja bem-vindo  " + "\n" +aluno.getNome());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Erro ao acessar os dados do aluno!", Toast.LENGTH_SHORT).show();
            openActivityStatus();
        }

        //###################### LISTA DE EXERCICIO DEFAULT ###########################
        if( (helperExercicio.getNextId()==1) && (Register.id_login>0))
        {
            for (Exercicio exercicio:lexercicioDefault.getlExercicio())
            {
                exercicio.setId_login(Register.id_login);
                helperExercicio.insertData(exercicio);
            }
        }

        //######################### FOTO - TELA HOME ##################################
        imageView_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openGallery();
                } catch (Exception e) {
                }
            }
        });

        //######################### NAVIGATION ##################################
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

        //Imagem de voltar para tela principal
        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                   openActivityCadastro();
                } catch (Exception e) {
                }
            }
        });

        //Imagem de voltar para tela principal
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    openActivityDashboard();
                } catch (Exception e) {
                }
            }
        });


    /*    //Imagem de imc para tela principal
        btn_imc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //openActivityImc();
                    openActivityAtividades();
                } catch (Exception e) {
                }
            }
        });*/


        //Imagem de taxa Metabolica para tela principal
        btn_taxa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityTaxa();
                } catch (Exception e) {
                }
            }
        });

        //Button de Composição Corporal
        btn_corporal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityCorporal();
                } catch (Exception e) {
                }
            }
        });

        //Button de Comparativo FOTO
        btn_comparativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityComparativo();
                } catch (Exception e) {
                }
            }
        });

        //Button de Corrida
        btn_corrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityPace();
                } catch (Exception e) {
                }
            }
        });

        //Button de Peso
        btn_peso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityPeso();
                } catch (Exception e) {
                }
            }
        });

        //Button de VO2
        btn_vo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityVo2();
                } catch (Exception e) {
                }
            }
        });

        //Button de Suporte
        btn_suporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivitySuporte();
                } catch (Exception e) {
                }
            }
        });

        btn_ajuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //openActivityMonitorado();
                } catch (Exception e) {
                }
            }
        });

        btn_monitorado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityMonitorado();
                } catch (Exception e) {
                }
            }
        });

        btn_treino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityTreino();
                } catch (Exception e) {
                }
            }
        });

        btn_dicas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityDicas();
                } catch (Exception e) {
                }
            }
        });

        btn_suporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivitySuporte();
                } catch (Exception e) {
                }
            }
        });
    }

    public void openActivityPace()
    {
        Intent intent = new Intent(this,Pace.class);
        startActivity(intent);
    }

    public void openActivityImc()
    {
//        Intent intent = new Intent(this,Imc.class);
////        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    public void openActivityAtividades()
    {
//        Intent intent = new Intent(this,Atividades.class);
//        startActivity(intent);
       // addNotification();
        scheduleNotification ();
        Toast.makeText(getApplicationContext(), "Em breve --> fase de desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    public void openActivityPesoIdeal()
    {
        Intent intent = new Intent(this,PesoIdeal.class);
        startActivity(intent);
    }

    public void openActivityTaxa()
    {
        Intent intent = new Intent(this,Taxa.class);
        startActivity(intent);
    }

    public void openActivityCadastro()
    {
        Intent intent = new Intent(this,Cadastro.class);
        startActivity(intent);
    }

    public void openActivityCorporal()
    {
        Intent intent = new Intent(this,Corporal.class);
        startActivity(intent);
    }

    public void openActivityComparativo()
    {
        Intent intent = new Intent(this,Comparativo.class);
        startActivity(intent);
    }

    public void openActivityVo2()
    {
        Intent intent = new Intent(this,Vo2.class);
        startActivity(intent);
    }

    public void openActivityPeso()
    {
        Intent intent = new Intent(this,PesoIdeal.class);
        startActivity(intent);
    }

    public void openActivityRegister()
    {
        Intent intent = new Intent(this,Register.class);
        startActivity(intent);
    }

    public void openActivityStatus()
    {
        Intent intent = new Intent(this,Status.class);
        startActivity(intent);
    }

    public void openActivityDashboard()
    {
        Intent intent = new Intent(this,Dashboard.class);
        startActivity(intent);
    }

    public void openActivityMonitorado()
    {
        Intent intent = new Intent(this,Monitorado.class);
        startActivity(intent);
    }

    public void openActivityTreino()
    {
        Intent intent = new Intent(this, Selecao.class);
        startActivity(intent);
    }

    public void openActivityDicas()
    {
        Toast.makeText(getApplicationContext(), "Em desenvolvimento", Toast.LENGTH_SHORT).show();
    }


    public void openActivitySuporte()
    {
        Intent intent = new Intent(this,Suporte.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(), "Em desenvolvimento", Toast.LENGTH_SHORT).show();
    }


    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView_pic.setImageURI(imageUri);

            //Altera no banco o caminho default "-" para caminho selecionado pelo aluno
            if (aluno.getId_login()!=0) {
                aluno.setPathFoto(saveToInternalStorage(imageView_pic));
                if (helperAluno.updateName(aluno) > 0) {//update
                    Toast.makeText(getApplicationContext(), "Foto alterada com sucesso!", Toast.LENGTH_SHORT).show();
                    Register.pathFoto = aluno.getPathFoto();
                }
                else
                    Toast.makeText(getApplicationContext(), "Erro ao alterar a foto!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String saveToInternalStorage(ImageView imgView)
    {
            String pathFotoPerfil = "-";
            imgView.setDrawingCacheEnabled(true);
            Bitmap bitmap = imgView.getDrawingCache();
            File root = Environment.getExternalStorageDirectory();
            File file = new File(root.getAbsolutePath()+"/Android/data/com.example.avfisica/files/fotoPerfil.jpg");
            try
            {
                file.createNewFile();
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                ostream.close();
                pathFotoPerfil=root.getAbsolutePath()+"/Android/data/com.example.avfisica/files/fotoPerfil.jpg";
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return pathFotoPerfil;
    }


    //########## Sincronismo ################
    private class MyTaskSincronismo extends AsyncTask<Void, Integer, Long> {
        String result;
        @Override
        protected Long doInBackground(Void... voids)
        {
            PagamentoRepository pagamentoRepository = new PagamentoRepository();
            Pagamento pagamento = new Pagamento();

            if (aluno.getId_login()>0) {
             //   pagamento = pagamentoRepository.findPagamentoByIdLogin(util.extractMonthOfday(), aluno.getId_login());
             //   if (pagamento!=null)
             //   {
                    flag_Pagamento = true;
                    if (updateTbAluno(aluno))
                    {
                        if (updateTbPeso())
                        {
                            if (updateTbCorporal())
                            {
                                if(updateTbVo2()) {

                                    if(updateTbTaxa()) {
                                        updateTbCorrida();
                                    }
                                }
                            }
                        }
                    }

                    flag_Finished_sinc = true;
                //}
               //else
                   //flag_Pagamento = false;
            }

            return (long)0;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result)
        {
            if (!flag_Pagamento)
                Toast.makeText(getApplicationContext(), "Aluno Pendente de mensalidade, verificar na Web o status!", Toast.LENGTH_SHORT).show();

            if (!flag_Finished_sinc)
                Toast.makeText(getApplicationContext(), "Falha no sincronismo!", Toast.LENGTH_SHORT).show();

            switch (flag_sinc_erro)
            {
                case 1:
                    Toast.makeText(getApplicationContext(), "Falha na atualização da tabela aluno banco local!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Falha na atualização da tabela peso banco local!", Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    Toast.makeText(getApplicationContext(), "Falha na atualização da tabela corporal banco local!", Toast.LENGTH_SHORT).show();
                    break;

                case 4:
                    Toast.makeText(getApplicationContext(), "Falha na atualização da tabela corrida VO2 banco local!", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(), "Falha na atualização da tabela corrida TaxaMetabolica banco local!", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

            progressBarSincronismo.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarSincronismo.setVisibility(View.VISIBLE);
        }


        private boolean updateTbAluno(Aluno aluno)
        {
            boolean flag = false;

            if (aluno.getStatus().equals("PENDENTE"))
            {
                Login login = new Login();
                login = helperLogin.getLogin();
                aluno = helperAluno.insertDataPost(aluno,login.getLogin(),login.getPassword());
                if (aluno.getMatricula()>0)
                {
                    aluno.setStatus("OK");
                    if (helperAluno.updateName(aluno) > 0)  //update
                    {
                        matricula = aluno.getMatricula();
                        Register.id_login = aluno.getId_login();
                        flag_sinc_erro = 0;
                        flag = true;
                    }
                    else
                        flag_sinc_erro = 1; //tb_aluno local não atualizada
                }
            }
            else
                flag = true;

            return flag;
        }

        private boolean updateTbPeso()
        {
            List<Peso> lpeso =  new ArrayList<Peso>();
            boolean flag = false;

            lpeso = helperPeso.getDataStatus("PENDENTE");
            if (lpeso.size()==0)
                flag = true;

            for (Peso peso:lpeso)
            {
                flag = false;

                if (peso.getStatus().equals("PENDENTE"))
                {
                    if (helperPeso.insertDataPost(peso).getId_peso()>0)
                    {
                        peso.setStatus("OK");
                        if (helperPeso.updateName(peso) > 0)  //update
                        {
                            flag_sinc_erro = 0;
                            flag = true;
                        }
                        else {
                            flag_sinc_erro = 2; //tb_peso local não atualizada
                            break;
                        }
                    }
                }
                else
                    flag = true;
            }
            return flag;
        }

        private boolean updateTbCorporal()
        {
            List<CorporalModel> lcorporal =  new ArrayList<CorporalModel>();
            boolean flag = false;

            lcorporal = helperCorporal.getDataStatus("PENDENTE");
            if (lcorporal.size()==0)
                flag = true;

            for (CorporalModel corporal:lcorporal)
            {
                flag = false;

                if (corporal.getStatus().equals("PENDENTE"))
                {
                    if (helperCorporal.insertDataPost(corporal).getId_corporal()>0)
                    {
                        corporal.setStatus("OK");
                        if (helperCorporal.updateName(corporal) > 0)  //update
                        {
                            flag_sinc_erro = 0;
                            flag = true;
                        }
                        else {
                            flag_sinc_erro = 3; //tb_corporal local não atualizada
                            break;
                        }
                    }
                }
                else
                    flag = true;
            }
            return flag;
        }

        private boolean updateTbCorrida()
        {
            List<Corrida> lcorrida =  new ArrayList<Corrida>();
            boolean flag = false;

            lcorrida = helperCorrida.getDataStatus("PENDENTE");
            if (lcorrida.size()==0)
                flag = true;

            for (Corrida corrida:lcorrida)
            {
                flag = false;

                if (corrida.getStatus().equals("PENDENTE"))
                {
                    if (helperCorrida.insertDataPost(corrida).getId_corrida()>0)
                    {
                        corrida.setStatus("OK");
                        if (helperCorrida.updateName(corrida) > 0)  //update
                        {
                            flag_sinc_erro = 0;
                            flag = true;
                        }
                        else {
                            flag_sinc_erro = 4; //tb_corrida local não atualizada
                            break;
                        }
                    }
                }
                else
                    flag = true;
            }
            return flag;
        }

        private boolean updateTbVo2()
        {
            List<Vo2Model> lvo2 =  new ArrayList<Vo2Model>();
            boolean flag = false;

            lvo2 = helperVo2.getDataStatus("PENDENTE");
            if (lvo2.size()==0)
                flag = true;

            for (Vo2Model vo2:lvo2)
            {
                flag = false;

                if (vo2.getStatus().equals("PENDENTE"))
                {
                    if (helperVo2.insertDataPost(vo2).getId_vo2()>0)
                    {
                        vo2.setStatus("OK");
                        if (helperVo2.updateName(vo2) > 0)  //update
                        {
                            flag_sinc_erro = 0;
                            flag = true;
                        }
                        else {
                            flag_sinc_erro = 4; //tb_peso local não atualizada
                            break;
                        }
                    }
                }
                else
                    flag = true;
            }
            return flag;
        }

        private boolean updateTbTaxa()
        {
            List<TaxaModel> ltaxa =  new ArrayList<TaxaModel>();
            boolean flag = false;

            ltaxa = helperTaxa.getDataStatus("PENDENTE");
            if (ltaxa.size()==0)
                flag = true;

            for (TaxaModel taxa:ltaxa)
            {
                flag = false;

                if (taxa.getStatus().equals("PENDENTE"))
                {
                    if (helperTaxa.insertDataPost(taxa).getId_taxa()>0)
                    {
                        taxa.setStatus("OK");
                        if (helperTaxa.updateName(taxa) > 0)  //update VERIFICAR TIVE QUE COLOCAR =
                        {
                            flag_sinc_erro = 0;
                            flag = true;
                        }
                        else {
                            flag_sinc_erro = 5; //tb_peso local não atualizada
                            break;
                        }
                    }
                }
                else
                    flag = true;
            }
            return flag;
        }

    }

    //######################## NOTIFICATIONS ##################################
    private void scheduleNotification () {
        boolean alarmeAtivo = (PendingIntent.getBroadcast(this,0,new Intent(this,MyNotificationPublisher.class),PendingIntent.FLAG_NO_CREATE)==null);
        //alarmeAtivo =true;

        if (alarmeAtivo && (LoginClass.flag_notification==true||Register.flag_notification==true)){
            Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
            notificationIntent.putExtra("notificationId", 1);
            Intent notificationIntent_2 = new Intent(this, MyNotificationPublisher.class);
            notificationIntent_2.putExtra("notificationId_2", 2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, notificationIntent, 0);
            PendingIntent pendingIntent_2 = PendingIntent.getBroadcast(this, 2, notificationIntent_2, 0);

            //  ############### FIRST ALARM ###############
            Calendar calSet = Calendar.getInstance();
            calSet.setTimeInMillis(System.currentTimeMillis());
            calSet.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            calSet.set(Calendar.HOUR_OF_DAY, 18);
            calSet.set(Calendar.MINUTE, 35);
            calSet.set(Calendar.SECOND, 0);
            calSet.set(Calendar.MILLISECOND, 0);
            //calendar.set(Calendar. AM_PM , Calendar. AM ) ;
            //calendar.add(Calendar. DAY_OF_MONTH , 19 ) ;
            // agendar o alarme
            AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme.setRepeating(AlarmManager.RTC_WAKEUP,
                    calSet.getTimeInMillis() ,7 * 24 * 3600 * 1000, pendingIntent);
            //  ############### FIRST ALARM ###############

            //  ############### SECOND ALARM ###############
            Calendar calSet_2 = Calendar.getInstance();
            calSet_2.setTimeInMillis(System.currentTimeMillis());
            calSet_2.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            calSet_2.set(Calendar.HOUR_OF_DAY, 18);
            calSet_2.set(Calendar.MINUTE, 30);
            calSet_2.set(Calendar.SECOND, 0);
            calSet_2.set(Calendar.MILLISECOND, 0);
            // agendar o alarme
            AlarmManager alarme_2 = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarme_2.setRepeating(AlarmManager.RTC_WAKEUP,
                    calSet_2.getTimeInMillis() ,7 * 24 * 3600 * 1000, pendingIntent_2);
            //  ############### FIRST ALARM ###############

        }
        else{}
    }

    //#########################################################################

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        activities.remove(this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void showCase(String title,String subtitle,View view, final int type ){
        new GuideView.Builder(MainActivity.this)
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
                            showCase("Peso","Clique e descubra suas funções!",btn_peso,2);
                        } else if (type == 2) {
                           // showCase("Editar", "Escolha a data e edit suas medidas!", imageButton_corp_edit, 3);
                        } else if (type == 3) {
                           // showCase("Info","Clique para mais informações!",imageButton_corp_info,4);
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
