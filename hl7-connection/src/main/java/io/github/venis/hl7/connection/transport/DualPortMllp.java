package io.github.venis.hl7.connection.transport;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface DualPortMllp extends Base {
    String host();

    int inboundPort();

    int outboundPort();

    Builder toBuilder();

    @Override
    default HL7Service createServer(HapiContext hapiContext) {
        return hapiContext.newServer(inboundPort(), outboundPort(), false);
    }

    class Builder extends DualPortMllp_Builder {
        public Builder() {
            host("0.0.0.0");
        }
    }
}
