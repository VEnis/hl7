package io.github.venis.hl7.connection;

import io.github.venis.hl7.connection.transport.Base;
import org.inferred.freebuilder.FreeBuilder;

import java.nio.charset.Charset;

@FreeBuilder
public interface Connection {

    Base transport();

    Encoding encoding();

    Charset charset();

    Builder toBuilder();

    class Builder extends Connection_Builder {
    }
}
