package io.github.venis.hl7.connection.transport;

import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface SinglePortMllp extends Base {

    int port();

    @Override
    default HL7Service createServer(HapiContext hapiContext) {
        return hapiContext.newServer(port(), false);
    }

    class Builder extends SinglePortMllp_Builder {
    }
}
