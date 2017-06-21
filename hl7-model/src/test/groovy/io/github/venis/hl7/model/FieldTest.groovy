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
package io.github.venis.hl7.model

import ca.uhn.hl7v2.model.v23.datatype.DT
import ca.uhn.hl7v2.model.v23.message.SIU_S12
import ca.uhn.hl7v2.validation.impl.DefaultValidation
import spock.lang.Shared
import spock.lang.Specification

class FieldTest extends Specification {
    public static final String VALID_FIELD_VALUE = "20170620"
    public static final String INVALID_FIELD_VALUE = "MyVal"

    @Shared
    private Field field

    void setup() {
        def message = new SIU_S12()
        message.setValidationContext(new DefaultValidation())
        field = new Field(new DT(message))
    }

    def "Field value can be set and retrieved for default component and subcomponent"() {
        when:
            field.setValue(VALID_FIELD_VALUE)
        then:
            field.getValue() == VALID_FIELD_VALUE
    }

    def "Field value can be set and retrieved for default component"() {
        when:
            field.setValue Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, VALID_FIELD_VALUE
        then:
            field.getValue(Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER) == VALID_FIELD_VALUE
    }

    def "Field value can be set and retrieved"() {
        when:
            field.setValue Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, VALID_FIELD_VALUE
        then:
            field.getValue(Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER) == VALID_FIELD_VALUE
    }

    def "IllegalArgumentException will be thrown if trying to set invalid value"() {
        when:
            field.setValue(INVALID_FIELD_VALUE)
        then:
            thrown(IllegalArgumentException)
    }

    def "NullPointerException will be thrown if passed hapi type is null"() {
        when:
            new Field(null)
        then:
            thrown(NullPointerException)
    }
}