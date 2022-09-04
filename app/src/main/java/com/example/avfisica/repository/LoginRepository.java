package com.example.avfisica.repository;

import com.example.avfisica.models.Login;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginRepository {
    public Login findByLogin(String strlogin, String password)
    {
        Login login = new Login();
        Gson gson = new Gson();

        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/login_name"+"/"+strlogin+"/"+password);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            login = gson.fromJson(data,Login.class);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return login;
    }

    public boolean getResetPassword(String strlogin)
    {
        boolean flag_return = false;
        String data ="";

        try {
            URL url = new URL("http://apirest-wifit.herokuapp.com/api/login_reset/"+"/"+strlogin);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                data = data + line;
            }
            if (line!="0")
                flag_return = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag_return;
    }

}
