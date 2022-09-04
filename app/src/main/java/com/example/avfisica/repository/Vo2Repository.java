package com.example.avfisica.repository;

import com.example.avfisica.Vo2;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.Vo2Model;
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

public class Vo2Repository {

    public List<Vo2Model> findPesoRangeByIdLogin(long id_vo2_init, long id_vo2_final, long id_login)
    {
        Vo2Model vo2 = new Vo2Model();
        Gson gson = new Gson();
        List<Vo2Model> lvo2 =  new ArrayList<Vo2Model>();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/vo2/range/"+Long.toString(id_vo2_init)+"/"+Long.toString(id_vo2_final)+"/"+Long.toString(id_login));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            Type usuariosListType = new TypeToken<ArrayList<Vo2Model>>(){}.getType();
            lvo2 = gson.fromJson(data, usuariosListType);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lvo2;
    }

    public long findCountid(long id_login)
    {
        String data ="";
        long ret = 0;

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/vo2/id_count/"+Long.toString(id_login));
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
