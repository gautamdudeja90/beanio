package org.beanio.types;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

public abstract class GeneralDateTypeSupport extends LocaleSupport implements ConfigurableTypeHandler, Cloneable {

    // the same format instance can be reused if this type handler is not shared
    // by multiple unmarshallers/marshallers, this can lead to significant
    // performance improvements when parsing many records
    protected transient DateFormat format;

    protected String pattern = null;
    protected boolean lenient = false;
    protected TimeZone timeZone = null;

    public GeneralDateTypeSupport() {}

    public GeneralDateTypeSupport(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns the date pattern used by the <tt>SimpleDateFormat</tt>.
     * @return the date pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the date pattern used by the <tt>SimpleDateFormat</tt>.
     * @param pattern the date pattern
     * @throws IllegalArgumentException if the date pattern is invalid
     */
    public void setPattern(String pattern) throws IllegalArgumentException {
        // validate the pattern
        try {
            if (pattern != null) {
                new SimpleDateFormat(pattern);
            }
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid date format pattern '" + pattern + "': " + ex.getMessage());
        }

        this.pattern = pattern;
    }

    /**
     * Sets the time zone for interpreting dates.  If not set, the system default
     * time zone is used.
     * @param name the time zone ID
     * @see TimeZone
     */
    public void setTimeZoneId(String name) {
        if (name == null || "".equals(name)) {
            timeZone = null;
        }
        else {
            timeZone = TimeZone.getTimeZone(name);
        }
    }

    /**
     * Returns the time zone used to interpret dates, or <tt>null</tt> if the default
     * time zone will be used.
     * @return the time zone ID
     * @see TimeZone
     */
    public String getTimeZoneId() {
        return timeZone == null ? null : timeZone.getID();
    }

    /**
     * Returns the configured {@link TimeZone} or null if not set.
     * @return the {@link TimeZone}
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * Returns whether the <tt>SimpleDateFormat</tt> is lenient.
     * @return <tt>true</tt> if lenient, <tt>false</tt> otherwise
     */
    public boolean isLenient() {
        return lenient;
    }

    /**
     * Sets whether the <tt>SimpleDateFormat</tt> is lenient.
     * @param lenient <tt>true</tt> if lenient, <tt>false</tt> otherwise
     */
    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    /**
     * Creates a default date format when no pattern is set.
     * @return the default date format
     */
    protected DateFormat createDefaultDateFormat() {
        return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
    }

    protected DateFormat getFormat() {
        return this.format != null ? this.format : createDateFormat();
    }

    /**
     * Creates the <tt>DateFormat</tt> to use to parse and format the field value.
     * @return the <tt>DateFormat</tt> for type conversion
     */
    protected DateFormat createDateFormat() {
        if (pattern == null) {
            return createDefaultDateFormat();
        } else {
            DateFormat df = new SimpleDateFormat(pattern, locale);
            df.setLenient(lenient);
            if (timeZone != null) {
                df.setTimeZone(timeZone);
            }
            return df;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.types.AbstractDateTypeHandler#newInstance(java.util.Properties)
     */
    public GeneralDateTypeSupport newInstance(Properties properties) throws IllegalArgumentException {
        String pattern = properties.getProperty(FORMAT_SETTING);
        if (pattern == null || "".equals(pattern)) {
            return this;
        }

        if (pattern.equals(getPattern())) {
            return this;
        }

        try {
            GeneralDateTypeSupport handler = (GeneralDateTypeSupport) this.clone();
            handler.setPattern(pattern);
            handler.lenient = this.lenient;
            handler.timeZone = getTimeZone();
            handler.format = handler.createDateFormat();
            return handler;
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
}

