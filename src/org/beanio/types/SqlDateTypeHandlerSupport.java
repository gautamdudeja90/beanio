package org.beanio.types;

import java.sql.Date;
import java.util.Properties;

public abstract class SqlDateTypeHandlerSupport extends GeneralDateTypeSupport {

    protected Date parseDate(String text) throws TypeConversionException {
        if ("".equals(text))
            return null;

        /*ParsePosition pp = new ParsePosition(0);
        Date date = getFormat().parse(text, pp);
        if (pp.getErrorIndex() >= 0 || pp.getIndex() != text.length()) {
            throw new TypeConversionException("Invalid date");
        }*/

        return null;
    }

    public SqlDateTypeHandlerSupport newInstance(Properties properties) throws IllegalArgumentException {
        String pattern = properties.getProperty(FORMAT_SETTING);
        if (pattern == null || "".equals(pattern)) {
            return this;
        }
        if (pattern.equals(getPattern())) {
            return this;
        }

        try {
            SqlDateTypeHandlerSupport handler = (SqlDateTypeHandlerSupport) this.clone();
            handler.setPattern(pattern);
            handler.lenient = this.lenient;
            handler.timeZone = this.timeZone;
            //handler.format = handler.createDateFormat(); TODO
            return handler;
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Converts a {@link Date} to text.
     * @param date the {@link Date} to convert
     * @return the formatted text
     */
    protected String formatDate(Date date) {
        return date == null ? null : getFormat().format(date);
    }
}
