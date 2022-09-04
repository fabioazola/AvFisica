package com.example.avfisica.repository;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.Login;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AlunoRepository {
    public Aluno findByAluno(Login login)
    {
        Aluno aluno = new Aluno();
        Gson gson = new Gson();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/aluno/id_login"+"/"+Long.toString(login.getId())+"/"+login.getLogin()+"/"+login.getPassword());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            aluno = gson.fromJson(data,Aluno.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aluno;
    }
}
