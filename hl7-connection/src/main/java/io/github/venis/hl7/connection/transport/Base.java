package io.github.venis.hl7.connection.transport;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;

public interface Base {
    HL7Service createServer(HapiContext hapiContext);
}
