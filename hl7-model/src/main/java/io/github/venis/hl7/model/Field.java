/*
 * MIT License
 *
 * Copyright (c) 2017 Vyacheslav Enis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.venis.hl7.model;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.util.Terser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import static io.github.venis.hl7.model.Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER;

/**
 * Class that represents single hl7 field (wraps hapi Type)
 *
 * @see Type
 */
@RequiredArgsConstructor
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class Field implements Serializable {
    private static final long serialVersionUID = -6247885277540978188L;

    /**
     * Hapi type to wrap
     */
    @NonNull
    private final Type hapiType;

    /**
     * Returns field value for default component and sub-component
     *
     * @return Field value
     */
    public String getValue() {
        return getValue(DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER);
    }

    /**
     * Returns field value for passed component and default sub-component
     *
     * @param component Component number to get value for
     * @return Field value
     */
    public String getValue(final int component) {
        return getValue(component, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER);
    }

    /**
     * Returns field value for passed component and sub-component
     *
     * @param component    Component number to get value for
     * @param subComponent Sub-component number to get value for
     * @return Field value
     */
    public String getValue(final int component, final int subComponent) {
        return Terser.getPrimitive(hapiType, component, subComponent).getValue();
    }

    /**
     * Sets new field value for default component and sub-component
     *
     * @param newValue New field value
     */
    public void setValue(final String newValue) {
        setValue(DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, newValue);
    }

    /**
     * Sets new field value passed component and default sub-component
     *
     * @param component Component number to set value for
     * @param newValue  New field value
     */
    public void setValue(final int component, final String newValue) {
        setValue(component, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, newValue);
    }

    /**
     * Sets new field value for passed component and sub-component
     *
     * @param component    Component number to set value for
     * @param subComponent Sub-component number to set value for
     * @param newValue     New field value
     */
    public void setValue(final int component, final int subComponent, final String newValue) {
        try {
            Terser.getPrimitive(hapiType, component, subComponent).setValue(newValue);
        } catch (DataTypeException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
