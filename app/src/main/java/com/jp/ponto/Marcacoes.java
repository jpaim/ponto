package com.jp.ponto;

import android.text.style.TtsSpan;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.TimeZone.*;

/**
 * Created by JorgePaim on 08/07/2015.
 */
public class Marcacoes {
    private String data;
    private String hora;
    private int id;

    public Marcacoes(String data, String hora, int id) {
        this.data = data;
        this.hora = hora;
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}


