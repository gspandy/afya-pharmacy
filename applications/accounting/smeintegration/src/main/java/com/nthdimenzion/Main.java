package com.nthdimenzion;

import com.pastel.PastelExport;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: Nthdimenzion
 */

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello Main");
        //final Date date = new Date(114, 10, 26);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 3, 31);
        PastelExport.Export(calendar.getTime());
    }
}
