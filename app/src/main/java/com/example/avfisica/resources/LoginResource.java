package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class LoginResource {
  myDbHelper myhelper;
  public static String teste;
  //Construtor

   public LoginResource(Context context)
  {
    myhelper = new  myDbHelper(context);
  }

  //################### BANCO DE DADOS NUVEM ###############
  //POST (INSERT , UPDATE)
  public Login insertDataPost(Login login, String password)
  {
    Util util = new Util();
    Login loginReturn = new Login();

    try{
      JSONObject cred = new JSONObject();
      cred.put("id", "0");
      cred.put("login", login.getLogin());
      cred.put("password", login.getPassword());

      //String url="http://192.168.0.102:8080/api/fluxoAula";
      String url="http://apirest-wifit.herokuapp.com/api/login/"+login.getLogin()+"/"+password;
      URL object=new URL(url);

      HttpURLConnection con = (HttpURLConnection) object.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);

      PrintStream printStream = new PrintStream(con.getOutputStream());
      printStream.println(cred); //seta o que voce vai enviar

      con.connect(); //envia para o servidor

      String jsonDeResposta = new Scanner(con.getInputStream()).next(); //pega resposta
      Log.d("HTTP POST RETURN", jsonDeResposta);
      try {
        loginReturn = (Login) util.convertJsonToObject(jsonDeResposta, login, "tb_login");
      }catch (Exception e){
        loginReturn.setId(0); //erro
      }
    }
    //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
    catch (IOException | JSONException e){
      e.printStackTrace();
      loginReturn.setId(0); //insere erro
    }
    return loginReturn;
  }

    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Login login)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID, login.getId());
        contentValues.put(myDbHelper.LOGIN, login.getLogin());
        contentValues.put(myDbHelper.PASSWORD, login.getPassword());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public int updateName(Login login)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID,login.getId());
        contentValues.put(myDbHelper.LOGIN, login.getLogin());
        contentValues.put(myDbHelper.PASSWORD, login.getPassword());
        String[] whereArgs= {Long.toString(login.getId())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID+" = ?",whereArgs );
        return count;
    }


    public Login getLogin()
   {
        Login login = new Login ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.ID,
                myDbHelper.LOGIN,
                myDbHelper.PASSWORD};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            long id =cursor.getInt(cursor.getColumnIndex(myDbHelper.ID));
            String email_login =cursor.getString(cursor.getColumnIndex(myDbHelper.LOGIN));
            String  password =cursor.getString(cursor.getColumnIndex(myDbHelper.PASSWORD));

            login.setId(id);
            login.setLogin(email_login);
            login.setPassword(password);
        }
        return login;
    }


    //CLASSE STATICA MODEL DO BANCO LOCAL tb_aluno
    public static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "AV_FISICA_LOGIN_BD";    // Database Name
        private static final String TABLE_NAME = "tb_login";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID = "Id";     // Column I (Primary Key)
        private static final String LOGIN = "Login";
        private static final String PASSWORD = "Password";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + ID + " INTEGER PRIMARY KEY, "
                + LOGIN + " VARCHAR(255) ,"
                + PASSWORD + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                // Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {}
        }
    }



}
