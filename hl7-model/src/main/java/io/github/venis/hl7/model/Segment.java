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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.venis.hl7.model.Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER;
import static io.github.venis.hl7.model.Constants.DEFAULT_REPETITION_NUMBER;

@RequiredArgsConstructor
@Slf4j
public class Segment implements Serializable {
    private static final long serialVersionUID = 4225314827658228610L;
    @NonNull
    private ca.uhn.hl7v2.model.Segment hapiSegment;

    public List<Field> getAllFields(int fieldNumber) {
        try {
            return Arrays.stream(hapiSegment.getField(fieldNumber))
                    .map(Field::new)
                    .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No fields was found for given field number", e);
            return new ArrayList<>();
        }
    }

    public Field getField(int fieldNumber) {
        return getField(fieldNumber, DEFAULT_REPETITION_NUMBER);
    }

    public Field getField(int fieldNumber, int repetition) {
        try {
            return new Field(hapiSegment.getField(fieldNumber, repetition));
        } catch (HL7Exception e) {
            log.debug("Field with given number and repetition was not found", e);
            return null;
        }
    }

    public Field getConditionalField(int fieldNumber, int componentToCheck, int subComponentToCheck, String
            valueToCheck) {
        return getAllFields(fieldNumber).stream()
                .filter(field -> field.getValue(componentToCheck, subComponentToCheck) != null)
                .filter(field -> field.getValue(componentToCheck, subComponentToCheck).equals(valueToCheck))
                .findFirst().orElse(null);
    }

    public Field getConditionalField(int fieldNumber, int componentToCheck, String valueToCheck) {
        return getConditionalField(fieldNumber, componentToCheck, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, valueToCheck);
    }
}
