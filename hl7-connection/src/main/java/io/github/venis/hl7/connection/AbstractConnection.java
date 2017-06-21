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

    public abstract void startAndWait();

    public abstract void stop();

    public abstract boolean isRunning();

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
