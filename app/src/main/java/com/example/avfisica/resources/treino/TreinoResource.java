package com.example.avfisica.resources.treino;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.treino.TreinoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TreinoResource {
    myDbHelper myhelper;

    //Construtor
    public TreinoResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public TreinoModel insertDataPost(TreinoModel treino)
    {
        TreinoModel treinoReturn = new TreinoModel();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_treino", treino.getId_treino());
            cred.put("id_ficha", treino.getId_login());
            cred.put("data", treino.getData());
            cred.put("id_login", treino.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/treino";
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
                treinoReturn = (TreinoModel) util.convertJsonToObject(jsonDeResposta, treino, "tb_treino");
            }catch (Exception e){
                treinoReturn.setId_treino(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            treinoReturn.setId_treino(0); //erro
        }
        return treinoReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(TreinoModel treino)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_TREINO, getNextId());
        contentValues.put(myDbHelper.ID_FICHA, treino.getId_ficha());
        contentValues.put(myDbHelper.DATA, treino.getData());
        contentValues.put(myDbHelper.ID_LOGIN, treino.getId_login());
        contentValues.put(myDbHelper.STATUS, treino.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<TreinoModel> getData(long id_login_input)
    {
        List<TreinoModel> ltreino =  new ArrayList<TreinoModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_TREINO,
                myDbHelper.ID_FICHA,
                myDbHelper.DATA,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

       try{
        Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[3] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.ID_TREINO +" ASC", null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            TreinoModel treino = new TreinoModel();

            String id_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));
            String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
            String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
            String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

            treino.setId_treino(Long.parseLong(id_treino));
            treino.setId_ficha(Long.parseLong(id_ficha));
            treino.setData(data);
            treino.setId_login(Long.parseLong(id_login));
            treino.setStatus(status);
            ltreino.add(treino);

        }
       }catch (Exception e){

       }
        return ltreino;
    }

    public List<TreinoModel> getDataStatus(String input_status)
    {
        List<TreinoModel> ltreino =  new ArrayList<TreinoModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_treino",
                    "id_ficha",
                    "data",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                TreinoModel treino = new TreinoModel();

                String id_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                treino.setId_treino(Long.parseLong(id_treino));
                treino.setId_ficha(Long.parseLong(id_treino));
                treino.setData(data);
                treino.setId_login(Long.parseLong(id_login));
                treino.setStatus(status);
                ltreino.add(treino);
            }
        }catch (Exception e){

        }
        return ltreino;
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

    public long getLastTreino(String input_ficha)
    {
        int count= 0;
        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                "id_treino",
                "id_ficha",
                "data",
                "id_login",
                "status"}, "id_ficha" + "= ? ", new String[]{String.valueOf(input_ficha)}, null, null, null, null);

        if (cursor.getCount()>1) {
            cursor.moveToLast(); //atual
            cursor.moveToPrevious(); //anterior
            String id_treino = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TREINO));

            count = Integer.parseInt(id_treino);
        }
        cursor.close();

        return count;
    }

    public long getData_(String minData, String maxData)
    {
        int count= 0;
        try {
            String countQuery = "SELECT *  FROM " + myDbHelper.TABLE_NAME + " WHERE  data>='"+minData+"' AND data<='"+maxData+"'";
            SQLiteDatabase db = myhelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
            cursor.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return count;
    }

    /* SE FOR UTILIZAR DEVERA REFATORAR OU SEJA DEVERA DELTAR O ITEM DA TABELA TREINO E CARGA_PESO
    public  int delete(long id_treino)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_treino)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_TREINO+" = ?",whereArgs);
        return  count;
    }*/

    public int updateName(TreinoModel treino)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_TREINO,treino.getId_treino());
        contentValues.put(myDbHelper.ID_FICHA,treino.getId_ficha());
        contentValues.put(myDbHelper.DATA,treino.getData());
        contentValues.put(myDbHelper.STATUS,treino.getStatus());
        String[] whereArgs= {Long.toString(treino.getId_treino())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_TREINO+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_TREINO_BD";    // Database Name
        private static final String TABLE_NAME = "tb_treino";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_TREINO="id_treino";     // (Primary Key)
        private static final String ID_FICHA="id_ficha";     // (Foring Key)
        private static final String DATA = "data";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_TREINO+" INTEGER PRIMARY KEY, "
                    + ID_FICHA+" VARCHAR(225),"
                    + DATA+" INTEGER, "
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
