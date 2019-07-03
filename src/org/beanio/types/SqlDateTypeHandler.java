package org.beanio.types;

import java.sql.Date;

public class SqlDateTypeHandler  extends SqlDateTypeHandlerSupport {

    public Object parse(String text) throws TypeConversionException {
        return super.parseDate(text);
    }

    public String format(Object value) {
        return super.formatDate((Date) value);
    }

    public Class<?> getType() {
        return Date.class;
    }
}
