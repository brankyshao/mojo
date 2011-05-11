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
package org.codehaus.mojo.chronos.jmeter;

import java.util.Properties;

import org.codehaus.mojo.chronos.responsetime.ResponsetimeSample;
import org.jdom.Element;

/**
 * This class represents a JMeter log entry defined in jtl2.1 format (or later).
 * 
 * @author ksr@lakeside.dk
 */
public class Jtl21Sample extends ResponsetimeSample {
    
    private static final long serialVersionUID = -3776902838855740550L;

    private final int responsetime;

    private final long timestamp;

    private final boolean success;

    private final String threadId;

    /**
     * @param attributes
     *            the attributes of t he sample element
     */
    public Jtl21Sample(Properties attributes) {
        responsetime = Integer.parseInt(attributes.getProperty("t"));
        timestamp = Long.parseLong(attributes.getProperty("ts"));
        success = "true".equals(attributes.getProperty("s"));
        threadId = attributes.getProperty("tn").intern();
    }
    
    private Jtl21Sample(int responsetime, long timestamp, boolean success, String threadId) {
        this.responsetime = responsetime;
        this.timestamp = timestamp;
        this.success = success;
        this.threadId = threadId;
    }

    /**
     * @return Return the name of this sample
     */
    public static String getSampleName(Properties attributes) {
        return attributes.getProperty("lb");
    }

    /**
     * @see ResponsetimeSample#getResponsetime()
     * @return Returns the responsetime.
     */
    public final int getResponsetime() {
        return responsetime;
    }

    /**
     * @see ResponsetimeSample#getTimestamp()
     * @return Returns the timestamp.
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the success.
     * @see ResponsetimeSample#isSuccess()
     */
    public final boolean isSuccess() {
        return success;
    }

    /**
     * @return Returns the threadgroupId.
     * @see ResponsetimeSample#getThreadId()
     */
    public final String getThreadId() {
        return threadId;
    }

    public final Element toXML() {
        Element xml = new Element("jtl21sample");
        xml.setAttribute("responsetime", Integer.toString(responsetime));
        xml.setAttribute("timestamp", Long.toString(timestamp));
        xml.setAttribute("success", Boolean.toString(success));
        xml.setAttribute("threadId", threadId);
        return xml;
    }
    
    /**
     * Transforms the xml into a <code>Jtl21Sample</code> entity.
     * 
     * @param xml
     *            The xml to parse.
     * @return The corresponding <code>Jtl21Sample</code> instance.
     */
    public static Jtl21Sample fromXML(Element xml) {
        int responsetime = Integer.parseInt(xml.getAttributeValue("responsetime"));
        long timestamp = Long.parseLong(xml.getAttributeValue("timestamp"));
        boolean success = Boolean.parseBoolean(xml.getAttributeValue("success"));
        String threadId = xml.getAttributeValue("threadId");
        
        return new Jtl21Sample(responsetime, timestamp, success, threadId);
    }
}