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
package org.codehaus.mojo.chronos;

public class ReportConfigStub implements ReportConfig {

    public int getAverageduration() {
        return 20000;
    }

    public String getDescription() {
        return "description";
    }

    public String getId() {
        return "id";
    }

    public int getResponsetimedivider() {
        return 10;
    }

    public long getThreadcountduration() {
        return 20000;
    }

    public String getTitle() {
        return "Title";
    }

    public boolean isShowaverage() {
        return true;
    }

    public boolean isShowdetails() {
        return true;
    }

    public boolean isShowgc() {
        return true;
    }

    public boolean isShowhistogram() {
        return true;
    }

    public boolean isShowinfotable() {
        return true;
    }

    public boolean isShowpercentile() {
        return true;
    }

    public boolean isShowresponse() {
        return true;
    }

    public boolean isShowsummary() {
        return true;
    }

    public boolean isShowthroughput() {
        return true;
    }

    public boolean isShowtimeinfo() {
        return true;
    }

    public boolean isShowsummarycharts() {
        return true;
    }

    /* Merged from Atlassion */
    public double getHistoryChartUpperBound() {
        return 30000;
    }

    public String getMetadata() {
        return "src/test/resources/metadata.properties";
    }
}