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

import org.jdom.Element;

import java.util.Iterator;
import java.util.List;

/**
 * @author ksr@lakeside.dk
 */
public class ResponsetimeSampleGroup extends ResponsetimeSamples {
    private static final long serialVersionUID = -5442330396281337888L;

    private static int lastIndex = 0;

    private final String name;
    private final int index;

    public ResponsetimeSampleGroup(String name) {
        this(name, ++lastIndex);
    }
    
    private ResponsetimeSampleGroup(String name, int index) {
        this.name = name;
        this.index = index;
        
        if(lastIndex < this.index) {
            lastIndex = this.index;
        }
    }

    public final int getIndex() {
        return index;
    }

    /**
     * @return the name of this samplegroup
     */
    public final String getName() {
        return name;
    }
    
    public final Element toXML() {
        Element xml = new Element("responsetimesamplegroup");
        xml.setAttribute("name", name);
        xml.setAttribute("index", Integer.toString(index));
        for (Iterator iterator = this.samples.iterator(); iterator.hasNext();) {
            xml.addContent(((ResponsetimeSample)iterator.next()).toXML());
        }

        return xml;
    }

    public static ResponsetimeSampleGroup fromXml(Element xml) {
        if(!xml.getName().equals("responsetimesamplegroup")) {
            throw new IllegalArgumentException("Unknown tag: " + xml.getName());
        }
        
        String name = xml.getAttributeValue("name");
        int index = Integer.parseInt(xml.getAttributeValue("index"));

        List samples = xml.getChildren();
        final ResponsetimeSampleGroup result = new ResponsetimeSampleGroup(name, index);
        for (Iterator iterator = samples.iterator(); iterator.hasNext();) {
            Element sXml = (Element)iterator.next();
            result.add(ResponsetimeSample.fromXml(sXml));
        }
        return result;
    }
}
