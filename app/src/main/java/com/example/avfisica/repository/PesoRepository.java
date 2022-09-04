package com.example.avfisica.repository;

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

public class PesoRepository {

    public List<Peso> findPesoRangeByIdLogin(long id_peso_init, long id_peso_final, long id_login)
    {
        Peso peso = new Peso();
        Gson gson = new Gson();
        List<Peso> lpeso =  new ArrayList<Peso>();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/peso/range/"+Long.toString(id_peso_init)+"/"+Long.toString(id_peso_final)+"/"+Long.toString(id_login));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            Type usuariosListType = new TypeToken<ArrayList<Peso>>(){}.getType();
            lpeso = gson.fromJson(data, usuariosListType);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lpeso;
    }

    public long findCountid(long id_login)
    {
            String data ="";
            long ret = 0;

            try {
                URL url = new URL("http://apirest-wifit.herokuapp.com/api/peso/id_count/"+Long.toString(id_login));
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
