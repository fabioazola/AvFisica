package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.Vo2;
import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.Vo2Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Vo2Resource {
    myDbHelper myhelper;

    //Construtor
    public Vo2Resource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public Vo2Model insertDataPost(Vo2Model vo2)
    {
        Vo2Model vo2Return = new Vo2Model();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_vo2", vo2.getId_vo2());
            cred.put("data", vo2.getData());
            cred.put("vo2", vo2.getVo2());
            cred.put("distancia", vo2.getDistancia());
            cred.put("nivel", vo2.getObjetivo()); //objetivo
            cred.put("id_login", vo2.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/vo2";
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
                vo2Return = (Vo2Model) util.convertJsonToObject(jsonDeResposta, vo2, "tb_vo2");
            }catch (Exception e){
                vo2Return.setId_vo2(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            vo2Return.setId_vo2(0); //erro
        }
        return vo2Return;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Vo2Model vo2)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_VO2, getNextId());
        contentValues.put(myDbHelper.DATA, vo2.getData());
        contentValues.put(myDbHelper.VO2, vo2.getVo2());
        contentValues.put(myDbHelper.DISTANCIA, vo2.getDistancia());
        contentValues.put(myDbHelper.OBJETIVO, vo2.getObjetivo());
        contentValues.put(myDbHelper.ID_LOGIN, vo2.getId_login());
        contentValues.put(myDbHelper.STATUS, vo2.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<Vo2Model> getData(long id_login_input)
    {
        List<Vo2Model> lvo2 =  new ArrayList<Vo2Model>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_VO2,
                myDbHelper.DATA,
                myDbHelper.VO2,
                myDbHelper.DISTANCIA,
                myDbHelper.OBJETIVO,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[5] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.ID_VO2 +" ASC", null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Vo2Model _vo2 = new Vo2Model ();

                String id_vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_VO2));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.VO2));
                String distancia =cursor.getString(cursor.getColumnIndex(myDbHelper.DISTANCIA));
                String objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                _vo2.setId_vo2(Long.parseLong(id_vo2));
                _vo2.setData(data);
                _vo2.setVo2(Float.parseFloat(vo2));
                _vo2.setDistancia(Float.parseFloat(distancia));
                _vo2.setObjetivo(Float.parseFloat(objetivo));
                _vo2.setId_login(Long.parseLong(id_login));
                _vo2.setStatus(status);
                lvo2.add(_vo2);

            }
        }catch (Exception e){

        }
        return lvo2;
    }

    public List<Vo2Model> getDataStatus(String input_status)
    {
        List<Vo2Model> lvo2 =  new ArrayList<Vo2Model>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_vo2",
                    "data",
                    "vo2",
                    "distancia",
                    "objetivo",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Vo2Model _vo2 = new Vo2Model ();

                String id_vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_VO2));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.VO2));
                String distancia =cursor.getString(cursor.getColumnIndex(myDbHelper.DISTANCIA));
                String objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                _vo2.setId_vo2(Long.parseLong(id_vo2));
                _vo2.setData(data);
                _vo2.setVo2(Float.parseFloat(vo2));
                _vo2.setDistancia(Float.parseFloat(distancia));
                _vo2.setObjetivo(Float.parseFloat(objetivo));
                _vo2.setId_login(Long.parseLong(id_login));
                _vo2.setStatus(status);
                lvo2.add(_vo2);
            }
        }catch (Exception e){

        }
        return lvo2;
    }

    public Vo2Model getData_all()
    {
        Vo2Model _vo2 = new Vo2Model ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_VO2,
                myDbHelper.DATA,
                myDbHelper.VO2,
                myDbHelper.DISTANCIA,
                myDbHelper.OBJETIVO,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String id_vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_VO2));
            String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
            String  vo2 =cursor.getString(cursor.getColumnIndex(myDbHelper.VO2));
            String  distancia =cursor.getString(cursor.getColumnIndex(myDbHelper.DISTANCIA));
            String  objetivo =cursor.getString(cursor.getColumnIndex(myDbHelper.OBJETIVO));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
            String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

            _vo2.setId_vo2(Long.parseLong(id_vo2));
            _vo2.setData(data);
            _vo2.setVo2(Float.parseFloat(vo2));
            _vo2.setDistancia(Float.parseFloat(distancia));
            _vo2.setObjetivo(Float.parseFloat(objetivo));
            _vo2.setId_login(Long.parseLong(id_login));
            _vo2.setStatus(status);
        }

        if(_vo2==null){
            _vo2.setId_login(0);
        }

        return _vo2;
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

    public  int delete(long id_vo2)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_vo2)};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.ID_VO2+" = ?",whereArgs);
        return  count;
    }

    public int updateName(Vo2Model vo2)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_VO2,vo2.getId_vo2());
        contentValues.put(myDbHelper.DATA,vo2.getData());
        contentValues.put(myDbHelper.VO2,vo2.getVo2());
        contentValues.put(myDbHelper.DISTANCIA,vo2.getDistancia());
        contentValues.put(myDbHelper.OBJETIVO,vo2.getObjetivo());
        contentValues.put(myDbHelper.STATUS,vo2.getStatus());
        String[] whereArgs= {Long.toString(vo2.getId_vo2())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_VO2+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_VO2_BD";    // Database Name
        private static final String TABLE_NAME = "tb_vo2";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_VO2="id_vo2";     // Column I (Primary Key)
        private static final String DATA = "data";    //Column II
        private static final String VO2= "vo2";    // Column III
        private static final String DISTANCIA= "distancia";    // Column III
        private static final String OBJETIVO= "objetivo";    // Column IV
        private static final String ID_LOGIN = "id_login";    // Column IV
        private static final String STATUS = "status";    // Column X
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_VO2+" INTEGER PRIMARY KEY, "
                +DATA+" VARCHAR(255), "
                + VO2+" VARCHAR(225),"
                + DISTANCIA+" VARCHAR(225),"
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
