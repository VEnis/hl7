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
package io.github.venis.hl7.model;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.Version;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.util.ReflectionUtil;
import ca.uhn.hl7v2.util.Terser;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@ToString(exclude = {"terserMessage"}, callSuper = true)
@Slf4j
public class Message extends Group implements Serializable {
    private transient Terser terserMessage;

    public Message(ca.uhn.hl7v2.model.Message hapiMessage) {
        super(hapiMessage);
        this.terserMessage = new Terser(hapiMessage);
    }

    public Message(String type, Version version) {
        this(type, version.getVersion());
    }

    public Message(String type, String version) {
        this(type, version, Constants.DEFAULT_PROCESSING_ID);
    }

    public Message(String type, Version version, String processingId) {
        this(type, version.getVersion(), processingId);
    }

    public Message(String type, String version, String processingId) {
        this(createMessage(type, version, processingId));
    }

    private static ca.uhn.hl7v2.model.Message createMessage(String type, String version, String processingId) {
        String[] messageTypeParts = type.split("_");
        if (messageTypeParts.length != 2) {
            throw new IllegalArgumentException("Passed hl7 type is invalid: " + type);
        }

        DefaultModelClassFactory defaultModelClassFactory = new DefaultModelClassFactory();

        try {
            Class<? extends ca.uhn.hl7v2.model.Message> messageClass = defaultModelClassFactory.getMessageClass(type, version, false);
            ca.uhn.hl7v2.model.Message message = ReflectionUtil.instantiateMessage(messageClass, defaultModelClassFactory);

            String part1 = messageTypeParts[0];
            String part2 = messageTypeParts[1];

            ((AbstractMessage) message).initQuickstart(part1, part2, processingId);
            return message;
        } catch (HL7Exception | IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public String getByTerser(String terserSpecification) {
        try {
            return terserMessage.get(terserSpecification);
        } catch (HL7Exception e) {
            log.debug("No result extracted by terser specification", e);
            return null;
        }
    }

    public void setByTerser(String terserSpecification, String newValue) {
        try {
            terserMessage.set(terserSpecification, newValue);
        } catch (HL7Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public ca.uhn.hl7v2.model.Message getHapiMessage() {
        return (ca.uhn.hl7v2.model.Message) hapiGroup;
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.terserMessage = new Terser((ca.uhn.hl7v2.model.Message) hapiGroup);
    }
}
