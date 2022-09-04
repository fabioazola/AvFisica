package com.example.avfisica.repository;

import com.example.avfisica.models.Pagamento;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PagamentoRepository {

    public Pagamento findPagamentoByIdLogin(String mes, long id_login)
    {
        Pagamento pagamento = new Pagamento();
        Gson gson = new Gson();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/pagamento/"+Long.toString(id_login)+"/"+mes);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            pagamento = gson.fromJson(data,Pagamento.class);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pagamento;
    }
}
