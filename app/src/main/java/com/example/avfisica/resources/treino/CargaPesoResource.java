package com.example.avfisica.resources.treino;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.treino.CargaPeso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CargaPesoResource {
    myDbHelper myhelper;

    //Construtor
    public CargaPesoResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public CargaPeso insertDataPost(CargaPeso cargaPeso)
    {
        CargaPeso cargaPesoReturn = new CargaPeso();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_carga_peso", cargaPeso.getId_carga_peso());
            cred.put("id_exercicio", cargaPeso.getId_exercicio());
            cred.put("id_treino", cargaPeso.getId_treino());
            cred.put("peso", cargaPeso.getPeso());
            cred.put("repeticao", cargaPeso.getRepeticao());
            cred.put("flagExecutado", cargaPeso.getFlagExecutado());
            cred.put("id_login", cargaPeso.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/cargaPeso";
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
                cargaPesoReturn = (CargaPeso) util.convertJsonToObject(jsonDeResposta, cargaPeso, "tb_cargaPeso");
            }catch (Exception e){
                cargaPesoReturn.setId_carga_peso(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            cargaPesoReturn.setId_carga_peso(0); //erro
        }
        return cargaPesoReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(CargaPeso cargaPeso)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_CARGA_PESO, getNextId());
        contentValues.put(myDbHelper.ID_EXERCICIO, cargaPeso.getId_exercicio());
        contentValues.put(myDbHelper.ID_TREINO, cargaPeso.getId_treino());
        contentValues.put(myDbHelper.PESO, cargaPeso.getPeso());
        contentValues.put(myDbHelper.REPETICAO, cargaPeso.getRepeticao());
        contentValues.put(myDbHelper.FLAGEXECUTADO, cargaPeso.getFlagExecutado());
        contentValues.put(myDbHelper.OBS, cargaPeso.getObs());
        contentValues.put(myDbHelper.ID_LOGIN, cargaPeso.getId_login());
        contentValues.put(myDbHelper.STATUS, cargaPeso.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        dbb.close();

        return ret;
    }

    public List<CargaPeso> getCargaPeso(String input_treino, String input_exercicio)
    {
        List<CargaPeso> lcargaPeso =  new ArrayList<CargaPeso>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{

            String countQuery = "SELECT  * FROM " + myDbHelper.TABLE_NAME +" WHERE id_treino="+input_treino+ " and id_exercicio="+input_exercicio;
            Cursor cursor = db.rawQuery(countQuery, null);
            while (cursor.moveToNext())
            {
                CargaPeso cargaPeso = new CargaPeso ();

                String id_carga_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CARGA_PESO));
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String id_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));
                String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  repeticao =cursor.getString(cursor.getColumnIndex(myDbHelper.REPETICAO));
                String  flagExecutado =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAGEXECUTADO));
                String  obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                cargaPeso.setId_carga_peso(Long.parseLong(id_carga_peso));
                cargaPeso.setId_exercicio(Long.parseLong(id_exercicio));
                cargaPeso.setId_treino(Long.parseLong(id_treino));
                cargaPeso.setPeso(Float.parseFloat(peso));
                cargaPeso.setRepeticao(Long.parseLong(repeticao));
                cargaPeso.setFlagExecutado(Long.parseLong(flagExecutado));
                cargaPeso.setObs(obs);
                cargaPeso.setId_login(Long.parseLong(id_login));
                cargaPeso.setStatus(status);

                lcargaPeso.add(cargaPeso);
            }
        }catch (Exception e){

        }
        return lcargaPeso;
    }

    public int getCargaPesoExecutado(String input_treino, String input_exercicio)
    {
        int count = 0;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{

            String countQuery = "SELECT * FROM " + myDbHelper.TABLE_NAME +" WHERE id_treino="+input_treino+ " and id_exercicio="+input_exercicio+" and flagExecutado=1" ;
            Cursor cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
        }catch (Exception e){

        }
        return count;
    }

    public List<CargaPeso> getCargaPesoBytreino(String input_treino)
    {
        List<CargaPeso> lcargaPeso =  new ArrayList<CargaPeso>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_carga_peso",
                    "id_exercicio",
                    "id_treino",
                    "peso",
                    "repeticao",
                    "flagExecutado",
                    "obs",
                    "id_login",
                    "status"}, "id_treino" + "= ? ", new String[]{String.valueOf(input_treino)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                CargaPeso cargaPeso = new CargaPeso ();

                String id_carga_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CARGA_PESO));
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String id_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));
                String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String  repeticao =cursor.getString(cursor.getColumnIndex(myDbHelper.REPETICAO));
                String  flagExecutado =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAGEXECUTADO));
                String  obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                cargaPeso.setId_carga_peso(Long.parseLong(id_carga_peso));
                cargaPeso.setId_exercicio(Long.parseLong(id_exercicio));
                cargaPeso.setId_treino(Long.parseLong(id_treino));
                cargaPeso.setPeso(Float.parseFloat(peso));
                cargaPeso.setRepeticao(Long.parseLong(repeticao));
                cargaPeso.setFlagExecutado(Long.parseLong(flagExecutado));
                cargaPeso.setObs(obs);
                cargaPeso.setId_login(Long.parseLong(id_login));
                cargaPeso.setStatus(status);
                lcargaPeso.add(cargaPeso);
            }
        }catch (Exception e){

        }
        return lcargaPeso;
    }

    public List<CargaPeso> getDataStatus(String input_status)
    {
        List<CargaPeso> lcargaPeso =  new ArrayList<CargaPeso>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_carga_peso",
                    "id_exercicio",
                    "id_treino",
                    "peso",
                    "repeticao",
                    "flagExecutado",
                    "obs",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                CargaPeso cargaPeso = new CargaPeso ();

                String id_carga_peso =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CARGA_PESO));
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String id_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));
                String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String  repeticao =cursor.getString(cursor.getColumnIndex(myDbHelper.REPETICAO));
                String  flagExecutado =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAGEXECUTADO));
                String  obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                cargaPeso.setId_carga_peso(Long.parseLong(id_carga_peso));
                cargaPeso.setId_exercicio(Long.parseLong(id_exercicio));
                cargaPeso.setId_treino(Long.parseLong(id_treino));
                cargaPeso.setPeso(Float.parseFloat(peso));
                cargaPeso.setRepeticao(Long.parseLong(repeticao));
                cargaPeso.setFlagExecutado(Long.parseLong(flagExecutado));
                cargaPeso.setObs(obs);
                cargaPeso.setId_login(Long.parseLong(id_login));
                cargaPeso.setStatus(status);
                lcargaPeso.add(cargaPeso);
            }
        }catch (Exception e){

        }
        return lcargaPeso;
    }

    public int getNextId()
    {
        int count= 0;

        String countQuery = "SELECT  * FROM " + myDbHelper.TABLE_NAME;
        SQLiteDatabase db = myhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            String id_carga_peso = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CARGA_PESO));

            count = Integer.parseInt(id_carga_peso);
        }

        cursor.close();

        if (count == 0)
            count = 1;
        else
            count ++;

        return count;
    }

    public  int delete(long id_carga_peso)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_carga_peso)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_CARGA_PESO+" = ?",whereArgs);

        db.close();
        return  count;
    }

    public int updateName(CargaPeso cargaPeso)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_CARGA_PESO,cargaPeso.getId_carga_peso());
        contentValues.put(myDbHelper.ID_EXERCICIO,cargaPeso.getId_exercicio());
        contentValues.put(myDbHelper.ID_TREINO,cargaPeso.getId_treino());
        contentValues.put(myDbHelper.PESO,cargaPeso.getPeso());
        contentValues.put(myDbHelper.REPETICAO,cargaPeso.getRepeticao());
        contentValues.put(myDbHelper.FLAGEXECUTADO,cargaPeso.getFlagExecutado());
        contentValues.put(myDbHelper.OBS,cargaPeso.getObs());
        contentValues.put(myDbHelper.STATUS,cargaPeso.getStatus());
        String[] whereArgs= {Long.toString(cargaPeso.getId_carga_peso())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_CARGA_PESO+" = ?",whereArgs );
        db.close();
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_carga_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_CARGA_PESO_BD";    // Database Name
        private static final String TABLE_NAME = "tb_carga_peso";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_CARGA_PESO="id_carga_peso";    // (Primary Key)
        private static final String ID_EXERCICIO="id_exercicio";     // (foring Key)
        private static final String ID_TREINO="id_treino";           // (foring Key)
        private static final String PESO="peso";
        private static final String REPETICAO="repeticao";
        private static final String FLAGEXECUTADO="flagExecutado";
        private static final String OBS="obs";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_CARGA_PESO+" INTEGER PRIMARY KEY, "
                    + ID_EXERCICIO + " INTEGER,"
                    + ID_TREINO + " INTEGER,"
                    + PESO+" VARCHAR(255), "
                    + REPETICAO + " INTEGER,"
                    + FLAGEXECUTADO + " INTEGER,"
                    + OBS + " INTEGER,"
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
