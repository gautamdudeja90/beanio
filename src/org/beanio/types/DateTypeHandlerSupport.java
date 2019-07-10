/*
 * Copyright 2013 Kevin Seim
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

import java.text.*;
import java.util.*;

/**
 * This abstract type handler uses a <tt>SimpleDateFormat</tt> class to parse and format 
 * <tt>java.util.Date</tt> objects.  If no pattern is set, <tt>DateFormat.getInstance()</tt> 
 * is used to create a default date format.  By default, <tt>lenient</tt> is false.
 * 
 * @author Kevin Seim
 * @since 2.1.0
 * @see Date
 * @see DateFormat
 * @see SimpleDateFormat
 */
public abstract class DateTypeHandlerSupport extends GeneralDateTypeSupport {

    /**
     * Constructs a new AbstractDateTypeHandler.
     */
    public DateTypeHandlerSupport() { }

    /**
     * Constructs a new AbstractDateTypeHandler.
     * @param pattern the {@link SimpleDateFormat} pattern
     */
    public DateTypeHandlerSupport(String pattern) {
        super(pattern);
    }
    
    /**
     * Parses text into a {@link Date}.
     * @param text the text to parse
     * @return the parsed {@link Date}
     * @throws TypeConversionException
     */
    protected Date parseDate(String text) throws TypeConversionException {
        if ("".equals(text))
            return null;

        ParsePosition pp = new ParsePosition(0);
        Date date = getFormat().parse(text, pp);
        if (pp.getErrorIndex() >= 0 || pp.getIndex() != text.length()) {
            throw new TypeConversionException("Invalid date");
        }
        return date;
    }
}
