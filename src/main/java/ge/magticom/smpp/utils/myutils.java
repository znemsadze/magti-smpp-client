package ge.magticom.smpp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class myutils {

    public static String dateToStrTm(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String sdt = "";
        try {
            sdt = sdf.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdt;
    }


}
