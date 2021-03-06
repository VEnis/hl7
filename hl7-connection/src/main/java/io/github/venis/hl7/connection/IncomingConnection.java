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

    public abstract Builder toBuilder();

    public abstract Map<AppRoutingDataImpl, ReceivingApplication> applications();

    public abstract List<ConnectionListener> connectionListeners();

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
        HL7Service hl7Service = transport().createServer(initHapiContext());
        connectionListeners().forEach(hl7Service::registerConnectionListener);
        applications().forEach(hl7Service::registerApplication);
        return hl7Service;
    }

    public static class Builder extends IncomingConnection_Builder {
    }
}
