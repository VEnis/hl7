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

import ca.uhn.hl7v2.Version
import ca.uhn.hl7v2.parser.PipeParser
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification

class MessageTest extends Specification {
    @Shared
    private ca.uhn.hl7v2.model.Message testHapiMessage = PipeParser.instanceWithNoValidation.parse(
            "MSH|^~\\&|GE_VM|HHC|CLOVERLEAF||201407151530||ADT^A04|63383_394_AO|T|2.4||||||ASCII\r" +
                    "PID|1||007000528||GECBTONORTH^TESTFIVE||||||||(111)222-3333^PRN~(444)555-6666^CPN||ENG~GER\r" +
                    "NK1|1|TESTFIVE^CONTACT|||(111)222-3333^PRN~(444)555-6666^CPN||EMCON\r" +
                    "NK1|2|MCGARVEY'S SALOON|||(111)222-3333^PRN~(444)555-6666^CPN||PTEMP\r" +
                    "IN1|1|C68|GEICO AUTO INS\r" +
                    "IN1|2|C99|COMMERCIAL INS MISC\r" +
                    "IN1|3||||||||||||||||S|||||3\r")

    def "I can get internal hapi message"() {
        expect:
            assert message.getHapiMessage() == expectedHapiMessage
        where:
            expectedHapiMessage | message
            testHapiMessage     | new Message(testHapiMessage)
    }

    def "I can get value from message using terser expression"() {
        expect:
            message.getByTerser(terserExpression) == expectedResult
        where:
            terserExpression | expectedResult
            "/PID-3"         | "007000528"
            "/PID-5-2"       | "TESTFIVE"
            "/NK1(1)-2"      | "MCGARVEY'S SALOON"
            "/ABC"           | null

            message = new Message(testHapiMessage)
    }

    def "I can set new value to the message using terser expression"() {
        expect:
            message.setByTerser(terserExpression, newValue)
            message.getByTerser(terserExpression) == newValue
        where:
            terserExpression | newValue
            "/PID-2"         | "ABC"
            "/NK1(1)-2"      | "JOHN"

            message = new Message(testHapiMessage)
    }

    def "I cannot set new value to the message using invalid terser expression"() {
        when:
            message.setByTerser(invalidTerserExpression, newValue)
        then:
            thrown(IllegalArgumentException)
        where:
            invalidTerserExpression | newValue
            "/ABC"                  | "ABC"

            message = new Message(testHapiMessage)
    }

    def "I can create message with given type, version and processing id"() {
        expect:
            Message message = new Message(type, version, processingId)
            assert message.getHapiMessage().getVersion() == version
            assert message.getSegment("MSH").getField(9).getValue(1) + "_" + message.getSegment("MSH").getField(9).getValue(2) == type
            assert message.getSegment("MSH").getField(11).getValue(1)
        where:
            version | type      | processingId
            "2.2"   | "ADT_A01" | "P"
            "2.3"   | "ADT_A02" | "T"
            "2.4"   | "ORU_R01" | "W"
            "2.3"   | "SIU_S12" | "R"
    }

    def "I can create message with given type, version and processing id passing version as HAPI enumeration"() {
        expect:
            Message message = new Message(type, version, processingId)
            assert message.getHapiMessage().getVersion() == expectedVersion
        where:
            version     | type      | processingId || expectedVersion
            Version.V22 | "ADT_A01" | "P"          || "2.2"
            Version.V23 | "ADT_A02" | "T"          || "2.3"
            Version.V24 | "ORU_R01" | "W"          || "2.4"
            Version.V25 | "SIU_S12" | "R"          || "2.5"
    }

    def "I can create message with given type and version leaving processing id to it's default value"() {
        expect:
            Message message = new Message(type, version)
            assert message.getSegment("MSH").getField(11).getValue() == defaultProcessingId
        where:
            version     | type      | defaultProcessingId
            Version.V22 | "ADT_A01" | Constants.DEFAULT_PROCESSING_ID
            Version.V23 | "ADT_A02" | Constants.DEFAULT_PROCESSING_ID
            Version.V24 | "ORU_R01" | Constants.DEFAULT_PROCESSING_ID
            Version.V25 | "SIU_S12" | Constants.DEFAULT_PROCESSING_ID
    }

    def "I can serialize and deserialize object and get value from hl7 message using terser expression"() {
        given:
            def serializedMessage = SerializationUtils.serialize(message)
            Message deserializedMessage = SerializationUtils.deserialize(serializedMessage)
        expect:
            deserializedMessage.getByTerser(terserExpression) == expectedResult
        where:
            terserExpression | expectedResult
            "/PID-3"         | "007000528"
            "/PID-5-2"       | "TESTFIVE"

            message = new Message(testHapiMessage)
    }

    def "Message with not supported type will be created as generic message"() {
        expect:
            def message = new Message("abc_xyz", "2.3")
            assert message.getHapiMessage().getClass() == expectedMessageClass
            assert message.getSegment("MSH").getField(9).getValue(1) + "_" + message.getSegment("MSH").getField(9).getValue(2) == invalidType

        where:
            invalidType | expectedMessageClass
            "abc_xyz"   | ca.uhn.hl7v2.model.GenericMessage.V23
    }

    def "Message with invalid type will not be created and exception will be thrown"() {
        when:
            new Message("abc_xyz_def", "2.3")
        then:
            thrown(IllegalArgumentException)
    }

    def "Message with invalid version will not be created and exception will be thrown"() {
        when:
            new Message("ADT_A01", "2.0")
        then:
            thrown(IllegalArgumentException)
    }

}
