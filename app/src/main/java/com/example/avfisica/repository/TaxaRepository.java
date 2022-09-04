package com.example.avfisica.repository;

import com.example.avfisica.models.TaxaModel;
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

public class TaxaRepository {

    public List<TaxaModel> findPesoRangeByIdLogin(long id_calorias_init, long id_calorias_final, long id_login)
    {
        TaxaModel calorias = new TaxaModel();
        Gson gson = new Gson();
        List<TaxaModel> lcalorias =  new ArrayList<TaxaModel>();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/taxaMetabolica/range/"+Long.toString(id_calorias_init)+"/"+Long.toString(id_calorias_final)+"/"+Long.toString(id_login));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            Type usuariosListType = new TypeToken<ArrayList<TaxaModel>>(){}.getType();
            lcalorias = gson.fromJson(data, usuariosListType);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lcalorias;
    }

    public long findCountid(long id_login)
    {
        String data ="";
        long ret = 0;

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/taxaMetabolica/id_count/"+Long.toString(id_login));
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
