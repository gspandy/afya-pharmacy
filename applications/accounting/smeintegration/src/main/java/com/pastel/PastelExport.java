package com.pastel;

import com.google.common.base.Preconditions;
import com.nthdimenzion.common.Initialise;
import com.pastel.service.PastelExportService;

import java.io.IOException;
import java.util.Date;

/**
 * Author: Nthdimenzion
 */

public final class PastelExport {

    public static String Export(Date transactionsExportedUptoDate) throws IOException {
        //System.out.println("Export......");
        Preconditions.checkNotNull(transactionsExportedUptoDate);
        PastelExportService pastelExportService = Initialise.ApplicationContext.getBean("pastelExportService",PastelExportService.class);
        final Date startDate = pastelExportService.findFinancialYearStartDate(transactionsExportedUptoDate);
/*
        final Date startDate = new Date(114, 9, 31);
        transactionsExportedUptoDate = new Date(114, 11,31);
*/
        final String outputFolderPath = pastelExportService.export(startDate, transactionsExportedUptoDate);
        pastelExportService.createLogForUnmatchedGlAccounts(transactionsExportedUptoDate, outputFolderPath);
        return outputFolderPath;
    }

}
