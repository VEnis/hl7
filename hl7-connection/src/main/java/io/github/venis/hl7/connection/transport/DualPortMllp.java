package io.github.venis.hl7.connection.transport;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface DualPortMllp extends Base {
    String host();

    int inboundPort();

    int outboundPort();

    Builder toBuilder();

    class Builder extends DualPortMllp_Builder {
    }
}
