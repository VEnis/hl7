package io.github.venis.hl7.connection;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.validation.builder.support.NoValidationBuilder;
import io.github.venis.hl7.connection.transport.Base;

import java.nio.charset.Charset;


public abstract class AbstractConnection {

    public abstract Base transport();

    public abstract Encoding encoding();

    public abstract Charset charset();

    public abstract boolean detectCharsetInMessage();

    public abstract void start();

    public abstract void startAndWait() throws InterruptedException;

    public abstract void stop();

    HapiContext initHapiContext() {
        HapiContext hapiContext = new DefaultHapiContext(new NoValidationBuilder());
        hapiContext.setLowerLayerProtocol(initLowerLayerProtocol());
        if (encoding() == Encoding.ER7) {
            hapiContext.getGenericParser().setPipeParserAsPrimary();
        } else {
            hapiContext.getGenericParser().setXMLParserAsPrimary();
        }

        return hapiContext;
    }

    private LowerLayerProtocol initLowerLayerProtocol() {
        LowerLayerProtocol lowerLayerProtocol = new MinLowerLayerProtocol(detectCharsetInMessage());
        if (!detectCharsetInMessage()) {
            lowerLayerProtocol.setCharset(charset());
        }

        return lowerLayerProtocol;
    }


}
