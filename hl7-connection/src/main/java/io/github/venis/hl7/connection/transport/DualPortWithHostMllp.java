package io.github.venis.hl7.connection.transport;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface DualPortWithHostMllp extends DualPortMllp {

    String host();

    class Builder extends DualPortWithHostMllp_Builder {
    }
}
