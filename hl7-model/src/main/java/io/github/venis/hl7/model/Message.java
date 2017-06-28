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

/**
 * Class that represents hl7 message
 */
@ToString(exclude = {"terserMessage"}, callSuper = true)
@Slf4j
public class Message extends Group implements Serializable {
    private static final int CREATE_MESSAGE_TYPE_EXPECTED_PARTS_COUNT = 2; //NOPMD
    private static final long serialVersionUID = -127057638264163059L;
    private transient Terser terserMessage;

    /**
     * Creates message object by hapi message
     *
     * @param hapiMessage Hapi message to wrap
     */
    public Message(final ca.uhn.hl7v2.model.Message hapiMessage) {
        super(hapiMessage);
        this.terserMessage = new Terser(hapiMessage);
    }

    /**
     * Creates message by message type and version where version is hapi enum
     *
     * @param type    Message type (like "ADT_A01")
     * @param version Message version
     * @see Version
     */
    public Message(final String type, final Version version) {
        this(type, version.getVersion());
    }

    /**
     * Creates message by message type and version where version is string
     *
     * @param type    Message type like "ADT_A01"
     * @param version Message version  like "2.3"
     * @see Version
     */
    public Message(final String type, final String version) {
        this(type, version, Constants.DEFAULT_PROCESSING_ID);
    }

    /**
     * Creates message by message type, version where version is hapi enum and processing id
     *
     * @param type         Message type like "ADT_A01"
     * @param version      Message version
     * @param processingId Processing id like "P"
     * @see Version
     */
    public Message(final String type, final Version version, final String processingId) {
        this(type, version.getVersion(), processingId);
    }

    /**
     * Creates message by message type, version where version is string and processing id
     *
     * @param type         Message type like "ADT_A01"
     * @param version      Message version like "2.3"
     * @param processingId Processing id like "P"
     */
    public Message(final String type, final String version, final String processingId) {
        this(createMessage(type, version, processingId));
    }

    /**
     * Actual method that creates hapi message object by given type, version and processing id
     *
     * @param type         Message type like "ADT_A01"
     * @param version      Message version like "2.3"
     * @param processingId Processing id like "P"
     * @return Hapi message
     */
    private static ca.uhn.hl7v2.model.Message createMessage(final String type, final String version, final String processingId) {
        final String[] messageTypeParts = type.split("_");
        if (messageTypeParts.length != CREATE_MESSAGE_TYPE_EXPECTED_PARTS_COUNT) {
            throw new IllegalArgumentException("Passed hl7 type is invalid: " + type);
        }

        final DefaultModelClassFactory factory = new DefaultModelClassFactory();

        try {
            final Class<? extends ca.uhn.hl7v2.model.Message> messageClass = factory.getMessageClass(type, version, false);
            final ca.uhn.hl7v2.model.Message message = ReflectionUtil.instantiateMessage(messageClass, factory);

            final String part1 = messageTypeParts[0];
            final String part2 = messageTypeParts[1];

            ((AbstractMessage) message).initQuickstart(part1, part2, processingId);
            return message;
        } catch (HL7Exception | IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Returns value for some field by terser specification
     *
     * @param terser Terser specification
     * @return Value or null if nothing was found
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public String getByTerser(final String terser) {
        try {
            return terserMessage.get(terser);
        } catch (HL7Exception e) {
            log.debug("No result extracted by terser specification", e);
            return null;
        }
    }

    /**
     * Sets new value for some field by terser specification
     *
     * @param terser Terser specification
     * @param value  New value to be set
     */
    public void setByTerser(final String terser, final String value) {
        try {
            terserMessage.set(terser, value);
        } catch (HL7Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Returns corresponded hapi message
     *
     * @return Hapi message
     */
    public ca.uhn.hl7v2.model.Message getHapiMessage() {
        return (ca.uhn.hl7v2.model.Message) hapiGroup;
    }

    @SuppressWarnings("PMD.MethodArgumentCouldBeFinal")
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.terserMessage = new Terser((ca.uhn.hl7v2.model.Message) hapiGroup);
    }
}
