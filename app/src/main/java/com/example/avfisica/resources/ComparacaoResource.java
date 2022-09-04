package com.example.avfisica.resources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.avfisica.models.Comparacao;
import java.util.ArrayList;
import java.util.List;

public class ComparacaoResource {
    myDbHelper myhelper;

    //Construtor
    public ComparacaoResource(Context context)
    {
        myhelper = new myDbHelper(context);
    }


    //################### BANCO DE DADOS LOCAL  CONTÉM O CREATE, QUERY, INSERT, UPDATE E DELETE###############
    public long insertData(Comparacao comparacao)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.DATA, comparacao.getData());
        contentValues.put(myDbHelper.PATH, comparacao.getPath());
        contentValues.put(myDbHelper.TIPO, comparacao.getTipo());
        contentValues.put(myDbHelper.ID_LOGIN, comparacao.getId_login());
        long ret = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return ret;
    }

    public List<Comparacao> getData(String tipo_input)
    {
        List<Comparacao> lcomparacao =  new ArrayList<Comparacao>();
        SQLiteDatabase db = myhelper.getWritableDatabase();

        try{
        Cursor cursor =db.query(myDbHelper.TABLE_NAME, new String[]{"id_comparacao", "data",
                "path", "tipo", "id_login"}, "tipo" + "= ? ", new String[]{String.valueOf(tipo_input)}, null, null, null, null);

        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            Comparacao comparacao = new Comparacao ();

            String id_comparacao =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_COMPARACAO));
            String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
            String path =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH));
            String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));

            comparacao.setId_comparacao(Long.parseLong(id_comparacao));
            comparacao.setData(data);
            comparacao.setPath(path);
            comparacao.setTipo(tipo);
            comparacao.setId_login(Long.parseLong(id_login));
            lcomparacao.add(comparacao);

        }
       }catch (Exception e){
        e.printStackTrace();
       }
        return lcomparacao;
    }

    public Comparacao getData_all()
    {
        Comparacao comparacao = new Comparacao ();
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {
                myDbHelper.ID_COMPARACAO,
                myDbHelper.DATA,
                myDbHelper.PATH,
                myDbHelper.TIPO,
                myDbHelper.ID_LOGIN};

        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            String id_comparacao =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_COMPARACAO));
            String data =cursor.getString(cursor.getColumnIndex(myDbHelper.DATA));
            String path =cursor.getString(cursor.getColumnIndex(myDbHelper.PATH));
            String tipo =cursor.getString(cursor.getColumnIndex(myDbHelper.TIPO));
            String  id_login =cursor.getString(cursor.getColumnIndex(myDbHelper.ID_LOGIN));

            comparacao.setId_comparacao(Long.parseLong(id_comparacao));
            comparacao.setData(data);
            comparacao.setPath(path);
            comparacao.setTipo(tipo);
            comparacao.setId_login(Long.parseLong(id_login));
        }

        if(comparacao==null){
            comparacao.setId_login(0);
        }

        return comparacao;
    }

    public  int delete(long id_comparacao)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={Long.toString(id_comparacao)};

        int count =db.delete(myDbHelper.TABLE_NAME , myDbHelper.ID_COMPARACAO+" = ?",whereArgs);
        return  count;
    }

    public int updateName(Comparacao comparacao)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.ID_COMPARACAO,comparacao.getId_comparacao());
        contentValues.put(myDbHelper.DATA,comparacao.getData());
        contentValues.put(myDbHelper.PATH,comparacao.getPath());
        contentValues.put(myDbHelper.TIPO,comparacao.getTipo());
        String[] whereArgs= {Long.toString(comparacao.getId_login())};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.ID_LOGIN+" = ?",whereArgs );
        return count;
    }



    //CLASSE STATICA MODEL DO BANCO LOCAL tb_comparacao
    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "AV_FISICA_COMPARACAO_BD";    // Database Name
        private static final String TABLE_NAME = "tb_comparacao";   // Table Name
        private static final int DATABASE_Version = 4;    // Database Version
        private static final String ID_COMPARACAO="id_comparacao";     // Column I (Primary Key)
        private static final String DATA = "data";    //Column II
        private static final String PATH= "path";    // Column III
        private static final String TIPO= "tipo";    // Column IV
        private static final String ID_LOGIN = "id_login";    // Column IV
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+ID_COMPARACAO+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +DATA+" VARCHAR(255), "
                    + PATH+" VARCHAR(225),"
                    + TIPO+" VARCHAR(225),"
                    + ID_LOGIN + " INTEGER);";
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
