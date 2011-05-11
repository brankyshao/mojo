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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Plugin adding responsetime charts to the report.
 * 
 * @author ksr@lakeside.dk
 */
public final class ChronosResponsetimePlugin implements ChronosReportPlugin {
    private ResponsetimeSamples samples;

    public ChronosResponsetimePlugin(ResponsetimeSamples samples) {
        this.samples = samples;
    }

    public ChartSource getSummaryChartSource() {
        return new SummaryResponsetimeChartSource(samples);
    }

    public Map getDetailChartSources() {
        Map testname2ChartSource = new LinkedHashMap();
        if(!(samples instanceof GroupedResponsetimeSamples)) {
            return testname2ChartSource;
        }
        GroupedResponsetimeSamples groupedSamples = (GroupedResponsetimeSamples)samples;
        for (Iterator it2 = groupedSamples.getSampleGroups().iterator(); it2.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it2.next();
            DetailsResponsetimeChartSource source = new DetailsResponsetimeChartSource(sampleGroup);
            testname2ChartSource.put(sampleGroup.getName(), source);
        }
        return testname2ChartSource;
    }
}
