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

import ca.uhn.hl7v2.HL7Exception;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.venis.hl7.model.Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER;
import static io.github.venis.hl7.model.Constants.DEFAULT_REPETITION_NUMBER;

/**
 * Class that represents hl7 message segment
 */
@Slf4j
@SuppressWarnings("PMD.OnlyOneReturn")
public class Segment implements Serializable {
    private static final long serialVersionUID = 4225314827658228610L;

    /**
     * Hapi segment to wrap
     */
    @NonNull
    private final ca.uhn.hl7v2.model.Segment hapiSegment;

    /**
     * Creates new segment for passed hapi segment
     *
     * @param hapiSegment Hapi segment to wrap
     */
    public Segment(final ca.uhn.hl7v2.model.Segment hapiSegment) {
        this.hapiSegment = hapiSegment;
    }

    /**
     * Return all repeatable fields with given number
     *
     * @param fieldNumber Field number to return
     * @return List of fields
     */
    public List<Field> getAllFields(final int fieldNumber) {
        try {
            return Arrays.stream(hapiSegment.getField(fieldNumber))
                         .map(Field::new)
                         .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No fields was found for given field number", e);
            return new ArrayList<>();
        }
    }

    /**
     * Returns field by it's number
     *
     * @param fieldNumber Field to return
     * @return Field
     */
    public Field getField(final int fieldNumber) {
        return getField(fieldNumber, DEFAULT_REPETITION_NUMBER);
    }

    /**
     * Returns field by it's number and repetition number
     *
     * @param fieldNumber Field number
     * @param repetition  Repetition number
     * @return Field
     */
    public Field getField(final int fieldNumber, final int repetition) {
        try {
            return new Field(hapiSegment.getField(fieldNumber, repetition));
        } catch (HL7Exception e) {
            log.debug("Field with given number and repetition was not found", e);
            return null;
        }
    }

    /**
     * Returns field from list of the repeatable fields which component and sub-component value equals to the given one
     *
     * @param fieldNumber  Field number to check
     * @param component    Component to check
     * @param subComponent Sub-component to check
     * @param value        Value to compare
     * @return Field
     */
    public Field getConditionalField(final int fieldNumber, final int component, final int subComponent, final String value) {
        return getAllFields(fieldNumber).stream()
                                        .filter(field -> field.getValue(component, subComponent) != null)
                                        .filter(field -> field.getValue(component, subComponent).equals(value))
                                        .findFirst().orElse(null);
    }

    /**
     * Returns field from list of the repeatable fields which component default sub-component value equals to the given
     * one
     *
     * @param fieldNumber Field number to check
     * @param component   Component to check
     * @param value       Value to compare
     * @return Field
     */
    public Field getConditionalField(final int fieldNumber, final int component, final String value) {
        return getConditionalField(fieldNumber, component, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, value);
    }
}
