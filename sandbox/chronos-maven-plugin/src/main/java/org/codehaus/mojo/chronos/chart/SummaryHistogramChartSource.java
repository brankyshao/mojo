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

import java.util.ResourceBundle;

import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;
import org.jfree.chart.JFreeChart;

/**
 * This class is responsible for generating a histogram summarising response times.
 * 
 * @author ksr@lakeside.dk
 */
public final class SummaryHistogramChartSource extends HistogramChartGenerator implements ChartSource {
    private ResponsetimeSamples samples;

    public SummaryHistogramChartSource(ResponsetimeSamples samples) {
        this.samples = samples;
    }

    public JFreeChart getChart(ResourceBundle bundle, ReportConfig config) {
        return createHistogramChart(samples, bundle.getString("chronos.label.histogram.alltests"), bundle, config);
    }

    public String getFileName(ResourceBundle bundle, ReportConfig config) {
        return "histogram-summary-" + config.getId();
    }

    public boolean isEnabled(ResourceBundle bundle, ReportConfig config) {
        return config.isShowsummary() && config.isShowhistogram() && !config.isShowdetails();
    }

}
