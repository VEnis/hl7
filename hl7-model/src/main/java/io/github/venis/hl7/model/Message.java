package io.github.venis.hl7.model;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.util.Terser;
import lombok.NonNull;
import lombok.ToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@ToString(callSuper = true, of = "")
public class Message extends Group implements Serializable {
    @NonNull private transient Terser terserMessage;

    public Message(ca.uhn.hl7v2.model.Message hapiMessage) {
        super(hapiMessage);
        this.terserMessage = new Terser(hapiMessage);
    }

//    public Message(String version, String type, String processingId) {
//        this(createMessage(version, type, processingId));
//    }
//
//    private static ca.uhn.hl7v2.model.Message createMessage(String version, String type, String processingId) {
//        String[] messageTypeParts = type.split("_");
//        if (messageTypeParts.length != 2) throw new IllegalArgumentException("Passed hl7 type is invalid: " + type);
//
//        DefaultModelClassFactory defaultModelClassFactory = new DefaultModelClassFactory();
//
//        try {
//            Class<? extends ca.uhn.hl7v2.model.Message> messageClass = defaultModelClassFactory.getMessageClass(type, version, false);
//            ca.uhn.hl7v2.model.Message message = ReflectionUtil.instantiateMessage(messageClass, defaultModelClassFactory);
//
//            String part1 = messageTypeParts[0];
//            String part2 = messageTypeParts[1];
//
//            ((AbstractMessage) message).initQuickstart(part1, part2, processingId);
//            return message;
//        } catch (HL7Exception | IOException e) {
//            throw new IllegalArgumentException(e.getMessage(), e);
//        }
//    }

    public String getByTerser(String terserSpecification) {
        try {
            return terserMessage.get(terserSpecification);
        } catch (HL7Exception e) {
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
