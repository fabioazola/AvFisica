package com.example.avfisica.resources.treino;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.treino.Exercicio;
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

public class ExercicioResource {
    myDbHelper myhelper;

    //Construtor
    public ExercicioResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public Exercicio insertDataPost(Exercicio exercicio)
    {
        Exercicio exercicioReturn = new Exercicio();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_exercicio", exercicio.getId_excercicio());
            cred.put("nome", exercicio.getId_login());
            cred.put("id_login", exercicio.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/exercicio";
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
                exercicioReturn = (Exercicio) util.convertJsonToObject(jsonDeResposta, exercicio, "tb_exercicio");
            }catch (Exception e){
                exercicioReturn.setId_excercicio(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            exercicioReturn.setId_excercicio(0); //erro
        }
        return exercicioReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Exercicio exercicio)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_EXERCICIO, exercicio.getId_excercicio());
        contentValues.put(myDbHelper.NOME, exercicio.getNome());
        contentValues.put(myDbHelper.TIPO, exercicio.getTipo());
        contentValues.put(myDbHelper.PATH_GIF, exercicio.getPath_gif());
        contentValues.put(myDbHelper.DRAWABLE, exercicio.getDrawable());
        contentValues.put(myDbHelper.ID_LOGIN, exercicio.getId_login());
        contentValues.put(myDbHelper.STATUS, exercicio.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public Exercicio getExercicioNome(String input_nome)
    {
        Exercicio exercicio =  new Exercicio();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_exercicio",
                    "nome",
                    "tipo",
                    "path_gif",
                    "drawable",
                    "id_login",
                    "status"}, "nome" + "= ? ", new String[]{String.valueOf(input_nome)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String path_gif =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH_GIF));
                String drawable =cursor.getString(cursor.getColumnIndex(myDbHelper.DRAWABLE));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                exercicio.setId_excercicio(Long.parseLong(id_exercicio));
                exercicio.setNome(nome);
                exercicio.setTipo(tipo);
                exercicio.setPath_gif(path_gif);
                exercicio.setDrawable(drawable);
                exercicio.setId_login(Long.parseLong(id_login));
                exercicio.setStatus(status);
            }
        }catch (Exception e){

        }
        return exercicio;
    }


    public Exercicio getExercicioID(long idExercicio)
    {
        Exercicio exercicio = new Exercicio ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_EXERCICIO,
                myDbHelper.NOME,
                myDbHelper.TIPO,
                myDbHelper.PATH_GIF,
                myDbHelper.DRAWABLE,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[0] + "="+ Long.toString(idExercicio),null, null, null, myDbHelper.ID_EXERCICIO +" ASC", null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String path_gif =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH_GIF));
                String drawable =cursor.getString(cursor.getColumnIndex(myDbHelper.DRAWABLE));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                exercicio.setId_excercicio(Long.parseLong(id_exercicio));
                exercicio.setNome(nome);
                exercicio.setTipo(tipo);
                exercicio.setPath_gif(path_gif);
                exercicio.setDrawable(drawable);
                exercicio.setId_login(Long.parseLong(id_login));
                exercicio.setStatus(status);

            }
        }catch (Exception e){

        }
        return exercicio;
    }

    public List<Exercicio> getData(long id_login_input)
    {
        List<Exercicio> lexercicio =  new ArrayList<Exercicio>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_EXERCICIO,
                myDbHelper.NOME,
                myDbHelper.TIPO,
                myDbHelper.PATH_GIF,
                myDbHelper.DRAWABLE,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[5] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.NOME +" ASC", null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Exercicio exercicio = new Exercicio ();

                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String path_gif =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH_GIF));
                String drawable =cursor.getString(cursor.getColumnIndex(myDbHelper.DRAWABLE));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                exercicio.setId_excercicio(Long.parseLong(id_exercicio));
                exercicio.setNome(nome);
                exercicio.setTipo(tipo);
                exercicio.setPath_gif(path_gif);
                exercicio.setDrawable(drawable);
                exercicio.setId_login(Long.parseLong(id_login));
                exercicio.setStatus(status);
                lexercicio.add(exercicio);

            }
        }catch (Exception e){

        }
        return lexercicio;
    }

    public List<Exercicio> getDataMembro(long id_login_input, String membro)
    {
        List<Exercicio> lexercicio =  new ArrayList<Exercicio>();
        String countQuery = "SELECT *  FROM " + myDbHelper.TABLE_NAME + " WHERE  tipo='"+membro+"' AND id_login='"+Long.toString(id_login_input)+"'";
        SQLiteDatabase db = myhelper.getReadableDatabase();
        try{
            Cursor cursor = db.rawQuery(countQuery, null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Exercicio exercicio = new Exercicio ();

                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String path_gif =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH_GIF));
                String drawable =cursor.getString(cursor.getColumnIndex(myDbHelper.DRAWABLE));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                exercicio.setId_excercicio(Long.parseLong(id_exercicio));
                exercicio.setNome(nome);
                exercicio.setTipo(tipo);
                exercicio.setPath_gif(path_gif);
                exercicio.setDrawable(drawable);
                exercicio.setId_login(Long.parseLong(id_login));
                exercicio.setStatus(status);
                lexercicio.add(exercicio);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lexercicio;
    }


    public List<Exercicio> getDataStatus(String input_status)
    {
        List<Exercicio> lexercicio =  new ArrayList<Exercicio>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_exercicio",
                    "nome",
                    "tipo",
                    "path_gif",
                    "drawable",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                Exercicio exercicio = new Exercicio ();

                String id_exercicio =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));
                String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String path_gif =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH_GIF));
                String drawable =cursor.getString(cursor.getColumnIndex(myDbHelper.DRAWABLE));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                exercicio.setId_excercicio(Long.parseLong(id_exercicio));
                exercicio.setNome(nome);
                exercicio.setTipo(tipo);
                exercicio.setPath_gif(path_gif);
                exercicio.setDrawable(drawable);
                exercicio.setId_login(Long.parseLong(id_login));
                exercicio.setStatus(status);
                lexercicio.add(exercicio);
            }
        }catch (Exception e){

        }
        return lexercicio;
    }

    public int getNextId()
    {
        int count= 0;

        String countQuery = "SELECT  * FROM " + myDbHelper.TABLE_NAME;
        SQLiteDatabase db = myhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            String id_exercicio = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_EXERCICIO));

            count = Integer.parseInt(id_exercicio);
        }

        cursor.close();

        if (count == 0)
            count = 1;
        else
            count ++;

        return count;
    }

    public  int delete(long id_treino)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_treino)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_EXERCICIO+" = ?",whereArgs);
        return  count;
    }

    public int updateName(Exercicio exercicio)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_EXERCICIO,exercicio.getId_excercicio());
        contentValues.put(myDbHelper.NOME,exercicio.getNome());
        contentValues.put(myDbHelper.TIPO,exercicio.getTipo());
        contentValues.put(myDbHelper.PATH_GIF,exercicio.getPath_gif());
        contentValues.put(myDbHelper.DRAWABLE,exercicio.getDrawable());
        contentValues.put(myDbHelper.STATUS,exercicio.getStatus());
        String[] whereArgs= {Long.toString(exercicio.getId_excercicio())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_EXERCICIO+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_EXERCICIO_BD";    // Database Name
        private static final String TABLE_NAME = "tb_exercicio";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_EXERCICIO="id_exercicio";     // (Primary Key)
        private static final String NOME = "nome";
        private static final String TIPO = "tipo";
        private static final String PATH_GIF = "path_gif";
        private static final String DRAWABLE = "drawable";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_EXERCICIO+" INTEGER PRIMARY KEY, "
                    + NOME+" VARCHAR(255), "
                    + TIPO+" VARCHAR(255), "
                    + PATH_GIF+" VARCHAR(255), "
                    + DRAWABLE+" VARCHAR(255), "
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
