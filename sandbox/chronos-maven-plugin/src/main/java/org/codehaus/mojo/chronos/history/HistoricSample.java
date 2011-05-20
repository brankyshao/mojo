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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.mojo.chronos.gc.GCSamples;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This is a historic sample representing the statistics from a previous run.
 * 
 * @author ksr@lakeside.dk
 */
public class HistoricSample {
    private static final int DEFAULT_DURATION = 20000;

    private static final long serialVersionUID = 8492792243093456318L;

    private long timestamp;
    private double gcRatio = -1d;
    private double collectedPrSecond = -1d;

    private double responsetimeAverage = -1d;
    private double responsetime95Percentile = -1d;
    private HashMap individualPercentiles;
    private HashMap individualAverages;

    private double maxAverageThroughput = -1d;

    public HistoricSample(GroupedResponsetimeSamples responseSamples, GCSamples gcSamples) {
        timestamp = responseSamples.getFirstTimestamp();
        responsetimeAverage = responseSamples.getAverage(1);
        responsetime95Percentile = responseSamples.getPercentile95(1);
        individualAverages = new HashMap();
        individualPercentiles = new HashMap();
        Iterator it = responseSamples.getSampleGroups().iterator();
        while (it.hasNext()) {
            ResponsetimeSampleGroup group = (ResponsetimeSampleGroup)it.next();
            individualAverages.put(group.getName(), new Double(group.getAverage(1)));
            individualPercentiles.put(group.getName(), new Double(group.getPercentile95(1)));
        }
        if(gcSamples != null) {
            gcRatio = gcSamples.getGarbageCollectionRatio(responseSamples.getTotalTime());
            collectedPrSecond = gcSamples.getCollectedKBPerSecond(responseSamples.getTotalTime());
        }
        int averageDuration = Math.max(DEFAULT_DURATION, (int)responsetime95Percentile);
        maxAverageThroughput = responseSamples.getMaxAverageThroughput(averageDuration, 1);
    }

    private HistoricSample() {
        // Do nothing
    }

    /**
     * @return Returns the timestamp.
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Returns the gcRatio.
     */
    public final double getGcRatio() {
        return gcRatio;
    }

    /**
     * @return Returns the collectedPrSecond.
     */
    public final double getCollectedPrSecond() {
        return collectedPrSecond;
    }

    /**
     * @return Returns the responsetimeAverage.
     */
    public final double getResponsetimeAverage() {
        return responsetimeAverage;
    }

    /**
     * @return Returns the responsetime95Percrntile.
     */
    public final double getResponsetime95Percentile() {
        return responsetime95Percentile;
    }

    public final Set getGroupNames() {
        return individualAverages.keySet();
    }

    public final double getResponsetimeAverage(String groupName) {
        return ((Double)individualAverages.get(groupName)).doubleValue();
    }

    public final double getResponsetimePercentiles(String groupName) {
        return ((Double)individualPercentiles.get(groupName)).doubleValue();
    }

    /**
     * @return Returns the maxAverageThroughput.
     */
    public final double getMaxAverageThroughput() {
        return maxAverageThroughput;
    }

    public final Element toXML() {
        Element historyXML = new Element("history");

        historyXML.setAttribute("timestamp", Long.toString(timestamp));
        historyXML.setAttribute("gcRatio", Double.toString(gcRatio));
        historyXML.setAttribute("collectedPrSecond", Double.toString(collectedPrSecond));

        historyXML.setAttribute("responsetimeAverage", Double.toString(responsetimeAverage));
        historyXML.setAttribute("responsetime95Percentile", Double.toString(responsetime95Percentile));
        historyXML.setAttribute("maxAverageThroughput", Double.toString(maxAverageThroughput));

        Element individualPercentilesXML = new Element("individualPercentiles");
        for (Iterator iterator = individualPercentiles.entrySet().iterator(); iterator.hasNext();) {
            Entry entry = (Entry)iterator.next();

            individualPercentilesXML.addContent(new Element("entry").setAttribute("key", (String)entry.getKey())
                    .setAttribute("value", ((Double)entry.getValue()).toString()));
        }
        historyXML.addContent(individualPercentilesXML);

        Element individualAveragesXML = new Element("individualAverages");
        for (Iterator iterator = individualAverages.entrySet().iterator(); iterator.hasNext();) {
            Entry entry = (Entry)iterator.next();

            individualAveragesXML.addContent(new Element("entry").setAttribute("key", (String)entry.getKey())
                    .setAttribute("value", ((Double)entry.getValue()).toString()));
        }
        historyXML.addContent(individualAveragesXML);

        return historyXML;
    }

    public static HistoricSample fromXML(File file) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(file);

        Element historyXML = document.getRootElement();
        if(!"history".equals(historyXML.getName())) {
            throw new JDOMException("Invalid XML structure - history tag expected, but was " + historyXML.getName());
        }

        HistoricSample hs = new HistoricSample();
        hs.timestamp = Long.parseLong(historyXML.getAttributeValue("timestamp"));
        hs.gcRatio = Double.parseDouble(historyXML.getAttributeValue("gcRatio"));
        hs.collectedPrSecond = Double.parseDouble(historyXML.getAttributeValue("collectedPrSecond"));

        hs.responsetimeAverage = Double.parseDouble(historyXML.getAttributeValue("responsetimeAverage"));
        hs.responsetime95Percentile = Double.parseDouble(historyXML.getAttributeValue("responsetime95Percentile"));
        hs.maxAverageThroughput = Double.parseDouble(historyXML.getAttributeValue("maxAverageThroughput"));

        hs.individualPercentiles = populateMap(historyXML.getChild("individualPercentiles"));
        hs.individualAverages = populateMap(historyXML.getChild("individualAverages"));
        
        return hs;
    }

    private static HashMap populateMap(Element xml) {
        HashMap res = new HashMap();

        for (Iterator iterator = xml.getChildren().iterator(); iterator.hasNext();) {
            Element entryXML = (Element)iterator.next();

            res.put(entryXML.getAttributeValue("key"), Double.valueOf(entryXML.getAttributeValue("value")));
        }
        return res;
    }
}
