package io.github.venis.hl7.connection.transport;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface DualPortMllp extends Base {
    int inboundPort();

    int outboundPort();

    @Override
    default HL7Service createServer(HapiContext hapiContext) {
        return hapiContext.newServer(inboundPort(), outboundPort(), false);
    }

    class Builder extends DualPortMllp_Builder {
    }
}
