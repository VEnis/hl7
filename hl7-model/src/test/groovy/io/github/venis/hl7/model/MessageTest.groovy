package io.github.venis.hl7.model

import ca.uhn.hl7v2.parser.PipeParser
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

    def "I can get raw Hl7 message"() {
        expect:
            assert message.getHapiMessage() == expectedHapiMessage
        where:
            expectedHapiMessage | message
            testHapiMessage         | new Message(testHapiMessage)
    }
}
