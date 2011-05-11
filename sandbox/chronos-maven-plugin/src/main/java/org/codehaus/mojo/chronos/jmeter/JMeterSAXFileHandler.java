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

import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSample;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Properties;

/**
 * SAXHandler for JMeter xml logs.
 * 
 * @author ksr@lakeside.dk
 */
public final class JMeterSAXFileHandler extends DefaultHandler {
    private final GroupedResponsetimeSamples samples = new GroupedResponsetimeSamples();

    private Properties sampleAttributes;

    private boolean inProperty = false;

    private boolean insideSample = false;

    private StringBuffer testMethodNameSB = new StringBuffer();

    private Properties parentSampleAttributes;

    /**
     * @see DefaultHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("sampleResult".equals(qName)) {
            // jtl20
            Properties props = new Properties();
            for (int i = 0; i < attributes.getLength(); i++) {
                props.put(attributes.getQName(i), attributes.getValue(i));
            }
            sampleAttributes = props;
            insideSample = true;
        } else if("property".equals(qName)) {
            // jtl20
            // TODO be sure that log cannot contain other types of character
            // data under junitSamples properties
            inProperty = true;
        } else if("httpSample".equals(qName) || "sample".equals(qName)) {
            // jtl21

            if(insideSample) {
                parentSampleAttributes = sampleAttributes;
            }

            Properties props = new Properties();
            for (int i = 0; i < attributes.getLength(); i++) {
                props.put(attributes.getQName(i), attributes.getValue(i));
            }
            sampleAttributes = props;
            insideSample = true;
        }
    }

    /**
     * this method can be called multiple times in one element if there's enough chars.
     * 
     * @see DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length) {
        if(insideSample && inProperty) {
            testMethodNameSB.append(new String(ch, start, length));
        }
    }

    /**
     * @see DefaultHandler#endElement(String, String, String)
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("property".equals(qName)) {
            inProperty = false;
        } else if("sampleResult".equals(qName)) {
            // jtl20
            String sampleName = Jtl20Sample.getSampleName(sampleAttributes, testMethodNameSB.toString());
            ResponsetimeSample sample = new Jtl20Sample(sampleAttributes);
            samples.put(sampleName, sample);
            testMethodNameSB.setLength(0);
            insideSample = false;
            sampleAttributes = null;
        } else if("httpSample".equals(qName) || "sample".equals(qName)) {
            // jtl21
            if(!insideSample) {
                sampleAttributes = parentSampleAttributes;
            }

            String sampleName = Jtl21Sample.getSampleName(sampleAttributes);
            ResponsetimeSample sample = new Jtl21Sample(sampleAttributes);
            samples.put(sampleName, sample);
            testMethodNameSB.setLength(0);
            sampleAttributes = null;
            insideSample = false;
        }
    }

    /**
     * @return the generated samples obtained by parsing the logfile
     */
    public GroupedResponsetimeSamples getJMeterSamples() {
        return samples;
    }
}
