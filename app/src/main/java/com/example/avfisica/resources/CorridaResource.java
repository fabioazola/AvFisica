package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Corrida;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CorridaResource {
  myDbHelper myhelper;

  //Construtor
  public CorridaResource(Context context)
  {
    myhelper = new myDbHelper(context);
  }

  //################### BANCO DE DADOS NUVEM ###############
  //POST (INSERT , UPDATE)
  public Corrida insertDataPost(Corrida corrida)
  {
    Corrida corridaReturn = new Corrida();
    Util util = new Util();
    try{
      JSONObject cred = new JSONObject();
      cred.put("id_corrida", corrida.getId_corrida());
      cred.put("data", corrida.getData());
      cred.put("distancia", corrida.getDistancia());
      cred.put("tempo", corrida.getTempo());
      cred.put("pace", corrida.getPace());
      cred.put("vo2", corrida.getVo2());
      cred.put("nivel", corrida.getNivel());
      cred.put("id_login", corrida.getId_login());

      //String url="http://192.168.0.102:8080/api/corrida";
      String url="http://apirest-wifit.herokuapp.com/api/corrida";
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
        corridaReturn = (Corrida) util.convertJsonToObject(jsonDeResposta, corrida, "tb_corrida");
      }catch (Exception e){
        corridaReturn.setId_login(0); //erro
      }
    }
    //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
    catch (IOException | JSONException e){
      e.printStackTrace();

      corridaReturn.setId_login(0); //erro
    }
    return corridaReturn;
  }


  //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
  public long insertData(Corrida corrida)
  {
    SQLiteDatabase dbb = myhelper.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(myDbHelper.ID_CORRIDA, getNextId());
    contentValues.put(myDbHelper.DATA, corrida.getData());
    contentValues.put(myDbHelper.DISTANCIA, corrida.getDistancia());
    contentValues.put(myDbHelper.TEMPO, corrida.getTempo());
    contentValues.put(myDbHelper.PACE, corrida.getPace());
    contentValues.put(myDbHelper.VO2, corrida.getVo2());
    contentValues.put(myDbHelper.NIVEL, corrida.getNivel());
    contentValues.put(myDbHelper.ID_LOGIN, corrida.getId_login());
    contentValues.put(myDbHelper.STATUS, corrida.getStatus());
    long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
    return ret;
  }

  public List<Corrida> getData(long id_login_input)
  {
    List<Corrida> lcorrida =  new ArrayList<Corrida>();
    SQLiteDatabase db = myhelper.getWritableDatabase();
    String[] columns = {
            myDbHelper.ID_CORRIDA,
            myDbHelper.DATA,
            myDbHelper.DISTANCIA,
            myDbHelper.TEMPO,
            myDbHelper.PACE,
            myDbHelper.VO2,
            myDbHelper.NIVEL,
            myDbHelper.ID_LOGIN,
            myDbHelper.STATUS};
    try{
      Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[7] + "="+ Long.toString(id_login_input),null, null, null, null, null);
      StringBuffer buffer= new StringBuffer();
      while (cursor.moveToNext())
      {
        Corrida corrida = new Corrida ();

        String id_corrida =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CORRIDA));
        String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
        String distancia =cursor.getString(cursor.getColumnIndex(myDbHelper.DISTANCIA));
        String tempo =cursor.getString(cursor.getColumnIndex(myDbHelper.TEMPO));
        String pace =cursor.getString(cursor.getColumnIndex(myDbHelper.PACE));
        String vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.VO2));
        String nivel =cursor.getString(cursor.getColumnIndex(myDbHelper.NIVEL));
        String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
        String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

        corrida.setId_corrida(Long.parseLong(id_corrida));
        corrida.setData(data);
        corrida.setDistancia((Float.parseFloat(distancia)));
        corrida.setTempo(tempo);
        corrida.setPace((Float.parseFloat(pace)));
        corrida.setVo2((Float.parseFloat(vo2)));
        corrida.setNivel((Long.parseLong(nivel)));
        corrida.setId_login(Long.parseLong(id_login));
        corrida.setStatus(status);
        lcorrida.add(corrida);
      }
    }catch (Exception e){

    }
    return lcorrida;
  }

  public List<Corrida> getDataStatus(String input_status)
  {
    List<Corrida> lcorrida =  new ArrayList<Corrida>();
    SQLiteDatabase db = myhelper.getWritableDatabase();
    try{
      Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
              "id_corrida",
              "data",
              "distancia",
              "tempo",
              "pace",
              "vo2",
              "nivel",
              "id_login",
              "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

      StringBuffer buffer= new StringBuffer();
      while (cursor.moveToNext()) {
        Corrida corrida = new Corrida();

        String id_corrida = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CORRIDA));
        String data = cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
        String distancia = cursor.getString(cursor.getColumnIndex(myDbHelper.DISTANCIA));
        String tempo = cursor.getString(cursor.getColumnIndex(myDbHelper.TEMPO));
        String pace = cursor.getString(cursor.getColumnIndex(myDbHelper.PACE));
        String vo2 = cursor.getString(cursor.getColumnIndex(myDbHelper.VO2));
        String nivel = cursor.getString(cursor.getColumnIndex(myDbHelper.NIVEL));
        String id_login = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
        String status = cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

        corrida.setId_corrida(Long.parseLong(id_corrida));
        corrida.setData(data);
        corrida.setDistancia((Float.parseFloat(distancia)));
        corrida.setTempo(tempo);
        corrida.setPace((Float.parseFloat(pace)));
        corrida.setVo2((Float.parseFloat(vo2)));
        corrida.setNivel((Long.parseLong(nivel)));
        corrida.setId_login(Long.parseLong(id_login));
        corrida.setStatus(status);
        lcorrida.add(corrida);
      }

    }catch (Exception e)
    {
      e.printStackTrace();
    }
    return lcorrida;
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

  public  int delete(long id_corrida)
  {
    SQLiteDatabase db = myhelper.getWritableDatabase();
    String[] whereArgs ={Long.toString(id_corrida)};

    int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_CORRIDA+" = ?",whereArgs);
    return  count;
  }

  public int updateName(Corrida corrida)
  {
    SQLiteDatabase db = myhelper.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(myDbHelper.ID_CORRIDA,corrida.getId_corrida());
    contentValues.put(myDbHelper.DATA, corrida.getData());
    contentValues.put(myDbHelper.DISTANCIA, corrida.getDistancia());
    contentValues.put(myDbHelper.TEMPO, corrida.getTempo());
    contentValues.put(myDbHelper.PACE, corrida.getPace());
    contentValues.put(myDbHelper.VO2, corrida.getVo2());
    contentValues.put(myDbHelper.NIVEL, corrida.getNivel());
    contentValues.put(myDbHelper.STATUS,corrida.getStatus());
    String[] whereArgs= {Long.toString(corrida.getId_corrida())};
    int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_CORRIDA+" = ?",whereArgs );
    return count;
  }


  //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
  static class myDbHelper extends SQLiteOpenHelper
  {
    private static final String DATABASE_NAME = "AV_FISICA_CORRIDA_BD";    // Database Name
    private static final String TABLE_NAME = "tb_corrida";   // Table Name
    private static final int DATABASE_Version = 4;    // Database Version
    private static final String ID_CORRIDA="id_corrida"; // Column I (Primary Key)
    private static final String DATA = "data";
    private static final String DISTANCIA= "distancia";
    private static final String TEMPO= "tempo";
    private static final String PACE= "pace";
    private static final String VO2= "vo2";
    private static final String NIVEL= "nivel";
    private static final String ID_LOGIN = "id_login";
    private static final String STATUS = "status";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            " ("+ID_CORRIDA+" INTEGER, "
            +DATA+" VARCHAR(255) ,"
            + DISTANCIA+" VARCHAR(225),"
            +TEMPO+" VARCHAR(225),"
            +PACE+" VARCHAR(225),"
            +VO2+" VARCHAR(225),"
            +NIVEL+" VARCHAR(225),"
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
