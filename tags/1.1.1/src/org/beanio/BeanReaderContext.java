/*
 * Copyright 2010-2011 Kevin Seim
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
package org.beanio;

import java.util.*;

/**
 * When a <tt>BeanReaderException</tt> is thrown, all errors and the current state of the 
 * <tt>BeanReader</tt> can be accessed from the <tt>BeanReaderContext</tt>.  Depending on the type
 * of exception, some information may be missing.
 * 
 * @author Kevin Seim
 * @since 1.0
 * @see BeanReaderException
 */
public interface BeanReaderContext {

    /**
     * Returns the line number of the failed record, or 0 if the stream does not use
     * new lines to terminate records.
     * @return the line number of the failed record
     */
    public int getRecordLineNumber();

    /**
     * Returns the raw text of the record being parsed, or <tt>null</tt> if not supported
     * by the input stream format (such as XML).
     * @return the raw text of the record
     */
    public String getRecordText();

    /**
     * Returns the name of the record from the stream configuration.  The record name
     * may be null if was not determined before the exception occurred.
     * @return the name of the record from the stream configuration
     */
    public String getRecordName();

    /**
     * Returns <tt>true</tt> if there are one or more record level errors.
     * @return <tt>true</tt> if there are one or more record level errors
     */
    public boolean hasRecordErrors();

    /**
     * Returns the collection of record level error messages.
     * @return the collection of record level error messages
     */
    public Collection<String> getRecordErrors();

    /**
     * Returns the unparsed text of a field from the record.  Field text may be null
     * under the following circumstances:
     * <ul>
     * <li>A record level exception was thrown before a field was parsed</li>
     * <li>The field name is invalid</li>
     * <li>The field did not exist in the record</li>
     * </ul>
     * <p>If the field is a collection, this method returns the field text for
     * the first occurrence of the field.</p>
     * @param fieldName the name of the field to get the text for
     * @return the unparsed field text
     */
    public String getFieldText(String fieldName);

    /**
     * Returns the unparsed text of a field from the record.  Field text may be null
     * under the following circumstances:
     * <ul>
     * <li>A record level exception was thrown before a field was parsed</li>
     * <li>The field name is invalid</li>
     * <li>The field did not exist in the record</li>
     * </ul>
     * @param fieldName the name of the field to get the text for
     * @param index the index of the field, beginning at 0, for collection type
     *   fields
     * @return the unparsed field text
     */
    public String getFieldText(String fieldName, int index);
    
    /**
     * Returns <tt>true</tt> if there are one or more field level errors.
     * @return <tt>true</tt> if there are one or more field level errors.
     */
    public boolean hasFieldErrors();

    /**
     * Returns a Map of all field errors, where the Map key is the field name.
     * @return a Map of all field errors
     */
    public Map<String, Collection<String>> getFieldErrors();

    /**
     * Returns the field errors for a given field.
     * @param fieldName the name of the field
     * @return the collection of field errors for the named field
     */
    public Collection<String> getFieldErrors(String fieldName);

}