package org.beanio.types.util;

import java.sql.Date;

public class DateFormatUtil {

    /**
     * Converts java.util.Date input to java.sql.Date
     * @param utilDate
     * @return
     */
    public static Date convertToSQLDate(java.util.Date utilDate) {
        Date sqlStartDate = new java.sql.Date(utilDate.getTime());
        return sqlStartDate;
    }
}
