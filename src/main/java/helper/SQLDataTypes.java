package helper;

import java.util.Locale;

public class SQLDataTypes {
    public static final String INTEGER = "INT";
    public static final String SMALL_INTEGER = "SMALLINT";
    public static final String BIG_INTEGER = "BIGINT";
    public static final String FLOAT = "FLOAT";
    public static final String DOUBLE = "DOUBLE";
    public static final String DATE = "DATE";
    public static final String DATETIME = "DATETIME";
    public static final String TIME_STAMP = "TIMESTAMP";
    public static final String TEXT = "TEXT";

    public static final String VARCHAR255 = "VARCHAR(255)";
    public static final String VARCHAR50 = "VARCHAR(50)";

    //helper method to decide APOSTROPHE or not
    public static boolean needApostrophe(String type) {
        if (type.toLowerCase(Locale.ROOT).contains("varchar") || type.equals(TEXT) || type.equals(DATE) || type.equals(DATETIME) || type.equals(TIME_STAMP)) {
            return true;
        }
        return false;
    }
}
