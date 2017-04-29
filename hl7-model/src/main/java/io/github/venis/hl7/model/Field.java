package io.github.venis.hl7.model;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.util.Terser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import static io.github.venis.hl7.model.Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER;

@RequiredArgsConstructor
public class Field implements Serializable {
    @NonNull
    private Type hapiType;

    public String getValue() {
        return getValue(DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER);
    }

    public String getValue(int componentNumber) {
        return getValue(componentNumber, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER);
    }

    public String getValue(int componentNumber, int subComponentNumber) {
        return Terser.getPrimitive(hapiType, componentNumber, subComponentNumber).getValue();
    }

    public void setValue(String newValue) {
        setValue(DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, newValue);
    }

    public void setValue(int componentNumber, String newValue) {
        setValue(componentNumber, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, newValue);
    }

    public void setValue(int componentNumber, int subComponentNumber, String newValue) {
        try {
            Terser.getPrimitive(hapiType, componentNumber, subComponentNumber).setValue(newValue);
        } catch (DataTypeException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
