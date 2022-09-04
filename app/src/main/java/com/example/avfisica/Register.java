package com.example.avfisica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.avfisica.models.Login;
import com.example.avfisica.repository.LoginRepository;
import com.example.avfisica.resources.LoginResource;


public class Register extends AppCompatActivity {

    Button btn_avancar;
    ProgressBar progressBarRegister;
    ImageView imageView_password;
    EditText editText_email, editText_senha, editText_senha_2;
    public static String email, senha, pathFoto, senha_2;
    public static long id_login=0;
    public static boolean flag_notification = false;

    LoginRepository loginRepository;
    LoginResource helperLogin;
    Login login = new Login();
    LoginTaskPost loginPost;
    boolean flag_insert_post = false;
    boolean flag_password = true;
    boolean flag_login = true;

    Util uitl = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_avancar = (Button) findViewById(R.id.button_avancar);
        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_senha = (EditText) findViewById(R.id.editText_senha);
        editText_senha_2 = (EditText) findViewById(R.id.editText_senha_again);
        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);
        imageView_password = (ImageView) findViewById(R.id.imageView_reg_password);
        helperLogin = new LoginResource(this);
        progressBarRegister.setVisibility(View.INVISIBLE);

        loginRepository = new LoginRepository();

        //Imagem de VIEW PASSWORD
        imageView_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(flag_password==true) {
                        flag_password=false;
                        editText_senha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        editText_senha_2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    else {
                        flag_password = true;
                        editText_senha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editText_senha_2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                } catch (Exception e) {
                }
            }
        });

        // Button Avançar
        btn_avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //######################## check email ########################
                    String email_ = editText_email.getText().toString().trim();
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // wifit@wifit.com
                    String emailPattern_ = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+"; // wifit@wifit.com.br

                    if (email_.matches(emailPattern)||email_.matches(emailPattern_)){

                        email = editText_email.getText().toString();
                        senha = uitl.getSHA256(editText_senha.getText().toString());
                        senha_2 = editText_senha_2.getText().toString();

                        if (senha.isEmpty() || email.isEmpty() || senha_2.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "ERRO", Toast.LENGTH_SHORT).show();
                        } else {
                            if(uitl.verifySHA256(senha,senha_2))
                            {
                                login.setId(0);
                                login.setLogin(editText_email.getText().toString());
                                login.setPassword(senha);

                                Toast.makeText(getApplicationContext(), "Aguarde, Conectando com o Banco da Nuvem", Toast.LENGTH_SHORT).show();

                                loginPost = new LoginTaskPost();
                                loginPost.execute();
                            }else Toast.makeText(getApplicationContext(), "Senha Incorreta", Toast.LENGTH_SHORT).show();
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
    }


    private class LoginTaskPost extends AsyncTask<Void, Integer, Long> {
        String result;
        @Override
        protected Long doInBackground(Void... voids) {
            Login _login = new Login();
            Login _loginRepository = new Login();
            flag_insert_post = false;

            _loginRepository = loginRepository.findByLogin(login.getLogin(),login.getPassword());
            if ((_loginRepository!=null) && (_loginRepository.getId()!=0))
            {
                flag_login = false;  // informar login existente
            }
            else {
                _login = helperLogin.insertDataPost(login, login.getPassword());
                if (_login != null) {
                    flag_insert_post = true;
                    //dados utilizado pela tela de cadastro
                    email = _login.getLogin();
                    id_login = _login.getId();
                } else {

                }
            }
            return (long)0;
        }
        protected void onPostExecute(Long result)
        {
            if (!flag_insert_post){
                if (flag_login){
                Toast.makeText(getApplicationContext(), "Verificar a conexão, login não cadastrado ou atualizado!", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getApplicationContext(), "Este Login já existe!", Toast.LENGTH_SHORT).show();
            }
            else
                openActivityCadastro();

            progressBarRegister.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarRegister.setVisibility(View.VISIBLE);
        }
    }

    public void openActivityCadastro()
    {
        flag_notification = true;
        Intent intent = new Intent(this,Cadastro.class);
        startActivity(intent);
    }

}
