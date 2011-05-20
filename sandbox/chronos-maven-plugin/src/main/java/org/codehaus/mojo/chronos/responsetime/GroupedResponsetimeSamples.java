/*
 * Chronos - Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * All source code and information supplied as part of Chronos is
 * copyright to National Board of e-Health.
 *
 * The source code has been released under a dual license - meaning you can
 * use either licensed version of the library with your code.
 *
 * It is released under the LGPL (GNU Lesser General Public License), either
 * version 2.1 of the License, or (at your option) any later version. A copy
 * of which can be found at the link below.
 * http://www.gnu.org/copyleft/lesser.html
 *
 * $HeadURL$
 * $Id$
 */
package org.codehaus.mojo.chronos.responsetime;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * A grouping collection of samples (grouped by the name of the samples).
 * 
 * @author ksr@lakeside.dk
 */
public final class GroupedResponsetimeSamples extends ResponsetimeSamples {
    private static final String GROUPEDRESPONSETIMESAMPLES_TAG = "groupedresponsetimesamples";
    private static final String SUCCEEDED_ATTRIBUTE = "succeeded";
    private static final String GROUP_TAG = "responsetimesamplegroup";

    private static final long serialVersionUID = 5054656881107118329L;


    private final SortedMap sampleGroupsByName = new TreeMap();

    public void addAll(GroupedResponsetimeSamples other) {
        Iterator groupIt = other.getSampleGroups().iterator();
        while (groupIt.hasNext()) {
            ResponsetimeSampleGroup group = (ResponsetimeSampleGroup) groupIt.next();
            addGroup(group);
        }
    }

    private void addGroup(ResponsetimeSampleGroup group) {
        String groupName = group.getName();
        getSampleGroup(groupName).addAll(group);
        this.addAll(group);
    }

    /**
     * Add a sample (and group it).
     *
     * @param sampleName The name of the sample (individual testcase)
     * @param sample A result from invocation of that sample.
     * @see ResponsetimeSamples#add(ResponsetimeSample)
     */
    public void put(String sampleName, ResponsetimeSample sample) {
        super.add(sample);
        getSampleGroup(sampleName).add(sample);
    }

    private ResponsetimeSamples getSampleGroup(String sampleName) {
        ResponsetimeSamples sampleGroup = (ResponsetimeSamples) sampleGroupsByName.get(sampleName);
        if(sampleGroup == null) {
            sampleGroup = new ResponsetimeSampleGroup(sampleName);
            sampleGroupsByName.put(sampleName, sampleGroup);
        }
        return sampleGroup;
    }

    /**
     * @return a list of {@link ResponsetimeSampleGroup} sorted by the name of the group
     */
    public List getSampleGroups() {
        return new ArrayList(sampleGroupsByName.values());
    }

    public Element toXML() {
        Element xml = new Element(GROUPEDRESPONSETIMESAMPLES_TAG);
        xml.setAttribute(SUCCEEDED_ATTRIBUTE, Integer.toString(succeeded));

        for (Iterator iterator = sampleGroupsByName.entrySet().iterator(); iterator.hasNext();) {
            Entry entry = (Entry)iterator.next();
            final ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup) entry.getValue();
            xml.addContent(sampleGroup.toXML());
        }

        return xml;
    }

    public static GroupedResponsetimeSamples fromXmlFile(File file) throws JDOMException, IOException {
        GroupedResponsetimeSamples grs = new GroupedResponsetimeSamples();
        if (!file.exists()) {
            return grs;
        }
        SAXBuilder sb = new SAXBuilder();
        Document doc = sb.build(file);

        if(!doc.getRootElement().getName().equals(GROUPEDRESPONSETIMESAMPLES_TAG)) {
            throw new IllegalArgumentException("Invalid xml file structure - expected GroupedResponsetimeSamples, but was " + doc.getRootElement().getName());
        }


        Element xml = doc.getRootElement();
        grs.succeeded = Integer.parseInt(xml.getAttributeValue(SUCCEEDED_ATTRIBUTE));

        List groups = xml.getChildren(GROUP_TAG);
        for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
            Element gXml = (Element)iterator.next();

            ResponsetimeSampleGroup rsg = ResponsetimeSampleGroup.fromXml(gXml);

            grs.addGroup(rsg);
        }
        return grs;
    }
}