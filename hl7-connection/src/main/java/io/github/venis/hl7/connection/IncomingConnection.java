package io.github.venis.hl7.connection;

import ca.uhn.hl7v2.app.ConnectionListener;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.impl.AppRoutingDataImpl;
import org.inferred.freebuilder.FreeBuilder;

import java.util.List;
import java.util.Map;

@FreeBuilder
public abstract class IncomingConnection extends AbstractConnection {

    private HL7Service hl7Service;

    abstract Builder toBuilder();

    abstract Map<AppRoutingDataImpl, ReceivingApplication> applications();

    abstract List<ConnectionListener> connectionListeners();

    @Override
    public void start() {
        hl7Service.start();
    }

    @Override
    public void startAndWait() throws InterruptedException {
        hl7Service.startAndWait();
    }

    @Override
    public void stop() {
        hl7Service.stop();
    }

    private void initHl7Service() {
        hl7Service = transport().createServer(initHapiContext());
        connectionListeners().forEach(e -> hl7Service.registerConnectionListener(e));
        applications().forEach((routing, handler) -> hl7Service.registerApplication(routing, handler));
    }

    public static class Builder extends IncomingConnection_Builder {
        @Override
        public IncomingConnection build() {
            IncomingConnection connection = super.build();
            connection.initHl7Service();
            return connection;
        }
    }
}
