/*
 * MIT License
 *
 * Copyright (c) 2017 Vyacheslav Enis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
