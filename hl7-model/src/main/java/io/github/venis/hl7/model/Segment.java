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
