package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.avfisica.Util;
import com.example.avfisica.models.Aluno;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//controler
public class AlunoResource {
    myDbHelper myhelper;
    public static String teste;
    //Construtor
    public AlunoResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }


    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public Aluno insertDataPost(Aluno aluno, String login, String password)
    {
        Aluno alunoReturn = new Aluno();
        Util util = new Util();
        try{
                JSONObject cred = new JSONObject();
                cred.put("matricula", aluno.getMatricula());
                cred.put("nome", aluno.getNome());
                cred.put("altura", aluno.getAltura());
                cred.put("peso", aluno.getPeso());
                cred.put("sexo", aluno.getSexo());
                cred.put("cidade", "-");
                cred.put("endereco", "-");
                cred.put("telefone", "-");
                cred.put("idade", aluno.getIdade());
                cred.put("email", aluno.getEmail());
                cred.put("cintaMac", aluno.getCintaMac());
                cred.put("id_login", aluno.getId_login());

                //String url="http://192.168.0.102:8080/api/fluxoAula";
                String url="http://apirest-wifit.herokuapp.com/api/aluno/"+login+"/"+password;
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
                    alunoReturn = (Aluno) util.convertJsonToObject(jsonDeResposta, aluno, "tb_aluno");
                }catch (Exception e){
                    alunoReturn.setMatricula(0); //erro
                }
            }
            //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
            catch (IOException | JSONException e){
                e.printStackTrace();

                alunoReturn.setMatricula(0); //erro
            }
            return alunoReturn;
        }



    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Aluno aluno)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.MATRICULA, aluno.getMatricula());
        contentValues.put(myDbHelper.NOME, aluno.getNome());
        contentValues.put(myDbHelper.ALTURA, aluno.getAltura());
        contentValues.put(myDbHelper.PESO, aluno.getPeso());
        contentValues.put(myDbHelper.SEXO, aluno.getSexo());
        contentValues.put(myDbHelper.CIDADE, aluno.getCidade());
        contentValues.put(myDbHelper.ENDERECO, aluno.getEndereco());
        contentValues.put(myDbHelper.TELEFONE, aluno.getTelefone());
        contentValues.put(myDbHelper.IDADE, aluno.getIdade());
        contentValues.put(myDbHelper.EMAIL, aluno.getEmail());
        contentValues.put(myDbHelper.PATHFOTO, "-");
        contentValues.put(myDbHelper.CINTAMAC, aluno.getCintaMac());
        contentValues.put(myDbHelper.ID_LOGIN, aluno.getId_login());
        contentValues.put(myDbHelper.STATUS, aluno.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public Aluno getData(long idlogin)
    {
        Aluno aluno = new Aluno ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.MATRICULA,
                            myDbHelper.NOME,
                            myDbHelper.ALTURA,
                            myDbHelper.PESO,
                            myDbHelper.SEXO,
                            myDbHelper.CIDADE,
                            myDbHelper.ENDERECO,
                            myDbHelper.TELEFONE,
                            myDbHelper.IDADE,
                            myDbHelper.EMAIL,
                            myDbHelper.PATHFOTO,
                            myDbHelper.CINTAMAC,
                            myDbHelper.ID_LOGIN,
                            myDbHelper.STATUS};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            long matricula = 0;
            matricula = cursor.getInt(cursor.getColumnIndex(myDbHelper.MATRICULA));
            String nome =cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
            String  altura =cursor.getString(cursor.getColumnIndex(myDbHelper.ALTURA));
            String  peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
            String  sexo =cursor.getString(cursor.getColumnIndex(myDbHelper.SEXO));
            String  cidade =cursor.getString(cursor.getColumnIndex(myDbHelper.CIDADE));
            String  endereco =cursor.getString(cursor.getColumnIndex(myDbHelper.ENDERECO));
            String  telefone =cursor.getString(cursor.getColumnIndex(myDbHelper.TELEFONE));
            String  idade =cursor.getString(cursor.getColumnIndex(myDbHelper.IDADE));
            String  email =cursor.getString(cursor.getColumnIndex(myDbHelper.EMAIL));
            String  pathFoto =cursor.getString(cursor.getColumnIndex(myDbHelper.PATHFOTO));
            String  cintaMac =cursor.getString(cursor.getColumnIndex(myDbHelper.CINTAMAC));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
            String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

            aluno.setMatricula(matricula);
            aluno.setNome(nome);
            aluno.setAltura(altura);
            aluno.setPeso(Float.parseFloat(peso));
            aluno.setSexo(sexo);
            aluno.setCidade(cidade);
            aluno.setEndereco(endereco);
            aluno.setTelefone(telefone);
            aluno.setIdade(Long.parseLong(idade));
            aluno.setEmail(email);
            aluno.setPathFoto(pathFoto);
            aluno.setCintaMac(cintaMac);
            aluno.setId_login(Long.parseLong(id_login));
            aluno.setStatus(status);
        }
        return aluno;
    }

    public Aluno getData_all()
    {
        Aluno aluno = new Aluno ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.MATRICULA,
                myDbHelper.NOME,
                myDbHelper.ALTURA,
                myDbHelper.PESO,
                myDbHelper.SEXO,
                myDbHelper.CIDADE,
                myDbHelper.ENDERECO,
                myDbHelper.TELEFONE,
                myDbHelper.IDADE,
                myDbHelper.EMAIL,
                myDbHelper.PATHFOTO,
                myDbHelper.CINTAMAC,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try {
            Cursor cursor = db.query(myDbHelper.TABLE_NAME, columns, null, null, null, null, null);
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()) {
                long matricula = cursor.getInt(cursor.getColumnIndex(myDbHelper.MATRICULA));
                String nome = cursor.getString(cursor.getColumnIndex(myDbHelper.NOME));
                String altura = cursor.getString(cursor.getColumnIndex(myDbHelper.ALTURA));
                String peso = cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String sexo = cursor.getString(cursor.getColumnIndex(myDbHelper.SEXO));
                String cidade = cursor.getString(cursor.getColumnIndex(myDbHelper.CIDADE));
                String endereco = cursor.getString(cursor.getColumnIndex(myDbHelper.ENDERECO));
                String telefone = cursor.getString(cursor.getColumnIndex(myDbHelper.TELEFONE));
                String idade = cursor.getString(cursor.getColumnIndex(myDbHelper.IDADE));
                String email = cursor.getString(cursor.getColumnIndex(myDbHelper.EMAIL));
                String pathFoto = cursor.getString(cursor.getColumnIndex(myDbHelper.PATHFOTO));
                String cintaMac = cursor.getString(cursor.getColumnIndex(myDbHelper.CINTAMAC));
                String id_login = cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String status = cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                aluno.setMatricula(matricula);
                aluno.setNome(nome);
                aluno.setAltura(altura);
                aluno.setPeso(Float.parseFloat(peso));
                aluno.setSexo(sexo);
                aluno.setCidade(cidade);
                aluno.setEndereco(endereco);
                aluno.setTelefone(telefone);
                aluno.setIdade(Long.parseLong(idade));
                aluno.setEmail(email);
                aluno.setPathFoto(pathFoto);
                aluno.setCintaMac(cintaMac);
                aluno.setId_login(Long.parseLong(id_login));
                aluno.setStatus(status);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(aluno==null){
            aluno.setMatricula(0);
        }

        return aluno;
    }

    public  int delete(String uname)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={uname};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.NOME+" = ?",whereArgs);
        return  count;
    }

    public int updateName(Aluno aluno)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.MATRICULA,aluno.getMatricula());
        contentValues.put(myDbHelper.NOME,aluno.getNome());
        contentValues.put(myDbHelper.IDADE,aluno.getIdade());
        contentValues.put(myDbHelper.SEXO,aluno.getSexo());
        contentValues.put(myDbHelper.ALTURA,aluno.getAltura());
        contentValues.put(myDbHelper.PESO,aluno.getPeso());
        contentValues.put(myDbHelper.PATHFOTO,aluno.getPathFoto());
        contentValues.put(myDbHelper.CINTAMAC,aluno.getCintaMac());
        contentValues.put(myDbHelper.STATUS,aluno.getStatus());
        String[] whereArgs= {Long.toString(aluno.getId_login())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_LOGIN+" = ?",whereArgs );
        return count;
    }


    //CLASSE STATICA MODEL DO BANCO LOCAL tb_aluno
    public static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "AV_FISICA_ALUNO_BD";    // Database Name
        private static final String TABLE_NAME = "tb_aluno";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String MATRICULA = "Matricula";     // Column I (Primary Key)
        private static final String NOME = "Name";
        private static final String ALTURA = "Altura";
        private static final String PESO = "Peso";
        private static final String SEXO = "Sexo";
        private static final String CIDADE = "Cidade";
        private static final String ENDERECO = "Endereco";
        private static final String TELEFONE = "Telefone";
        private static final String IDADE = "Idade";
        private static final String EMAIL = "Email";
        private static final String PATHFOTO = "PathFoto";
        private static final String CINTAMAC = "CintaMac";
        private static final String ID_LOGIN = "Id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + MATRICULA + " INTEGER PRIMARY KEY, "
                + NOME + " VARCHAR(255) ,"
                + ALTURA + " VARCHAR(225),"
                + PESO + " VARCHAR(225),"
                + SEXO + " VARCHAR(225),"
                + CIDADE + " VARCHAR(225),"
                + ENDERECO + " VARCHAR(225),"
                + TELEFONE + " VARCHAR(225),"
                + IDADE + " VARCHAR(225),"
                + EMAIL + " VARCHAR(225),"
                + PATHFOTO + " VARCHAR(225),"
                + CINTAMAC + " VARCHAR(225),"
                + ID_LOGIN + " INTEGER,"
                + STATUS + " VARCHAR(255));";  //"OK" / "PENDENTE"
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
