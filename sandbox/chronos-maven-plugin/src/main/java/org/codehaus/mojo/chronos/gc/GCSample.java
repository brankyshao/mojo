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
package org.codehaus.mojo.chronos.gc;

import java.io.IOException;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Contains info from a garbagecollection logentry.
 * 
 * @author ksr@lakeside.dk
 */
public final class GCSample {

    private final double timestamp;

    private final int heapBefore;

    private final int heapAfter;

    private final int heapTotal;

    private final double processingTime;

    public GCSample(double timestamp, int heapBefore, int heapAfter, int heapTotal, double processingTime) {
        this.timestamp = timestamp;
        this.heapAfter = heapAfter;
        this.heapBefore = heapBefore;
        this.heapTotal = heapTotal;
        this.processingTime = processingTime;
    }

    /**
     * @return Returns the timestamp.
     */
    public double getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the heapBefore.
     */
    public int getHeapBefore() {
        return heapBefore;
    }

    /**
     * @return Returns the heapAfter.
     */
    public int getHeapAfter() {
        return heapAfter;
    }

    /**
     * @return Returns the heapTotal.
     */
    public int getHeapTotal() {
        return heapTotal;
    }

    /**
     * @return Returns the processingTime.
     */
    public double getProcessingTime() {
        return processingTime;
    }

    public Element toXML() {
        Element xml = new Element("gcsample");

        xml.setAttribute("timestamp", Double.toString(timestamp));
        xml.setAttribute("heapBefore", Integer.toString(heapBefore));
        xml.setAttribute("heapAfter", Integer.toString(heapAfter));
        xml.setAttribute("heapTotal", Integer.toString(heapTotal));
        xml.setAttribute("processingTime", Double.toString(processingTime));

        return xml;
    }

    public static GCSample fromXML(Element gcXML) throws JDOMException, IOException {
        double timestamp = Double.parseDouble(gcXML.getAttributeValue("timestamp"));
        int heapBefore = Integer.parseInt(gcXML.getAttributeValue("heapBefore"));
        int heapAfter = Integer.parseInt(gcXML.getAttributeValue("heapAfter"));
        int heapTotal = Integer.parseInt(gcXML.getAttributeValue("heapTotal"));
        double processingTime = Double.parseDouble(gcXML.getAttributeValue("processingTime"));

        return new GCSample(timestamp, heapBefore, heapAfter, heapTotal, processingTime);
    }
}