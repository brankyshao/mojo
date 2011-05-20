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

import junit.framework.TestCase;

public class ReportMojoTest extends TestCase {

    public void testSimple() throws Exception {
        TestHelper.performReport("src/test/resources/test1-junitsamples.jtl", "src/test/resources/test1-gc.txt",
                "test1");
    }

    public void testJtl22Combined2() throws Exception {
        TestHelper.performReport("src/test/resources/combinedtest-jtl22-summaryreport.jtl", null, "test5");
    }

    public void testOutputName() {
        ReportMojo mojo = new ReportMojo();
        mojo.reportid = "out";
        assertEquals("out", mojo.getOutputName());
    }

    public void testGetGc() {
        ReportMojo mojo = new ReportMojo();
        mojo.project = TestHelper.newMavenProject();
        assertFalse(mojo.getConfig().isShowgc());
    }

    public void testGetId() {
        ReportMojo mojo = new ReportMojo();
        mojo.reportid = "xx";
        assertEquals("xx", mojo.getConfig().getId());
        mojo.reportid = "yy";
        assertEquals("yy", mojo.getConfig().getId());
        mojo.reportid = null;
        mojo.dataid = "zz";
        assertEquals("zz", mojo.getConfig().getId());
    }
}