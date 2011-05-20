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
package org.codehaus.mojo.chronos.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

/**
 * Holder to handle historic results and calculate statistics.
 * 
 * @author ksr@lakeside.dk
 */
public final class HistoricSamples {

    private Set groupNames = new LinkedHashSet();
    private List samples = new ArrayList();

    public void addHistoricSample(HistoricSample sample) {
        samples.add(sample);
        groupNames.addAll(sample.getGroupNames());
    }

    public String[] getGroupNames() {
        return (String[])groupNames.toArray(new String[groupNames.size()]);
    }

    public TimeSeries getAverageTime(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimeAverage();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getAverageTime(String name, final String groupName) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimeAverage(groupName);
            }

            public boolean accept(HistoricSample sample) {
                return sample.getGroupNames().contains(groupName);
            }
        });
    }

    public TimeSeries getpercentile95(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetime95Percentile();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getPercentile95(String name, final String groupName) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getResponsetimePercentiles(groupName);
            }

            public boolean accept(HistoricSample sample) {
                return sample.getGroupNames().contains(groupName);
            }
        });
    }

    public TimeSeries getThroughput(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getMaxAverageThroughput();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getGcRatio(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getGcRatio();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    public TimeSeries getKbCollectedPrSecond(String name) {
        return visitAll(name, new HistoricSampleExtractor() {
            public double extract(HistoricSample sample) {
                return sample.getCollectedPrSecond();
            }

            public boolean accept(HistoricSample sample) {
                return true;
            }
        });
    }

    private TimeSeries visitAll(String name, HistoricSampleExtractor visitor) {
        TimeSeries series = new TimeSeries(name);
        Iterator it = samples.iterator();
        while (it.hasNext()) {
            HistoricSample sample = (HistoricSample)it.next();
            if(visitor.accept(sample)) {
                Millisecond timestamp = new Millisecond(new Date(sample.getTimestamp()));
                double value = visitor.extract(sample);
                series.addOrUpdate(timestamp, value);
            }
        }
        return series;
    }

    /**
     * Base interface for extracting statistics from historic results.
     * 
     * @author kent (creator)
     * @author $LastChangedBy$ $LastChangedDate$
     * @version $Revision$
     */
    interface HistoricSampleExtractor {
        double extract(HistoricSample sample);

        boolean accept(HistoricSample sample);
    }
}
