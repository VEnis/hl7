/**
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
import ca.uhn.hl7v2.util.SegmentFinder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.venis.hl7.model.Constants.DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER;
import static io.github.venis.hl7.model.Constants.DEFAULT_REPETITION_NUMBER;

@ToString(of = "hapiGroup")
@Slf4j
public class Group implements Serializable {
    protected ca.uhn.hl7v2.model.Group hapiGroup;
    private transient SegmentFinder hapiSegmentFinder;

    public Group(ca.uhn.hl7v2.model.Group hapiGroup) {
        this.hapiGroup = hapiGroup;
        this.hapiSegmentFinder = new SegmentFinder(hapiGroup);
    }

    public List<Group> getGroups(String groupName) {
        try {
            return Arrays.stream(hapiGroup.getAll(groupName))
                    .map(e -> new Group((ca.uhn.hl7v2.model.Group) e))
                    .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No groups was found for given group name", e);
            return new ArrayList<>();
        }
    }

    public Group getGroup(String groupName) {
        return getGroup(groupName, DEFAULT_REPETITION_NUMBER);
    }

    public Group getGroup(String groupName, int repetition) {
        try {
            return new Group(hapiSegmentFinder.getGroup(groupName, repetition));
        } catch (HL7Exception e) {
            log.debug("Group with given name and repetition was not found", e);
            return null;
        }
    }

    public List<Segment> getSegments(String segmentName) {
        try {
            return Arrays.stream(hapiGroup.getAll(segmentName))
                    .map(e -> new Segment((ca.uhn.hl7v2.model.Segment) e))
                    .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No segments was found for given segment name", e);
            return new ArrayList<>();
        }
    }

    public Segment addRepeatableSegment(String segmentName) {
        return getSegment(segmentName, getSegments(segmentName).size());
    }

    public Group addRepeatableGroup(String groupName) {
        return getGroup(groupName, getGroups(groupName).size());
    }

    public Segment getConditionalSegment(String segmentName, int fieldNumber, int fieldRepetition, int componentToCheck, int subComponentToCheck, String valueToCheck) {
        return getSegments(segmentName).stream()
                .filter(segment -> segment.getField(fieldNumber, fieldRepetition).getValue(componentToCheck, subComponentToCheck).equals(valueToCheck))
                .findFirst().orElse(null);
    }

    public Segment getConditionalSegment(String segmentName, int fieldNumber, int componentToCheck, int subComponentToCheck, String valueToCheck) {
        return getConditionalSegment(segmentName, fieldNumber, DEFAULT_REPETITION_NUMBER, componentToCheck, subComponentToCheck, valueToCheck);
    }

    public Segment getConditionalSegment(String segmentName, int fieldNumber, int componentToCheck, String valueToCheck) {
        return getConditionalSegment(segmentName, fieldNumber, DEFAULT_REPETITION_NUMBER, componentToCheck, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, valueToCheck);
    }

    public Segment getConditionalSegment(String segmentName, int fieldNumber, String valueToCheck) {
        return getConditionalSegment(segmentName, fieldNumber, DEFAULT_REPETITION_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, valueToCheck);
    }

    public Segment getSegment(String segmentName) {
        return getSegment(segmentName, DEFAULT_REPETITION_NUMBER);
    }

    public Segment getSegment(String segmentName, int repetition) {
        try {
            return new Segment(hapiSegmentFinder.getSegment(segmentName, repetition));
        } catch (HL7Exception e) {
            log.debug("Segment with given name and repetition was not found", e);
            return null;
        }
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.hapiSegmentFinder = new SegmentFinder(hapiGroup);
    }
}
