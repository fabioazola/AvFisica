package com.example.avfisica;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GPS {

    Context mContext;

    public GPS(Context mContext) {
        this.mContext = mContext;
    }

    public LocationManager getLocationManager(){
        //classe LocationManager para obter dados da localizações
         /*Libera os seguintes serviços
        1 - consultar a lista de todos os LocationProvides para o último local conhecido do usuários;
        2 - registrar e desregistrar atualizações periódicas da localização do usuário através de um provider (especificado por
            um critério ou nome);
        3 - registrar e desregistrar um determinado Intent para ser disparado se o dispositivo entrar nas proximidade de certa latitude/longitude
            (especificada pelo raio em metros).
        */
        LocationManager locationManager;

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager;
    }


    public double[] updateWithNewLocation(Location location) {
        double[] latLongDouble = new double[4];

        if (location != null) {
            latLongDouble[0] = location.getLatitude();
            latLongDouble[1] = location.getLongitude();
            latLongDouble[2] = location.getAltitude();
            latLongDouble[3] = location.getSpeed();
        } else {
            latLongDouble[0] = 0;
            latLongDouble[1] = 0;
            latLongDouble[2] = 0;
            latLongDouble[3] = 0;
        }
        return latLongDouble;
    }

}
