package helper;

import entity.Category;
import service.CategoryService;

import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDate;

public class Test {
    public static void main(String[] args) {
        double d = 5.769351;
        String P1 = NumberFormat.getPercentInstance().format(d);
        System.out.println(P1);
    }
}
