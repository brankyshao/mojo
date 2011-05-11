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
package org.codehaus.mojo.chronos.chart;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.chronos.ReportConfigStub;
import org.codehaus.mojo.chronos.Utils;
import org.codehaus.mojo.chronos.jmeter.JMeterLogParser;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Testclass for {@link GraphGenerator}
 * 
 * @author ksr@lakeside.dk
 */
public class GraphGeneratorTest extends TestCase {

    ResourceBundle bundle;
    private ChartRendererStub renderer;

    protected void setUp() throws Exception {
        bundle = Utils.getBundle(Locale.getDefault());
        renderer = new ChartRendererStub();
    }

    /**
     * Tests if charts can be generated without errors.
     * 
     * @throws Exception
     * @throws MavenReportException
     */
    public void testDoGenerateReport() throws Exception {
        File file = new File("src/test/resources/test2-junitsamples.jtl");
        ResponsetimeSamples jmeterSamples = new JMeterLogParser().parseJMeterLog(file);
        List plugins = Collections.singletonList(new ChronosHistogramPlugin(jmeterSamples));
        GraphGenerator gen = new GraphGenerator(plugins);
        gen.generateGraphs(renderer, bundle, new ReportConfigStub());
        assertEquals(6, renderer.charts.size());
    }
}
