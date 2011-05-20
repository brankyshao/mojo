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
package org.codehaus.mojo.chronos.report;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.mojo.chronos.ReportConfigStub;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.chart.ChronosHistogramPlugin;
import org.codehaus.mojo.chronos.chart.GraphGenerator;
import org.codehaus.mojo.chronos.jmeter.JMeterLogParser;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Testclass for {@link GraphGenerator}
 * 
 * @author ksr@lakeside.dk
 */
public class ReportGeneratorTest extends TestCase {

    ResourceBundle bundle;

    private ResponsetimeSamples samples;

    protected void setUp() throws Exception {
        File file = new File("src/test/resources/test1-junitsamples.jtl");
        samples = new JMeterLogParser().parseJMeterLog(file);
        bundle = Utils.getBundle(Locale.getDefault());
    }

    /**
     * Tests if report can be generated without errors. The {@link SinkStub} stubs the {@link Sink} class.
     * 
     * @throws MavenReportException
     */
    public void testDoGenerateReport() throws MavenReportException {
        ReportConfigStub config = new ReportConfigStub();
        List plugins = Collections.singletonList(new ChronosHistogramPlugin(samples));
        ReportGenerator gen = new ReportGenerator(bundle, config, new GraphGenerator(plugins));
        gen.doGenerateReport(new SinkStub(), samples);
    }
}
