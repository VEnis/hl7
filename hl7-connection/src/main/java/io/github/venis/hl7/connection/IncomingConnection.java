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

    private HL7Service service;

    abstract Builder toBuilder();

    abstract Map<AppRoutingDataImpl, ReceivingApplication> applications();

    abstract List<ConnectionListener> connectionListeners();

    @Override
    public void start() {
        if (null != service) {
            throw new IllegalStateException("Connection already started");
        }
        service = initService();
        service.start();
    }

    @Override
    public void startAndWait() {
        if (null != service) {
            throw new IllegalStateException("Connection already started");
        }
        service = initService();
        try {
            service.startAndWait();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        if (null == service) {
            throw new IllegalStateException("Connection is not started");
        }
        service.stop();
        service = null;
    }

    @Override
    public boolean isRunning() {
        return null != service;
    }

    private HL7Service initService() {
        HL7Service service = transport().createServer(initHapiContext());
        connectionListeners().forEach(service::registerConnectionListener);
        applications().forEach(service::registerApplication);
        return service;
    }

    public static class Builder extends IncomingConnection_Builder {
    }
}
