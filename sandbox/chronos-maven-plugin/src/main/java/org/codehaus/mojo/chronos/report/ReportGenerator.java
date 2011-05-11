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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.mojo.chronos.ReportConfig;
import org.codehaus.mojo.chronos.chart.ChartSource;
import org.codehaus.mojo.chronos.chart.GraphGenerator;
import org.codehaus.mojo.chronos.responsetime.GroupedResponsetimeSamples;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSampleGroup;
import org.codehaus.mojo.chronos.responsetime.ResponsetimeSamples;

/**
 * Generates the JMeter report.
 * 
 * @author ksr@lakeside.dk
 */
public final class ReportGenerator {
    private static final String IMG_EXT = ".png";

    private NumberFormat formatter;

    private ReportConfig config;

    private ResourceBundle bundle;

    private ReportSink reportSink;

    private Sink sink;

    private GraphGenerator graphs;

    /**
     * @param bundle
     *            The {@link ResourceBundle} to extract messages from
     * @param config
     *            The {@link ReportConfig} of the report generation
     */
    public ReportGenerator(ResourceBundle bundle, ReportConfig config, GraphGenerator graphs) {
        this.formatter = new DecimalFormat("#.#");
        this.bundle = bundle;
        this.config = config;
        this.graphs = graphs;
    }

    /**
     * Generate a report (as an html page).
     * 
     * @param aSink
     *            The {@link Sink} to output the report content to
     * @param samples
     *            The {@link ResponsetimeSamples} to create a report from
     * @throws MavenReportException
     *             Thrown if the report generator fails.
     */
    public void doGenerateReport(Sink aSink, ResponsetimeSamples samples) throws MavenReportException {
        this.reportSink = new ReportSink(bundle, aSink);
        this.sink = aSink;

        aSink.head();
        aSink.text(bundle.getString("chronos.description"));
        aSink.head_();
        aSink.body();

        constructReportHeaderSection();
        if(config.isShowsummary()) {
            constructReportSummarySection(samples);
        }
        if(config.isShowdetails() && samples instanceof GroupedResponsetimeSamples) {
            constructIndividualTestsSection((GroupedResponsetimeSamples)samples);
        }
        aSink.body_();
        aSink.flush();
        aSink.close();
    }

    private void constructReportHeaderSection() throws MavenReportException {
        String title = config.getTitle();
        String description = config.getDescription();
        String anchor = "Report" + config.getId();
        
        reportSink.constructHeaderSection(title, description, anchor);
        
        Map metadataFile = parseMetadata(config.getMetadata());
        String titleMetadata = bundle.getString("chronos.label.summary");
        String anchorMetadata = "Summary" + config.getId();
        reportSink.metadataTable(titleMetadata, anchorMetadata, metadataFile);
    }

