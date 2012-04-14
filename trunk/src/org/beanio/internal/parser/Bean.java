/*
 * Copyright 2011-2012 Kevin Seim
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
package org.beanio.internal.parser;

import java.lang.reflect.Constructor;
import java.util.*;

import org.beanio.*;

/**
 * A component used to aggregate {@link Property}'s into a bean object, which
 * may also be a property of a parent bean object itself. 
 * 
 * <p>A bean may only have children that implement {@link Property}.</p>
 * 
 * @author Kevin Seim
 * @since 2.0
 */
public class Bean extends PropertyComponent implements Property {

    // the bean object
    private ParserLocal<Object> bean = new ParserLocal<Object>() {
        public Object createDefaultValue() {
            return isRequired() ? null : Value.MISSING;
        }
    };
    // the constructor for creating this bean object (if null, the no-arg constructor is used)
    private Constructor<?> constructor;
    // used to temporarily hold constructor argument values when a constructor is specified
    // TODO remove this instance variable
    private Object[] constructorArgs;
    
    /**
     * Constructs a new <tt>Bean</tt>.
     */
    public Bean() { }
       
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#clearValue()
     */
    public void clearValue(ParsingContext context) {
        for (Component child : getChildren()) {
            ((Property) child).clearValue(context);
        }
        bean.set(context, isRequired() ? null : Value.MISSING);
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#defines(java.lang.Object)
     */
    public boolean defines(Object bean) {    
        if (bean == null || getType() == null) {
            return false;
        }
        
        if (!getType().isAssignableFrom(bean.getClass())) {
            return false;
        }
        
        if (!isIdentifier()) {
            return true;
        }
        
        // check identifying properties
        for (Component child : getChildren()) {
            Property property = (Property) child;
            
            // if the child property is not used to identify records, no need to go further
            if (!property.isIdentifier()) {
                continue;
            }
            
            Object value = property.getAccessor().getValue(bean);
            if (!property.defines(value)) {
                return false;
            }
        }
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser.Property#createValue()
     */
    public Object createValue(ParsingContext context) {
        Object b = null;
        
        // populate constructor arguments first
        if (constructor != null) {
            // lazily create...
            boolean create = false;
            
            for (Component child : getChildren()) {
                Property property = (Property) child;
                
                PropertyAccessor accessor = property.getAccessor();
                if (accessor == null) {
                    throw new IllegalStateException("Accessor not set for property value '" + child.getName() + "'");
                }
                if (!accessor.isConstructorArgument()) {
                    continue;
                }
                
                Object value = property.createValue(context);
                if (value == Value.INVALID) {
                    return Value.INVALID;
                }
                else if (value == Value.MISSING) {
                    value = null;
                }
                else {
                    create = true;
                }
                
                constructorArgs[accessor.getConstructorArgumentIndex()] = value;
            }
            
            if (create) {
                b = newInstance();
            }
        }
        
        
        for (Component child : getChildren()) {
            Property property = (Property) child;
            if (property.getAccessor().isConstructorArgument()) {
                continue;
            }
            
            Object value = property.createValue(context);
            if (value == Value.INVALID) {
                bean.set(context, b);
                return Value.INVALID;
            }
            // explicitly null values must still be set on the bean...
            else if (value != Value.MISSING) {
                if (b == null) {
                    b = newInstance();
                }

                try {
                    property.getAccessor().setValue(b, value);
                }
                catch (Exception ex) {
                    throw new BeanIOException("Failed to set property '" + property.getName() + 
                        "' on bean '" + getName() + "'", ex);
                }
            }
        }

        if (b == null) {
            b = isRequired() ? newInstance() : Value.MISSING;
        }
        
        bean.set(context, b);
        return b;
    }
    
    /*
     * (non-Javadoc)
     * @see org.beanio.parser2.Property#getValue()
     */
    public Object getValue(ParsingContext context) {
        return bean.get(context);
    }

    /*
     * Sets the bean object and populates all of its child properties.
     * 
     */
    public void setValue(ParsingContext context, Object value) {
        if (value == null) {
            clearValue(context);
            return;
        }
        
        Object b = value;
        Object defaultValue = null; //bean == null ? Value.MISSING : null;

        for (Component child : getChildren()) {
            Property property = (Property) child;
            
            Object propertyValue = defaultValue;
            if (b != null) {
                propertyValue = property.getAccessor().getValue(b);
            }

            property.setValue(context, propertyValue);
        }
        
        bean.set(context, b);
    }
    
    /**
     * Creates a new instance of this bean object.
     * @return the new bean <tt>Object</tt>
     */
    protected Object newInstance() {
        // if the bean class is null, the record will be ignored and null is returned here
        Class<?> beanClass = getType();
        if (beanClass == null) {
            return null;
        }
        
        try {
            if (constructor == null) {
                return beanClass.newInstance();
            }
            else {
                return constructor.newInstance(constructorArgs);
            }
        }
        catch (Exception e) {
            throw new BeanReaderException("Failed to instantiate class '" + beanClass.getName() + "'", e);
        }
    }

    @Override
    protected boolean isSupportedChild(Component child) {
        return child instanceof Property;
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequired(required);
    }

    /*
     * (non-Javadoc)
     * @see org.beanio.internal.parser.Property#type()
     */
    public int type() {
        return (isMap()) ? Property.MAP : Property.COMPLEX;
    }
    
    /**
     * Returns whether the bean object implements {@link Map}.
     * @return true if the bean object implements {@link Map}, false otherwise
     */
    protected boolean isMap() {
        return Map.class.isAssignableFrom(getType());
    }
    
    /**
     * Returns the {@link Constructor} used to instantiate this bean object, or null
     * if the default no-arg constructor is used.
     * @return the {@link Constructor}
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * Sets the {@link Constructor} used to instantiate this bean object.
     * @param constructor the {@link Constructor}
     */
    public void setConstructor(Constructor<?> constructor) {
        this.constructorArgs = constructor == null ? null : new Object[constructor.getParameterTypes().length];
        this.constructor = constructor;
    }
    
    @Override
    public void registerLocals(Set<ParserLocal<?>> locals) {
        if (locals.add(bean)) {
            super.registerLocals(locals);
        }
    }
    
    @Override
    public Bean clone() {
        Bean clone = (Bean) super.clone();
        if (clone.constructorArgs != null) {
            clone.constructorArgs = (Object[]) constructorArgs.clone();
        }
        return clone;
    }
}
