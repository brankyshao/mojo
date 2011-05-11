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

import org.apache.maven.model.Model;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.site.renderer.DefaultSiteRenderer;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.mojo.chronos.jmeter.JMeterTestMojo;

import java.io.File;

public class TestHelper {

    public static void performReport(String jtlFile, String gcFile, final String id) throws Exception {
        performReport(newMavenProject(), new DefaultSiteRenderer(), jtlFile, gcFile, id);
    }

    public static void performReport(MavenProject project, SiteRenderer siteRenderer, String jtlFile, String gcFile,
            final String dataId) throws Exception {
        removeDataDir(dataId);
        //This is normally done by the JMeterTestMojo.
        parseJMeterLog(dataId, jtlFile);
        if(gcFile != null) {
            parseGcLog(dataId, gcFile);
        }


        ReportMojo mojo = new ReportMojo();
        mojo.dataid = dataId;
        mojo.title = "title";
        mojo.description = "here is my description";
        mojo.siteRenderer = siteRenderer;
        mojo.outputDirectory = "target/chronos/" + dataId;
        mojo.project = project;
        mojo.showdetails = false;
        mojo.showhistogram = false;
        mojo.showresponse = false;
        mojo.metadata = "src/test/resources/metadata.properties";
        mojo.execute();
    }

    public static void removeDataDir(String dataId) {
        File file = new File("target/chronos/" + dataId);
        if(file.exists()) {
            deleteRecursive(file);
        }
    }

    public static void parseJMeterLog(String dataId, String jtlFile) throws MojoExecutionException {
        JMeterTestMojo.parseJmeterOutput(new File(jtlFile), dataId);
    }


    private static void parseGcLog(String dataId, String gcFile) throws MojoExecutionException {
        JMeterTestMojo.parseGCLog(new File(gcFile), dataId);
    }

    private static void deleteRecursive(File file) {
        if(file.isDirectory()) {
            File[] elements = file.listFiles();
            for (int i = 0; i < elements.length; i++) {
                deleteRecursive(elements[i]);
            }
        }
        file.delete();
    }

    public static MavenProject newMavenProject() {
        Model model = new Model();
        model.setName("test");
        model.setUrl("url");
        MavenProject project = new MavenProject(model);
        project.setFile(new File("pom.xml"));
        return project;
    }
}