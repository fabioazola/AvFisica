package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Peso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PesoResource {
  myDbHelper myhelper;

  //Construtor
  public PesoResource(Context context)
  {
    myhelper = new myDbHelper(context);
  }

  //################### BANCO DE DADOS NUVEM ###############
  //POST (INSERT , UPDATE)
  public Peso insertDataPost(Peso peso)
  {
    Peso pesoReturn = new Peso();
    Util util = new Util();
    try{
      JSONObject cred = new JSONObject();
      cred.put("id_peso", peso.getId_peso());
      cred.put("data", peso.getData());
      cred.put("peso", peso.getPeso());
      cred.put("objetivo", peso.getObjetivo());
      cred.put("id_login", peso.getId_login());

      //String url="http://192.168.0.102:8080/api/fluxoAula";
      String url="http://apirest-wifit.herokuapp.com/api/peso";
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
        pesoReturn = (Peso) util.convertJsonToObject(jsonDeResposta, peso, "tb_peso");
      }catch (Exception e){
        pesoReturn.setId_peso(0); //erro
      }
    }
    //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
    catch (IOException | JSONException e){
      e.printStackTrace();

      pesoReturn.setId_peso(0); //erro
    }
    return pesoReturn;
  }


  //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
  public long insertData(Peso peso)
  {
    SQLiteDatabase dbb = myhelper.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(myDbHelper.ID_PESO, getNextId());
    contentValues.put(myDbHelper.DATA, peso.getData());
    contentValues.put(myDbHelper.PESO, peso.getPeso());
    contentValues.put(myDbHelper.OBJETIVO, peso.getObjetivo());
    contentValues.put(myDbHelper.ID_LOGIN, peso.getId_login());
    contentValues.put(myDbHelper.STATUS, peso.getStatus());
    long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
    return ret;
  }

  public List<Peso> getData(long id_login_input)
  {
    List<Peso> lpeso =  new ArrayList<Peso>();
    SQLiteDatabase db = myhelper.getWritableDatabase();
    String[] columns = {
            myDbHelper.ID_PESO,
            myDbHelper.DATA,
            myDbHelper.PESO,
            myDbHelper.OBJETIVO,
            myDbHelper.ID_LOGIN,
            myDbHelper.STATUS};

    try{
      Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[4] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.ID_PESO +" ASC", null);
      StringBuffer buffer= new StringBuffer();
      while (cursor.moveToNext())
      {
        Peso _peso = new Peso ();

        String id_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_PESO));
        String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
        String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
        String objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
        String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
        String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

        _peso.setId_peso(Long.parseLong(id_peso));
        _peso.setData(data);
        _peso.setPeso(Float.parseFloat(peso));
        _peso.setObjetivo(Float.parseFloat(objetivo));
        _peso.setId_login(Long.parseLong(id_login));
        _peso.setStatus(status);
        lpeso.add(_peso);

      }
    }catch (Exception e){

    }
    return lpeso;
  }

  public List<Peso> getDataStatus(String input_status)
  {
    List<Peso> lpeso =  new ArrayList<Peso>();
    SQLiteDatabase db = myhelper.getWritableDatabase();
    try{
      Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
              "id_peso",
              "data",
              "peso",
              "objetivo",
              "id_login",
              "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

      StringBuffer buffer= new StringBuffer();
      while (cursor.moveToNext())
      {
        Peso _peso = new Peso ();

        String id_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_PESO));
        String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
        String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
        String objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
        String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
        String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

        _peso.setId_peso(Long.parseLong(id_peso));
        _peso.setData(data);
        _peso.setPeso(Float.parseFloat(peso));
        _peso.setObjetivo(Float.parseFloat(objetivo));
        _peso.setId_login(Long.parseLong(id_login));
        _peso.setStatus(status);
        lpeso.add(_peso);
      }
    }catch (Exception e){

    }
    return lpeso;
  }

  public Peso getData_all()
  {
    Peso _peso = new Peso ();
    SQLiteDatabase db = myhelper.getWritableDatabase();
    String[] columns = {
            myDbHelper.ID_PESO,
            myDbHelper.DATA,
            myDbHelper.PESO,
            myDbHelper.OBJETIVO,
            myDbHelper.ID_LOGIN,
            myDbHelper.STATUS};

    Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
    StringBuffer buffer= new StringBuffer();
    while (cursor.moveToNext())
    {
      String id_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_PESO));
      String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
      String  peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
      String  objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
      String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
      String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

      _peso.setId_peso(Long.parseLong(id_peso));
      _peso.setData(data);
      _peso.setPeso(Long.parseLong(peso));
      _peso.setObjetivo(Long.parseLong(objetivo));
      _peso.setId_login(Long.parseLong(id_login));
      _peso.setStatus(status);
    }

    if(_peso==null){
      _peso.setId_login(0);
    }

    return _peso;
  }

  public int getNextId()
  {
    String countQuery = "SELECT  * FROM " + myDbHelper.TABLE_NAME;
    SQLiteDatabase db = myhelper.getReadableDatabase();
    Cursor cursor = db.rawQuery(countQuery, null);
    int count = cursor.getCount();
    cursor.close();

    if (count == 0)
      count = 1;
    else
      count ++;
    return count;
  }

  public  int delete(long id_peso)
  {
    SQLiteDatabase db = myhelper.getWritableDatabase();
    String[] whereArgs ={Long.toString(id_peso)};

    int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.ID_PESO+" = ?",whereArgs);
    return  count;
  }

  public int updateName(Peso peso)
  {
    SQLiteDatabase db = myhelper.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(myDbHelper.ID_PESO,peso.getId_peso());
    contentValues.put(myDbHelper.DATA,peso.getData());
    contentValues.put(myDbHelper.PESO,peso.getPeso());
    contentValues.put(myDbHelper.OBJETIVO,peso.getObjetivo());
    contentValues.put(myDbHelper.STATUS,peso.getStatus());
    String[] whereArgs= {Long.toString(peso.getId_peso())};
    int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_PESO+" = ?",whereArgs );
    return count;
  }



  //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
  static class myDbHelper extends SQLiteOpenHelper
  {
    private static final String DATABASE_NAME = "AV_FISICA_PESO_BD";    // Database Name
    private static final String TABLE_NAME = "tb_peso";   // Table Name
    private static final int DATABASE_Version = 4;    // Database Version
    private static final String ID_PESO="id_peso";     // Column I (Primary Key)
    private static final String DATA = "data";    //Column II
    private static final String PESO= "peso";    // Column III
    private static final String OBJETIVO= "objetivo";    // Column IV
    private static final String ID_LOGIN = "id_login";    // Column IV
    private static final String STATUS = "status";    // Column X
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+ID_PESO+" INTEGER PRIMARY KEY, "
            +DATA+" VARCHAR(255), "
            + PESO+" VARCHAR(225),"
            + OBJETIVO+" VARCHAR(225),"
            + ID_LOGIN + " INTEGER,"
            + STATUS + " VARCHAR(255));";
    private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
    private Context context;


    public myDbHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_Version);
      this.context=context;
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
        // Message.message(context,"OnUpgrade");
        db.execSQL(DROP_TABLE);
        onCreate(db);
      }catch (Exception e) {
        //  Message.message(context,""+e);
      }
    }
  }
}
