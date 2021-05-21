package helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertHelper {
    private static SimpleDateFormat formater;
    public static String convertJavaDateToSqlDateTime(Date date) {
        if(date == null) {
            return null;
        }
        formater =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formater.format(date);

    }

    public static String convertJavaDateToSqlDate(Date date) {
        if(date == null) {
            return null;
        }
        formater =  new SimpleDateFormat("yyyy-MM-dd");
        return formater.format(date);
    }

    public static Date convertSqlDateToJavaDate(java.sql.Date sqlDate) {
        if(sqlDate == null) {
            return null;
        }
        return new Date(sqlDate.getTime());
    }
    public static Date convertSqlTimeStampToJavaDate(java.sql.Timestamp sqlTime) {
        if(sqlTime == null) {
            return null;
        }
        return new Date(sqlTime.getTime());
    }
}
