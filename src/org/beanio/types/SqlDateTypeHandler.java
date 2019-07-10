package org.beanio.types;

import java.sql.Date;

/**
 * This type handler uses a <tt>SimpleDateFormat</tt> class to parse and format
 * <tt>java.sql.Date</tt> objects.  If no pattern is set, <tt>DateFormat.getInstance()</tt>
 * is used to create a default date format.  By default, <tt>lenient</tt> is false.
 *
 * @author Juan Ramos
 * @since 2.2.0
 */
public class SqlDateTypeHandler extends SqlDateTypeHandlerSupport {

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#parse(java.lang.String)
     */
    public Object parse(String text) throws TypeConversionException {
        return super.parseDate(text);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#format(java.lang.Object)
     */
    public String format(Object value) {
        return super.formatDate((Date) value);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.TypeHandler#getType()
     */
    public Class<?> getType() {
        return Date.class;
    }
}
