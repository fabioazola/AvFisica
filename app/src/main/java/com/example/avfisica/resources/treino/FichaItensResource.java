package com.example.avfisica.resources.treino;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.treino.Ficha;
import com.example.avfisica.models.treino.FichaItens;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FichaItensResource {
    myDbHelper myhelper;

    //Construtor
    public FichaItensResource(Context context)
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
    public long insertData(FichaItens fichaItens)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_FICHA_ITENS, getNextId());
        contentValues.put(myDbHelper.ID_FICHA, fichaItens.getId_ficha());
        contentValues.put(myDbHelper.ID_EXERCICIO, fichaItens.getId_exercicio());
        contentValues.put(myDbHelper.QUANTIDADE, fichaItens.getQuantidade());
        contentValues.put(myDbHelper.REPETICAO, fichaItens.getRepeticao());
        contentValues.put(myDbHelper.OBS, fichaItens.getObs());
        contentValues.put(myDbHelper.ID_LOGIN, fichaItens.getId_login());
        contentValues.put(myDbHelper.STATUS, fichaItens.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        dbb.close();

        return ret;
    }

    public List<FichaItens> getFichaItens(String input_ficha)
    {
        List<FichaItens> lfichaItens =  new ArrayList<FichaItens>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha_itens",
                    "id_ficha",
                    "id_exercicio",
                    "quantidade",
                    "repeticao",
                    "obs",
                    "id_login",
                    "status"}, "id_ficha" + "= ? ", new String[]{String.valueOf(input_ficha)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                FichaItens fichaItens = new FichaItens ();

                String id_ficha_itens =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA_ITENS));
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String quantidade =cursor.getString(cursor.getColumnIndex(myDbHelper.QUANTIDADE));
                String repeticao =cursor.getString(cursor.getColumnIndex(myDbHelper.REPETICAO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                fichaItens.setId_ficha_itens(Long.parseLong(id_ficha_itens));
                fichaItens.setId_ficha(Long.parseLong(id_ficha));
                fichaItens.setId_exercicio(Long.parseLong(id_exercicio));
                fichaItens.setQuantidade(Long.parseLong(quantidade));
                fichaItens.setRepeticao(Long.parseLong(repeticao));
                fichaItens.setObs(obs);
                fichaItens.setId_login(Long.parseLong(id_login));
                fichaItens.setStatus(status);
                lfichaItens.add(fichaItens);
            }
        }catch (Exception e){

        }
        return lfichaItens;
    }

    public List<FichaItens> getDataStatus(String input_status)
    {
        List<FichaItens> lfichaItens =  new ArrayList<FichaItens>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_ficha_itens",
                    "id_ficha",
                    "id_exercicio",
                    "quantidade",
                    "repeticao",
                    "obs",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                FichaItens fichaItens = new FichaItens ();

                String id_ficha_itens =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA_ITENS));
                String id_ficha =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA));
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String quantidade =cursor.getString(cursor.getColumnIndex(myDbHelper.QUANTIDADE));
                String repeticao =cursor.getString(cursor.getColumnIndex(myDbHelper.REPETICAO));
                String obs =cursor.getString(cursor.getColumnIndex(myDbHelper.OBS));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                fichaItens.setId_ficha(Long.parseLong(id_ficha_itens));
                fichaItens.setId_ficha(Long.parseLong(id_ficha));
                fichaItens.setId_ficha(Long.parseLong(id_exercicio));
                fichaItens.setQuantidade(Long.parseLong(quantidade));
                fichaItens.setRepeticao(Long.parseLong(repeticao));
                fichaItens.setObs(obs);
                fichaItens.setId_login(Long.parseLong(id_login));
                fichaItens.setStatus(status);
                lfichaItens.add(fichaItens);
            }
        }catch (Exception e){

        }
        return lfichaItens;
    }

    public int getNextId()
    {
        int count= 0;

        String countQuery = "SELECT  * FROM " + myDbHelper.TABLE_NAME;
        SQLiteDatabase db = myhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            String id_ficha_itens = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_FICHA_ITENS));

            count = Integer.parseInt(id_ficha_itens);
        }

        cursor.close();

        if (count == 0)
            count = 1;
        else
            count ++;

        return count;
    }

    public  int delete(long id_fichaItens)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_fichaItens)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_FICHA_ITENS+" = ?",whereArgs);

        db.close();
        return  count;
    }

    public int updateName(FichaItens fichaItens)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_FICHA_ITENS,fichaItens.getId_ficha_itens());
        contentValues.put(myDbHelper.ID_FICHA,fichaItens.getId_ficha());
        contentValues.put(myDbHelper.ID_EXERCICIO,fichaItens.getId_exercicio());
        contentValues.put(myDbHelper.QUANTIDADE,fichaItens.getQuantidade());
        contentValues.put(myDbHelper.REPETICAO, fichaItens.getRepeticao());
        contentValues.put(myDbHelper.OBS, fichaItens.getObs());
        contentValues.put(myDbHelper.STATUS,fichaItens.getStatus());
        String[] whereArgs= {Long.toString(fichaItens.getId_ficha_itens())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_FICHA_ITENS+" = ?",whereArgs );
        db.close();
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_FICHA_ITENS_BD";    // Database Name
        private static final String TABLE_NAME = "tb_ficha_itens";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_FICHA_ITENS="id_ficha_itens";    // (Primary Key)
        private static final String ID_FICHA="id_ficha";                // (foring Key)
        private static final String ID_EXERCICIO="id_exercicio";        // (foring Key)
        private static final String QUANTIDADE="quantidade";
        private static final String REPETICAO="repeticao";
        private static final String OBS="obs";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_FICHA_ITENS+" INTEGER PRIMARY KEY, "
                    + ID_FICHA + " INTEGER,"
                    + ID_EXERCICIO + " INTEGER,"
                    + QUANTIDADE+" INTEGER, "
                    + REPETICAO+" INTEGER, "
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
