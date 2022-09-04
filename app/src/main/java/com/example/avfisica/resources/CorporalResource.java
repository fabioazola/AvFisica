package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.avfisica.Util;
import com.example.avfisica.models.CorporalModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CorporalResource {
    myDbHelper myhelper;

    //Construtor
    public CorporalResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    //################### BANCO DE DADOS NUVEM ###############
    //POST (INSERT , UPDATE)
    public CorporalModel insertDataPost(CorporalModel corporal)
    {
        CorporalModel corporalReturn = new CorporalModel();
        Util util = new Util();
        try{
            JSONObject cred = new JSONObject();
            cred.put("id_corporal", corporal.getId_corporal());
            cred.put("data", corporal.getData());
            cred.put("abdominal", corporal.getAbdominal());
            cred.put("pescoco", corporal.getPescoco());
            cred.put("quadril", corporal.getQuadril());
            cred.put("bicepsDir", corporal.getBicepsDir());
            cred.put("bicepsEsq", corporal.getBicepsEsq());
            cred.put("coxaDir", corporal.getCoxaDir());
            cred.put("coxaEsq", corporal.getCoxaEsq());
            cred.put("gordura", corporal.getGordura());
            cred.put("torax", corporal.getTorax());
            cred.put("cintura", corporal.getCintura());
            cred.put("pantDir", corporal.getPantDir());
            cred.put("pantEsq", corporal.getPantEsq());
            cred.put("antBDir", corporal.getAntbDir());
            cred.put("antBEsq", corporal.getAntbEsq());
            cred.put("id_login", corporal.getId_login());

            //String url="http://192.168.0.102:8080/api/corporal";
            String url="http://apirest-wifit.herokuapp.com/api/corporal";
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
                corporalReturn = (CorporalModel) util.convertJsonToObject(jsonDeResposta, corporal, "tb_corporal");
            }catch (Exception e){
                corporalReturn.setId_corporal(0); //erro
            }
        }
        //catch (IOException | JSONException | NoSuchAlgorithmException | KeyManagementException e){
        catch (IOException | JSONException e){
            e.printStackTrace();

            corporalReturn.setId_corporal(0); //erro
        }
        return corporalReturn;
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(CorporalModel corporal)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_CORPRAL, getNextId());
        contentValues.put(myDbHelper.DATA, corporal.getData());
        contentValues.put(myDbHelper.ABDOMINAL, corporal.getAbdominal());
        contentValues.put(myDbHelper.PESCOCO, corporal.getPescoco());
        contentValues.put(myDbHelper.QUADRIL, corporal.getQuadril());
        contentValues.put(myDbHelper.BICEPSDIR, corporal.getBicepsDir());
        contentValues.put(myDbHelper.BICEPSESQ, corporal.getBicepsEsq());
        contentValues.put(myDbHelper.COXADIR, corporal.getCoxaDir());
        contentValues.put(myDbHelper.COXAESQ, corporal.getCoxaEsq());
        contentValues.put(myDbHelper.GORDURA, corporal.getGordura());
        contentValues.put(myDbHelper.TORAX, corporal.getTorax());
        contentValues.put(myDbHelper.CINTURA, corporal.getCintura());
        contentValues.put(myDbHelper.PANTDIR, corporal.getPantDir());
        contentValues.put(myDbHelper.PANTESQ, corporal.getPantEsq());
        contentValues.put(myDbHelper.ANTBDIR, corporal.getAntbDir());
        contentValues.put(myDbHelper.ANTBESQ, corporal.getAntbEsq());
        contentValues.put(myDbHelper.ID_LOGIN, corporal.getId_login());
        contentValues.put(myDbHelper.STATUS, corporal.getStatus());

        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<CorporalModel> getData(long id_login_input)
    {
        List<CorporalModel> lcoropral =  new ArrayList<CorporalModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_CORPRAL,
                myDbHelper.DATA,
                myDbHelper.ABDOMINAL,
                myDbHelper.PESCOCO,
                myDbHelper.QUADRIL,
                myDbHelper.BICEPSDIR,
                myDbHelper.BICEPSESQ,
                myDbHelper.COXADIR,
                myDbHelper.COXAESQ,
                myDbHelper.GORDURA,
                myDbHelper.TORAX,
                myDbHelper.CINTURA,
                myDbHelper.PANTDIR,
                myDbHelper.PANTESQ,
                myDbHelper.ANTBDIR,
                myDbHelper.ANTBESQ,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[16] + "="+ Long.toString(id_login_input),null, null, null, myDbHelper.ID_CORPRAL, null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                CorporalModel corpo = new CorporalModel ();

                String id_corporal =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CORPRAL));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String abdominal =cursor.getString(cursor.getColumnIndex(myDbHelper.ABDOMINAL));
                String pescoco =cursor.getString(cursor.getColumnIndex(myDbHelper.PESCOCO));
                String quadril =cursor.getString(cursor.getColumnIndex(myDbHelper.QUADRIL));
                String bicepsDir =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSDIR));
                String bicepsEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSESQ));
                String coxaDir =cursor.getString(cursor.getColumnIndex(myDbHelper.COXADIR));
                String coxaEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.COXAESQ));
                String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
                String torax =cursor.getString(cursor.getColumnIndex(myDbHelper.TORAX));
                String cintura =cursor.getString(cursor.getColumnIndex(myDbHelper.CINTURA));
                String pantDir =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTDIR));
                String pantEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTESQ));
                String antbDir =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBDIR));
                String antbEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBESQ));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                corpo.setId_corporal(Long.parseLong(id_corporal));
                corpo.setData(data);
                corpo.setAbdominal((Float.parseFloat(abdominal)));
                corpo.setPescoco((Float.parseFloat(pescoco)));
                corpo.setQuadril((Float.parseFloat(quadril)));
                corpo.setBicepsDir((Float.parseFloat(bicepsDir)));
                corpo.setBicepsEsq((Float.parseFloat(bicepsEsq)));
                corpo.setCoxaDir((Float.parseFloat(coxaDir)));
                corpo.setCoxaEsq((Float.parseFloat(coxaEsq)));
                corpo.setGordura((Float.parseFloat(gordura)));
                corpo.setTorax((Float.parseFloat(torax)));
                corpo.setCintura((Float.parseFloat(cintura)));
                corpo.setPantDir((Float.parseFloat(pantDir)));
                corpo.setPantEsq((Float.parseFloat(pantEsq)));
                corpo.setAntbDir((Float.parseFloat(antbDir)));
                corpo.setAntbEsq((Float.parseFloat(antbEsq)));
                corpo.setId_login(Long.parseLong(id_login));
                corpo.setStatus(status);
                lcoropral.add(corpo);
            }
        }catch (Exception e){
            lcoropral.clear(); //falha
        }
        return lcoropral;
    }

    public List<CorporalModel> searchData(String searchdata)
    {
        List<CorporalModel> lcoropral =  new ArrayList<CorporalModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_CORPRAL,
                myDbHelper.DATA,
                myDbHelper.ABDOMINAL,
                myDbHelper.PESCOCO,
                myDbHelper.QUADRIL,
                myDbHelper.BICEPSDIR,
                myDbHelper.BICEPSESQ,
                myDbHelper.COXADIR,
                myDbHelper.COXAESQ,
                myDbHelper.GORDURA,
                myDbHelper.TORAX,
                myDbHelper.CINTURA,
                myDbHelper.PANTDIR,
                myDbHelper.PANTESQ,
                myDbHelper.ANTBDIR,
                myDbHelper.ANTBESQ,
                myDbHelper.ID_LOGIN,
                myDbHelper.STATUS};
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, columns, columns[1] + "="+ (searchdata),null, null, null, null, null);
            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                CorporalModel corpo = new CorporalModel ();

                String id_corporal =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CORPRAL));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String abdominal =cursor.getString(cursor.getColumnIndex(myDbHelper.ABDOMINAL));
                String pescoco =cursor.getString(cursor.getColumnIndex(myDbHelper.PESCOCO));
                String quadril =cursor.getString(cursor.getColumnIndex(myDbHelper.QUADRIL));
                String bicepsDir =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSDIR));
                String bicepsEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSESQ));
                String coxaDir =cursor.getString(cursor.getColumnIndex(myDbHelper.COXADIR));
                String coxaEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.COXAESQ));
                String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
                String torax =cursor.getString(cursor.getColumnIndex(myDbHelper.TORAX));
                String cintura =cursor.getString(cursor.getColumnIndex(myDbHelper.CINTURA));
                String pantDir =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTDIR));
                String pantEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTESQ));
                String antbDir =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBDIR));
                String antbEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBESQ));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                corpo.setId_corporal(Long.parseLong(id_corporal));
                corpo.setData(data);
                corpo.setAbdominal((Float.parseFloat(abdominal)));
                corpo.setPescoco((Float.parseFloat(pescoco)));
                corpo.setQuadril((Float.parseFloat(quadril)));
                corpo.setBicepsDir((Float.parseFloat(bicepsDir)));
                corpo.setBicepsEsq((Float.parseFloat(bicepsEsq)));
                corpo.setCoxaDir((Float.parseFloat(coxaDir)));
                corpo.setCoxaEsq((Float.parseFloat(coxaEsq)));
                corpo.setGordura((Float.parseFloat(gordura)));
                corpo.setTorax((Float.parseFloat(torax)));
                corpo.setCintura((Float.parseFloat(cintura)));
                corpo.setPantDir((Float.parseFloat(pantDir)));
                corpo.setPantEsq((Float.parseFloat(pantEsq)));
                corpo.setAntbDir((Float.parseFloat(antbDir)));
                corpo.setAntbEsq((Float.parseFloat(antbEsq)));
                corpo.setId_login(Long.parseLong(id_login));
                corpo.setStatus(status);
                lcoropral.add(corpo);

            }
        }catch (Exception e){

        }
        return lcoropral;
    }

    public List<CorporalModel> getDataStatus(String input_status)
    {
        List<CorporalModel> lcoropral =  new ArrayList<CorporalModel>();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        try{
            Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{
                    "id_corporal",
                    "data",
                    "Abdominal",
                    "Pescoco",
                    "Quadril",
                    "BicepsDir",
                    "BicepsEsq",
                    "CoxaDir",
                    "CoxaEsq",
                    "Gordura",
                    "Torax",
                    "Cintura",
                    "PantDir",
                    "PantEsq",
                    "AntbDir",
                    "AntbEsq",
                    "id_login",
                    "status"}, "status" + "= ? ", new String[]{String.valueOf(input_status)}, null, null, null, null);

            StringBuffer buffer= new StringBuffer();
            while (cursor.moveToNext())
            {
                CorporalModel corpo = new CorporalModel ();

                String id_corporal =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_CORPRAL));
                String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
                String abdominal =cursor.getString(cursor.getColumnIndex(myDbHelper.ABDOMINAL));
                String pescoco =cursor.getString(cursor.getColumnIndex(myDbHelper.PESCOCO));
                String quadril =cursor.getString(cursor.getColumnIndex(myDbHelper.QUADRIL));
                String bicepsDir =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSDIR));
                String bicepsEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.BICEPSESQ));
                String coxaDir =cursor.getString(cursor.getColumnIndex(myDbHelper.COXADIR));
                String coxaEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.COXAESQ));
                String gordura =cursor.getString(cursor.getColumnIndex(myDbHelper.GORDURA));
                String torax =cursor.getString(cursor.getColumnIndex(myDbHelper.TORAX));
                String cintura =cursor.getString(cursor.getColumnIndex(myDbHelper.CINTURA));
                String pantDir =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTDIR));
                String pantEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.PANTESQ));
                String antbDir =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBDIR));
                String antbEsq =cursor.getString(cursor.getColumnIndex(myDbHelper.ANTBESQ));
                String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));
                String  status =cursor.getString(cursor.getColumnIndex(myDbHelper.STATUS));

                corpo.setId_corporal(Long.parseLong(id_corporal));
                corpo.setData(data);
                corpo.setAbdominal((Float.parseFloat(abdominal)));
                corpo.setPescoco((Float.parseFloat(pescoco)));
                corpo.setQuadril((Float.parseFloat(quadril)));
                corpo.setBicepsDir((Float.parseFloat(bicepsDir)));
                corpo.setBicepsEsq((Float.parseFloat(bicepsEsq)));
                corpo.setCoxaDir((Float.parseFloat(coxaDir)));
                corpo.setCoxaEsq((Float.parseFloat(coxaEsq)));
                corpo.setGordura((Float.parseFloat(gordura)));
                corpo.setTorax((Float.parseFloat(torax)));
                corpo.setCintura((Float.parseFloat(cintura)));
                corpo.setPantDir((Float.parseFloat(pantDir)));
                corpo.setPantEsq((Float.parseFloat(pantEsq)));
                corpo.setAntbDir((Float.parseFloat(antbDir)));
                corpo.setAntbEsq((Float.parseFloat(antbEsq)));
                corpo.setId_login(Long.parseLong(id_login));
                corpo.setStatus(status);
                lcoropral.add(corpo);
            }
        }catch (Exception e){

        }
        return lcoropral;
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

    public  int delete(long id_corporal)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_corporal)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_CORPRAL+" = ?",whereArgs);
        return  count;
    }

    public int updateName(CorporalModel corporal)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_CORPRAL,corporal.getId_corporal());
        contentValues.put(myDbHelper.DATA, corporal.getData());
        contentValues.put(myDbHelper.ABDOMINAL, corporal.getAbdominal());
        contentValues.put(myDbHelper.PESCOCO, corporal.getPescoco());
        contentValues.put(myDbHelper.QUADRIL, corporal.getQuadril());
        contentValues.put(myDbHelper.BICEPSDIR, corporal.getBicepsDir());
        contentValues.put(myDbHelper.BICEPSESQ, corporal.getBicepsEsq());
        contentValues.put(myDbHelper.COXADIR, corporal.getCoxaDir());
        contentValues.put(myDbHelper.COXAESQ, corporal.getCoxaEsq());
        contentValues.put(myDbHelper.GORDURA, corporal.getGordura());
        contentValues.put(myDbHelper.TORAX, corporal.getTorax());
        contentValues.put(myDbHelper.CINTURA, corporal.getCintura());
        contentValues.put(myDbHelper.PANTDIR, corporal.getPantDir());
        contentValues.put(myDbHelper.PANTESQ, corporal.getPantEsq());
        contentValues.put(myDbHelper.ANTBDIR, corporal.getAntbDir());
        contentValues.put(myDbHelper.ANTBESQ, corporal.getAntbEsq());
        contentValues.put(myDbHelper.STATUS,corporal.getStatus());
        String[] whereArgs= {Long.toString(corporal.getId_corporal())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_CORPRAL+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_peso
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_CORPORAL_BD";    // Database Name
        private static final String TABLE_NAME = "tb_corporal";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_CORPRAL="id_corporal"; // Column I (Primary Key)
        private static final String DATA = "data";
        private static final String ABDOMINAL= "Abdominal";
        private static final String PESCOCO= "Pescoco";
        private static final String QUADRIL= "Quadril";
        private static final String BICEPSDIR= "BicepsDir";
        private static final String BICEPSESQ= "BicepsEsq";
        private static final String COXADIR= "CoxaDir";
        private static final String COXAESQ= "CoxaEsq";
        private static final String GORDURA= "Gordura";
        private static final String TORAX= "Torax";
        private static final String CINTURA= "Cintura";
        private static final String PANTDIR= "PantDir";
        private static final String PANTESQ= "PantEsq";
        private static final String ANTBDIR= "AntbDir";
        private static final String ANTBESQ= "AntbEsq";
        private static final String ID_LOGIN = "id_login";
        private static final String STATUS = "status";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_CORPRAL+" INTEGER PRIMARY KEY, "
                +DATA+" VARCHAR(255) ,"
                + ABDOMINAL+" VARCHAR(225),"
                +PESCOCO+" VARCHAR(225),\"+PESCOCO+\" VARCHAR(225),"
                +QUADRIL+" VARCHAR(225),"
                +BICEPSDIR+" VARCHAR(225),"
                +BICEPSESQ+" VARCHAR(225),"
                +COXADIR+" VARCHAR(225),"
                +COXAESQ+" VARCHAR(225),"
                +GORDURA+" VARCHAR(225),"
                +TORAX+" VARCHAR(225),"
                +CINTURA+" VARCHAR(225),"
                +PANTDIR+" VARCHAR(225),"
                +PANTESQ+" VARCHAR(225),"
                +ANTBDIR+" VARCHAR(225),"
                +ANTBESQ+" VARCHAR(225),"
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
