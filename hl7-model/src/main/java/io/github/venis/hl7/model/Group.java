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

/**
 * Class that represent single hl7 group of segments (wraps hapi group)
 *
 * @see ca.uhn.hl7v2.model.Group
 */
@ToString(of = "hapiGroup")
@Slf4j
@SuppressWarnings("PMD.OnlyOneReturn")
public class Group implements Serializable {
    private static final long serialVersionUID = 7858744287265892350L;

    /**
     * Hapi group to wrap
     */
    protected ca.uhn.hl7v2.model.Group hapiGroup;

    /**
     * Segment fiender to get group parts
     */
    private transient SegmentFinder hapiSegmentFinder;

    /**
     * Creates group class for passed hapi group
     *
     * @param hapiGroup Hapi group to wrap
     */
    public Group(final ca.uhn.hl7v2.model.Group hapiGroup) {
        this.hapiGroup = hapiGroup;
        this.hapiSegmentFinder = new SegmentFinder(hapiGroup);
    }

    /**
     * Return list of groups for given group name
     *
     * @param name Group name to search
     * @return List of groups
     */
    public List<Group> getGroups(final String name) {
        try {
            return Arrays.stream(hapiGroup.getAll(name))
                         .map(element -> new Group((ca.uhn.hl7v2.model.Group) element))
                         .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No groups was found for given group name", e);
            return new ArrayList<>();
        }
    }

    /**
     * Return single group with given name and default repetition number
     *
     * @param name Group name to search
     * @return Group
     */
    public Group getGroup(final String name) {
        return getGroup(name, DEFAULT_REPETITION_NUMBER);
    }

    /**
     * Return single group with given name and passed repetition number
     *
     * @param name       Group name to search
     * @param repetition Repetition number
     * @return Group
     */
    public Group getGroup(final String name, final int repetition) {
        try {
            return new Group(hapiSegmentFinder.getGroup(name, repetition));
        } catch (HL7Exception e) {
            log.debug("Group with given name and repetition was not found", e);
            return null;
        }
    }

    /**
     * Return list of groups for given group name
     *
     * @param name Segment name to search
     * @return Segment
     */
    public List<Segment> getSegments(final String name) {
        try {
            return Arrays.stream(hapiGroup.getAll(name))
                         .map(element -> new Segment((ca.uhn.hl7v2.model.Segment) element))
                         .collect(Collectors.toList());
        } catch (HL7Exception e) {
            log.debug("No segments was found for given segment name", e);
            return new ArrayList<>();
        }
    }

    /**
     * Adds new repeatable segment with given name
     *
     * @param name Name of segment to add
     * @return Added segment
     */
    public Segment addRepeatableSegment(final String name) {
        return getSegment(name, getSegments(name).size());
    }

    /**
     * Adds new repeatable group with given name
     *
     * @param name Group name to add
     * @return Added group
     */
    public Group addRepeatableGroup(final String name) {
        return getGroup(name, getGroups(name).size());
    }

    /**
     * Helper method to get segment by value of one of it's field value
     *
     * @param name            Name of the segment to find
     * @param field           Field number to check
     * @param fieldRepetition Field repetition to check
     * @param component       Field component to check
     * @param subComponent    Field sub-component to check
     * @param value           Field value to compare with
     * @return Segment or null
     */
    public Segment getConditionalSegment(final String name, final int field, final int fieldRepetition, final int component, final int subComponent, final String value) {
        return getSegments(name).stream()
                                .filter(segment -> segment.getField(field, fieldRepetition).getValue(component, subComponent).equals(value))
                                .findFirst().orElse(null);
    }

    /**
     * Helper method to get segment by value of one of it's field value. Field repetition is default
     *
     * @param name         Name of the segment to find
     * @param field        Field number to check
     * @param component    Field component to check
     * @param subComponent Field sub-component to check
     * @param value        Field value to compare with
     * @return Segment or null
     */
    public Segment getConditionalSegment(final String name, final int field, final int component, final int subComponent, final String value) {
        return getConditionalSegment(name, field, DEFAULT_REPETITION_NUMBER, component, subComponent, value);
    }

    /**
     * Helper method to get segment by value of one of it's field value. Field repetition and sub-component are default
     *
     * @param name      Name of the segment to find
     * @param field     Field number to check
     * @param component Field component to check
     * @param value     Field value to compare with
     * @return Segment or null
     */
    public Segment getConditionalSegment(final String name, final int field, final int component, final String value) {
        return getConditionalSegment(name, field, DEFAULT_REPETITION_NUMBER, component, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, value);
    }

    /**
     * Helper method to get segment by value of one of it's field value. Field repetition, component and sub-component
     * are default
     *
     * @param name  Name of the segment to find
     * @param field Field number to check
     * @param value Field value to compare with
     * @return Segment or null
     */
    public Segment getConditionalSegment(final String name, final int field, final String value) {
        return getConditionalSegment(name, field, DEFAULT_REPETITION_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, DEFAULT_COMPONENT_SUB_COMPONENT_NUMBER, value);
    }

    /**
     * Returns segment with given name and default repetition number
     *
     * @param segmentName Segment name to get
     * @return Segment
     */
    public Segment getSegment(final String segmentName) {
        return getSegment(segmentName, DEFAULT_REPETITION_NUMBER);
    }

    /**
     * Returns segment with given name and passed repetition number
     *
     * @param segmentName Segment name to get
     * @param repetition  Required repetition number
     * @return Segment
     */

    public Segment getSegment(final String segmentName, final int repetition) {
        try {
            return new Segment(hapiSegmentFinder.getSegment(segmentName, repetition));
        } catch (HL7Exception e) {
            log.debug("Segment with given name and repetition was not found", e);
            return null;
        }
    }

    @SuppressWarnings("PMD.MethodArgumentCouldBeFinal")
    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        stream.defaultReadObject();
        this.hapiSegmentFinder = new SegmentFinder(hapiGroup);
    }
}
