package io.github.venis.hl7.connection.transport;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface SinglePortMllp extends Base {

    String host();

    int port();

    Builder toBuilder();

    class Builder extends SinglePortMllp_Builder {
    }
}
