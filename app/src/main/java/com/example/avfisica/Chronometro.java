package com.example.avfisica;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.widget.Chronometer;

public class Chronometro {
    Context context;
    Chronometer chronometer;
    long pauseOffset;

    public Chronometro(Context context, Chronometer chronometer) {
        this.chronometer = chronometer;
        this.context = context;
        this.pauseOffset = 0;
    }

    public boolean createChronometer() {
        boolean flag = false;
        try {
            this.chronometer.setBase(SystemClock.elapsedRealtime());
            this.chronometer.setAutoSizeTextTypeUniformWithConfiguration(30, 50, 1, 1);
            this.chronometer.setBackgroundColor(Color.WHITE); // set green color for the background of a chronometer
            this.chronometer.setTextColor(Color.BLACK);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean start() {
        boolean flag = false;
        try {
            this.chronometer.setBase(SystemClock.elapsedRealtime() - this.pauseOffset);
            this.chronometer.start();// The toggle is disabled
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean stop() {
        boolean flag = false;
        try {
            this.chronometer.stop();
            this.pauseOffset = SystemClock.elapsedRealtime() - this.chronometer.getBase();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean reset() {
        boolean flag = false;
        try {
            this.chronometer.setBase(SystemClock.elapsedRealtime());
            this.pauseOffset = 0;
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
