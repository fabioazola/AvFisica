package com.example.avfisica;

import com.example.avfisica.models.Aluno;
import com.example.avfisica.models.TaxaModel;
import com.example.avfisica.models.Corrida;
import com.example.avfisica.models.Login;
import com.example.avfisica.models.Peso;
import com.example.avfisica.models.CorporalModel;
import com.example.avfisica.models.Vo2Model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class Util {

    public  Object convertJsonToObject(String json, Object objPost, String tabela){

        Object obj = new Object();
        String textosplit [];
        String textosplit_aux [];

        switch (tabela)
        {
            case "tb_login":
                Login objlogin = new Login();
                objlogin = (Login)objPost;
                textosplit = json.split(":");
                textosplit_aux = textosplit[1].split(",");
                objlogin.setId( Long.parseLong(textosplit_aux[0]));
                obj = objlogin;
                break;

            case "tb_aluno":
                Aluno objAluno = new Aluno();
                objAluno = (Aluno)objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objAluno.setMatricula(Long.parseLong(textosplit_aux[0]));
                obj = objAluno;
                break;

            case "tb_peso":
                Peso objPeso = new Peso();
                objPeso = (Peso)objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objPeso.setId_peso(Long.parseLong(textosplit_aux[0]));
                obj = objPeso;
                break;

            case "tb_corporal":
                CorporalModel objCorporal = new CorporalModel();
                objCorporal = (CorporalModel)objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objCorporal.setId_corporal(Long.parseLong(textosplit_aux[0]));
                obj = objCorporal;
                break;

            case "tb_corrida":
                Corrida objCorrida = new Corrida();
                objCorrida = (Corrida)objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objCorrida.setId_corrida(Long.parseLong(textosplit_aux[0]));
                obj = objCorrida;
                break;

            case "tb_vo2":
                Vo2Model objVo2 = new Vo2Model();
                objVo2 = (Vo2Model)objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objVo2.setId_vo2(Long.parseLong(textosplit_aux[0]));
                obj = objVo2;
                break;

            case "tb_taxaMetabolica":
                TaxaModel objTaxa = new TaxaModel();
                objTaxa = (TaxaModel) objPost;
                textosplit = json.split(":");
                textosplit_aux= textosplit[1].split(",");
                objTaxa.setId_taxa(Long.parseLong(textosplit_aux[0]));
                obj = objTaxa;
                break;

            default:
                break;
        }

        return obj;
    }

    public String extractMonthOfday()
    {
        String currentTime = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());

        switch (currentTime)
        {
            case "01":
                currentTime = "JANEIRO";
                break;
            case "02":
                currentTime = "FEVEREIRO";
                break;
            case "03":
                currentTime = "MARCO";
                break;
            case "04":
                currentTime = "ABRIL";
                break;
            case "05":
                currentTime = "MAIO";
                break;
            case "06":
                currentTime = "JUNHO";
                break;
            case "07":
                currentTime = "JULHO";
                break;
            case "08":
                currentTime = "AGOSTO";
                break;
            case "09":
                currentTime = "SETEMBRO";
                break;
            case "10":
                currentTime = "OUTUBRO";
                break;
            case "11":
                currentTime = "NOVEMBRO";
                break;
            case "12":
                currentTime = "DEZEMBRO";
                break;
            default:
                break;
        }
        return currentTime;
    }

    public String getSHA256(String str)throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        String senhahex = "null";

        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        byte messageDigest[] = algorithm.digest(str.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            hexString.append(String.format("%02X", 0xFF & b)); //evitar números negativos
        }
        senhahex = hexString.toString();

        return senhahex;
    }

    public boolean verifySHA256(String strSHA256, String str_comp)throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        String senhaHexComp = getSHA256(str_comp);
        return senhaHexComp.equals(strSHA256);
    }
}