    private Map parseMetadata(String metadata) throws MavenReportException {
        if(metadata == null) {
            return Collections.emptyMap();
        }

        Map res = new LinkedHashMap();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(metadata)));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                int tabIndex = strLine.indexOf('\t');
                if(tabIndex == -1) {
                    res.put(strLine, "&lt;no value&gt;");
                } else {
                    res.put(strLine.substring(0, tabIndex), strLine.substring(tabIndex + 1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    private void constructReportSummarySection(ResponsetimeSamples samples) {
        String text = bundle.getString("chronos.label.summary");
        String anchor = "Summary" + config.getId();
        reportSink.title2(text, anchor);
        constructReportHotLinks();

        if(config.isShowinfotable() && samples instanceof GroupedResponsetimeSamples) {
            sink.table();
            sink.tableRow();
            reportSink.th("chronos.label.tests");
            if(config.isShowtimeinfo()) {
                reportSink.th("chronos.label.percentile95");
                reportSink.th("chronos.label.averagetime");
            }
            reportSink.th("chronos.label.iterations");
            reportSink.th("chronos.label.successrate");
            sink.tableRow_();

            GroupedResponsetimeSamples groupedSamples = (GroupedResponsetimeSamples)samples;
            for (Iterator it = groupedSamples.getSampleGroups().iterator(); it.hasNext();) {
                ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
                sink.tableRow();

                reportSink.sinkCellLink(sampleGroup.getName(), "#a" + sampleGroup.getIndex() + config.getId());
                if(config.isShowtimeinfo()) {
                    int responsetimedivider = config.getResponsetimedivider();
                    double percentile95 = sampleGroup.getPercentile95(responsetimedivider);
                    reportSink.sinkCell(formatter.format(percentile95));
                    double average = sampleGroup.getAverage(config.getResponsetimedivider());
                    reportSink.sinkCell(formatter.format(average));
                }
                // Line merged from Atlessian.
                // reportSink.sinkCell("" + sampleGroup.size() * config.getResponsetimedivider());
                reportSink.sinkCell("" + sampleGroup.size());
                reportSink.sinkCell(formatter.format(sampleGroup.getSuccessrate()) + " %");
                sink.tableRow_();
            }
            sink.table_();
            reportSink.sinkLineBreak();
        }

        if(!config.isShowsummarycharts()) {
            return;
        }

        for (Iterator iterator = graphs.getSummaryChartSources().iterator(); iterator.hasNext();) {
            ChartSource chartSource = (ChartSource)iterator.next();
            if(chartSource.isEnabled(bundle, config)) {
                reportSink.graphics(chartSource.getFileName(bundle, config) + IMG_EXT);
            }
        }
    }

    private void constructIndividualTestsSection(GroupedResponsetimeSamples samples) {
        reportSink.title2(bundle.getString("chronos.label.testcases"), "Test_Cases" + config.getId());
        constructReportHotLinks();

        for (Iterator it = samples.getSampleGroups().iterator(); it.hasNext();) {
            ResponsetimeSampleGroup sampleGroup = (ResponsetimeSampleGroup)it.next();
            reportSink.title3(sampleGroup.getName(), sampleGroup.getIndex() + config.getId());

            if(config.isShowinfotable()) {
                sink.table();
                List headerLabels = new ArrayList();

                if(config.isShowtimeinfo()) {
                    headerLabels.add("chronos.label.mintime");
                    headerLabels.add("chronos.label.averagetime");
                    headerLabels.add("chronos.label.percentile95");
                    headerLabels.add("chronos.label.maxtime");
                }
                headerLabels.add("chronos.label.iterations");
                headerLabels.add("chronos.label.failures");
                headerLabels.add("chronos.label.successrate");
                List dataLine = new ArrayList();
                if(config.isShowtimeinfo()) {
                    dataLine.add(formatter.format(sampleGroup.getMin(config.getResponsetimedivider())));
                    dataLine.add(formatter.format(sampleGroup.getAverage(config.getResponsetimedivider())));
                    dataLine.add(formatter.format(sampleGroup.getPercentile95(config.getResponsetimedivider())));
                    dataLine.add(formatter.format(sampleGroup.getMax(config.getResponsetimedivider())));
                }
                dataLine.add("" + sampleGroup.size() * config.getResponsetimedivider());
                dataLine.add(formatter.format(sampleGroup.getFailed()));
                dataLine.add(formatter.format(sampleGroup.getSuccessrate()) + " %");
                List dataLines = new ArrayList();
                dataLines.add(dataLine);

                reportSink.table(headerLabels, dataLines);
            }
            for (Iterator iterator = graphs.getDetailsChartSources(sampleGroup.getName()).iterator(); iterator
                    .hasNext();) {
                ChartSource source = (ChartSource)iterator.next();
                if(source.isEnabled(bundle, config)) {
                    reportSink.graphics(source.getFileName(bundle, config) + IMG_EXT);
                }
            }
        }
        reportSink.sinkLineBreak();
    }

    private void constructReportHotLinks() {
        sink.section3();
        if(config.isShowsummary()) {
            reportSink.sinkLink(bundle.getString("chronos.label.summary"), "Summary" + config.getId());
        }

        if(config.isShowdetails()) {
            reportSink.sinkLink(bundle.getString("chronos.label.testcases"), "Test_Cases" + config.getId());
        }
        sink.section3_();
    }

}
