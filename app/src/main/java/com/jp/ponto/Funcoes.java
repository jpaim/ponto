package com.jp.ponto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by lab01 on 18/08/2015.
 */
class Funcoes {

    public static String getCurDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new GregorianCalendar().getTime());
    }

    public static String getCurDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new GregorianCalendar().getTime());
    }


    public static int getHour() {
        return new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        return new GregorianCalendar().get(Calendar.MINUTE);
    }

    public static long difDateTime(String dt2,String dt1) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Calendar c1 = GregorianCalendar.getInstance();
        c1.setTime(df.parse(dt1));
        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTime(df.parse(dt2));

        long dif = c2.getTimeInMillis() - c1.getTimeInMillis();

        return dif;
    }

}
