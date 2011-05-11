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

import java.io.File;

/**
 * Class containing legacy methods.<br />
 * These methods SHOULD not be used.
 * 
 * @author ads
 */
public class UtilsLegacy {

    /* pp */static File getGcSamplesSer(File baseDir, String id) {
        File chronosDir = Utils.getChronosDir(baseDir);
        return new File(chronosDir, Utils.GC_FILE_PREFIX + id + Utils.SERIALIZED_FILE_EXTENSION);
    }

    /* pp */static File getPerformanceSamplesSer(File baseDir, String id) {
        File chronosDir = Utils.getChronosDir(baseDir);
        return new File(chronosDir, Utils.PERFORMANCESAMPLE_FILE_PREFIX + id + Utils.SERIALIZED_FILE_EXTENSION);
    }
}