package com.example.avfisica.repository;

import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Peso;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CorporalRepository {

    public List<CorporalModel> findCorporalRangeByIdLogin(long id_corporal_init, long id_corporal_final, long id_login)
    {
        CorporalModel peso = new CorporalModel();
        Gson gson = new Gson();
        List<CorporalModel> lcorporal =  new ArrayList<CorporalModel>();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/corporal/range/"+Long.toString(id_corporal_init)+"/"+Long.toString(id_corporal_final)+"/"+Long.toString(id_login));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            Type usuariosListType = new TypeToken<ArrayList<CorporalModel>>(){}.getType();
            lcorporal = gson.fromJson(data, usuariosListType);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lcorporal;
    }

    public long findCountid(long id_login)
    {
            String data ="";
            long ret = 0;

            try {
                URL url = new URL("http://apirest-wifit.herokuapp.com/api/corporal/id_count/"+Long.toString(id_login));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while((line = bufferedReader.readLine()) != null){
                    data = data + line;
                }
                ret = Long.parseLong(data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }
}
