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

import junit.framework.TestCase;

import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.site.renderer.DefaultSiteRenderer;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.mojo.chronos.TestHelper;

public class HistoryReportMojoTest extends TestCase {

    private static final String HISTORY_ID = "HistoryTest";
    private static final File HISTORY_FOLDER = new File("target/chronos/history");
    private static final String BASE_RES = "src/test/resources/";

    public void test() throws Exception {
        MavenProject mavenProject = TestHelper.newMavenProject();
        SiteRenderer siteRenderer = new DefaultSiteRenderer();

        TestHelper.performReport(mavenProject, siteRenderer, BASE_RES + "test1-junitsamples.jtl", BASE_RES
                + "test1-gc.txt", HISTORY_ID);

        saveHistory(mavenProject, siteRenderer, HISTORY_ID);

        TestHelper.performReport(mavenProject, siteRenderer, BASE_RES + "combinedtest-jtl22-summaryreport.jtl", null,
                HISTORY_ID);
        saveHistory(mavenProject, siteRenderer, HISTORY_ID);

        HistoryReportMojo hrmojo = new HistoryReportMojo();
        hrmojo.setDataid(HISTORY_ID);
        hrmojo.setHistorydir(HISTORY_FOLDER);
        hrmojo.project = mavenProject;
        hrmojo.siteRenderer = new DefaultSiteRenderer();
        hrmojo.setOutputDirectory(HISTORY_FOLDER.getParentFile().getAbsolutePath());
        hrmojo.execute();
    }

    private void saveHistory(MavenProject mavenProject, SiteRenderer siteRenderer, String id) throws Exception {
        SaveHistoryMojo shmojo = new SaveHistoryMojo();
        shmojo.setHistorydir(HISTORY_FOLDER);
        shmojo.setDataid(id);
        shmojo.project = mavenProject;
        shmojo.execute();
    }
}