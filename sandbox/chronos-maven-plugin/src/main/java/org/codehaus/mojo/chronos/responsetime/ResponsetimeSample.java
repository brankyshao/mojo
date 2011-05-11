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

import java.io.Serializable;

import org.codehaus.mojo.chronos.jmeter.Jtl20Sample;
import org.codehaus.mojo.chronos.jmeter.Jtl21Sample;
import org.jdom.Element;

/**
 * Contains info from a jmeter logentry.
 * 
 * @author ksr@lakeside.dk
 */
public abstract class ResponsetimeSample implements Serializable {
    /**
     * @return the responsetime
     */
    public abstract int getResponsetime();

    /**
     * Returns the timestamp of this sample, as defined by * {@link System#currentTimeMillis()}.
     * 
     * @return the timestamp of this sample.
     */
    public abstract long getTimestamp();

    /**
     * Was the invocation successful or did it fail?
     * 
     * @return whether the invocation succeeded or not.
     */
    public abstract boolean isSuccess();

    /**
     * What is the threadgroup id of this sample? This is derived by the threadname.
     * 
     * @return the threadgroupid of this samples
     */
    public abstract String getThreadId();

    /**
     * Returns a XML representation of the <code>ResponsetimeSample</code>.
     * 
     * @return XML representation.
     */
    public abstract Element toXML();

    /**
     * Transforms the xml into a <code>ResponsetimeSample</code> entity.
     * 
     * @param xml
     *            The xml to parse.
     * @return The corresponding <code>ResponsetimeSample</code> instance.
     */
    public static ResponsetimeSample fromXml(Element xml) {
        if("jtl20sample".equals(xml.getName())) {
            return Jtl20Sample.fromXML(xml);
        } else if("jtl21sample".equals(xml.getName())) {
            return Jtl21Sample.fromXML(xml);
        }
        throw new IllegalStateException("Unknown tag: " + xml.getName());
    }
}