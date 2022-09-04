package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.avfisica.Util;
import com.example.avfisica.models.TaxaModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaxaResource {
    myDbHelper myhelper;

    //Construtor
    public TaxaResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public TaxaModel insertDataPost(TaxaModel taxa)
    {
        TaxaModel taxaReturn = new TaxaModel();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_taxa_metabolica", taxa.getId_taxa());
            cred.put("data", taxa.getData());
            cred.put("taxa", taxa.getOption()); //modificar
            cred.put("tipo", taxa.getTipo());
            cred.put("kilos", taxa.getPeso());
            cred.put("tempo", taxa.getTempo());
            cred.put("gasto_treino", taxa.getGasto_treino());
            cred.put("proteina", taxa.getProteina());
            cred.put("gordura", taxa.getGordura());
            cred.put("carbo", taxa.getCarboidrato());
            cred.put("id_login", taxa.getId_login());

            //String url="http://192.168.0.102:8080/api/fluxoAula";
            String url="http://apirest-wifit.herokuapp.com/api/taxaMetabolica";
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
                taxaReturn = (TaxaModel) util.convertJsonToObject(jsonDeResposta, taxa, "tb_taxaMetabolica");
            }catch (Exception e){
                taxaReturn.setId_taxa(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            taxaReturn.setId_taxa(0); //erro
        }
        return taxaReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(TaxaModel taxa)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_TAXA, getNextId());
        contentValues.put(myDbHelper.DATA, taxa.getData());
        contentValues.put(myDbHelper.TIPO, taxa.getTipo());
        contentValues.put(myDbHelper.PESO, taxa.getPeso());
        contentValues.put(myDbHelper.TEMPO, taxa.getTempo());
        contentValues.put(myDbHelper.GASTO_TREINO, taxa.getGasto_treino());
        contentValues.put(myDbHelper.OPTION, taxa.getOption());
        contentValues.put(myDbHelper.PROTEINA, taxa.getProteina());
        contentValues.put(myDbHelper.GORDURA, taxa.getGordura());
        contentValues.put(myDbHelper.CARBOIDRATO, taxa.getCarboidrato());
        contentValues.put(myDbHelper.ID_LOGIN, taxa.getId_login());
        contentValues.put(myDbHelper.STATUS, taxa.getStatus());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<TaxaModel> getData(long id_login_input)
    {
        List<TaxaModel> ltaxa =  new ArrayList<TaxaModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_TAXA,
                myDbHelper.DATA,
                myDbHelper.TIPO,
                myDbHelper.PESO,
                myDbHelper.TEMPO,
                myDbHelper.GASTO_TREINO,
                myDbHelper.OPTION,
                myDbHelper.PROTEINA,
                myDbHelper.GORDURA,
                myDbHelper.CARBOIDRATO,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[10] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.ID_TAXA +" ASC", null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                TaxaModel _taxa = new TaxaModel();

                String id_taxa =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TAXA));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String tempo =cursor.getString(cursor.getColumnIndex(myDbHelper.TEMPO));
                String gasto_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.GASTO_TREINO));
                String option =cursor.getString(cursor.getColumnIndex(myDbHelper.OPTION));
                String proteina =cursor.getString(cursor.getColumnIndex(myDbHelper.PROTEINA));
                String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
                String carboidrato =cursor.getString(cursor.getColumnIndex(myDbHelper.CARBOIDRATO));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                _taxa.setId_taxa(Long.parseLong(id_taxa));
                _taxa.setData(data);
                _taxa.setTipo(tipo);
                _taxa.setPeso(Float.parseFloat(peso));
                _taxa.setTempo(Float.parseFloat(tempo));
                _taxa.setGasto_treino(Float.parseFloat(gasto_treino));
                _taxa.setOption(Float.parseFloat(option));
                _taxa.setProteina(Float.parseFloat(proteina));
                _taxa.setGordura(Float.parseFloat(gordura));
                _taxa.setCarboidrato(Float.parseFloat(carboidrato));
                _taxa.setId_login(Long.parseLong(id_login));
                _taxa.setStatus(status);
                ltaxa.add(_taxa);

            }
        }catch (Exception e){

        }
        return ltaxa;
    }

    public List<TaxaModel> getDataStatus(String input_status)
    {
        List<TaxaModel>ltaxa =  new ArrayList<TaxaModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_taxa",
                    "data",
                    "tipo",
                    "peso",
                    "tempo",
                    "gasto_treino",
                    "option",
                    "proteina",
                    "gordura",
                    "carboidrato",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                TaxaModel _taxa = new TaxaModel();

                String id_taxa =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TAXA));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
                String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
                String tempo =cursor.getString(cursor.getColumnIndex(myDbHelper.TEMPO));
                String gasto_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.GASTO_TREINO));
                String option =cursor.getString(cursor.getColumnIndex(myDbHelper.OPTION));
                String proteina =cursor.getString(cursor.getColumnIndex(myDbHelper.PROTEINA));
                String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
                String carboidrato =cursor.getString(cursor.getColumnIndex(myDbHelper.CARBOIDRATO));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                _taxa.setId_taxa(Long.parseLong(id_taxa));
                _taxa.setData(data);
                _taxa.setTipo(tipo);
                _taxa.setPeso(Float.parseFloat(peso));
                _taxa.setTempo(Float.parseFloat(tempo));
                _taxa.setGasto_treino(Float.parseFloat(gasto_treino));
                _taxa.setOption(Float.parseFloat(option));
                _taxa.setProteina(Float.parseFloat(proteina));
                _taxa.setGordura(Float.parseFloat(gordura));
                _taxa.setCarboidrato(Float.parseFloat(carboidrato));
                _taxa.setId_login(Long.parseLong(id_login));
                _taxa.setStatus(status);
                ltaxa.add(_taxa);
            }
        }catch (Exception e){

        }
        return ltaxa;
    }

    public TaxaModel getData_all()
    {
        TaxaModel _taxa = new TaxaModel();
        List<TaxaModel>ltaxa =  new ArrayList<TaxaModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_TAXA,
                myDbHelper.DATA,
                myDbHelper.TIPO,
                myDbHelper.PESO,
                myDbHelper.TEMPO,
                myDbHelper.GASTO_TREINO,
                myDbHelper.OPTION,
                myDbHelper.PROTEINA,
                myDbHelper.GORDURA,
                myDbHelper.CARBOIDRATO,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};

        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
          //  CaloriasModel _calorias = new CaloriasModel();

            String id_taxa =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_TAXA));
            String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
            String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
            String peso =cursor.getString(cursor.getColumnIndex(myDbHelper.PESO));
            String tempo =cursor.getString(cursor.getColumnIndex(myDbHelper.TEMPO));
            String gasto_treino =cursor.getString(cursor.getColumnIndex(myDbHelper.GASTO_TREINO));
            String option =cursor.getString(cursor.getColumnIndex(myDbHelper.OPTION));
            String proteina =cursor.getString(cursor.getColumnIndex(myDbHelper.PROTEINA));
            String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
            String carboidrato =cursor.getString(cursor.getColumnIndex(myDbHelper.CARBOIDRATO));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
            String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

            _taxa.setId_taxa(Long.parseLong(id_taxa));
            _taxa.setData(data);
            _taxa.setTipo(tipo);
            _taxa.setPeso(Float.parseFloat(peso));
            _taxa.setTempo(Float.parseFloat(tempo));
            _taxa.setGasto_treino(Float.parseFloat(gasto_treino));
            _taxa.setOption(Float.parseFloat(option));
            _taxa.setProteina(Float.parseFloat(proteina));
            _taxa.setGordura(Float.parseFloat(gordura));
            _taxa.setCarboidrato(Float.parseFloat(carboidrato));
            _taxa.setId_login(Long.parseLong(id_login));
            _taxa.setStatus(status);
            ltaxa.add(_taxa);
        }

        if(_taxa==null){
            _taxa.setId_login(0);
        }

        return _taxa;
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

    public  int delete(long id_taxa)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_taxa)};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.ID_TAXA+" = ?",whereArgs);
        return  count;
    }

    public int updateName(TaxaModel taxa)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_TAXA, taxa.getId_taxa());
        contentValues.put(myDbHelper.DATA, taxa.getData());
        contentValues.put(myDbHelper.TIPO, taxa.getTipo());
        contentValues.put(myDbHelper.PESO, taxa.getPeso());
        contentValues.put(myDbHelper.TEMPO, taxa.getTempo());
        contentValues.put(myDbHelper.GASTO_TREINO, taxa.getGasto_treino());
        contentValues.put(myDbHelper.OPTION, taxa.getOption());
        contentValues.put(myDbHelper.PROTEINA, taxa.getProteina());
        contentValues.put(myDbHelper.GORDURA, taxa.getGordura());
        contentValues.put(myDbHelper.CARBOIDRATO, taxa.getCarboidrato());
        contentValues.put(myDbHelper.STATUS, taxa.getStatus());
        String[] whereArgs= {Long.toString(taxa.getId_taxa())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_TAXA+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "TAXAMETABOLICA_BD";    // Database Name
        private static final String TABLE_NAME = "tb_taxaMetabolica";   // Table Name
        private static final int DATABASE_Version = 3;    // Database Version
        private static final String ID_TAXA="id_taxa";     // Column I (Primary Key)
        private static final String DATA = "data";    //Column II
        private static final String TIPO= "tipo";    // Column III
        private static final String PESO= "peso";    // Column IV
        private static final String TEMPO= "tempo";    // Column IV
        private static final String GASTO_TREINO= "gasto_treino";    // Column IV
        private static final String OPTION= "option";    // Column IV
        private static final String PROTEINA= "proteina";    // Column IV
        private static final String GORDURA= "gordura";    // Column IV
        private static final String CARBOIDRATO= "carboidrato";    // Column IV
        private static final String ID_LOGIN = "id_login";    // Column IV
        private static final String STATUS = "status";    // Column X
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_TAXA+" INTEGER PRIMARY KEY, "
                +DATA+" VARCHAR(255), "
                + TIPO+" VARCHAR(225),"
                + PESO+" VARCHAR(225),"
                + TEMPO+" VARCHAR(225),"
                + GASTO_TREINO+" VARCHAR(225),"
                + OPTION+" VARCHAR(225),"
                + PROTEINA+" VARCHAR(225),"
                + GORDURA+" VARCHAR(225),"
                + CARBOIDRATO+" VARCHAR(225),"
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
