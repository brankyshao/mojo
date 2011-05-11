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

import junit.framework.TestCase;
import org.codehaus.mojo.chronos.TestHelper;

import java.io.File;

public class SaveHistoryMojoTest extends TestCase {

    public void testHistory() throws Exception {
        final String dataId = "SaveHistoryTest";

        TestHelper.removeDataDir(dataId);
        //This is normally done by the JMeterTestMojo.
        TestHelper.parseJMeterLog(dataId, "src/test/resources/combinedtest-jtl22-summaryreport.jtl");

        
        SaveHistoryMojo shmojo = new SaveHistoryMojo();
        shmojo.setHistorydir(new File("target/chronos/"));
        shmojo.setDataid(dataId);
        shmojo.project = TestHelper.newMavenProject();
        shmojo.execute();
    }
}