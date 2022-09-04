package com.example.avfisica;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Login;
import com.example.avfisica.repository.LoginRepository;
import com.example.avfisica.resources.AlunoResource;
import com.example.avfisica.resources.LoginResource;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cadastro extends AppCompatActivity {
  Button btn_cadastrar, btn_voltar;
  EditText editText_idade, editText_altura, editText_peso,editText_nome, editText_cintaMac;
  TextView textView_view, textView_faixa, textView_senha;
  CheckBox checkBox_mulher, checkBox_homem;
  TextView textView_atual;
  TextView textView_futura;
  ImageButton btn_pace;
  Switch switch_sexo;
  Float resultado;
  String t1, t2, t3, t4, t5;
  String email,senha;

  AlunoResource helperAluno;
  Aluno aluno = new Aluno();
  Aluno alunoUpdate_front = new Aluno();
  boolean flag_update_insert_local = false;
  boolean flag_insert_post = false;

  boolean flag_nova_senha = false;
  String strNovaSenha = "";
  String strSenhaAtual ="";

  LoginResource helperLogin;
  Login login = new Login();
  LoginRepository loginRepository;

  Util util = new Util();

  //constante de status
  byte flag_erro = 0;
  final byte INSERT_BD_LOCAL_OK   = 1;
  final byte UPLOAD_BD_LOCAL_OK  = 2;
  final byte INSERT_BD_LOCAL     = 3;
  final byte UPLOAD_BD_LOCAL     = 4;
  final byte NOVA_SENHA_OK       = 5;
  final byte NOVA_SENHA_LOGIN    = 6;
  final byte NOVA_SENHA_PASSWORD = 7;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cadastro);

    btn_cadastrar = (Button) findViewById(R.id.button_cadastrar);
    btn_voltar = (Button) findViewById(R.id.button_salvar);
    editText_altura = (EditText) findViewById(R.id.editText_altura);
    editText_altura.setFilters(new InputFilter[]{new Cadastro.DecimalDigitsInputFilter(3, 1)});
    editText_idade = (EditText) findViewById(R.id.editText_idade);
    editText_idade.setFilters(new InputFilter[]{new Cadastro.DecimalDigitsInputFilter(3, 1)});
    editText_peso = (EditText) findViewById(R.id.editText_peso);
    editText_peso.setFilters(new InputFilter[]{new Cadastro.DecimalDigitsInputFilter(3, 1)});
    editText_nome = (EditText) findViewById(R.id.editText_name);
    textView_senha = (TextView) findViewById(R.id.textView_troca_senha);
    textView_faixa = (TextView) findViewById(R.id.textView_faixa);
    checkBox_homem = (CheckBox) findViewById(R.id.checkBox_homem);
    checkBox_mulher = (CheckBox) findViewById(R.id.checkBox_mulher);
    editText_cintaMac = (EditText) findViewById(R.id.editText_cintaMac);

    helperAluno = new AlunoResource(this);
    helperLogin = new LoginResource(this);
    loginRepository = new LoginRepository();

    //######### update front-end #############
    alunoUpdate_front = helperAluno.getData_all();
    if(alunoUpdate_front.getId_login()!=0)
    {
      editText_nome.setText(alunoUpdate_front.getNome());
      editText_idade.setText(Long.toString(alunoUpdate_front.getIdade()));
      editText_altura.setText(alunoUpdate_front.getAltura());
      editText_peso.setText(Float.toString(alunoUpdate_front.getPeso()));
      editText_cintaMac.setText(alunoUpdate_front.getCintaMac());

      if (alunoUpdate_front.getSexo().equals("M")) {
        // switch_sexo.setChecked(false);
        checkBox_homem.setChecked(true);
        checkBox_mulher.setChecked(false);
      }
      else {
        checkBox_homem.setChecked(false);
        checkBox_mulher.setChecked(true);
      }
    }

    checkBox_homem.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if (checkBox_homem.isChecked())
        {
          t3 = "M";
          checkBox_mulher.setChecked(false);
          //Perform action when you touch on checkbox and it change to selected state
        }
        else
        {
          //Perform action when you touch on checkbox and it change to unselected state
        }
      }
    });


    checkBox_mulher.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        if (checkBox_mulher.isChecked())
        {
          t3 = "F";
          checkBox_homem.setChecked(false);
          //Perform action when you touch on checkbox and it change to selected state
        }
        else
        {
          //Perform action when you touch on checkbox and it change to unselected state
        }
      }
    });

    // Text
    textView_senha.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          newSenha();
        } catch (Exception e) {
        }
      }
    });

    //Salvar
    btn_cadastrar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          t1 = editText_nome.getText().toString();
          t2 = editText_idade.getText().toString();
          t4 = editText_altura.getText().toString();
          t5 = editText_peso.getText().toString();

          if (checkBox_homem.isChecked())
            t3 = "M";
          else if (checkBox_mulher.isChecked())
            t3 = "F";
          else
            t3="";

          if(t1.isEmpty()||t2.isEmpty()||t3.isEmpty()||t4.isEmpty()||t5.isEmpty()) {
            Toast.makeText(getApplicationContext(), "ERRO", Toast.LENGTH_SHORT).show();
          }
          else {
            if (Register.id_login!=0) {
              aluno.setMatricula(MainActivity.matricula); //matricula statica
              aluno.setNome(editText_nome.getText().toString());
              aluno.setIdade(Long.parseLong(editText_idade.getText().toString()));
              aluno.setAltura(editText_altura.getText().toString());
              aluno.setPeso(Float.parseFloat(editText_peso.getText().toString()));
              aluno.setStatus("PENDENTE");
              aluno.setSexo(t3);

              aluno.setEmail(Register.email);
              aluno.setId_login(Register.id_login);
              aluno.setPathFoto(Register.pathFoto);

              if (!editText_cintaMac.getText().toString().isEmpty())
                aluno.setCintaMac(editText_cintaMac.getText().toString());
              else
                aluno.setCintaMac("-");


              new MyTaskPost().execute();
            }
            else
            {
              if(LoginClass.id_login!=0){
                aluno.setMatricula(MainActivity.matricula); //matricula statica
                aluno.setNome(editText_nome.getText().toString());
                aluno.setIdade(Long.parseLong(editText_idade.getText().toString()));
                aluno.setAltura(editText_altura.getText().toString());
                aluno.setPeso(Float.parseFloat(editText_peso.getText().toString()));
                aluno.setStatus("PENDENTE");
                aluno.setSexo(t3);

                aluno.setEmail(LoginClass.email);
                aluno.setId_login(LoginClass.id_login);
                aluno.setPathFoto(LoginClass.pathFoto);

                aluno.setCintaMac(editText_cintaMac.getText().toString());

                new MyTaskPost().execute();
              }
              else Toast.makeText(getApplicationContext(), "Verificar a conexão, login não cadastrado!", Toast.LENGTH_SHORT).show();
            }

          }

          //######### update front-end #############
          alunoUpdate_front = helperAluno.getData_all();
          editText_nome.setText(alunoUpdate_front.getNome());
          editText_idade.setText(Long.toString(alunoUpdate_front.getIdade()));
          editText_altura.setText(alunoUpdate_front.getAltura());
          editText_peso.setText(Float.toString(alunoUpdate_front.getPeso()));
          editText_cintaMac.setText(alunoUpdate_front.getCintaMac());

        } catch (Exception e) {
          e.printStackTrace();
          Toast.makeText(getApplicationContext(), "Falha login não cadastrado ou alterado!", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private class MyTaskPost extends AsyncTask<Void, Integer, Long> {
    String result;
    @Override
    protected Long doInBackground(Void... voids) {
      if (!flag_nova_senha)
      {
        flag_update_insert_local = false;

        //banco local
        if (helperAluno.getData(aluno.getId_login()).getId_login() != 0) {
          if (helperAluno.updateName(aluno) > 0) ; //update
          {
            flag_update_insert_local = true;
            flag_erro = UPLOAD_BD_LOCAL_OK;
          }
        } else if (helperAluno.insertData(aluno) >= 0) //insert
        {
          flag_update_insert_local = true;
          flag_erro = INSERT_BD_LOCAL_OK;
        }
      }
      else if (flag_nova_senha)
      {
        flag_nova_senha = false;
        login = verifyResetPassword(Register.email, strSenhaAtual);

        if ((login==null)|| login.getId()==0)
        {
          flag_erro = NOVA_SENHA_LOGIN;
        }
        else if (login.getPassword()==null)
        {
          flag_erro = NOVA_SENHA_PASSWORD;
        }
        else //carrega a nova senha para fazer update na nuvem
        {
          try
          {
              login.setPassword(util.getSHA256(strNovaSenha));
              if (helperLogin.insertDataPost(login, util.getSHA256(strSenhaAtual)).getId()>0)
              {
              flag_erro = NOVA_SENHA_OK;
              }
          }
          catch (NoSuchAlgorithmException e)
          {
            e.printStackTrace();
          }
          catch (UnsupportedEncodingException e)
          {
            e.printStackTrace();
          }
        }
      }
      return (long)0;
    }

    protected void onProgressUpdate(Integer... progress) {
      //setProgressPercent(progress[0]);
    }

    protected void onPostExecute(Long result)
    {
      switch (flag_erro)
      {
        case INSERT_BD_LOCAL_OK:
        case UPLOAD_BD_LOCAL_OK:
          openActivityMain();
          break;
        case INSERT_BD_LOCAL:
        case UPLOAD_BD_LOCAL:
          Toast.makeText(getApplicationContext(), "Erro ao cadastrar, atualizar no banco local!", Toast.LENGTH_SHORT).show();
          break;
        case NOVA_SENHA_OK:
          Toast.makeText(getApplicationContext(),  "Senha alterada!", Toast.LENGTH_SHORT).show();
          break;
        case NOVA_SENHA_LOGIN:
          Toast.makeText(getApplicationContext(),  "Login Incorreto!", Toast.LENGTH_SHORT).show();
          break;
        case NOVA_SENHA_PASSWORD:
          Toast.makeText(getApplicationContext(),  "Senha Atual Incorreta!", Toast.LENGTH_SHORT).show();
          break;
        default:
          break;
      }

    }
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }
  }


  public void openActivityMain()
  {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
      mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
      Matcher matcher = mPattern.matcher(dest);
      if (!matcher.matches())
        return "";
      return null;
    }
  }

  public void newSenha()
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(Cadastro.this);
    View mView  = Cadastro.this.getLayoutInflater().inflate(R.layout.dialog_senha,null);
    textView_atual = (TextView) mView.findViewById(R.id.textView_senha_atual);
    textView_futura = (TextView) mView.findViewById(R.id.textView_senha_futura);

    //cabeçalho da popup
    builder.setTitle("Trocar senha");
    builder.setView(mView);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        flag_nova_senha = true;
        strSenhaAtual = textView_atual.getText().toString();
        strNovaSenha = textView_futura.getText().toString();

        new MyTaskPost().execute();
      }
    });
    builder.show();

  }

  public Login verifyResetPassword(String strLogin, String senha)
  {
    Login _login = new Login();
    boolean flag = false;
   try {
         _login = loginRepository.findByLogin(strLogin, util.getSHA256(senha));

         if ((_login != null) && (_login.getId() != 0)) //erro de login
           if (!_login.getPassword().equals(util.getSHA256(senha)))
             _login.setPassword(null); //erro de password
   }
   catch (Exception e) {
   }

    return  _login;
  }

  @Override
  public void onBackPressed() {
        Intent intent = new Intent(Cadastro.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent); // retorna para pagina principal
  }


}
