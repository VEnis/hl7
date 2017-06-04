package io.github.venis.hl7.connection.transport;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface SinglePortWithHostMllp extends SinglePortMllp {

    String host();

    class Builder extends SinglePortWithHostMllp_Builder {
    }
}
