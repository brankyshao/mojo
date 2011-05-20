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

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;

import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;

/**
 * @author ksr@lakeside.dk
 */
public class JMeterLogParserTest extends TestCase {

    /**
     * Checks if the all samples from log is being parsed
     */
    public void testParseJMeterLog() throws Exception {
        File file = new File("src/test/resources/test1-junitsamples.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(6, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(3, sampleGroup.size());
        }
    }

    public void testParseJmeter23WebLog() throws Exception {
        File file = new File("src/test/resources/webtest-jmeter22-resulttable.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(2, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl20Combined() throws Exception {
        File file = new File("src/test/resources/combinedtest-jtl20-summaryreport.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl21Combined() throws Exception {
        File file = new File("src/test/resources/combinedtest-jtl21-summaryreport.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    public void testJtl22Combined2() throws Exception {
        File file = new File("src/test/resources/combinedtest-jtl22-summaryreport.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(150, sampleGroup.size());
        }
    }

    /**
     * Nested httpSample elements breaks jmeter performance reporting.<br />
     * See <a href="http://jira.codehaus.org/browse/MOJO-1343 JiraTask">JIRA</a> for more information.
     */
    public void testJtlNestedHttpSample() throws Exception {
        File file = new File("src/test/resources/jmeter-nested-httpsample.jtl");
        GroupedResponsetimeSamples samples = new JMeterLogParser().parseJMeterLog(file);
        assertEquals(4, samples.getSampleGroups().size());
        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            assertEquals(1, sampleGroup.size());
        }
    }

}
