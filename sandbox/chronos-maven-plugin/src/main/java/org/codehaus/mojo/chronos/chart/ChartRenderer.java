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

import org.jfree.chart.JFreeChart;

/**
 * ChartRenderer. Made as an interface to enable unittesting...
 * 
 * @author ksr@lakeside.dk
 */
public interface ChartRenderer {

    /**
     * Instructs the <code>ChartRenderer</code> to render the chart.
     * 
     * @param name
     *            The name of the generated chart.
     * @param chart
     *            <code>JFreeChart</code> containing the data to render.
     * @throws IOException
     *             Thrown if the operation fails.
     */
    void renderChart(String name, JFreeChart chart) throws IOException;

}
