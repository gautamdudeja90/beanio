/*
 * Copyright 2010-2012 Kevin Seim
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
package org.beanio.internal;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.beanio.*;
import org.beanio.internal.compiler.StreamCompiler;
import org.beanio.internal.parser.Stream;

/**
 * The <tt>DefaultStreamFactory</tt> stores configured stream definitions used
 * to create bean readers and writers.  A single factory instance may be accessed 
 * concurrently by multiple threads.
 *  
 * @author Kevin Seim
 * @since 1.0
 */
public class DefaultStreamFactory extends StreamFactory {

    private StreamCompiler compiler;
    private Map<String, Stream> contextMap = new ConcurrentHashMap<String, Stream>();

    /**
     * Constructs a new <tt>DefaultStreamFactory</tt>.
     */
    public DefaultStreamFactory() { }

    @Override
    protected void init() {
        super.init();
        this.compiler = new StreamCompiler(getClassLoader());
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.StreamManager#configure(java.io.InputStream)
     */
    public void load(InputStream in) throws IOException, BeanIOConfigurationException {
        Collection<Stream> streams = compiler.loadMapping(in);
        for (Stream stream : streams) {
            addStream(stream);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.StreamManager#createBeanReader(java.lang.String, java.io.Reader, java.util.Locale)
     */
    public BeanReader createReader(String name, Reader in, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        
        Stream stream = getStream(name);
        switch (stream.getMode()) {
            case Stream.READ_WRITE_MODE:
            case Stream.READ_ONLY_MODE:
                return stream.createBeanReader(in, locale);
            default:
                throw new IllegalArgumentException("Read mode not supported for stream mapping '" + name + "'");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.StreamManager#createBeanWriter(java.lang.String, java.io.Writer)
     */
    public BeanWriter createWriter(String name, Writer out) {
        Stream stream = getStream(name);
        switch (stream.getMode()) {
            case Stream.READ_WRITE_MODE:
            case Stream.WRITE_ONLY_MODE:
                return stream.createBeanWriter(out);
            default:
                throw new IllegalArgumentException("Write mode not supported for stream mapping '" + name + "'");
        }
    }

    /**
     * Returns the named stream.
     * @param name the name of the stream
     * @return the {@link Stream}
     * @throws IllegalArgumentException if there is no stream configured for the given name
     */
    protected Stream getStream(String name) throws IllegalArgumentException {
        Stream s = contextMap.get(name);
        if (s == null) {
            throw new IllegalArgumentException("No stream mapping configured for name '" + name + "'");
        }
        return s;
    }

    /**
     * Adds a stream to this manager.
     * @param stream the {@link Stream} to add
     */
    public void addStream(Stream stream) {
        contextMap.put(stream.getName(), stream);
    }

    /**
     * Removes the named stream from this manager.
     * @param name the name of the stream to remove
     * @return the removed {@link Stream}, or <tt>null</tt> if
     *   the there was no stream for the given name
     */
    public Stream removeStream(String name) {
        return contextMap.remove(name);
    }

    /**
     * Sets the mapping compiler to use for compiling streams.
     * @param compiler the {@link StreamCompiler}
     */
    public void setCompiler(StreamCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public boolean isMapped(String streamName) {
        return contextMap.containsKey(streamName);
    }
}