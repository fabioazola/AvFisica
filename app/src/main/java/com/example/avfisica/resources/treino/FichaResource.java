package com.example.avfisica.resources.treino;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.treino.Ficha;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FichaResource {
    myDbHelper myhelper;

    //Construtor
    public FichaResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public Ficha insertDataPost(Ficha ficha)
    {
        Ficha fichaReturn = new Ficha();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_ficha", ficha.getId_ficha());
            cred.put("nome", ficha.getId_login());
            cred.put("id_login", ficha.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/ficha";
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
                fichaReturn = (Ficha) util.convertJsonToObject(jsonDeResposta, ficha, "tb_ficha");
            }catch (Exception e){
                fichaReturn.setId_ficha(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            fichaReturn.setId_ficha(0); //erro
        }
        return fichaReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Ficha ficha)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_FICHA, getNextId());
        contentValues.put(myDbHelper.NOME, ficha.getNome());
        contentValues.put(myDbHelper.DATA, ficha.getData());
        contentValues.put(myDbHelper.FLAG_ATIVO, ficha.getFlagAtivo());
        contentValues.put(myDbHelper.OBS, ficha.getObs());
        contentValues.put(myDbHelper.ID_LOGIN, ficha.getId_login());
        contentValues.put(myDbHelper.STATUS, ficha.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<Ficha> getData(long id_login_input)
    {
        List<Ficha> lficha =  new ArrayList<Ficha>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_FICHA,
                myDbHelper.NOME,
                myDbHelper.DATA,
                myDbHelper.FLAG_ATIVO,
                myDbHelper.OBS,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[5] + "="+ Long.toString(id_login_input),null, null, null, null, null); //myDbHelper.NOME +" ASC"
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Ficha ficha = new Ficha ();

                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setData(data);
                ficha.setNome(nome);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
                lficha.add(ficha);

            }
        }catch (Exception e){

        }
        return lficha;
    }

    public List<Ficha> getFichaAtivo(long flagAtivo)
    {
        List<Ficha> lficha =  new ArrayList<Ficha>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_FICHA,
                myDbHelper.NOME,
                myDbHelper.DATA,
                myDbHelper.FLAG_ATIVO,
                myDbHelper.OBS,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[3] + "="+ Long.toString(flagAtivo),null, null, null, null, null); //myDbHelper.NOME +" ASC"
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Ficha ficha = new Ficha ();

                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setData(data);
                ficha.setNome(nome);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
                lficha.add(ficha);

            }
        }catch (Exception e){

        }
        return lficha;
    }


    public Ficha getFicha(String input_nome)
    {
        Ficha ficha =  new Ficha();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha",
                    "nome",
                    "data",
                    "flagAtivo",
                    "obs",
                    "id_login",
                    "status"}, "nome" + "= ? ", new String[]{String.valueOf(input_nome)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setNome(nome);
                ficha.setData(data);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
            }
        }catch (Exception e){

        }
        return ficha;
    }

    public Ficha getFichaByIdFicha(String input_id_ficha)
    {
        Ficha ficha =  new Ficha();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha",
                    "nome",
                    "data",
                    "flagAtivo",
                    "obs",
                    "id_login",
                    "status"}, "id_ficha" + "= ? ", new String[]{String.valueOf(input_id_ficha)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setNome(nome);
                ficha.setData(data);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
            }
        }catch (Exception e){

        }
        return ficha;
    }

    public List<Ficha> getDataStatus(String input_status)
    {
        List<Ficha> lficha =  new ArrayList<Ficha>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha",
                    "nome",
                    "data",
                    "flagAtivo",
                    "obs",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Ficha ficha = new Ficha ();

                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setNome(nome);
                ficha.setData(data);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
                lficha.add(ficha);
            }
        }catch (Exception e){

        }
        return lficha;
    }

    public Ficha getData(String minData, String maxData)
    {
        Ficha ficha =  new Ficha();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha",
                    "nome",
                    "data",
                    "flagAtivo",
                    "obs",
                    "id_login",
                    "status"}, "data" + " BETWEEN ? AND ?", new String[] {
                    minData, maxData}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String flag_ativo =cursor.getString(cursor.getColumnIndex(myDbHelper.FLAG_ATIVO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                ficha.setId_ficha(Long.parseLong(id_ficha));
                ficha.setNome(nome);
                ficha.setData(data);
                ficha.setFlagAtivo(Integer.parseInt(flag_ativo));
                ficha.setObs(obs);
                ficha.setId_login(Long.parseLong(id_login));
                ficha.setStatus(status);
            }
        }catch (Exception e){

        }
        return ficha;
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

    /* SE FOR UTILIZAR DEVERA REFATORAR OU SEJA DEVERA DELTAR O ITEM DA TABELA FICHA E FICHA_ITENS
    public  int delete(long id_treino)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_treino)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_TREINO+" = ?",whereArgs);
        return  count;
    }*/

    public int updateName(Ficha ficha)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_FICHA,ficha.getId_ficha());
        contentValues.put(myDbHelper.NOME,ficha.getNome());
        contentValues.put(myDbHelper.DATA,ficha.getData());
        contentValues.put(myDbHelper.FLAG_ATIVO,ficha.getFlagAtivo());
        contentValues.put(myDbHelper.OBS,ficha.getObs());
        contentValues.put(myDbHelper.STATUS,ficha.getStatus());
        String[] whereArgs= {Long.toString(ficha.getId_ficha())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_FICHA+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_FICHA_BD";    // Database Name
        private static final String TABLE_NAME = "tb_ficha";   // Table Name
        private static final int DATABASE_Version = 5;    // Database Version
        private static final String ID_FICHA="id_ficha";     // (Primary Key)
        private static final String NOME = "nome";
        private static final String DATA = "data";
        private static final String FLAG_ATIVO = "flagAtivo";
        private static final String OBS = "obs";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_FICHA+" INTEGER PRIMARY KEY, "
                + NOME+" VARCHAR(255), "
                + DATA+" VARCHAR(255), "
                + FLAG_ATIVO+" VARCHAR(255), "
                + OBS+" VARCHAR(255), "
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
