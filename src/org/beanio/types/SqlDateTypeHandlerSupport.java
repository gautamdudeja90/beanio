/*
 * Copyright 2019 Juan Ramos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beanio.types;

import org.beanio.types.util.DateFormatUtil;

import java.sql.Date;
import java.text.ParsePosition;
import java.util.Properties;

/**
 * This type handler uses a <tt>SimpleDateFormat</tt> class to parse and format
 * <tt>java.sql.Date</tt> objects.  If no pattern is set, <tt>DateFormat.getInstance()</tt>
 * is used to create a default date format.  By default, <tt>lenient</tt> is false.
 *
 * @author Juan Ramos
 * @since 2.2.0
 */
public abstract class SqlDateTypeHandlerSupport extends GeneralDateTypeSupport {

    protected Date parseDate(String text) throws TypeConversionException {
        if ("".equals(text))
            return null;

        ParsePosition pp = new ParsePosition(0);
        java.util.Date date = getFormat().parse(text, pp);
        if (pp.getErrorIndex() >= 0 || pp.getIndex() != text.length()) {
            throw new TypeConversionException("Invalid date");
        }

        return DateFormatUtil.convertToSQLDate(date);
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
            handler.format = handler.createDateFormat();
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
