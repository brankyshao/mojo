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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;

public class ChartRendererStub implements ChartRenderer {
    final Map charts = new HashMap();

    public void renderChart(String name, JFreeChart chart) throws IOException {
        charts.put(name, chart);
    }

}
