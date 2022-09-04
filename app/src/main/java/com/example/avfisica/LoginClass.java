package com.example.avfisica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.TaxaModel;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Corrida;
import com.example.avfisica.models.Login;
import com.example.avfisica.models.Pagamento;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.Vo2Model;
import com.example.avfisica.repository.AlunoRepository;
import com.example.avfisica.repository.TaxaRepository;
import com.example.avfisica.repository.CorporalRepository;
import com.example.avfisica.repository.CorridaRepository;
import com.example.avfisica.repository.LoginRepository;
import com.example.avfisica.repository.PagamentoRepository;
import com.example.avfisica.repository.PesoRepository;
import com.example.avfisica.repository.Vo2Repository;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.LoginResource;
import com.example.avfisica.resources.TaxaResource;
import com.example.avfisica.resources.CorporalResource;
import com.example.avfisica.resources.CorridaResource;
import com.example.avfisica.resources.PesoResource;
import com.example.avfisica.resources.Vo2Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LoginClass extends AppCompatActivity {

    Button btn_log_avancar;
    ProgressBar progressBarRegister;

    EditText editText_log_email, editText_log_senha;
    TextView textView_esq_senha;
    ImageView imageView_password;
    public static String email, senha, pathFoto;
    public static long id_login=0;
    public static boolean flag_notification = false;

    PagamentoRepository pagamentoRepository;
    PesoRepository pesoRepository;
    PesoResource helperPeso;
    CorridaRepository corridaRepository;
    CorridaResource helperCorrida;
    AlunoRepository alunoRepository;
    AlunoResource helperAluno;
    CorporalRepository corporalRepository;
    CorporalResource helperCorporal;
    Vo2Repository vo2Repository;
    Vo2Resource helperVo2;
    TaxaRepository taxaRepository;
    TaxaResource helperTaxa;
    LoginRepository loginRepository;
    LoginResource helperLogin;


    Util util = new Util();

    Login login = new Login();
    LoginTaskPost loginPost;

    boolean flag_erro_cadastro = false;
    byte flag_erro = 0;
    boolean flagResetSenha = false;
    final byte UPLOAD_OK =0;
    final byte LOGIN_CADASTRO=1;
    final byte LOGIN_INCORRETO=2;
    final byte MENSALIDADE=3;
    final byte PESO=4;
    final byte CORRIDA=5;
    final byte ALUNO=6;
    final byte CORPORAL=7;
    final byte VO2=8;
    final byte TAXA=9;
    final byte RESET_SENHA_OK =10;
    final byte SENHA_RESET =11;

    boolean flag_password=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_log_avancar = (Button) findViewById(R.id.button_log_avancar);
        editText_log_email = (EditText) findViewById(R.id.editText_log_email);
        editText_log_senha = (EditText) findViewById(R.id.editText_log_senha);
        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);
        textView_esq_senha = (TextView)findViewById(R.id.textView_esq_senha);
        imageView_password = (ImageView) findViewById(R.id.imageView_password);

        loginRepository = new LoginRepository();
        helperLogin = new LoginResource(this);
        pagamentoRepository = new PagamentoRepository();
        pesoRepository = new PesoRepository();
        helperPeso = new PesoResource(this);
        corridaRepository = new CorridaRepository();
        helperCorrida = new CorridaResource(this);
        alunoRepository = new AlunoRepository();
        helperAluno = new AlunoResource(this);
        corporalRepository = new CorporalRepository();
        helperCorporal = new CorporalResource(this);
        vo2Repository = new Vo2Repository();
        helperVo2 = new Vo2Resource(this);
        taxaRepository = new TaxaRepository();
        helperTaxa = new TaxaResource(this);


        progressBarRegister.setVisibility(View.INVISIBLE);



        //################################################################

        //Imagem de VIEW PASSWORD
        imageView_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(flag_password==true) {
                        flag_password=false;
                        editText_log_senha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    else {
                        flag_password = true;
                        editText_log_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                } catch (Exception e) {
                }
            }
        });

        // Button Avançar
        btn_log_avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //######################## check email ########################
                    String email_ = editText_log_email.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // wifit@wifit.com
                    String emailPattern_ = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"; // wifit@wifit.com.br
                    if (email_.matches(emailPattern)||email_.matches(emailPattern_))
                    {
                       // Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();

                    email = editText_log_email.getText().toString();
                    senha = editText_log_senha.getText().toString();

                    if (senha.isEmpty()||email.isEmpty()){
                        Toast.makeText(getApplicationContext(), "ERRO", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        login.setId(0);
                        login.setLogin(editText_log_email.getText().toString());
                        login.setPassword(util.getSHA256(editText_log_senha.getText().toString()));

                        Toast.makeText(getApplicationContext(), "Aguarde, Conectando com o Banco da Nuvem", Toast.LENGTH_SHORT).show();

                        loginPost = new  LoginTaskPost();
                        loginPost.execute();
                    }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Endereço de e-mail incorreto", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            }
        });

        // Text
        textView_esq_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email = editText_log_email.getText().toString();
                    if (email.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "Preencha o campo login ", Toast.LENGTH_SHORT).show();
                    }
                    else {
// ####################### VERSÃO FUTURA ##########################
//                        login.setId(0);
//                        login.setLogin(editText_log_email.getText().toString());
//                        login.setPassword(util.getSHA256(editText_log_senha.getText().toString()));
//                        flagResetSenha =  true;
//
//                        Toast.makeText(getApplicationContext(), "Aguarde, Conectando com o Banco da Nuvem", Toast.LENGTH_SHORT).show();
//
//                        loginPost = new  LoginTaskPost();
//                        loginPost.execute();
// ###################################################################
                        sendEmail();
                        Toast.makeText(getApplicationContext(), "Aguarde o e-mail com a nova senha!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Verificar a conexão, login não cadastrado!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //########## Sincronismo Download ################
    private class LoginTaskPost extends AsyncTask<Void, Integer, Long> {
        String result;
        @Override
        protected Long doInBackground(Void... voids) {
            flag_erro = 0;

            if (flagResetSenha)
            {
                flagResetSenha = false;
                if (verifyResetPassword()) {
                    if (loginRepository.getResetPassword(login.getLogin())) {
                        flag_erro = RESET_SENHA_OK;
                    }
                }
            }
            else if (verifyLogin())
            {
                //if (verifyPagamento())        // SEGUNDA VERSÃO--> MONEY
              //  {
                    if (downloadTbPeso())
                    {
                        if (downloadTbCorrida())
                        {
                            if (downloadTbCorporal())
                            {
                                if(downloadTbVo2())
                                {
                                    if(downloadTbTaxa()) {
                                        downloadTbAluno();
                                    }
                                }
                            }
                        }
                    }
                }
       //     }

            return (long)0;
        }

        protected void onPostExecute(Long result)
        {
            switch (flag_erro)
            {
                case UPLOAD_OK:
                    Toast.makeText(getApplicationContext(), "Upload concluido com sucesso", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_CADASTRO: //verifyLogin
                    Toast.makeText(getApplicationContext(), "Verificar a conexão, login não cadastrado!", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_INCORRETO: //verifyLogin senha incorreta
                    Toast.makeText(getApplicationContext(), "Password Incorreto!", Toast.LENGTH_SHORT).show();
                    break;
                case MENSALIDADE: //verifyPagamento
                    Toast.makeText(getApplicationContext(), "Pendente de mensalidade!", Toast.LENGTH_SHORT).show();
                    break;
                case PESO:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela peso!", Toast.LENGTH_SHORT).show();
                    break;
                case CORRIDA:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela corrida!", Toast.LENGTH_SHORT).show();
                    break;
                case ALUNO:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela aluno!", Toast.LENGTH_SHORT).show();
                    break;
                case CORPORAL:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela corporal!", Toast.LENGTH_SHORT).show();
                    break;
                case VO2:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela vo2!", Toast.LENGTH_SHORT).show();
                    break;
                case TAXA:
                    Toast.makeText(getApplicationContext(), "Falha no download da tabela TaxaMetabolica!", Toast.LENGTH_SHORT).show();
                    break;
                case RESET_SENHA_OK:
                    new AlertDialog.Builder(LoginClass.this)
                            .setTitle("Nova SENHA")
                            .setMessage("->Será encaminhada para o seu email a nova senha")
                            .show();
                    break;
                case SENHA_RESET:
                    new AlertDialog.Builder(LoginClass.this)
                            .setTitle("Primeiro Acesso com esta senha")
                            .setMessage("Devera trocar por outra")
                            .show();
                    break;

                default:
                    break;
            }

            progressBarRegister.setVisibility(View.INVISIBLE);
            if (flag_erro==UPLOAD_OK)
            {
                flag_notification = true;
                openMainActivy();
            }
            if (flag_erro_cadastro == true) // Caso o cadastro esteja errado
            {
                    flag_notification = true;
                    openCadastro();
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarRegister.setVisibility(View.VISIBLE);
        }
    }

    //########## metodos de verifiaçao ################
    public boolean verifyLogin()
    {
        Login _login = new Login();
        boolean flag = false;

        _login = loginRepository.findByLogin(login.getLogin(),login.getPassword());

        if ((_login!=null) && (_login.getId()!=0))
        {
            if (_login.getPassword().equals(login.getPassword()))
                flag = true;
            else if (_login.getPassword().equals(senha)) //password sem sha256 "senha resetada"
            {
                flag = true;
                flag_erro_cadastro = true; //força abrir a tela de cadastro para alterar a senha
                this.flag_erro = SENHA_RESET;
            }
            else if (!flag)
                this.flag_erro = LOGIN_INCORRETO;

            if (flag)
            {
                //dados utilizado pela tela de cadastro
                email = _login.getLogin();
                Register.email = email;
                id_login = _login.getId();

                //Atualiza o objeto que sera utilizado no sincronismo
                login = _login;

                //banco local
                if (helperLogin.getLogin().getId() != 0)
                {
                    if (helperLogin.updateName(login) == 0) ; //update
                    {
                        Toast.makeText(LoginClass.this,
                                "Falha ao aterar os dados", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (helperLogin.insertData(login) == 0) //insert
                {
                    Toast.makeText(LoginClass.this,
                            "Falha ao inserir os dados", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
            this.flag_erro = LOGIN_CADASTRO;

        return  flag;
    }

    public boolean verifyPagamento()
    {
        Pagamento pagamento = new Pagamento();
        boolean flag = false;

        pagamento = pagamentoRepository.findPagamentoByIdLogin(util.extractMonthOfday(), id_login);
        if (pagamento!=null)
        {
            flag = true;
        }
        else
            this.flag_erro = MENSALIDADE;

        return flag;
    }

    public boolean downloadTbPeso()
    {
        List<Peso> lpeso = new ArrayList<Peso>();
        boolean flag = false;
        try {
            long id_peso_init = helperPeso.getNextId();
            long id_peso_final = pesoRepository.findCountid(id_login);

            if (id_peso_init < id_peso_final) {
                lpeso = pesoRepository.findPesoRangeByIdLogin(id_peso_init, id_peso_final, id_login);
                for (Peso peso : lpeso) {
                    flag = false;
                    if (helperPeso.insertData(peso) > 0)
                        flag = true;
                    else
                        break;
                }
            } else
                flag = true;
        }catch (Exception e)
        {
            flag_erro = PESO;
        }
        return flag;
    }

    public boolean downloadTbCorrida()
    {
        List<Corrida> lcorrida = new ArrayList<Corrida>();
        boolean flag = false;
        try {
            long id_corrida_init = helperCorrida.getNextId();
            long id_corrida_final = corridaRepository.findCountid(id_login);

            if (id_corrida_init < id_corrida_final) {
                lcorrida = corridaRepository.findPesoRangeByIdLogin(id_corrida_init, id_corrida_final, id_login);
                for (Corrida corrida : lcorrida) {
                    flag = false;
                    if (helperCorrida.insertData(corrida) > 0)
                        flag = true;
                    else
                        break;
                }
            } else
                flag = true;
        }catch (Exception e)
        {
            flag_erro = CORRIDA;
        }
        return flag;
    }

    public boolean downloadTbVo2()
    {
        List<Vo2Model> lvo2 = new ArrayList<Vo2Model>();
        boolean flag = false;
        try {
            long id_vo2_init = helperVo2.getNextId();
            long id_vo2_final = vo2Repository.findCountid(id_login);

            if (id_vo2_init < id_vo2_final) {
                lvo2 = vo2Repository.findPesoRangeByIdLogin(id_vo2_init, id_vo2_final, id_login);
                for (Vo2Model vo2 : lvo2) {
                    flag = false;
                    if (helperVo2.insertData(vo2) > 0)
                        flag = true;
                    else
                        break;
                }
            } else
                flag = true;
        }catch (Exception e)
        {
            flag_erro = VO2;
        }
        return flag;
    }

    public boolean downloadTbTaxa()
    {
        List<TaxaModel> ltaxa = new ArrayList<TaxaModel>();
        boolean flag = false;
        try {
            long id_taxa_init = helperTaxa.getNextId();
            long id_taxa_final = taxaRepository.findCountid(id_login);

            if (id_taxa_init < id_taxa_final) {
                ltaxa = taxaRepository.findPesoRangeByIdLogin(id_taxa_init, id_taxa_final, id_login);
                for (TaxaModel taxa : ltaxa) {
                    flag = false;
                    if (helperTaxa.insertData(taxa) > 0)
                        flag = true;
                    else
                        break;
                }
            } else
                flag = true;
        }catch (Exception e)
        {
            flag_erro = TAXA;
        }
        return flag;
    }

    public boolean downloadTbAluno()
    {
        Aluno aluno = new Aluno();
        boolean flag = false;

        try {
            aluno = alunoRepository.findByAluno(login);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Objects.isNull(aluno)) {
                    flag_erro = ALUNO;
                    flag_erro_cadastro = true;
                }
                else {
                    if (aluno.getId_login()!=0)
                    {
                        aluno.setStatus("OK");
                        if (helperAluno.insertData(aluno) > 0)
                            flag = true;
                    }
                    else
                        flag_erro = ALUNO;
                }
            }


        }catch (Exception e)
        {
            flag_erro = ALUNO;
        }
        return flag;
    }

    public boolean downloadTbCorporal()
    {
        List<CorporalModel> lcorporal = new ArrayList<CorporalModel>();
        boolean flag = false;
        try {
            long id_corporal_init = helperCorporal.getNextId();
            long id_corporal_final = corporalRepository.findCountid(id_login);

            if (id_corporal_init < id_corporal_final) {
                lcorporal = corporalRepository.findCorporalRangeByIdLogin(id_corporal_init, id_corporal_final, id_login);
                for (CorporalModel corporal : lcorporal) {
                    flag = false;
                    if (helperCorporal.insertData(corporal) > 0)
                        flag = true;
                    else
                        break;
                }
            } else
                flag = true;
        }catch (Exception e)
        {
            flag_erro = CORPORAL;
        }
        return flag;
    }

    public boolean verifyResetPassword()
    {
        Login _login = new Login();
        boolean flag = false;

        _login = loginRepository.findByLogin(login.getLogin(), login.getPassword() );

        if ((_login!=null) && (_login.getId()!=0))
                flag = true;
        else
            this.flag_erro = LOGIN_CADASTRO;

        return  flag;
    }



    //########## Abertura de Tela ################
    public void openMainActivy()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //########## Abertura de Cadastro -- Erro no cadastro ################
    public void openCadastro()
    {
        Intent intent = new Intent(this, Cadastro.class);
        startActivity(intent);
    }
    public void sendEmail(){

        String[] TO = {"wifit.suporte@gmail.com"};
        String[] CC = {"matheusfscotton@hotmail.com","fabioazola@gmail.com"};
        String emailbody = "Por favor alterar a minha senha!" + "Login --> " + editText_log_email.getText().toString();
        String emailassunto = "Troca de senha";
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailassunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailbody);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            alertAviso();
            // Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginClass.this,
                    "E-mail não instalado", Toast.LENGTH_SHORT).show();
        }
    }
   public void alertAviso(){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginClass.this);
            builder.setMessage("A nova senha será enviada para o seu e-mail/login!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

}
